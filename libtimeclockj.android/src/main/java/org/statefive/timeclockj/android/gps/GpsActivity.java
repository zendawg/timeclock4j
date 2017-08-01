package org.statefive.timeclockj.android.gps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GpsActivity extends Activity {

  private static String TAG = "libtimeclockj.android";
  private TextView textViewData;
  LocationManager mlocManager = null;
  private LocationListener mlocListener;
  private Location[] zones;
  private Handler handler;

  /**
   * Called when the activity is first created.
   * @param savedInstanceState If the activity is being re-initialised after
   * previously being shut down then this Bundle contains the data it most
   * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    String[] zoneNames = new String[]{"Broadwaters", "Ferndale",
    "Officina da Capoeira Class, Worcester", "Conviver Capoeira Class, MAC"};
    zones = new Location[4];

    Location zone1 = new Location(zoneNames[0]);
    Location zone2 = new Location(zoneNames[1]);
    Location zone3 = new Location(zoneNames[2]);
    Location zone4 = new Location(zoneNames[3]);
    zone1.setLatitude(52.40156);
    zone1.setLongitude(-2.233652);
    zone2.setLatitude(52.394942);
    zone2.setLongitude(-2.278325);
    zone3.setLatitude(52.19678);
    zone3.setLongitude(-2.23562);
    zone4.setLatitude(52.43032);
    zone4.setLongitude(-1.93706);
    zones[0] = zone1;
    zones[1] = zone2;
    zones[2] = zone3;
    zones[3] = zone4;

    Log.i(TAG, "onCreate");
    setContentView(R.layout.main);
    textViewData = (TextView) this.findViewById(R.id.textViewData);
    System.out.println(textViewData);

    Intent serviceIntent = new Intent();
    serviceIntent.setAction("org.statefive.timeclockj.android.gps.GPS_SERVICE");
    startService(serviceIntent);
//    Thread t = new Thread(new Runnable() {
//
//      @Override
//      public void run() {
//        System.out.println("Waking up");
//        Looper.prepare();
//        handler = new Handler();
//        Looper.loop();

        mlocListener = new MyLocationListener();
        if (mlocManager == null) {
          mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        System.out.println("Running task!");
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
        try{Thread.sleep(60000);}catch(Exception ex){}
//      }
//    });
//    System.out.println("About to start thread...");
//    t.start();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    System.out.println("Quitting");
    stopService(new Intent(GpsActivity.this, GpsService.class));
    if (mlocManager != null) {
      mlocManager.removeUpdates(mlocListener);
      mlocManager = null;
    }

  }
  private boolean inZone = false;
  private String currentZone = null;

  class GpsHandler extends Handler {
  }

  class MyLocationListener implements LocationListener {

    @Override
    public void onLocationChanged(Location loc) {

      System.out.println("Received an update: " + loc);
//      if (loc != null) {
//        update(loc.toString());
//      }

      loc.getLatitude();
      loc.getLongitude();
      if (loc == null) {
        return;
      }
      if (currentZone == null) {
        for (int i = 0; i < zones.length; i++) {
          if (loc.distanceTo(zones[i]) < 100) {
            inZone = true;
            currentZone = zones[i].getProvider();
            update(zones[i].getProvider() + ": In the zone!\nlat.: "
                    + loc.getLatitude() + "lon.: " + loc.getLongitude());
            break;
          }
        }
      } else {
        for (int i = 0; i < zones.length; i++) {
          if (zones[i].getProvider().equals(currentZone)) {
            if (loc.distanceTo(zones[i]) > 100) {
              System.out.println(zones[i] + ": Exiting...");
              inZone = false;
              currentZone = null;
              update(zones[i].getProvider() + ": Exiting...\nlat.: "
                      + loc.getLatitude() + "lon.: " + loc.getLongitude());
              break;
            }
          }
        }
      }
//      System.out.println("removing listener");
//      mlocManager.removeUpdates(mlocListener);
//      mlocManager = null;
    }

    int taskId = 0;
    private void update(String message) {
      SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
      textViewData.setText(textViewData.getText() + "\n" + (++taskId)
              + " " + message + " "
              + sdf.format(Calendar.getInstance().getTime()));

      Toast.makeText(getApplicationContext(),
              message,
              Toast.LENGTH_LONG).show();

    }

    @Override
    public void onProviderDisabled(String provider) {

      Toast.makeText(getApplicationContext(),
              "Gps Disabled",
              Toast.LENGTH_LONG).show();

    }

    @Override
    public void onProviderEnabled(String provider) {

      Toast.makeText(getApplicationContext(),
              "Gps Enabled", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
  }/* End of Class MyLocationListener */

}
