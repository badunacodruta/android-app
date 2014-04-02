#!/usr/bin/env python

from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer

import os
import json

delta = 0.0001
lat=44.4410
log=26.0471
index = 0

class Object:
    def to_JSON(self):
        return json.dumps(self, default=lambda o: o.__dict__, sort_keys=True, indent=4)

state = Object()
state.rooms = Object()


class CyclingHTTPRequestHandler(BaseHTTPRequestHandler):
    # /register/{roomId}/{useriD}
    # /get/{roomId}/{userId}/{lat}/{lng}
    def do_GET(self):
        global state


        self.send_response(200)
        self.send_header('Content-type','text/javascript')
        self.end_headers()  

        if '/register' in self.path:
            # /register/{roomId}/{useriD}

            params = self.path.split('/');
            roomId = params[2]
            userId = params[3]

            #check is room exists
            if not hasattr(state.rooms, roomId):
                usersInfo = Object()
                setattr(usersInfo, userId, Object())
                setattr(state.rooms, roomId, usersInfo)
            else:
                userList = getattr(state.rooms, roomId)
                #Add user if needed
                if not hasattr(userList, userId):
                    setattr(userList, userId, Object())
                    setattr(state.rooms, roomId, userList)

            self.wfile.write(state.to_JSON())

        if '/get' in self.path:
            # /get/{roomId}/{userId}/{lat}/{lng}

            params = self.path.split('/');
            roomId = params[2]
            userId = params[3]
            lat = params[4]
            lng = params[5]

            userList = getattr(state.rooms, roomId)
            userInfo = getattr(userList, userId)
            userInfo.lat = lat
            userInfo.lng = lng

            self.wfile.write(state.to_JSON())

        


    
def run():
    print('http server is starting...')

    #ip and port of servr
    #by default http server port is 80
    server_address = ('', 80)
    httpd = HTTPServer(server_address, CyclingHTTPRequestHandler)
    print('http server is running...')
    httpd.serve_forever()
    
if __name__ == '__main__':
    run()
