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

/**
 * Period spanning a start (clock-in) time and end (clock-out) time, with
 * optional description.
 *
 * If no clock-out time is supplied, the clock period is expected to be
 * continually adding up as the system time increases. Supplying a clock-out
 * time without a clock-in time will result in unexpected behaviour.
 * Typically, a description is usually only supplied once the clock-out time
 * has been provided.
 *
 * @author rich
 */
public class ClockPeriod extends Number {

  /** The time the clock period started. */
  private Long clockInTime;
  /** The time the clock period ended. */
  private Long clockOutTime;
  /** The description of the clock period. */
  private String description;
  /** */

  /**
   * The description contains information about the clock out and is optional.
   *
   *
   * @return the description; {@code null} if the description has never
   * been set (or has been set to {@code null}).
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description - typically, the description is set after the clock
   * out time has been set.
   *
   * @param description the textual description; may be {@code null}.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the clock-in time if it has been set.
   *
   * @return the clock-in time; if not set, @code{null} is returned.
   */
  public Long getClockInTime() {
    return clockInTime;
  }

  /**
   * Sets the clock-in time.
   *
   * @param clockInTime the non-negative clock-in time. Setting the clock-in
   * time to negative will result in unexpected behaviour.
   */
  public void setClockInTime(long clockInTime) {
    this.clockInTime = clockInTime;
  }

  /**
   * Gets the clock-out time if it has been set.
   *
   * @return the clock-out time; if not set, @code{null} is returned.
   */
  public Long getClockOutTime() {
    return clockOutTime;
  }

  /**
   * Sets the clock-in time.
   *
   * @param clockOutTime the non-negative clock-out time. Setting the clock-out
   * time to negative will result in unexpected behaviour.
   */
  public void setClockOutTime(long clockOutTime) {
    this.clockOutTime = clockOutTime;
  }



  /**
   * Returns the number of milliseconds the duration has lasted as a double.
   *
   * @return the value dictated by {@link #calculateTime()} as a double.
   */
  @Override
  public double doubleValue() {
    return (double)this.calculateTime();
  }

  /**
   * Returns the number of milliseconds the duration has lasted as a float.
   *
   * @return the value dictated by {@link #calculateTime()} as a float.
   */
  @Override
  public float floatValue() {
    return (float)this.calculateTime();
  }

  /**
   * Returns the number of milliseconds the duration has lasted as an int.
   *
   * @return the value dictated by {@link #calculateTime()} as an int.
   */
  @Override
  public int intValue() {
    return (int)this.calculateTime();
  }

  /**
   * Returns the number of milliseconds the duration has lasted as a long.
   *
   * @return the value dictated by {@link #calculateTime()}.
   */
  @Override
  public long longValue() {
    return this.calculateTime();
  }

  /**
   * Returns the number of milliseconds the period has lasted as a long.
   * 
   * @return the following value:
   *
   * <p>
   * <ul>
   *   <li>if only a clock-in time is given, the current system time
   * in milliseconds less the clock-in time is returned;</li>
   *   <li>if clock-in and clock-out time are given, the clock-out time
   * less the clock-in time is returned</li>
   *   <li>otherwise, -1 is returned;</li>
   * </ul>
   * </p>
   */
  public long calculateTime() {
    long time = -1;
    if (clockInTime != null && clockOutTime != null) {
      time = clockOutTime - clockInTime;
    } else if (clockInTime != null) {
      // the period has been clocked in, take the current time:
      time = System.currentTimeMillis() - clockInTime;
    }
    return time;
  }
}
