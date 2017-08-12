import dropbox
import boxsdk
import requests
import json

folders_to_sync = ['']
folders_to_ignore = [ '']
db_access_token = ''
dbx = dropbox.Dropbox(db_access_token)

class leaf():
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
            mini_list.append(leaf(entry.name, path+'/'+entry.name, entry.client_modified))
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
box_client_id=''
box_client_secret=''

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


box_id={}
client = boxsdk.Client(oauth)
# root_folder = client.folder(folder_id='0').get_items(limit=100, offset=0)
def check_in_folder(folder, path):
    for f in folder:
        if f.type == 'file':
            print f.id + ' ' + path + '/'+ f.name +  ' ' + str(f.get()['modified_at'])
        else:
            check_in_folder(client.folder(folder_id=f.id).get_items(limit=100, offset=0), path+'/'+f.name)

for folder in folders_to_sync:
    foo = client.folder(folder_id=box_id[folder]).get_items(limit=100, offset=0)
    path = '/'+folder
    for f in foo:
        if f.type == 'file':
            print f.id + ' ' + path + '/'+ f.name +  ' ' + str(f.get()['modified_at'])
        else:
            check_in_folder(client.folder(folder_id=f.id).get_items(limit=100, offset=0), path+'/'+f.name)

def main():
    print 'hello'
