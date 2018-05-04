/*
 * Copyright (c) Richard Meeking, Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.statefive.timeclockj.android.v2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import java.io.File;

/**
 *
 * @author rich
 */
public class Utils {

  private static Utils instance;

  /**
   * The report and clock tabs use this to ensure the file has finished parsing
   * successfully.
   */
  private ParseFileAsyncTask parseTask = null;

  /**
   * @since 1.1
   */
  private Utils() {
  }

  /**
   *
   * @return
   *
   * @since 1.1
   */
  public static Utils getInstance() {
    if (instance == null) {
      instance = new Utils();
    }
    return instance;
  }

  /**
   *
   * @param c
   * @return
   *
   * @since 1.1
   */
  public File getLocalTimeClockFile(Context c) {
    File fileTimeClock = null;
    PreferenceManager.setDefaultValues(c, R.xml.timeclockj, false);
//    SharedPreferences prefs =
//            PreferenceManager.getDefaultSharedPreferences(c);
//    String timelogFile = prefs.getString(
//            c.getResources().getString(R.string.timeclockj_timelog),
//            ".timelog");
    String timelogFile = c.getResources().getString(R.string.timeclockj_timelog);

//    boolean useSdCard = prefs.getBoolean(
//            c.getResources().getString(R.string.timeclockj_sdcard), true);
    File dir = c.getExternalFilesDir(null);
//    File dir = c.getFilesDir();;

//    if (!useSdCard) {
//      dir = c.getFilesDir();
//    }
    fileTimeClock = new File(dir.getAbsolutePath(), timelogFile);
    return fileTimeClock;
  }



  /**
   * Set the current parse task - it is up to callers to ensure that the current
   * task has completed before setting it here. Ideally, the parse task should
   * be set to {@code null} by the calling activity when the task is complete.
   *
   * @param task the new task to set.
   *
   * @since 1.1
   */
  public void setParseAsyncTask(ParseFileAsyncTask task) {
    this.parseTask = task;
  }

  /**
   * Gets the current parse task, if there is one.
   *
   * @return the current parse task, if there is one; {@code null} otherwise.
   *
   * @since 1.1
   */
  public ParseFileAsyncTask getParseFileAsyncTask() {
    return this.parseTask;
  }

  /**
   * Taken from http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
   * 
   * @param bytes
   * @param si
   * @return 
   */
  public static String humanReadableByteCount(long bytes, boolean si) {
    String byteCount = null;
    int unit = si ? 1000 : 1024;
    if (bytes < unit) {
      byteCount = bytes + " B";
    } else {
      int exp = (int) (Math.log(bytes) / Math.log(unit));
      String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
      byteCount = String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
    return byteCount;
  }
}
