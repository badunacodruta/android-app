package org.mihigh.cycling.location;

import android.location.Location;

import com.google.android.gms.location.LocationListener;

import org.mihigh.cycling.MainActivity;

public class LocationUpdater implements LocationListener {

  private MainActivity activity;

  public LocationUpdater(MainActivity activity) {
    this.activity = activity;
  }

  @Override
  public void onLocationChanged(Location location) {
    activity.setLatLng(location);
    new ServerPositionCommunicatior().execute(location, activity);
  }
}
