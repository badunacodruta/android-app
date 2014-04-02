package org.mihigh.cycling.location;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.location.Location;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.mihigh.cycling.commons.Json;

public class ServerPositionCommunicatior extends AsyncTask {


  //  public static final String HOST = "mciorobe.eur.adobe.com";
  public static final String HOST = "169.254.246.129";
  public static final String URI = "http://" + HOST + "/get/%s/%s/%s/%s";
  public static final String ROOM_ID = "room1";
  public static final String USER_ID = "MihaiC";


  @Override
  protected Object doInBackground(Object[] params) {
    Location location = (Location) params[0];

    HttpClient httpclient = new DefaultHttpClient();
    try {
      String uri = String.format(URI, ROOM_ID, USER_ID, location.getLatitude(), location.getLongitude());
      HttpResponse response = httpclient.execute(new HttpGet(uri));
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      response.getEntity().writeTo(out);
      String responseString = out.toString();

      return responseString;
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void onPostExecute(Object o) {
    String responseString = (String) o;
    super.onPostExecute(o);

    ServerRooms rooms = Json.parseJson(responseString, ServerRooms.class);


  }
}