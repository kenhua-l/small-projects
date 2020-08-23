import dropbox
import boxsdk
import requests
import json
import os
import sys
import datetime

SENSITIVE_FILE_NAME = 'config.dbs'
SENSITIVE_TOKEN_FILE = 'secret.dbs'

def get_folderList(fileName, method=1):
    if method==1:
        folder_arr = []
        with open(fileName, 'r') as f:
            for line in f:
                if line.strip('\n') != "":
                    folder_arr.append(line.strip('\n'))
        return folder_arr
    else:
        folder_dict = {}
        with open(fileName, 'r') as f:
            for line in f:
                name_id = line.strip('\n').split(', ')
                folder_dict[name_id[0]] = name_id[1]
        return folder_dict

def get_secret(key):
    secret = ''
    with open(SENSITIVE_TOKEN_FILE, 'r') as f:
        for line in f:
            key_val = line.strip('\n').split(' = ')
            print key_val
            if key_val[0] == key:
                secret = key_val[1]
                break
    return secret

folders_to_sync = get_folderList('tosync.folder')
folders_to_ignore = get_folderList('toignore.folder')

db_access_token = get_secret('db_access_token')
dbx = dropbox.Dropbox(db_access_token)

class Leaf():
    def __init__(self, name, path, date_modified):
        self.name = name
        self.path = path
        self.date_modified = date_modified

    def __repr__(self):
        return self.name + ' (' + str(self.date_modified) + ')'

def list_files_and_folders(path):
    mini_list = []
    mini_dict = {}
    for entry in dbx.files_list_folder(path).entries:
        if type(entry) == type(dropbox.files.FileMetadata()):
            mini_list.append(Leaf(entry.name, path+'/'+entry.name, entry.client_modified))
            print path+'/'+entry.name + ' ' + str(entry.client_modified)
        else:
            if (path + '/' + entry.name) in folders_to_ignore:
                continue
            mini_dict[entry.name] = list_files_and_folders(path+'/'+entry.name)
            mini_list.append(mini_dict)
            mini_dict = {}
    return mini_list

def dropbox_list_all():
    mega_dict = {}
    for entry in dbx.files_list_folder('').entries:
        if entry.name in folders_to_sync:
            print '---------' + entry.name + '---------'
            mega_dict[entry.name] = list_files_and_folders('/'+entry.name)
    return mega_dict

files_to_change = dropbox_list_all()
# print files_to_change

###################################### BOX ####################################
# box auth
box_client_id = get_secret('box_client_id')
box_client_secret = get_secret('box_client_secret')
box_id=get_folderList('boxid.folder', 0)

def secret_token(refresh_token):
    payload = {'grant_type':'refresh_token', 'refresh_token':refresh_token, 'client_id':box_client_id, 'client_secret': box_client_secret}
    r = requests.post('https://api.box.com/oauth2/token', payload)
    with open(SENSITIVE_FILE_NAME, 'r+') as f:
        f.seek(0)
        f.write(r.text)
        f.truncate()
    f.close()
    print r.text

def get_token():
    with open(SENSITIVE_FILE_NAME, 'r+') as f:
        data = json.load(f)
    f.close()
    return data

def get_access_token_from_file():
    data = get_token()
    return data['access_token']

def get_refresh_token_from_file():
    data = get_token()
    return data['refresh_token']

def get_access_token():
    secret_token(get_refresh_token_from_file())
    return get_access_token_from_file()

oauth = boxsdk.OAuth2(
    client_id=box_client_id,
    client_secret=box_client_secret,
    access_token=get_access_token(),
)

client = boxsdk.Client(oauth)
# root_folder = client.folder(folder_id='0').get_items(limit=100, offset=0)
list_to_create = []

def log(message):
    with open('log.txt', 'a') as f:
        f.write(message + '\n')

def sync_create_file(dropbox_path, box_folder_id):
    file_name = dropbox_path[1:].split('/')[-1]
    sys.stdout.write('Downloading from Dropbox -')
    sys.stdout.flush()
    with open('temp-'+file_name, 'wb') as temp_f:
        metadata, res = dbx.files_download(dropbox_path)
        temp_f.write(res.content)
    sys.stdout.write(' Uploading to Box -')
    sys.stdout.flush()
    client.folder(folder_id=box_folder_id).upload('temp-'+file_name, file_name)
    sys.stdout.write(' Done with ' + file_name + '\n')
    sys.stdout.flush()
    os.remove('temp-'+file_name)
    log('Uploaded ' + file_name)

def syncs_process():
    counter = 1
    log(str(datetime.datetime.now()))
    for f in list_to_create:
        print counter, '/', len(list_to_create), 'files left'
        sync_create_file(f[0], f[1])
        counter +=1

def check_in_folder(folder, path):
    for f in folder:
        if f.type == 'file':
            print f.id + ' ' + path + '/'+ f.name +  ' ' + str(f.get()['modified_at'])
        else:
            check_in_folder(client.folder(folder_id=f.id).get_items(limit=100, offset=0), path+'/'+f.name)

def recursive_box_check(list_of_item, current_box_folder_id, path):
    #for create only
    files_in_dropbox = [ item.path for item in list_of_item if isinstance(item, Leaf)]
    folders_in_dropbox = [ item for item in list_of_item if not isinstance(item, Leaf) ]
    items_in_box = client.folder(folder_id=current_box_folder_id).get_items(limit=100, offset=0)
    files_in_box = [ path+'/'+f.name for f in items_in_box if f.type == 'file']
    folders_in_box = [f for f in items_in_box if f.type != 'file']
    # upload from Dropbox to box
    for i in list(set(set(files_in_dropbox).difference(set(files_in_box)))):
        list_to_create.append((i, current_box_folder_id))

    for item in folders_in_dropbox:
        for key in item.keys():
            if key not in [f.name for f in folders_in_box]:
                child = client.folder(folder_id=current_box_folder_id).create_subfolder(key)
                recursive_box_check(item[key], child.id, path+'/'+key)
            else:
                folder_name = [f.name for f in folders_in_box]
                index = folder_name.index(key)
                box_f = folders_in_box[index]
                recursive_box_check(item[key], box_f.id, path+'/'+key)

for folder in folders_to_sync:
    recursive_box_check(files_to_change[folder], box_id[folder], '/'+folder)

syncs_process()

def main():
    print 'hello'
