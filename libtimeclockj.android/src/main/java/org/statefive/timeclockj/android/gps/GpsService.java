/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.statefive.timeclockj.android.gps;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/**
 *
 * @author rich
 */
public class GpsService extends Service {

  @Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		Toast.makeText(this, "Service created...", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Toast.makeText(this, "Service destroyed...", Toast.LENGTH_LONG).show();
	}

}
