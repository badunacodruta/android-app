package org.mihigh.cycling.location.dto;

public class UserDto {

  public String lat;
  public String lng;
  public String thumbnailUrl;

  public UserDto(String lat, String lng, String thumbnailUrl) {
    this.lat = lat;
    this.lng = lng;
    this.thumbnailUrl = thumbnailUrl;
  }


  UserDto() {
  }

}
