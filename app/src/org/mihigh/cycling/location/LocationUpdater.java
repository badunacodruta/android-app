package org.mihigh.cycling.location;

import android.location.Location;

import com.google.android.gms.location.LocationListener;

import org.mihigh.cycling.MapActivity;

public class LocationUpdater implements LocationListener {

  private MapActivity activity;

  public LocationUpdater(MapActivity activity) {
    this.activity = activity;
  }

  @Override
  public void onLocationChanged(Location location) {
    activity.setLatLng(location);
    new ServerPositionCommunicatior().execute(location, activity);
  }
}
