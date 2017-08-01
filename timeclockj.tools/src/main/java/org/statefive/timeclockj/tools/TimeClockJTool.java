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
package org.statefive.timeclockj.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.statefive.timeclockj.ClockException;
import org.statefive.timeclockj.ClockManager;
import org.statefive.timeclockj.TimeClockParser;
import org.statefive.timeclockj.TimeClockUtils;
import org.statefive.timeclockj.report.ReportOutput;
import org.statefive.timeclockj.cli.CommandLineUtils;

/**
 *
 * @author rich
 */
public class TimeClockJTool {

  /**
   * Status code for the app exiting with no error.
   */
  public static final int EXIT_NO_ERROR = 0;
  /**
   * Status code for the app exiting with a usage error.
   */
  public static final int EXIT_USAGE_ERROR = 1;
  /**
   * Status code for the app exiting with a failure to construct any objects
   * correctly.
   */
  public static final int EXIT_CONSTRUCTION_ERROR = 2;
  /**
   *    */
  private static final SimpleDateFormat DATE_FORMAT =
          new SimpleDateFormat(TimeClockParser.DATE_FORMAT);
  /**
   *    */
  private static final SimpleDateFormat DATE_TIME_FORMAT =
          new SimpleDateFormat(TimeClockParser.DATE_TIME_FORMAT);
  public static final String COMMAND_IN = "in";
  public static final String COMMAND_OUT = "out";
  public static final String COMMAND_REPORT = "report";
  public static final String COMMAND_LINE_DESCRIPTION = "description";
  public static final String COMMAND_LINE_DESCRIPTION_SHORT = "d";
  public static final String COMMAND_LINE_PROJECT = "project";
  public static final String COMMAND_LINE_PROJECT_SHORT = "p";
  public static final String COMMAND_LINE_OUTPUT_FILE = "output";
  public static final String COMMAND_LINE_OUTPUT_FILE_SHORT = "o";
  public static final String COMMAND_LINE_HTML = "html";
  public static final String COMMAND_LINE_HTML_SHORT = "m";
  public static final String COMMAND_LINE_START_DATE = "start-date";
  public static final String COMMAND_LINE_START_DATE_SHORT = "s";
  public static final String COMMAND_LINE_END_DATE = "end-date";
  public static final String COMMAND_LINE_END_DATE_SHORT = "e";

  public static void main(String[] args) {
    if (args.length == 0) {
      System.err.println("Try -h or --help");
      System.exit(-1);
    }
    CommandLineParser parser = new PosixParser();
    try {
      String command = args[0];

      List<String> argList = new ArrayList<String>(Arrays.asList(args));
      argList.remove(0);
      if (COMMAND_IN.equals(command)) {
        doClockIn(argList.toArray(new String[]{}));
        System.exit(TimeClockJTool.EXIT_NO_ERROR);
      } else if (COMMAND_OUT.equals(command)) {
        doClockOut(argList.toArray(new String[]{}));
        System.exit(TimeClockJTool.EXIT_NO_ERROR);
      } else if (COMMAND_REPORT.equals(command)) {
        doReports(argList.toArray(new String[]{}));
        System.exit(TimeClockJTool.EXIT_NO_ERROR);
      } else if (!command.startsWith("-")) {
        System.err.println("Unknown command: '" + command + "'");
        System.exit(-1);
      } else {
        Options options = CommandLineUtils.getCommonOptions();
        CommandLine cmd = parser.parse(options, args);
        cmd = parser.parse(options, args);
        String header = "Tool to clock-in and -out and produce reports.\n"
                + "Commands: in | out | report\n"
                + "Try timeclockj [command] -h or --help for individual commands.";
        CommandLineUtils.doBasicOptions("timeclockj [command] [OPTIONS]",
                header, options, cmd, TimeClockJTool.class);
        System.exit(TimeClockJTool.EXIT_NO_ERROR);
      }
    } catch (ParseException pex) {
      System.err.println(pex.getMessage());
    }
  }

  private static void doClockIn(String[] args) {

    Options options = CommandLineUtils.getCommonOptions();
    options.addOption(TimeClockJTool.COMMAND_LINE_PROJECT_SHORT,
            TimeClockJTool.COMMAND_LINE_PROJECT, true,
            "Set the project to clock in with. The default (blank) project will "
            + "be used if no project is specified. Specify projects with spaces "
            + "in using quotes.");
    options.addOption(TimeClockJTool.COMMAND_LINE_START_DATE_SHORT,
            TimeClockJTool.COMMAND_LINE_START_DATE, true,
            "Specify the clock-in date (format "
            + TimeClockParser.DATE_FORMAT.toLowerCase() + " or "
            + TimeClockParser.DATE_TIME_FORMAT.toLowerCase().replace(" ", "-")
            + ")");

    CommandLineParser parser = new PosixParser();

    try {
      Date startDate = null;
      CommandLine cmd = parser.parse(options, args);
      String header = "Clocks in. Options:";
      CommandLineUtils.doBasicOptions("timeclockj in [OPTIONS]", header,
              options, cmd, TimeClockJTool.class);
      String project = null;
      if (cmd.hasOption(TimeClockJTool.COMMAND_LINE_PROJECT)
              || cmd.hasOption(TimeClockJTool.COMMAND_LINE_PROJECT_SHORT)) {
        project = cmd.getOptionValue(TimeClockJTool.COMMAND_LINE_PROJECT);
      }
      if (cmd.hasOption(TimeClockJTool.COMMAND_LINE_START_DATE)
              || cmd.hasOption(TimeClockJTool.COMMAND_LINE_START_DATE_SHORT)) {
        String date = cmd.getOptionValue(TimeClockJTool.COMMAND_LINE_START_DATE);
        startDate = getDate(date);
      }
      try {
        File timelog = CommandLineUtils.getFile(cmd);
        if (!timelog.exists()) {
          timelog.createNewFile();
        }
        TimeClockUtils.parseFile(timelog);
        if (startDate != null) {
          TimeClockUtils.clockIn(timelog, project, startDate.getTime());
        } else {
          TimeClockUtils.clockIn(timelog, project);
        }
      } catch (ClockException coex) {
        System.err.println(coex.getMessage());
        System.exit(TimeClockJTool.EXIT_CONSTRUCTION_ERROR);
      } catch (IOException ioex) {
        System.err.println(ioex.getMessage());
        System.exit(TimeClockJTool.EXIT_CONSTRUCTION_ERROR);
      }
    } catch (ParseException pex) {
      System.err.println(pex.getMessage());
      System.exit(TimeClockJTool.EXIT_CONSTRUCTION_ERROR);
    } catch (java.text.ParseException pex) {
      System.err.println(pex.getMessage());
      System.exit(TimeClockJTool.EXIT_CONSTRUCTION_ERROR);
    }
  }

  private static void doClockOut(String[] args) {

    Options options = CommandLineUtils.getCommonOptions();
    options.addOption(TimeClockJTool.COMMAND_LINE_DESCRIPTION_SHORT,
            TimeClockJTool.COMMAND_LINE_DESCRIPTION, true,
            "Set the reason for clocking out.");
    options.addOption(TimeClockJTool.COMMAND_LINE_END_DATE_SHORT,
            TimeClockJTool.COMMAND_LINE_END_DATE, true,
            "Specify the clock-out date (format "
            + TimeClockParser.DATE_FORMAT.toLowerCase() + " or "
            + TimeClockParser.DATE_TIME_FORMAT.toLowerCase().replace(" ", "-")
            + ")");

    CommandLineParser parser = new PosixParser();

    String description = null;
    try {
      Date endDate = null;
      CommandLine cmd = parser.parse(options, args);
      String header = "Clocks out of the current clock. Options:";
      CommandLineUtils.doBasicOptions("timeclockj out [OPTIONS]", header,
              options, cmd, TimeClockJTool.class);
      if (cmd.hasOption(TimeClockJTool.COMMAND_LINE_DESCRIPTION)
              || cmd.hasOption(TimeClockJTool.COMMAND_LINE_DESCRIPTION_SHORT)) {
        description = cmd.getOptionValue(TimeClockJTool.COMMAND_LINE_DESCRIPTION);
      }
      if (cmd.hasOption(TimeClockJTool.COMMAND_LINE_END_DATE)
              || cmd.hasOption(TimeClockJTool.COMMAND_LINE_END_DATE_SHORT)) {
        String date = cmd.getOptionValue(TimeClockJTool.COMMAND_LINE_END_DATE);
        endDate = getDate(date);
      }
      File timelog = CommandLineUtils.getFile(cmd);
      if (!timelog.exists()) {
        timelog.createNewFile();
      }
      TimeClockUtils.parseFile(timelog);
      if (endDate != null) {
        TimeClockUtils.clockOut(timelog, description, endDate.getTime());
      } else {
        TimeClockUtils.clockOut(timelog, description);
      }
    } catch (java.text.ParseException pex) {
      System.err.println(pex.getMessage());
    } catch (ParseException pex) {
      System.err.println(pex.getMessage());
    } catch (ClockException coex) {
      System.err.println(coex.getMessage());
    } catch (IOException ioex) {
      System.err.println(ioex.getMessage());
    }
  }

  /**
   *
   * @param args
   */
  private static void doReports(String[] args) {
    Options options = getReportOptions();
    CommandLineParser parser = new PosixParser();

    try {
      CommandLine cmd = parser.parse(options, args);
      String header = "Generates timeclock reports for all projects"
              + " to stdout unless otherwise stated. Options:";
      CommandLineUtils.doBasicOptions("timeclockj report [OPTIONS]",
              header, options, cmd, TimeClockJTool.class);
      TimeClockUtils.parseFile(CommandLineUtils.getFile(cmd));
      String data = null;
      boolean html = false;
      Date startDate = null;
      Date endDate = null;
      String date = null;
      if (cmd.hasOption(TimeClockJTool.COMMAND_LINE_HTML)
              || cmd.hasOption(TimeClockJTool.COMMAND_LINE_HTML_SHORT)) {
        html = true;
      }
      if (cmd.hasOption(TimeClockJTool.COMMAND_LINE_START_DATE)
              || cmd.hasOption(TimeClockJTool.COMMAND_LINE_START_DATE_SHORT)) {
        date = cmd.getOptionValue(TimeClockJTool.COMMAND_LINE_START_DATE);
        startDate = getDate(date);
      }
      if (cmd.hasOption(TimeClockJTool.COMMAND_LINE_END_DATE)
              || cmd.hasOption(TimeClockJTool.COMMAND_LINE_END_DATE_SHORT)) {
        date = cmd.getOptionValue(TimeClockJTool.COMMAND_LINE_END_DATE);
        endDate = getDate(date);
      }

      if (cmd.hasOption(TimeClockJTool.COMMAND_LINE_PROJECT)
              || cmd.hasOption(TimeClockJTool.COMMAND_LINE_PROJECT_SHORT)) {
        // just print report for one:
        String project =
                cmd.getOptionValue(TimeClockJTool.COMMAND_LINE_PROJECT);
        if (html) {
          data = new ReportOutput().getHtmlOutput(
                  new String[]{project}, startDate, endDate);
        } else {
          data = new ReportOutput().getConsoleOutput(
                  new String[]{project});
        }
      } else {
        // print reports for *all* projects:
        if (html) {
          data = new ReportOutput().getHtmlOutput(
                  ClockManager.getInstance().getProjects(), startDate, endDate);
        } else {
          data = new ReportOutput().getConsoleOutput(
                  ClockManager.getInstance().getProjects());
        }
      }
      writeOutput(cmd, data);
    } catch (ParseException pex) {
      System.err.println(pex.getMessage());
    } catch (IOException ioex) {
      System.err.println(ioex.getMessage());
    } catch (java.text.ParseException pex) {
      System.err.println(pex.getMessage());
    }
  }

  /**
   *
   * @param cmd
   * @param data
   */
  private static void writeOutput(CommandLine cmd, String data)
          throws IOException {

    OutputStream os = System.out;
    boolean outputToFile = false;
    if (cmd.hasOption(TimeClockJTool.COMMAND_LINE_OUTPUT_FILE)
            || cmd.hasOption(TimeClockJTool.COMMAND_LINE_OUTPUT_FILE_SHORT)) {
      outputToFile = true;
      try {
        os = new FileOutputStream(
                cmd.getOptionValue(TimeClockJTool.COMMAND_LINE_OUTPUT_FILE));
      } catch (FileNotFoundException fnfex) {
        throw new IOException(fnfex.getMessage(), fnfex.getCause());
      }
    }
    os.write(data.getBytes());
    os.flush();
    os.close();
    if (outputToFile) {
      System.out.println("Written " + data.getBytes().length + " bytes.");
    }
  }

  /**
   *
   * @return
   */
  private static Options getReportOptions() {
    Options options = CommandLineUtils.getCommonOptions();
    OptionGroup optionGroup = new OptionGroup();
    Option optionProject = new Option(TimeClockJTool.COMMAND_LINE_PROJECT_SHORT,
            TimeClockJTool.COMMAND_LINE_PROJECT, true,
            "Print report for the specified project. The default (no-name) "
            + "project is defined as \"<default>\", surrounded with quotes for "
            + "report purposes.");
    optionGroup.addOption(optionProject);
    options.addOptionGroup(optionGroup);
    options.addOption(TimeClockJTool.COMMAND_LINE_OUTPUT_FILE_SHORT,
            TimeClockJTool.COMMAND_LINE_OUTPUT_FILE, true,
            "Instead of printing output to stdout, write to specified file. If "
            + "the file already exists, it is over-written.");
    options.addOption(TimeClockJTool.COMMAND_LINE_HTML_SHORT,
            TimeClockJTool.COMMAND_LINE_HTML, false,
            "Instead of outputing data in unmarked-up text, print the report "
            + "in  HTML.");
    options.addOption(TimeClockJTool.COMMAND_LINE_START_DATE_SHORT,
            TimeClockJTool.COMMAND_LINE_START_DATE, true,
            "Specify the start date. Clock periods prior to this will not "
            + "be included. (format "
            + TimeClockParser.DATE_FORMAT.toLowerCase() + " or "
            + TimeClockParser.DATE_TIME_FORMAT.toLowerCase().replace(" ", "-")
            + ")");
    options.addOption(TimeClockJTool.COMMAND_LINE_END_DATE_SHORT,
            TimeClockJTool.COMMAND_LINE_END_DATE, true,
            "Specify the end date. Clock periods after to this will not "
            + "be included. (format "
            + TimeClockParser.DATE_FORMAT.toLowerCase() + " or "
            + TimeClockParser.DATE_TIME_FORMAT.toLowerCase().replace(" ", "-")
            + ")");
    return options;
  }

  /**
   *
   * @param date
   * @return
   * @throws java.text.ParseException
   */
  private static Date getDate(String date) throws java.text.ParseException {
    Date d = null;
    try {
      d = DATE_TIME_FORMAT.parse(date.replace("-", " "));
    } catch (java.text.ParseException pex) {
      d = DATE_FORMAT.parse(date);
    }
    System.out.println("parsing " + date + " to get " + d);
    return d;
  }
}
