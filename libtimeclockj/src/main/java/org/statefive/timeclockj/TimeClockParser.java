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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * Parses an input stream containing clocked-in and -out times.
 *
 * @author rich
 */
public class TimeClockParser {

  /** Regex pattern for the date, for example 2010/10/04 20:55:08. */
  public static final String DATE_EXPRESSION =
    "\\d{4}/\\d{2}/\\d{2}\\s\\d{2}:\\d{2}:\\d{2}";
  /** Date format for the date, for example 2010/10/04. */
  public static final String DATE_FORMAT =
    "yyyy/MM/dd";
  /** Date format for the date, for example 20:55:08. */
  public static final String TIME_FORMAT =
    "HH:mm:ss";
  /** Date format for the date, for example 2010/10/04 20:55:08. */
  public static final String DATE_TIME_FORMAT =
    DATE_FORMAT + " " + TIME_FORMAT;
  /** Regex pattern determining a clock line. */
  public static final String LINE_EXPRESSION =
    "(i|o)\\s(" + TimeClockParser.DATE_EXPRESSION + ")($|(.*))";

  /**
   * Parses the input stream, reading clock lines from the input source.
   * As lines are parsed successfully (and meaningfully), they are
   * added to the {@link ClockManager}.
   *
   * @param is newline separated stream to read data from.
   */
  public void parse(InputStream is) throws IOException {
    LineNumberReader reader = new LineNumberReader(
      new InputStreamReader(is));
    String line = null;
    ClockLineFactory factory = new ClockLineFactory();
    while((line = reader.readLine()) != null) {
      ClockLine clockLine = factory.getClockLine(line);
      if (clockLine != null) {
        // now add it to the manager:
        try{
          if (clockLine.getClockStatus() == ClockInOutEnum.IN) {
            ClockManager.getInstance().clockIn(clockLine.getText(),
              clockLine.getClockDate().getTime());
          } else if (clockLine.getClockStatus() == ClockInOutEnum.OUT) {
            ClockManager.getInstance().clockOut(clockLine.getText(),
              clockLine.getClockDate().getTime());
          } else {
            // TODO log error. Either break away and do the next line:
            continue;
          }
        }
        catch(ClockInException ciex) {
          System.err.println(ciex.getMessage());
        }
        catch(ClockOutException coex) {
          System.err.println(coex.getMessage());
        }
      } else {
        // TODO log error? Although we're not really interested in
        // failed lines, so leave it for now
      }
    }
  }
}
