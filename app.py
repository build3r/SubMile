#!/usr/bin/env python
import web
import xml.etree.ElementTree as ET
from pymongo import MongoClient
import json
uri = "mongodb://gojek-sharath:QbXmwBtPdYeG4bwKMWwElvR3xER7NLQycaD5GXpIoUvoYk2OoXKRK2OjKrycNpMD21h8Vs3HozFYkPs1dc97vg==@gojek-sharath.documents.azure.com:10250/?ssl=true&ssl_cert_reqs=CERT_NONE"
client = MongoClient(uri)
db=client.test1_database
posts = db.posts
urls = (
    '/users/(.*)', 'get_user',
    '/add_route', 'add_route'
)

app = web.application(urls, globals())

class get_user:
    def GET(self, user):
        print str(user)
        ret_data = posts.find_one({"user_id": str(user)})
        print str(ret_data).encode('ascii','ignore')
        return str(ret_data).replace('u\'','\"').replace('\'','\"')

class add_route:
    def POST(self):
        post_data = web.data()
        print str(post_data)
        obj = json.loads(str(post_data))
        return posts.insert_one(obj).inserted_id

if __name__ == "__main__":
    app.run()
