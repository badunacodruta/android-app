#!/usr/bin/env python

from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer

import os
import json




class Object:
    def to_JSON(self):
        return json.dumps(self, default=lambda o: o.__dict__, sort_keys=True, indent=4)

state = Object()
state.rooms = Object()


testUser = Object()
testUser.lat = 44.430664
testUser.lng = 26.0350287
testUser.delta = 0.0001
testUser.roomId = "room1"
testUser.userId = "test"



class CyclingHTTPRequestHandler(BaseHTTPRequestHandler):
    # /register/{roomId}/{useriD}
    # /get/{roomId}/{userId}/{lat}/{lng}
    def do_GET(self):
        global state
        global testUser

        self.send_response(200)
        self.send_header('Content-type','text/javascript')
        self.end_headers()  

        if '/register' in self.path:
            # /register/{roomId}/{useriD}
            params = self.path.split('/');
            roomId = params[2]
            userId = params[3]

            self.register_user(roomId, userId)
            self.register_user(testUser.roomId, testUser.userId)
            self.wfile.write(state.to_JSON())

        if '/get' in self.path:
            # /get/{roomId}/{userId}/{lat}/{lng}
            params = self.path.split('/');
            roomId = params[2]
            userId = params[3]
            lat = params[4]
            lng = params[5]
            
            self.update_user_position(roomId, userId, lat, lng)

            testUser.lat += testUser.delta
            testUser.lng += testUser.delta
            self.update_user_position(testUser.roomId, testUser.userId, str(testUser.lat), str(testUser.lng))
            self.wfile.write(state.to_JSON())

    def register_user(self, roomId, userId):
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
    def update_user_position(self, roomId, userId, lat, lng):
        self.register_user(roomId, userId)
        userList = getattr(state.rooms, roomId)
        userInfo = getattr(userList, userId)
        userInfo.lat = lat
        userInfo.lng = lng
        userInfo.thumbnailUrl = "https://scontent-b-fra.xx.fbcdn.net/hphotos-frc3/t1.0-9/579844_431638206922417_1213358917_n.jpg"
        


    
def run():
    print('http server is starting...')

    #ip and port of servr
    #by default http server port is 80
    server_address = ('', 80)
    httpd = HTTPServer(server_address, CyclingHTTPRequestHandler)
    print('http server is running...')
    httpd.serve_forever()
    
run()
