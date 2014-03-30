package org.mihigh.cycling;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

public class MainActivity extends FragmentActivity implements
                                                   LocationListener,
                                                   GooglePlayServicesClient.ConnectionCallbacks,
                                                   GooglePlayServicesClient.OnConnectionFailedListener {

  private GoogleMap mMap;
  private LocationClient mLocationClient;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    setUpMapIfNeeded();
    mLocationClient = new LocationClient(this, this, this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    setUpMapIfNeeded();
  }

  private void setUpMapIfNeeded() {
    if (mMap != null) {
      return;
    }
    mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
    if (mMap == null) {
      return;
    }
    // Initialize map options. For example:
    // mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
  }

  @Override
  public void onConnected(Bundle bundle) {

  }

  @Override
  public void onDisconnected() {

  }

  @Override
  public void onLocationChanged(Location location) {

  }

  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {

  }

  @Override
  protected void onStart() {
    super.onStart();
    mLocationClient.connect();
  }

  @Override
  protected void onStop() {
    mLocationClient.disconnect();
    super.onStop();
  }
}
