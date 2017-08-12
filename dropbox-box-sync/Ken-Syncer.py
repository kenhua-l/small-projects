import dropbox
import boxsdk
import requests
import json
import os

//
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
print files_to_change

###################################### BOX ####################################
//

def secret_token(refresh_token):
    payload = {'grant_type':'refresh_token', 'refresh_token':refresh_token, 'client_id':box_client_id, 'client_secret': box_client_secret}
    r = requests.post('https://api.box.com/oauth2/token', payload)
    with open('', 'r+') as f:
        f.seek(0)
        f.write(r.text)
        f.truncate()
    f.close()
    print r.text

def get_token():
    with open('', 'r+') as f:
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
def check_in_folder(folder, path):
    for f in folder:
        if f.type == 'file':
            print f.id + ' ' + path + '/'+ f.name +  ' ' + str(f.get()['modified_at'])
        else:
            check_in_folder(client.folder(folder_id=f.id).get_items(limit=100, offset=0), path+'/'+f.name)

for folder in folders_to_sync:
    #for create only
    files_in_dropbox = [ item.path for item in files_to_change[folder] if isinstance(item, Leaf)]
    folders_in_dropbox = [ item for item in files_to_change[folder] if not isinstance(item, Leaf) ]
    # print files_in_dropbox, folders_in_dropbox
    items_in_box = client.folder(folder_id=box_id[folder]).get_items(limit=100, offset=0)
    path = '/'+folder
    files_in_box = [ path+'/'+f.name for f in items_in_box if f.type == 'file']
    folders_in_box = [f for f in items_in_box if f.type != 'file']
    # print files_in_box, folders_in_box
    # upload from Dropbox to box
    for i in list(set(set(files_in_dropbox).difference(set(files_in_box)))):
        file_name = i[1:].split('/')[-1]
        print "Downloading from Dropbox"
        with open('temp-'+file_name, 'wb') as temp_f:
            metadata, res = dbx.files_download(i)
            temp_f.write(res.content)
        print "Uploading to Box"
        client.folder(folder_id=box_id[folder]).upload('temp-'+file_name, file_name)
        print "Done"
        os.remove('temp-'+file_name)


    # path = '/'+folder
    # for f in items_in_box:
    #     if f.type == 'file':
    #         if focus in checklist:
    #             checklist.remove(focus)
    #         print f.id + ' ' + path + '/'+ f.name +  ' ' + str(f.get()['modified_at'])
    #     else:
    #         check_in_folder(client.folder(folder_id=f.id).get_items(limit=100, offset=0), path+'/'+f.name)
    # files_to_change[folder] = checklist

# print files_to_change
def main():
    print 'hello'
