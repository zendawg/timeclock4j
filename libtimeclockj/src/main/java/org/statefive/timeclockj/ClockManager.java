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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The clock manager is the main API calling point for clocking in and out
 * of projects. It keeps track of the current status of clock ins and outs,
 * the current project (if clocked in) as well as all previous clock reports.
 * 
 * @author rich
 */
public class ClockManager {

  public static final String DEFAULT_PROJECT_STRING = "<default>";

  /** Determine the current clocked in or out status. */
  private boolean clockedIn;
  /** Instance of the clock manager. */
  private static ClockManager clockManager;
  /** The currently clocked in project, if clocked in. */
  private String currentProject;
  /** Map of project-to-clock periods. */
  private Map<String, List<ClockPeriod>> projects =
    new HashMap<String, List<ClockPeriod>>();

  /**
   *
   */
  private ClockManager() {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        if (clockedIn) {
          // TODO add code to prompt listeners to clock out
        }
      }
    });

  }

  /**
   *
   * @return
   */
  public static ClockManager getInstance() {
    if (clockManager == null) {
      clockManager = new ClockManager();
    }
    return clockManager;
  }

  /**
   * Clocks in the specified project with the specified time.
   *
   * @param projectName the name of the project to clock in.
   *
   * @param clockInTime the time the project is to be clocked in.
   *
   * @throws ClockInException if already clocked in.
   */
  public void clockIn(String projectName, long clockInTime) throws ClockInException {
    if (this.clockedIn) {
      throw new ClockInException("You've already clocked in!");
    }
    this.currentProject = projectName;
    this.clockedIn = true;
    if (this.currentProject == null) {
      this.currentProject = DEFAULT_PROJECT_STRING;
    }
    ClockPeriod clockPeriod = new ClockPeriod();
    clockPeriod.setClockInTime(clockInTime);

    List<ClockPeriod> clockPeriods = this.projects.get(this.currentProject);
    if (clockPeriods == null) {
      // not been added before, create new list:
      clockPeriods = new ArrayList<ClockPeriod>();
      this.projects.put(this.currentProject, clockPeriods);
    }
    clockPeriods.add(clockPeriod);
  }

  /**
   * Clocks the current clocked-in project.
   *
   * @param description reason for clocking out (if not-{@code null}).
   *
   * @param clockOutTime time in milliseconds of the clock-out.
   *
   * @throws ClockOutException if no projects are clocked in.
   */
  public void clockOut(String description, long clockOutTime) throws ClockOutException {
    if (!this.clockedIn) {
      throw new ClockOutException("You've already clocked out!");
    }
    List<ClockPeriod> clockPeriods = this.projects.get(this.currentProject);
    int pos = clockPeriods.size()-1;
    ClockPeriod lastClock = clockPeriods.get(pos);
    lastClock.setClockOutTime(clockOutTime);
    lastClock.setDescription(description);
    this.currentProject = null;
    this.clockedIn = false;
  }

  /**
   * Determine if currently clocked in to a project.
   * 
   * @return {@code true} if clocked in to a project currently; {@code false}
   * otherwise.
   */
  public boolean isClockedIn() {
    return this.clockedIn;
  }

  /**
   * Get the currently clocked in project, if set.
   * 
   * @return the currently clocked in project, if clocked in; {@code null}
   * otherwise.
   */
  public String getCurrentProject() {
    return this.currentProject;
  }

  /**
   *
   * @param date
   * @return
   */
  public Date parseDate(String date) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat(
      TimeClockParser.DATE_TIME_FORMAT);
    return sdf.parse(date);
  }

  /**
   *
   * @param date
   * @return
   */
  public String formatDate(Date date) {
    SimpleDateFormat sdf = new SimpleDateFormat(
      TimeClockParser.DATE_TIME_FORMAT);
    return sdf.format(date);
  }

  /**
   * 
   * @return
   */
  public String[] getProjects() {
    // TODO YUK! Only had five minutes to do this
    String[] p = projects.keySet().toArray(new String[]{});
    List<String> l = Arrays.asList(p);
    Collections.sort(l, String.CASE_INSENSITIVE_ORDER);
    return l.toArray(new String[]{});
  }

  /**
   * Get the clock periods for the specified project.
   * 
   * @param projectName the name of the project to get clock periods for;
   * if {@code null}, it is assumed the default project is used (see 
   * {@link #DEFAULT_PROJECT_STRING}).
   * 
   * @return a list of clock periods if the specified project has any clock
   * periods associated with it; if there are no clock periods, {@code null}
   * is returned.
   */
  public List<ClockPeriod> getClockPeriods(String projectName) {
    List<ClockPeriod> clockPeriods = null;
    if (projectName == null) {
      projectName = ClockManager.DEFAULT_PROJECT_STRING;
    }
    if (this.projects.containsKey(projectName)) {
      clockPeriods = Collections.unmodifiableList(
        this.projects.get(projectName));
    } else {
      clockPeriods = Collections.unmodifiableList(new ArrayList<ClockPeriod>());
    }
    return clockPeriods;
  }
}