/*
 *  Copyright (C) 2011 rich
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.statefive.timeclockj;

import java.util.Calendar;
import java.util.Observable;

/**
 * Used by UI components to determine changes to the current clock model.
 * Changes are usually performed by UI components setting values to the model,
 * which then updates any listeners that rely on the data being changed.
 *
 * @author rich
 */
public class ClockModel extends Observable {
  /** For clock-ins only; shows the currently selected project that will be
   used when clocking-in. */
  private String project;
  /** For clock-outs only; shows the description set for the clock-out. */
  private String description;
  /** If set, specifies the year, month and date of the clock-in/-out. */
  private Calendar calendarDate;
  /** If set, specifies the hour and minute of the clock-in/-out. */
  private Calendar calendarTime;
  /** If set, denotes the date AND time of the clock time. */
  private Calendar calendarDateTime;
  
  public Calendar getCalendarDate() {
    return calendarDate;
  }

  public Calendar getCalendarDateTime() {
    return calendarDateTime;
  }

  /**
   * 
   * @param calendarDate
   * @param calendarTime
   */
  public void setCalendarDateTime(Calendar calendarDate, Calendar calendarTime) {
    if (calendarDate == null) {
      calendarDateTime = Calendar.getInstance();
    } else {
      calendarDateTime = calendarDate;
    }
    if (calendarTime != null) {
      calendarDateTime.set(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY));
      calendarDateTime.set(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE));
      calendarDateTime.set(Calendar.SECOND, calendarTime.get(Calendar.SECOND));
    }
    this.setChanged();
    this.notifyObservers(this);
  }

  public void setCalendarDate(Calendar calendarDate) {
    this.calendarDate = calendarDate;
    this.setChanged();
    this.notifyObservers(this);
  }

  public Calendar getCalendarTime() {
    return calendarTime;
  }

  public void setCalendarTime(Calendar calendarTime) {
    this.calendarTime = calendarTime;
    this.setChanged();
    this.notifyObservers(this);
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
    this.setChanged();
    this.notifyObservers(this);
  }

  public String getProject() {
    return project;
  }

  public void setProject(String project) {
    this.project = project;
    this.setChanged();
    this.notifyObservers(this);
  }

}
