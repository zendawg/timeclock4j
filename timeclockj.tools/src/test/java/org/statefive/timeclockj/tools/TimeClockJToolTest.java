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
package org.statefive.timeclockj.tools;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.security.Permission;
import java.util.regex.Pattern;

import junit.framework.TestCase;

/**
 * The inspiration for being able to test a Main method that uses
 * <tt>System.exit(int)</tt> was taken from
 * <a href="http://www.screaming-penguin.com/node/7570">
 * http://www.screaming-penguin.com/node/7570</a>.
 *
 * @author rich
 */
public class TimeClockJToolTest extends TestCase {

  private ByteArrayOutputStream out;
  private ByteArrayOutputStream err;
  private PrintStream stdout;
  private PrintStream stderr;
  private File timelogFile;

  protected static class ExitException extends SecurityException {

    public final int status;

    public ExitException(int status) {
      super("There is no escape!");
      this.status = status;
    }
  }

  private static class NoExitSecurityManager extends SecurityManager {

    @Override
    public void checkPermission(Permission perm) {
      // allow anything.
    }

    @Override
    public void checkPermission(Permission perm, Object context) {
      // allow anything.
    }

    @Override
    public void checkExit(int status) {
      super.checkExit(status);
      throw new ExitException(status);
    }
  }

  public TimeClockJToolTest() {
    super("RegexToolTest");
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
    System.setSecurityManager(new NoExitSecurityManager());

    this.stdout = System.out;
    this.stderr = System.err;
    this.out = new ByteArrayOutputStream();
    this.err = new ByteArrayOutputStream();
    System.setOut(new PrintStream(this.out));
    System.setErr(new PrintStream(this.err));
  }

  @Override
  public void tearDown() throws Exception {
    System.setSecurityManager(null);
    super.tearDown();
    System.setOut(this.stdout);
    System.setErr(this.stderr);
  }

  public void testFileCreatedSuccessfully() throws Exception {
    String timelogName = System.currentTimeMillis() + ".timeclockj-test.timelog";
    timelogFile = new File(timelogName);
    timelogFile.deleteOnExit();
    assertTrue("Timelog file already exists.", !timelogFile.exists());
    try {
      String[] args = new String[]{"in", "--file", timelogFile.getName()};
      TimeClockJTool.main(args);
    } catch (ExitException e) {
      assertTrue(timelogFile.exists());
      assertEquals(TimeClockJTool.EXIT_NO_ERROR, e.status);
    } catch (Exception ex) {
      fail(ex.getMessage());
    } finally {
      timelogFile.delete();
    }
  }

  public void testFileCreatedFailsForBadFile() throws Exception {
    try {
      String[] args = new String[]{"in", "--file", ".."};
      TimeClockJTool.main(args);
    } catch (ExitException e) {
      assertEquals(TimeClockJTool.EXIT_CONSTRUCTION_ERROR, e.status);
      // test the error stream (this.err) equals the following string:
      // .. (No such file or directory)
    } catch(Exception ex) {
      fail(ex.getMessage());
    }
  }

  /**
   * Written to confirm that feature request ### is OK.
   * @throws Exception
   */
  public void testCommandLineHeader() throws Exception {
    try {
      String[] args = new String[]{"-h"};
      TimeClockJTool.main(args);
    } catch (ExitException e) {
      String output = new String(this.out.toByteArray());
//      String header = "usage: timeclockj [command] [OPTIONS]\n"
//              + "Tool to clock-in and -out and produce reports.\n"
//              + "Commands: in | out | report\n"
//              + "Try timeclockj [command] -h or --help for individual commands.";
      // Allow for different line endings on different platforms
      String[] outputLines = Pattern.compile("\r\n|\n|\r").split(output);
      assertEquals("usage: timeclockj [command] [OPTIONS]", outputLines[0]);
      assertEquals("Tool to clock-in and -out and produce reports.", outputLines[1]);
      assertEquals("Commands: in | out | report", outputLines[2]);
      assertEquals("Try timeclockj [command] -h or --help for individual commands.", outputLines[3]);
//      assertTrue(output.startsWith(header));
      assertEquals(TimeClockJTool.EXIT_NO_ERROR, e.status);
    }
  }
}
