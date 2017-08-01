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

import junit.framework.TestCase;

/**
 *
 * @author rich
 */
public class ClockPeriodTest extends TestCase {

  public ClockPeriodTest(String testName) {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testSetGetText() {
    ClockPeriod clockperiod = new ClockPeriod();
    assertNull(clockperiod.getDescription());
    String text = "test text";
    clockperiod.setDescription(text);
    assertEquals(text, clockperiod.getDescription());
  }

  /**
   * Test of getClockInTime method, of class ClockPeriod.
   */
  public void testSetGetClockInTime() {
    ClockPeriod clockperiod = new ClockPeriod();
    assertNull(clockperiod.getClockInTime());
    long time = 10;
    clockperiod.setClockInTime(time);
    assertEquals((long)time, (long)clockperiod.getClockInTime());
  }

  /**
   * Test of getClockOutTime method, of class ClockPeriod.
   */
  public void testSetGetClockOutTime() {
    ClockPeriod clockperiod = new ClockPeriod();
    assertNull(clockperiod.getClockOutTime());
    long time = 10;
    clockperiod.setClockOutTime(time);
    assertEquals((long)time, (long)clockperiod.getClockOutTime());
  }

  /**
   * Test of doubleValue method, of class ClockPeriod.
   */
  public void testDoubleValue() {
    ClockPeriod clock = new ClockPeriod();
    clock.setClockInTime(0);
    clock.setClockOutTime(100);
    assertEquals(100.0, clock.doubleValue());
  }

  /**
   * Test of floatValue method, of class ClockPeriod.
   */
  public void testFloatValue() {
    ClockPeriod clock = new ClockPeriod();
    clock.setClockInTime(0);
    clock.setClockOutTime(100);
    assertEquals(100.0f, clock.floatValue());
  }

  /**
   * Test of intValue method, of class ClockPeriod.
   */
  public void testIntValue() {
    ClockPeriod clock = new ClockPeriod();
    clock.setClockInTime(0);
    clock.setClockOutTime(100);
    assertEquals(100, clock.intValue());
  }

  /**
   * Test of longValue method, of class ClockPeriod.
   */
  public void testLongValue() {
    ClockPeriod clock = new ClockPeriod();
    clock.setClockInTime(0);
    clock.setClockOutTime(100);
    assertEquals(100, clock.longValue());
  }

  public void testCalculateTimeWithClockInAndOut() {
    ClockPeriod clock = new ClockPeriod();
    long clockin = 100;
    long clockout = 1000;
    clock.setClockInTime(clockin);
    clock.setClockOutTime(clockout);
    assertEquals(clockout-clockin, clock.calculateTime());
  }

  public void testCalculateTimeWithNoClockInAndOut() {
    ClockPeriod clock = new ClockPeriod();
    assertEquals(-1, clock.calculateTime());
  }

  public void testCalculateTimeWithClockInOnly() {
    ClockPeriod clock = new ClockPeriod();
    long clockin = 100;
    long x = System.currentTimeMillis();
    clock.setClockInTime(clockin);
    long time = clock.calculateTime();
    long y = System.currentTimeMillis();
    assertTrue( (x-clockin) <= time);
    assertTrue( (y-clockin) >= time);
  }
}
