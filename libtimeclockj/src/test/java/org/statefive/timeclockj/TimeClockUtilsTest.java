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

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import junit.framework.TestCase;

/**
 *
 * @author rich
 */
public class TimeClockUtilsTest extends AbstractTimeClockJTest {

  File timelog = new File("txt.clock.test");
  
  public TimeClockUtilsTest(String testName) {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    timelog.delete();
  }

  /**
   * Test of clockOut method, of class TimeClockUtils.
   */
  public void testClockOutFileString() throws Exception {
    Date d = Calendar.getInstance().getTime();
    String text = "Clocking out...";
    ClockLine line = new ClockLine();
    // got to clock in before we clock out!
    TimeClockUtils.clockIn(timelog, null);
    line.setClockStatus(ClockInOutEnum.OUT);
    line.setText(text);
    ClockLine ret = TimeClockUtils.clockOut(timelog, text);
    Date d2 = Calendar.getInstance().getTime();
    assertEquals(ret.getClockStatus(), ClockInOutEnum.OUT);
    assertEquals(ret.getText(), text);
    // we don't actually know what time was clocked -
    // just need to make sure it's in the right range:
    assertTrue(d.getTime() <= ret.getClockDate().getTime() 
            && ret.getClockDate().getTime() <= d2.getTime());
  }

  /**
   * Test of clockOut method, of class TimeClockUtils.
   */
  public void testClockOutFileStringLong() throws Exception {
    Date d = Calendar.getInstance().getTime();
    String text = "Clocking out...";
    ClockLine line = new ClockLine();
    // got to clock in before we clock out!
    TimeClockUtils.clockIn(timelog, null);
    line.setClockStatus(ClockInOutEnum.OUT);
    line.setClockDate(d);
    line.setText(text);
    ClockLine ret = TimeClockUtils.clockOut(timelog, text, d.getTime());
    assertEquals(line, ret);
    // text isn't considered according to the API - so test it's the same:
    assertEquals(text, ret.getText());
  }

  /**
   * Test of clockIn method, of class TimeClockUtils.
   */
  public void testClockInFileString() throws Exception {
    Date d = Calendar.getInstance().getTime();
    String text = "Project A";
    ClockLine ret = TimeClockUtils.clockIn(timelog, text);
    Date d2 = Calendar.getInstance().getTime();
    ClockLine line = new ClockLine();
    line.setClockStatus(ClockInOutEnum.IN);
    line.setClockDate(d);
    line.setText(text);
//    assertEquals(line, ret);
    assertEquals(text, ret.getText());
    // we don't actually know what time was clocked -
    // just need to make sure it's in the right range:
    assertTrue(d.getTime() <= ret.getClockDate().getTime() 
            && ret.getClockDate().getTime() <= d2.getTime());
  }

  /**
   * Test of clockIn method, of class TimeClockUtils.
   */
  public void testClockInFileStringLong() throws Exception {
    System.out.println("TIME LOG = " + timelog.length());
    Date d = Calendar.getInstance().getTime();
    String text = "Project A";
    ClockLine line = new ClockLine();
    // got to clock in before we clock out!
    ClockLine ret = TimeClockUtils.clockIn(timelog, text, d.getTime());
    line.setClockStatus(ClockInOutEnum.IN);
    line.setClockDate(d);
    line.setText(text);
    assertEquals(line, ret);
    // text isn't considered according to the API - so test it's the same:
    assertEquals(text, ret.getText());
  }

  /**
   * Test of parseFile method, of class TimeClockUtils.
   */
  public void testParseFile() throws Exception {
    FileWriter fw = new FileWriter(timelog);
    fw.append(TestUtils.BASIC_CLOCK_DATA);
    fw.flush();
    fw.close();
    TimeClockUtils.parseFile(timelog);
    assertEquals(3, ClockManager.getInstance().getProjects().length);
    assertEquals(2, ClockManager.getInstance().getClockPeriods("Project A").size());
    assertEquals(1, ClockManager.getInstance().getClockPeriods("Project B").size());
    assertEquals(1, ClockManager.getInstance().getClockPeriods(
            ClockManager.DEFAULT_PROJECT_STRING).size());
  }

  /**
   * Test of parseFile method, of class TimeClockUtils.
   */
  public void testReloadFile() throws Exception {
    FileWriter fw = new FileWriter(timelog);
    fw.append(TestUtils.BASIC_CLOCK_DATA);
    fw.flush();
    fw.close();
    TimeClockUtils.reloadFile(timelog);
    assertEquals(3, ClockManager.getInstance().getProjects().length);
    assertEquals(2, ClockManager.getInstance().getClockPeriods("Project A").size());
    assertEquals(1, ClockManager.getInstance().getClockPeriods("Project B").size());
    assertEquals(1, ClockManager.getInstance().getClockPeriods(
            ClockManager.DEFAULT_PROJECT_STRING).size());
    TimeClockUtils.reloadFile(timelog);
    assertEquals(3, ClockManager.getInstance().getProjects().length);
    assertEquals(2, ClockManager.getInstance().getClockPeriods("Project A").size());
    assertEquals(1, ClockManager.getInstance().getClockPeriods("Project B").size());
    assertEquals(1, ClockManager.getInstance().getClockPeriods(
            ClockManager.DEFAULT_PROJECT_STRING).size());
  }
}
