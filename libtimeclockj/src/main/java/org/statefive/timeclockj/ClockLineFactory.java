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
package org.statefive.timeclockj;

import java.text.ParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @since 1.1
 * 
 * @author rich
 */
public class ClockLineFactory {

  /**
   * Attempts to create a clock line from the given input.
   * 
   * @param line a line of data, possibly matching the format for a clock-in
   * or -out.
   * 
   * @return a clock line, if one could be built; {@code null} otherwise.
   */
  public ClockLine getClockLine(String line) {
    ClockLine clockLine = null;
    Pattern p = Pattern.compile(TimeClockParser.LINE_EXPRESSION);
    Matcher m = p.matcher(line);
    if (m.matches()) {
      clockLine = new ClockLine();
      if ("i".equals(m.group(1))) {
        clockLine.setClockStatus(ClockInOutEnum.IN);
      } else if ("o".equals(m.group(1))) {
        clockLine.setClockStatus(ClockInOutEnum.OUT);
      } else {
        // TODO log? Either break away and do the next line:
        // it's not a clock line:
        clockLine = null;
      }
      try {
        if (clockLine != null) {
          Date d = ClockManager.getInstance().parseDate(m.group(2));
          clockLine.setClockDate(d);
        }
      } catch (ParseException pex) {
        // TODO log?  Either break away and do the next line:
        clockLine = null;
      }
      if (clockLine != null && m.groupCount() > 3 && m.group(4) != null) {
        clockLine.setText(m.group(4).trim());
      }
    }
    return clockLine;
  }
}
