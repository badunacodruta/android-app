package org.mihigh.cycling;


import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.mihigh.cycling.location.LocationUpdater;
import org.mihigh.cycling.location.dto.UserInfo;

/**
 * This the app's main Activity. It provides buttons for requesting the various features of the app, displays the
 * current location, the current address, and the status of the location client and updating services.
 *
 * {@link #getLocation} gets the current location using the Location Services getLastLocation() function. {@link } calls
 * geocoding to get a street address for the current location. {@link #startUpdates} sends a request to Location
 * Services to send periodic location updates to the Activity. {@link #stopUpdates} cancels previous periodic update
 * requests.
 *
 * The update interval is hard-coded to be 5 seconds.
 */
public class MapActivity extends FragmentActivity implements
                                                  GooglePlayServicesClient.ConnectionCallbacks,
                                                  GooglePlayServicesClient.OnConnectionFailedListener {

  private LocationRequest mLocationRequest;
  private LocationClient mLocationClient;
  SharedPreferences mPrefs;
  SharedPreferences.Editor mEditor;
  boolean mUpdatesRequested = false;
  private GoogleMap map;
  private LocationUpdater locationUpdater;
  private float zoom = 10;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    mLocationRequest = LocationRequest.create();
    mLocationRequest.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    mLocationRequest.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);
    mUpdatesRequested = false;
    mPrefs = getSharedPreferences(LocationUtils.SHARED_PREFERENCES, Context.MODE_PRIVATE);
    mEditor = mPrefs.edit();
    mLocationClient = new LocationClient(this, this, this);
    setUpMapIfNeeded();
  }

  @Override
  public void onStop() {
    if (mLocationClient.isConnected()) {
      stopPeriodicUpdates();
    }
    mLocationClient.disconnect();
    super.onStop();
  }

  @Override
  public void onPause() {
    mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, mUpdatesRequested);
    mEditor.commit();
    super.onPause();
  }

  @Override
  public void onStart() {
    super.onStart();
    mLocationClient.connect();
  }

  @Override
  public void onResume() {
    super.onResume();
    if (mPrefs.contains(LocationUtils.KEY_UPDATES_REQUESTED)) {
      mUpdatesRequested = mPrefs.getBoolean(LocationUtils.KEY_UPDATES_REQUESTED, false);

    } else {
      mEditor.putBoolean(LocationUtils.KEY_UPDATES_REQUESTED, false);
      mEditor.commit();
    }

    setUpMapIfNeeded();
  }

  private void setUpMapIfNeeded() {
    if (map != null) {
      return;
    }
    map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
    if (map == null) {
      return;
    }

    map.setMyLocationEnabled(true);
    map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
      @Override
      public void onCameraChange(CameraPosition cameraPosition) {
        zoom = cameraPosition.zoom;
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

    switch (requestCode) {
      case LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST:
        switch (resultCode) {
          case Activity.RESULT_OK:
            Log.d(LocationUtils.APPTAG, getString(R.string.resolved));
            break;
          default:
            Log.d(LocationUtils.APPTAG, getString(R.string.no_resolution));
            break;
        }
      default:
        Log.d(LocationUtils.APPTAG, getString(R.string.unknown_activity_request_code, requestCode));
        break;
    }
  }

  private boolean servicesConnected() {
    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

    if (ConnectionResult.SUCCESS == resultCode) {
      Log.d(LocationUtils.APPTAG, getString(R.string.play_services_available));
      return true;
    } else {
      Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
      if (dialog != null) {
        ErrorDialogFragment errorFragment = new ErrorDialogFragment();
        errorFragment.setDialog(dialog);
        errorFragment.show(getSupportFragmentManager(), LocationUtils.APPTAG);
      }
      return false;
    }
  }

  public void getLocation(View v) {
    if (servicesConnected()) {
      Location currentLocation = mLocationClient.getLastLocation();
    }
  }

  public void startUpdates(View v) {
    mUpdatesRequested = true;

    if (servicesConnected()) {
      startPeriodicUpdates();
    }
  }

  public void stopUpdates(View v) {
    mUpdatesRequested = false;

    if (servicesConnected()) {
      stopPeriodicUpdates();
    }
  }

  @Override
  public void onConnected(Bundle bundle) {
    if (mUpdatesRequested) {
      startPeriodicUpdates();
    }
    startUpdates(null);
  }

  @Override
  public void onDisconnected() {
  }

  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {
    if (connectionResult.hasResolution()) {
      try {
        connectionResult.startResolutionForResult(
            this,
            LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
      } catch (IntentSender.SendIntentException e) {
        e.printStackTrace();
      }
    } else {
      showErrorDialog(connectionResult.getErrorCode());
    }
  }


  public void setLatLng(Location location) {
    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
  }

  private void startPeriodicUpdates() {
    locationUpdater = new LocationUpdater(this);
    mLocationClient.requestLocationUpdates(mLocationRequest, locationUpdater);
  }

  private void stopPeriodicUpdates() {
    mLocationClient.removeLocationUpdates(locationUpdater);
  }

  private void showErrorDialog(int errorCode) {
    Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode,
                                                               this,
                                                               LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

    if (errorDialog != null) {
      ErrorDialogFragment errorFragment = new ErrorDialogFragment();
      errorFragment.setDialog(errorDialog);
      errorFragment.show(getSupportFragmentManager(), LocationUtils.APPTAG);
    }
  }

  public void updateOthersPossitions(HashMap<String, UserInfo> users) {
    map.clear();
    for (String userId : users.keySet()) {
      UserInfo userInfo = users.get(userId);
      map.addMarker(new MarkerOptions()
                        .title(userId)
                        .position(new LatLng(Double.valueOf(userInfo.lat), Double.valueOf(userInfo.lng))));
    }
  }

  public static class ErrorDialogFragment extends DialogFragment {
    private Dialog mDialog;
    public ErrorDialogFragment() {
      super();
      mDialog = null;
    }
    public void setDialog(Dialog dialog) {
      mDialog = dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      return mDialog;
    }
  }
}
