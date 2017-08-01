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

/**
 * 
 * @author rich
 */
public class Duration extends Number {

  /** */
  private String description;
  /** */
  private int hours;
  /** */
  private int minutes;
  /** */
  private int seconds;
  /** */
  private long startTime;
  /** */
  private long endTime;

  /**
   *
   */
  public Duration() {

  }

  public long getEndTime() {
    return endTime;
  }

  public void setEndTime(long endTime) {
    this.endTime = endTime;
  }

  public long getStartTime() {
    return startTime;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /**
   *
   * @return
   */
  public int getHours() {
    return hours;
  }

  /**
   *
   * @param hours
   */
  public void setHours(int hours) {
    this.hours = hours;
  }

  /**
   *
   * @return
   */
  public int getMinutes() {
    return minutes;
  }

  /**
   *
   * @param minutes
   */
  public void setMinutes(int minutes) {
    this.minutes = minutes;
  }

  /**
   *
   * @return
   */
  public int getSeconds() {
    return seconds;
  }

  /**
   *
   * @param seconds
   */
  public void setSeconds(int seconds) {
    this.seconds = seconds;
  }

  /**
   *
   * @return
   */
  @Override
  public double doubleValue() {
    return (double)this.calculateValue();
  }

  /**
   *
   * @return
   */
  @Override
  public float floatValue() {
    return (float)this.calculateValue();
  }

  /**
   *
   * @return
   */
  @Override
  public int intValue() {
    return (int)this.calculateValue();
  }

  /**
   *
   * @return
   */
  @Override
  public long longValue() {
    return this.calculateValue();
  }

  /**
   *
   * @return
   */
  private long calculateValue() {
    return ( ((seconds) + (minutes * 60) + (hours * 60 * 60)) * 1000);
  }

  /**
   * 
   * @param o
   * @return
   */
  @Override
  public boolean equals(Object o) {
    boolean equals = false;
    if (o != null && o instanceof Duration) {
      equals = ((Duration)o).longValue() == this.longValue();
    }
    return equals;
  }

  /**
   * 
   * @return
   */
  @Override
  public int hashCode() {
    return (int)this.longValue();
  }

  /**
   * 
   * @return
   */
  @Override
  public String toString() {
    String duration = "";
    if (hours > 0) {
      duration += hours + "h ";
    }
    if (minutes > 0 || seconds > 0) {
      String minutePadding = "";
      if (minutes < 10) {
        minutePadding = " ";
      }
      if (seconds > 0) {
        duration += minutePadding + minutes + "m ";
        if (seconds < 10) {
           duration += " ";
        }
        duration += seconds + "s";
      } else {
        duration += minutePadding + minutes + "m";
      }
    }
    return duration;
  }

}
