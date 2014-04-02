package org.mihigh.cycling.location;

import java.util.HashMap;

public class ServerRooms {

  public HashMap<String, HashMap<String, UserInfo>> rooms = new HashMap<String, HashMap<String, UserInfo>>();
}

class UserInfo {

  String lat;
  String lng;

  UserInfo() {
  }

  UserInfo(String lat, String lng) {
    this.lat = lat;
    this.lng = lng;
  }
}