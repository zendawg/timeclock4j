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
package org.statefive.timeclockj.report;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.statefive.timeclockj.ClockManager;
import org.statefive.timeclockj.ClockPeriod;

/**
 *
 * @author rich
 */
public class ReportFactory {

  private static ReportFactory instance;

  private ReportFactory() {

  }

  public static ReportFactory getInstance() {
    if (instance == null) {
      instance = new ReportFactory();
    }
    return instance;
  }
  
  /**
   * 
   * 
   * @param projectName
   * @return 
   */
  public ProjectReport getReport(String projectName) {
    return getReport(projectName, null, null);
  }

  /**
   * 
   * @param projectName
   * @param dStart
   * @param dEnd
   * @return 
   */
  public ProjectReport getReport(String projectName, Date dStart, Date dEnd) {
    ProjectReport report = new ProjectReport();
    report.setName(projectName);

    List<ClockPeriod> clockPeriods =
      ClockManager.getInstance().getClockPeriods(projectName);

    long totalTime = 0;
    Duration longestDuration = new Duration();
//    Iterator<ClockPeriod> it = clockPeriods.iterator(); it.hasNext() ;
    for (int i = 0; i < clockPeriods.size(); i++) {
      ClockPeriod clockPeriod = clockPeriods.get(i);
      if ((dStart == null || clockPeriod.getClockInTime() >= dStart.getTime())
              &&
          (dEnd == null ||
              (clockPeriod.getClockOutTime() != null
              &&
              clockPeriod.getClockOutTime() <= dEnd.getTime()))) {
        long time = clockPeriod.longValue();
        Duration duration = long2Duration(time);
        duration.setStartTime(clockPeriod.getClockInTime());
        if (clockPeriod.getClockOutTime() == null) {
          duration.setEndTime(Calendar.getInstance().getTimeInMillis());
        } else {
          duration.setEndTime(clockPeriod.getClockOutTime());
        }
        duration.setDescription(clockPeriod.getDescription());
        totalTime += clockPeriod.longValue();
        if (duration.longValue() > longestDuration.longValue()) {
          longestDuration = duration;
        }
        report.addDuration(duration);
      }
    }

    report.setLongestClock(longestDuration);
    report.setTotalTime(totalTime);
    if (projectName.equals(ClockManager.getInstance().getCurrentProject())) {
      report.setClockedIn(true);
    }

    return report;
  }

  /**
   *
   * @param time
   * @return
   */
  public Duration long2Duration(long time) {
    Duration duration = new Duration();
    duration.setHours((int)(time / 60 / 60 / 1000));
    duration.setMinutes((int)(time / 60 / 1000) % 60);
    duration.setSeconds((int)(time / 1000) % 60);
    return duration;
  }

}
