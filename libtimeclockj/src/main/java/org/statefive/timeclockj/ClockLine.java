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

import java.util.Date;

/**
 * Represents a line from a timelog file. Each line contains information about
 * the clock-type (in or out), the date and time of the clock and (if given)
 * a space followed by a textual description.
 *
 * <p>A clock-in is denoted by the character <i>i</i>, and a clock-out
 * is denoted by the <i>o</i>character. A textual description given for
 * a clock-in denotes the project clocking-in; a textual description for
 * a clock-out denotes descriptive text about the clock-out.</p>
 *
 * @author rich
 */
public class ClockLine {

  /** Determines if this line is clocked in or out. */
  private ClockInOutEnum clockStatus;

  /** The date the line was clocked in or out of. */
  private Date clockDate;

  /**
   * If a clock line is a clock-in line, the text denotes the project name.
   * If a clock line is a clock-out line, the text denotes the reason
   * for clocking out. A clock-in line may be null, which denotes the
   * default (no project name) project. A clock-out line may be null,
   * meaning there has been no need for a description for clocking out.
   */
  private String text;

  /**
   * Gets the clock date for this line.
   *
   * @return the clock time, if it has been set; {@code null} otherwise.
   */
  public Date getClockDate() {
    return clockDate;
  }

  /**
   * Sets the clock date to the specified clock date.
   *
   * @param clockDate the clock date to set; may be {@code null}.
   */
  public void setClockDate(Date clockDate) {
    this.clockDate = clockDate;
  }

  /**
   * Determines if this line is a clocked-in or -out line.
   *
   * @return the clock status; if the status has not been set,
   * {@link ClockInOutEnum#IN} is returned.
   */
  public ClockInOutEnum getClockStatus() {
    return clockStatus;
  }

  /**
   * Sets the specified clock status.
   *
   * @param clockStatus the clock status to set to.
   */
  public void setClockStatus(ClockInOutEnum clockStatus) {
    this.clockStatus = clockStatus;
  }

  /**
   * Gets the text for this line.
   *
   * @return the text, if it has been set; {@code null} otherwise.
   */
  public String getText() {
    return text;
  }

  /**
   * Sets the text for this line.
   *
   * @param text the text to set; may be {@code null}.
   */
  public void setText(String text) {
    this.text = text;
  }

  /**
   * Returns the string value of this line as specified in the class
   * header.
   *
   * @return the clock-in/-out value, date and text (if supplied when
   * constructing the clock line).
   */
  @Override
  public String toString() {
    String data = this.clockStatus.toString() + " " +
      ClockManager.getInstance().formatDate(this.clockDate);
    if (this.getText() != null) {
      data += " " + this.text;
    }
    return data;
  }

  /**
   * Two lines are considered equal if they contain the same status (in/out)
   * <i>and</i> the same date. The text of the line is not considered,
   * since this may have been changed by the user. Specifically, the
   * architecture of clocking-in and -out are such that only the status
   * and date should be important.
   * 
   * @param obj the object to compare against.
   * 
   * @return {@code true} if the two lines are equal according to the above
   * rules; false otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final ClockLine other = (ClockLine) obj;
    if (this.clockStatus != other.clockStatus) {
      return false;
    }
    if (this.clockDate != other.clockDate 
            && (this.clockDate == null
                    || !this.clockDate.equals(other.clockDate))) {
      return false;
    }
    return true;
  }

  /**
   * Get the lines hash code based on status and date.
   * 
   * @return an integer for the line.
   */
  @Override
  public int hashCode() {
    int hash = 7;
    hash = 97 * hash + (this.clockStatus != null ? this.clockStatus.hashCode() : 0);
    hash = 97 * hash + (this.clockDate != null ? this.clockDate.hashCode() : 0);
    return hash;
  }

}
