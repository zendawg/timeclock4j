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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.junit.Test;



/**
 *
 * @author rich
 */
public class ClockManagerTest extends AbstractTimeClockJTest {

  ClockManager clock = null;
  
  public ClockManagerTest(String testName) {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    clock = ClockManager.getInstance();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  /**
   * Test of getInstance method, of class ClockManager.
   */
  @Test
  public void testGetInstance() {
    assertNotNull(clock);
  }

  /**
   * Test of clockIn method, of class ClockManager.
   */
  @Test
  public void testClockInWithDefaultProject() throws Exception {
    long time = 26101973;
    assertTrue(!clock.isClockedIn());
    assertEquals(null, clock.getCurrentProject());
    clock.clockIn(null, time);
    assertTrue(clock.isClockedIn());
    assertEquals(ClockManager.DEFAULT_PROJECT_STRING, clock.getCurrentProject());
  }

  /**
   * Test of clockIn method, of class ClockManager.
   */
  @Test
  public void testClockInWithUserDefinedProject() throws Exception {
    String project = "proj";
    long time = 26101973;
    assertTrue(!clock.isClockedIn());
    assertEquals(null, clock.getCurrentProject());
    clock.clockIn(project, time);
    assertTrue(clock.isClockedIn());
    assertEquals(project, clock.getCurrentProject());
  }

  /**
   * Test of clockOut method, of class ClockManager.
   */
  @Test
  public void testClockOutWithUserDefinedProject() throws Exception {
    long time = 26101973;
    String project = "projB";
    assertTrue(!clock.isClockedIn());
    assertEquals(null, clock.getCurrentProject());
    clock.clockIn(project, time);
    assertTrue(clock.isClockedIn());
    assertEquals(project, clock.getCurrentProject());
    clock.clockOut(null, time);
    assertTrue(!clock.isClockedIn());
    assertEquals(null, clock.getCurrentProject());
  }


  /**
   * Test of clockOut method, of class ClockManager.
   */
  @Test
  public void testClockOutWithDefaultProject() throws Exception {
    long time = 26101973;
    assertTrue(!clock.isClockedIn());
    assertEquals(null, clock.getCurrentProject());
    clock.clockIn(null, time);
    assertTrue(clock.isClockedIn());
    assertEquals(ClockManager.DEFAULT_PROJECT_STRING, clock.getCurrentProject());
    clock.clockOut(null, time);
    assertTrue(!clock.isClockedIn());
    assertEquals(null, clock.getCurrentProject());
  }

  /**
   * Test of isClockedIn method, of class ClockManager.
   */
  @Test
  public void testIsClockedIn() throws Exception {
    long time = 26101973;
    assertTrue(!clock.isClockedIn());
    assertEquals(null, clock.getCurrentProject());
    clock.clockIn(null, time);
    assertTrue(clock.isClockedIn());
  }

  /**
   * Test of getCurrentProject method, of class ClockManager.
   */
  @Test
  public void testGetCurrentProject() throws Exception {
    long time = 26101973;
    assertEquals(null, clock.getCurrentProject());
    clock.clockIn(null, time);
    assertEquals(ClockManager.DEFAULT_PROJECT_STRING, clock.getCurrentProject());
  }

  /**
   * Test of parseDate method, of class ClockManager.
   */
  @Test
  public void testParseDate() throws Exception {
    Date d = Calendar.getInstance().getTime();
    SimpleDateFormat sdf = new SimpleDateFormat(TimeClockParser.DATE_TIME_FORMAT);
    Date ret = clock.parseDate(sdf.format(d));
    assertEquals(d.toString(), ret.toString());
  }

  /**
   * Test of formatDate method, of class ClockManager.
   */
  @Test
  public void testFormatDate() {
    Date d = Calendar.getInstance().getTime();
    SimpleDateFormat sdf = new SimpleDateFormat(TimeClockParser.DATE_TIME_FORMAT);
    String ret = clock.formatDate(d);
    assertEquals(sdf.format(d), ret);
  }

  /**
   * Test of getProjects method, of class ClockManager.
   */
  @Test
  public void testGetProjects() throws Exception {
    String p1 = "p1";
    String p2 = "p2";
    String p3 = "p3";
    String p4 = "p4";
    assertEquals(null, clock.getCurrentProject());
    assertEquals(0, clock.getProjects().length);
    clock.clockIn(null, 0);
    clock.clockOut(null, 0);
    clock.clockIn(p1, 0);
    clock.clockOut(null, 0);
    clock.clockIn(p2, 0);
    clock.clockOut(null, 0);
    clock.clockIn(p3, 0);
    clock.clockOut(null, 0);
    clock.clockIn(p4, 0);
    clock.clockOut(null, 0);
    assertEquals(5, clock.getProjects().length);
  }

  /**
   * Test of getClockPeriods method, of class ClockManager.
   */
  @Test
  public void testGetClockPeriods() throws Exception {
    String p1 = "p1";
    String p2 = "p2";
    String p3 = "p3";
    assertEquals(null, clock.getCurrentProject());
    assertEquals(0, clock.getProjects().length);
    clock.clockIn(null, 0);
    clock.clockOut(null, 0);
    clock.clockIn(p1, 0);
    clock.clockOut(null, 0);
    clock.clockIn(p2, 0);
    clock.clockOut(null, 0);
    clock.clockIn(p3, 0);
    clock.clockOut(null, 0);
    clock.clockIn(p3, 0);
    clock.clockOut(null, 0);
    assertEquals(2, clock.getClockPeriods(p3).size());
    assertEquals(1, clock.getClockPeriods(p2).size());
    assertEquals(1, clock.getClockPeriods(p1).size());
    assertEquals(1, clock.getClockPeriods(null).size());
    assertEquals(0, clock.getClockPeriods("no such project").size());
  }
}
