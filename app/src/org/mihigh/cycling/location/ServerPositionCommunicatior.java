package org.mihigh.cycling.location;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.mihigh.cycling.commons.Constants;
import org.mihigh.cycling.commons.Json;
import org.mihigh.cycling.location.dto.ServerRooms;
import org.mihigh.cycling.location.dto.UserDto;
import org.mihigh.cycling.location.dto.UserInfo;

public class ServerPositionCommunicatior extends AsyncTask {

  private MapActivity mapActivity;

  @Override
  protected Object doInBackground(Object[] params) {
    Location location = (Location) params[0];
    mapActivity = (MapActivity) params[1];

    HttpClient httpclient = new DefaultHttpClient();
    try {
      String uri =
          String.format(Constants.URI,
                        Constants.ROOM_ID,
                        Constants.USER_ID,
                        location.getLatitude(),
                        location.getLongitude());
      HttpResponse response = httpclient.execute(new HttpGet(uri));
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      response.getEntity().writeTo(out);
      String responseString = out.toString();

      ServerRooms rooms = Json.parseJson(responseString, ServerRooms.class);
      HashMap<String, UserDto> usersInfo = rooms.rooms.get(Constants.ROOM_ID);
      HashMap<String, UserInfo> users = new HashMap<String, UserInfo>();

      for (String userId : usersInfo.keySet()) {
        UserDto userDto = usersInfo.get(userId);

        try {
          URL url = new URL(userDto.thumbnailUrl);
          Bitmap thumbnail = BitmapFactory.decodeStream(url.openConnection().getInputStream());
          users.put(userId, new UserInfo(userDto.lat, userDto.lng, thumbnail));
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      return users;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  protected void onPostExecute(Object o) {
    if (o == null) {
      return;
    }
    HashMap<String, UserInfo> users = (HashMap<String, UserInfo>) o;
    super.onPostExecute(o);

    mapActivity.updateOthersPossitions(users);
  }
}
