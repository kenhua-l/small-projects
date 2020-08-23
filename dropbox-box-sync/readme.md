# Dropbox - Box Syncer

## Requirements
1. Dropbox sdk
`sudo pip install dropbox`
2. Box sdk
`sudo pip install boxsdk`

## Steps
1. Fill in the variables of sensitive information (that can be found easily through console)
- dropbox_access_key
- box_client_id
- box_client_secret
2. Box requires a tedious authentication for access_token
- web browser to this (replacing those in <>) https://account.box.com/api/oauth2/authorize?response_type=code&client_id=<MY_CLIENT_ID>&redirect_uri=<MY_REDIRECT_URL>&state=<MY_SECURITY_TOKEN>
- login to box and grant permission. Get the Auth key from URL - <MY_REDIRECT_URI>?code=<MY_AUTHORIZATION_CODE>
- curl this with the sensitive information
`curl https://api.box.com/oauth2/token \
-d 'grant_type=authorization_code' \
-d 'code=<MY_AUTH_CODE>' \
-d 'client_id=<MY_CLIENT_ID>' \
-d 'client_secret=<MY_CLIENT_SECRET>' \
-X POST`
- you should get both access_token and refresh_token. take the whole json and put it into a safe file where you will access it in the code.
- list the folders you want to sync and the folders in the folders mentioned you want to ignore
- run `python Ken-Syncer.py`

## TODO
Currently, it only uploads new file and folders into box.

Next, should be Edit (check if file modified) and delete.
