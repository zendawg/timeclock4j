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
package org.statefive.timeclockj.android;

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
   * @deprecated this member variable may be removed in future versions.
   */
  private Context context;
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
    SharedPreferences prefs =
            PreferenceManager.getDefaultSharedPreferences(c);
    String timelogFile = prefs.getString(
            c.getResources().getString(R.string.timeclockj_timelog),
            ".timelog");
    String timelogDir = prefs.getString(
            c.getResources().getString(R.string.timeclockj_timelog_dir),
            null);
    if (timelogDir != null) {
      timelogFile = timelogDir + "/" + timelogFile;
    } else {
      timelogFile = "/" + timelogFile;
    }
    boolean useSdCard = prefs.getBoolean(
            c.getResources().getString(R.string.timeclockj_sdcard), true);
    File dir = Environment.getExternalStorageDirectory();
    if (!useSdCard) {
      dir = c.getFilesDir();
    }
    fileTimeClock = new File(dir.getAbsolutePath(), timelogFile);
    return fileTimeClock;
  }

  /**
   *
   * @return
   *
   * @since 1.1
   */
  public String getDropboxTimeClockDir(Context c) {
    PreferenceManager.setDefaultValues(c, R.xml.timeclockj, false);
    SharedPreferences prefs =
            PreferenceManager.getDefaultSharedPreferences(c);
    String dropboxDir = prefs.getString(
            c.getResources().getString(R.string.timeclockj_dropbox_dir),
            null);
    if (dropboxDir == null) {
      dropboxDir = "/";
    }
    if (!dropboxDir.startsWith("/")) {
      dropboxDir = "/" + dropboxDir;
    }
    return dropboxDir;
  }

  /**
   *
   * @param c
   *
   * @return
   *
   * @since 1.1
   */
  public boolean isDropboxCompatible(Context c) {
    boolean compatible = false;
    try {
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
      boolean useSdCard = prefs.getBoolean(
              c.getResources().getString(R.string.timeclockj_sdcard), false);
      boolean useDropbox = prefs.getBoolean(
              c.getResources().getString(R.string.timeclockj_dropbox), false);
      compatible = useDropbox && useSdCard;
    } catch (NullPointerException npex) {
      Log.e(this.getClass().getName(), npex.getMessage());
    }
    return compatible;
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
   *
   * @param context
   *
   * @deprecated use the no-args constructor instead.
   */
  private Utils(Context context) {
    this.context = context;
  }

  /**
   *
   * @param context
   *
   * @deprecated use {@link #getInstance()}.
   *
   * @return
   */
  public static Utils getInstance(Context context) {
    if (instance == null) {
      instance = new Utils(context);
    }
    return instance;
  }

  /**
   *
   * @deprecated use {@link #getLocalTimeClockFile(android.content.Context)}.
   * @return
   */
  public File getLocalTimeClockFile() {
    return getLocalTimeClockFile(context);
  }

  /**
   *
   * @deprecated use {@link #getDropboxTimeClockDir(android.content.Context)}.
   *
   * @return
   */
  public String getDropboxTimeClockDir() {
    return getDropboxTimeClockDir(context);
  }

  /**
   * @deprecated user {@link #isDropboxCompatible(android.content.Context)}.
   *
   * @return
   */
  public boolean isDropboxCompatible() {
    return isDropboxCompatible(context);
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
