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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rich
 */
public class ProjectReport {

  private String name;
  private long totalTime;
  private boolean clockedIn;
  private Duration longestClock;
  private List<Duration> durations = new ArrayList<Duration>();

  public boolean isClockedIn() {
    return clockedIn;
  }

  public void setClockedIn(boolean clockedIn) {
    this.clockedIn = clockedIn;
  }

  public List<Duration> getDurations() {
    return durations;
  }

  public void addDuration(Duration duration) {
    this.durations.add(duration);
  }

  public Duration getLongestClock() {
    return longestClock;
  }

  public void setLongestClock(Duration longestClock) {
    this.longestClock = longestClock;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getTotalTime() {
    return totalTime;
  }

  public void setTotalTime(long totalTime) {
    this.totalTime = totalTime;
  }

  @Override
  public String toString() {
    String clockedInStr = "Not clocked-in.";
    if (this.isClockedIn()) {
      clockedInStr = "Currently clocked-in.";
    }
    return "Project: " + this.getName() +
      "\n\t" + clockedInStr +
      "\n\tTotal Time: " + ReportFactory.getInstance().long2Duration(
                             this.getTotalTime()) +
      "\n\tLongest: " + this.getLongestClock().toString() +
      "\n\tClocks: " + this.getDurations().size();
  }

}
