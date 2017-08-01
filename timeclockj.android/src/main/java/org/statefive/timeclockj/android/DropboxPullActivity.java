/*
 * Copyright (c) 2010 Evenflow, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package org.statefive.timeclockj.android;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DropboxPullActivity extends Activity {

  private OutputStream os = null;
  
  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    DropboxUpdateTool.getInstance().updateListener(true);
    try {
      pullTimeClockFile();
    } catch (IOException ioex) {
      Toast toast = Toast.makeText(getApplicationContext(),
              "IO ERROR: " + ioex.getMessage(), Toast.LENGTH_LONG);
      toast.show();
      ioex.printStackTrace();
    } catch (DropboxException dex) {
      Toast toast = Toast.makeText(getApplicationContext(),
              "IO ERROR: " + dex.getMessage(), Toast.LENGTH_LONG);
      toast.show();
      dex.printStackTrace();
    } finally {
      DropboxUpdateTool.getInstance().updateListener(false);
      finish();
    }
  }

  /**
   *
   */
  private void pullTimeClockFile() throws IOException, DropboxException {

    File timelog = Utils.getInstance().getLocalTimeClockFile(
              DropboxPullActivity.this);
    try {
      System.out.println("DROPBOX pre " + timelog.lastModified());

      String dropboxDir =
              Utils.getInstance().getDropboxTimeClockDir(
              DropboxPullActivity.this);
      if (!dropboxDir.endsWith("/")) {
        dropboxDir = dropboxDir + "/";
      }
      os = new FileOutputStream(
              Environment.getExternalStorageDirectory()
              + File.separator + timelog.getName());

      // new thread!
      DropboxAPI.DropboxFileInfo info =
              DropboxUpdateTool.getInstance().getApi().getFile(dropboxDir + timelog.getName(), null, os, null);
      Toast toast = Toast.makeText(getApplicationContext(),
              "LENGTH of file info: "
              + Utils.humanReadableByteCount(info.getFileSize(), false), Toast.LENGTH_LONG);
      toast.show();

      // TODO Not sure this needs to be here?
      System.out.println("DROPBOX post " + timelog.lastModified());
//      long lastModified = timelog.lastModified();
//      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
//              DropboxActivity.this);
//      Editor prefsEditor = prefs.edit();
//      System.out.println("saving lastModified=" + lastModified);
//      prefsEditor.putLong(ClockActivity.LAST_MODIFIED, lastModified);
//      prefsEditor.commit();
    } finally {
      if (os != null) {
        try {
          os.close();
        } catch (IOException ioex) {
          Toast toast = Toast.makeText(getApplicationContext(),
                  "Close ERROR: " + ioex.getMessage(), Toast.LENGTH_LONG);
          toast.show();
        }
      }
    }
    Toast toast = Toast.makeText(getApplicationContext(),
            "Retrieved file, "
            + Utils.humanReadableByteCount(timelog.length(), false),
            Toast.LENGTH_LONG);
    toast.show();
  }
}
