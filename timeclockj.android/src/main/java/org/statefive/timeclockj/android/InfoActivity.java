/*
 *  Copyright (C) 2011 rich
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.statefive.timeclockj.android;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import java.io.File;
import java.util.Date;
import org.statefive.timeclockj.ClockManager;
import org.statefive.timeclockj.TimeClockUtils;
import org.statefive.timeclockj.ui.AboutHelper;

/**
 *
 * @author rich
 */
public class InfoActivity extends Activity {

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.infoactivity);
    TextView textViewFileSize = (TextView) this.findViewById(R.id.textViewFileSize);
    TextView textViewLastModified = (TextView) this.findViewById(R.id.textViewLastModified);
    
    File fileTimeClock = Utils.getInstance().getLocalTimeClockFile(this);
    textViewFileSize.setText("File size: " + Utils.humanReadableByteCount(fileTimeClock.length(), true));
    Date d = new Date(fileTimeClock.lastModified());
    textViewLastModified.setText("Last modified: " + ClockManager.getInstance().formatDate(d));

    Button buttonAbout = (Button) findViewById(R.id.buttonAbout);
    Button buttonContributors = (Button) findViewById(R.id.buttonContributors);
    Button buttonHelp = (Button) findViewById(R.id.buttonHelp);

    buttonAbout.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View view) {
        try {
          PackageInfo manager = getPackageManager().getPackageInfo(getPackageName(), 0);

          showWebContent(AboutHelper.getAboutHtml()
                  .replace(AboutHelper.DOLLAR_VERSION,
                           manager.versionName));
        } catch (NameNotFoundException e) {
          //Handle exception
        }
      }
    });

    buttonContributors.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View view) {
        showWebContent(AboutHelper.getContributorHtml());
      }
    });

    buttonHelp.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View view) {
        Log.i("INFO", "Version: " + TimeClockUtils.getVersion(ClockActivity.class, null));
        Intent i = new Intent(view.getContext(), HelpActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        InfoActivity.this.startActivity(i);

      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    int base = 1;
    menu.add(base, base, base, "Setup").setIcon(R.drawable.options);
    return true;
  }

  /**
   *
   * @param item
   * @return
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == 1) {
      Intent i = new Intent(getApplicationContext(), TimeClockJConfigActivity.class);
      InfoActivity.this.startActivity(i);
    }
    return true;
  }

  private void showWebContent(String content) {

    Intent i = new Intent(getApplicationContext(),
            ViewWebContentActivity.class);
    i.putExtra(ViewWebContentActivity.WEB_CONTENT, content);
    InfoActivity.this.startActivity(i);
  }
}
