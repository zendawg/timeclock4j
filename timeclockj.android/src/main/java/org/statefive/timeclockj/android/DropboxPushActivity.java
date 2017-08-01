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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DropboxPushActivity extends Activity {

  private OutputStream os = null;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    DropboxUpdateTool.getInstance().updateListener(true);
    try {
      pushTimeClockFile();
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
  private void pushTimeClockFile() throws IOException, DropboxException {

    String timelogDir =
            Utils.getInstance(DropboxPushActivity.this).getDropboxTimeClockDir(
            DropboxPushActivity.this);
    File timelog =
            Utils.getInstance(DropboxPushActivity.this).getLocalTimeClockFile(
            DropboxPushActivity.this);
    System.out.println(timelog.getAbsolutePath() + " *********** EXISTS?=" + timelog.exists());
    System.out.println(" *********** DIRECTORY=" + timelogDir);

    FileInputStream fis = new FileInputStream(timelog);
    // new thread!
    DropboxUpdateTool.getInstance().getApi().putFileOverwrite(timelogDir + File.separator + timelog.getName(),
            fis, timelog.length(),
            null);
    Toast toast = Toast.makeText(getApplicationContext(),
            timelog.length() + " bytes pushed", Toast.LENGTH_LONG);
    toast.show();
  }
}
