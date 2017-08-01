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
package org.statefive.timeclockj;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Date;

/**
 * 
 * @author rich
 */
public class TimeClockUtils {

  /**
   *
   * @param file
   * @param description
   * @throws IOException
   * @throws ClockException
   * @return 
   */
  public static ClockLine clockOut(File file, String description)
    throws IOException, ClockException {
    return TimeClockUtils.clockOut(file, description, System.currentTimeMillis());
  }

  /**
   *
   * @param file
   * @param description
   * @param time
   * @throws IOException
   * @throws ClockException
   * @return 
   */
  public static ClockLine clockOut(File file, String description, long time)
    throws IOException, ClockException {
    return TimeClockUtils.clock(ClockInOutEnum.OUT, file, description, time);
  }

  /**
   *
   * @param file
   * @param project
   * @throws IOException
   * @throws ClockException
   * @return 
   */
  public static ClockLine clockIn(File file, String project)
    throws IOException, ClockException {
    return TimeClockUtils.clockIn(file, project, System.currentTimeMillis());
  }

  /**
   *
   * @param file
   * @param project
   * @param time
   * @throws IOException
   * @throws ClockException
   * @return 
   */
  public static ClockLine clockIn(File file, String project, long time)
    throws IOException, ClockException {
    return TimeClockUtils.clock(ClockInOutEnum.IN, file, project, time);
  }

  /**
   *
   * @param file
   * @throws IOException
   */
  public static void parseFile(File file) throws IOException {
    FileInputStream fis = new FileInputStream(file);
    new TimeClockParser().parse(fis);
    fis.close();
  }

  /**
   * 
   * @param file
   * @throws IOException
   */
  public static void reloadFile(File file) throws IOException, ClockException {
    try {
      Field f = ClockManager.class.getDeclaredField("clockManager");
      f.setAccessible(true);
      f.set(ClockManager.class, null);
      TimeClockUtils.parseFile(file);
    } catch (NoSuchFieldException nsfex) {
      // TODO this information is no good to a user. Expand!
      throw new ClockException("This is a bug. Please post this issue: "
        + nsfex.getMessage());
    } catch (IllegalAccessException iaex) {
      // TODO this information is no good to a user. Expand!
      throw new ClockException("This is a bug. Please post this issue: "
        + iaex.getMessage());
    }
  }


  /**
   * Gets the implementation version, specified in the manifest, of the
   * specified class. The application name is displayed first followed
   * by the value found in the manifest.
   *
   * @param cla$$ the class to obtain version from.
   *
   * @param appName the application name to include in the output. If
   * {@code null} does not get included in the output.
   *
   * @return the implementation version of the software from the manifest file.
   */
  public static String getVersion(Class cla$$, String appName)
  {
    Package p = cla$$.getPackage();
    String version = p.getImplementationVersion();
    if (appName != null) {
      version = appName + " " + version;
    }
    return version;
  }

  /**
   * 
   * @param clockInOut
   * 
   * @param file
   * 
   * @param text
   * 
   * @param currentTime 
   * 
   * @throws IOException
   * 
   * @throws ClockException
   * 
   * @return the clock line representing the clock.
   */
  private static ClockLine clock(ClockInOutEnum clockInOut, File file,
    String text, long currentTime) throws IOException, ClockException {

    ClockLine clockLine = new ClockLine();
    if (ClockInOutEnum.OUT == clockInOut) {
      ClockManager.getInstance().clockOut(text, currentTime);
      clockLine.setClockStatus(ClockInOutEnum.OUT);
    } else {
      ClockManager.getInstance().clockIn(text, currentTime);
      clockLine.setClockStatus(ClockInOutEnum.IN);
    }
    clockLine.setClockDate(new Date(currentTime));
    clockLine.setText(text);


    FileOutputStream fos = new FileOutputStream(file, true);
    fos.write((clockLine.toString() + "\n").getBytes());
    fos.flush();
    fos.close();

    String output = null;
    if (ClockInOutEnum.IN == clockInOut) {
      output = "Clocked in: ";
    } else {
      output = "Clocked out: ";
    }
    output +=
      ClockManager.getInstance().formatDate(clockLine.getClockDate());
    System.out.print(output);
    // only print the project name if we're clocking in and the project
    // has a name:
    if (text != null && ClockInOutEnum.IN == clockLine.getClockStatus()) {
      System.out.print(" (" + text + ")");
    }
    System.out.println();
    return clockLine;
  }
}
