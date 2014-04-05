package org.mihigh.cycling.location.dto;

import android.graphics.Bitmap;

public class UserInfo {

  public String lat;
  public String lng;
  public Bitmap thumbnail;

  public UserInfo(String lat, String lng, Bitmap thumbnail) {
    this.lat = lat;
    this.lng = lng;
    this.thumbnail = thumbnail;
  }

  UserInfo() {
  }

}
