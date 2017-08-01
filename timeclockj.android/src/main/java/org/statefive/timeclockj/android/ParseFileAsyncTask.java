/*
 * Copyright (C) 2011 State Five
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import android.os.AsyncTask;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.statefive.timeclockj.ClockException;
import org.statefive.timeclockj.TimeClockUtils;

/**
 * Used to parse a timeclock file on a separate thread.
 * 
 * Callers wishing to be notified should add themselves as listeners.
 * 
 * @author rich
 * 
 * @since 1.1
 */
public class ParseFileAsyncTask extends AsyncTask<File, Integer, Long> {

  private List<FileParseListener> listeners = new ArrayList<FileParseListener>();

  public ParseFileAsyncTask() {
    super();
  }

  @Override
  protected Long doInBackground(File... params) {
    try {
      TimeClockUtils.reloadFile(params[0]);
    } catch (ClockException cex) {
//      showDialog("Clock Exception", cex.getMessage());
    } catch (IOException ioex) {
//      showDialog("I/O Exception", "An I/O error occurred: "
//              + ioex.getMessage());
    }
    return params[0].length();
  }

  @Override
  protected void onPostExecute(Long result) {
    super.onPostExecute(result);
    for (FileParseListener l : listeners) {
      l.fileParseUpdate(Status.FINISHED);
    }
  }

  @Override
  protected void onProgressUpdate(Integer... values) {
    super.onProgressUpdate(values);
    for (FileParseListener l : listeners) {
      l.fileParseUpdate(Status.RUNNING);
    }
  }

  /**
   * 
   * @param l 
   */
  public void addFileParseListener(FileParseListener l) {
    this.listeners.add(l);
  }

  /**
   * 
   * @param l
   * @return 
   */
  public boolean removeFileParseListener(FileParseListener l) {
    return this.listeners.remove(l);
  }
}
