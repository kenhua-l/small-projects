from instagram.client import InstagramAPI

# https://www.instagram.com/oauth/authorize/?client_id=0279ab24db9344f69052c0881b4bf4f4&redirect_uri=https://github.com/kenhua-l/small-projects&response_type=code
# code=41cbfe90b5774c3fa54fe1a05f7d4481

# curl -F 'client_id=0279ab24db9344f69052c0881b4bf4f4'     -F 'client_secret=9c5ee7cebda144da87b9fb6138aab832'     -F 'grant_type=authorization_code'     -F 'redirect_uri=https://github.com/kenhua-l/small-projects'     -F 'code=41cbfe90b5774c3fa54fe1a05f7d4481' https://api.instagram.com/oauth/access_token
# {"access_token": "180513666.0279ab2.3a368108977c4986808e391c4502fef8", "user": {"id": "180513666", "username": "kenfunny", "profile_picture": "https://scontent.cdninstagram.com/t51.2885-19/s150x150/18580026_424685234572292_4995985617465638912_a.jpg", "full_name": "Liew Ken Hua", "bio": "\ud83d\ude0e\ud83d\ude1c\ud83d\ude18 | \ud83c\udfa8", "website": "", "is_business": false}}

access_token = "180513666.0279ab2.3a368108977c4986808e391c4502fef8"
client_secret = "9c5ee7cebda144da87b9fb6138aab832"
api = InstagramAPI(access_token=access_token, client_secret=client_secret)
# user_id="180513666"
use = api.user("180513666")
print use
