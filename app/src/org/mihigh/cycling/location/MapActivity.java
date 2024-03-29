package org.mihigh.cycling.location;

import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.mihigh.cycling.R;
import org.mihigh.cycling.commons.Constants;
import org.mihigh.cycling.location.dto.UserInfo;

public class MapActivity extends FragmentActivity
    implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

  private LocationRequest locationRequest;
  private LocationClient locationClient;
  private LocationUpdater locationUpdater;
  private GoogleMap map;

  private float zoom = 17;

  // Used for setting the map view to the last GPS position
  private boolean useGpsUpdates = true;
  private boolean lastUpdateIsGps = true;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    setUpLocation();
    setUpMapIfNeeded();
  }

  private void setUpLocation() {
    locationRequest = LocationRequest.create();
    locationRequest.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);
    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    locationRequest.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

    locationUpdater = new LocationUpdater(this);

    locationClient = new LocationClient(this, this, this);
  }

  @Override
  public void onStop() {
    if (locationClient.isConnected()) {
      stopPeriodicUpdates();
    }
    locationClient.disconnect();
    super.onStop();
  }

  @Override
  public void onPause() {
    super.onPause();
  }

  @Override
  public void onStart() {
    super.onStart();
    locationClient.connect();
  }

  @Override
  public void onResume() {
    super.onResume();
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
        if (lastUpdateIsGps) {
          lastUpdateIsGps = false;
          return;
        }

        useGpsUpdates = false;
      }
    });

    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    map.animateCamera(CameraUpdateFactory.zoomTo(zoom));

    map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
      @Override
      public boolean onMyLocationButtonClick() {
        useGpsUpdates = true;
        return false;
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

  public void startUpdates() {
    if (servicesConnected()) {
      startPeriodicUpdates();
    }
  }

  @Override
  public void onConnected(Bundle bundle) {
    startUpdates();
  }

  @Override
  public void onDisconnected() {
  }

  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {
    if (connectionResult.hasResolution()) {
      try {
        connectionResult.startResolutionForResult(this, LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
      } catch (IntentSender.SendIntentException e) {
        e.printStackTrace();
      }
    } else {
      showErrorDialog(connectionResult.getErrorCode());
    }
  }

  public void setLatLng(Location location) {
    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    if (useGpsUpdates) {
      lastUpdateIsGps = true;
      map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }
  }

  private void startPeriodicUpdates() {
    locationClient.requestLocationUpdates(locationRequest, locationUpdater);
  }

  private void stopPeriodicUpdates() {
    locationClient.removeLocationUpdates(locationUpdater);
  }

  private void showErrorDialog(int errorCode) {
    Dialog errorDialog =
        GooglePlayServicesUtil.getErrorDialog(errorCode, this, LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
    if (errorDialog != null) {
      ErrorDialogFragment errorFragment = new ErrorDialogFragment();
      errorFragment.setDialog(errorDialog);
      errorFragment.show(getSupportFragmentManager(), LocationUtils.APPTAG);
    }
  }

  public void updateOthersPossitions(HashMap<String, UserInfo> users) {
    map.clear();
    Bitmap mask = BitmapFactory.decodeResource(this.getResources(), R.drawable.marker_mask);

    for (String userId : users.keySet()) {
      UserInfo userInfo = users.get(userId);
      if (userId.equalsIgnoreCase(Constants.USER_ID)) {
        continue;
      }
      LatLng position = new LatLng(Double.valueOf(userInfo.lat), Double.valueOf(userInfo.lng));
      userInfo.thumbnail = mask(userInfo.thumbnail, mask);
      map.addMarker(new MarkerOptions().title(userId).position(position)).setIcon(
          BitmapDescriptorFactory.fromBitmap(userInfo.thumbnail)
      );
    }
  }

  private Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
    Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
    Bitmap mask = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp2.getConfig());
    Canvas canvas = new Canvas(bmOverlay);
    canvas.drawBitmap(bmp1, new Matrix(), null);
    canvas.drawBitmap(bmp2, new Matrix(), null);
    return bmOverlay;
  }


  private Bitmap mask(Bitmap mainImage, Bitmap maskImage) {
    Bitmap result = Bitmap.createBitmap(maskImage.getWidth(), maskImage.getHeight(), Bitmap.Config.ARGB_8888);
    mainImage = Bitmap.createScaledBitmap(mainImage,
                                          maskImage.getWidth(),
                                          maskImage.getHeight(), false);
    Canvas canvas = new Canvas(result);

    Paint paint = new Paint();
    paint.setFilterBitmap(false);

    canvas.drawBitmap(mainImage, 0, 0, paint);
    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
    canvas.drawBitmap(maskImage, 0, 0, paint);
    paint.setXfermode(null);

    return result;
  }

}

