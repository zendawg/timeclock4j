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
package org.statefive.timeclockj.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.statefive.timeclockj.ClockManager;
import org.statefive.timeclockj.TimeClockUtils;

/**
 *
 * @author rich
 */
public class CommandLineUtils {

  public static final String COMMAND_LINE_HELP = "help";
  public static final String COMMAND_LINE_HELP_SHORT = "h";
  public static final String COMMAND_LINE_VERSION = "version";
  public static final String COMMAND_LINE_VERSION_SHORT = "v";
  public static final String COMMAND_LINE_FILE = "file";
  public static final String COMMAND_LINE_FILE_SHORT = "f";
  public static final String COMMAND_LINE_LIST = "list";
  public static final String COMMAND_LINE_LIST_SHORT = "l";
  public static final String COMMAND_LINE_LICENSE = "license";
  public static final String COMMAND_LINE_LICENSE_SHORT = "c";

  /**
   * 
   * @return
   */
  public static Options getCommonOptions() {

    Options options = new Options();
    String description = "Print this message then quit.";
    options.addOption(CommandLineUtils.COMMAND_LINE_HELP_SHORT,
            CommandLineUtils.COMMAND_LINE_HELP, false, description);
    description = "Print version information then quit.";
    options.addOption(CommandLineUtils.COMMAND_LINE_VERSION_SHORT,
            CommandLineUtils.COMMAND_LINE_VERSION, false, description);
    description = "Print copyleft license information then quit.";
    options.addOption(CommandLineUtils.COMMAND_LINE_LICENSE_SHORT,
            CommandLineUtils.COMMAND_LINE_LICENSE, false, description);
    description = "Set the timeclock file to use. If no file is specified, "
            + "`.timelog' is read from the user's home directory. If no such file "
            + "exists, it is created.";
    options.addOption(CommandLineUtils.COMMAND_LINE_FILE_SHORT,
            CommandLineUtils.COMMAND_LINE_FILE, true, description);
    description = "List projects then quit.";
    options.addOption(CommandLineUtils.COMMAND_LINE_LIST_SHORT,
            CommandLineUtils.COMMAND_LINE_LIST, false, description);

    return options;
  }

  /**
   *
   * @param command
   * @param header
   * @param options
   * @param cmd
   * @param cla$$
   */
  public static void doBasicOptions(String command, String header, Options options,
          CommandLine cmd, Class cla$$) {
    if (cmd.hasOption(CommandLineUtils.COMMAND_LINE_HELP)
            || cmd.hasOption(CommandLineUtils.COMMAND_LINE_HELP_SHORT)) {

      HelpFormatter formatter = new HelpFormatter();
      String footer = "REPORTING BUGS: Report bugs to <info@statefive.org>\n";
      footer += "COPYRIGHT: Richard Meeking, Contributors.\n"
              + "This program comes with ABSOLUTELY NO WARRANTY; for\n"
              + "details use the `--license' or `-l' option.\n"
              + "This is free software, and you are welcome to redistribute it\n"
              + "under certain conditions; again, use the `--license' "
              + "or `-l' option to view redistribution conditions.\n";

      formatter.printHelp(command, header, options, footer);
      System.exit(0);
    }
    if (cmd.hasOption(CommandLineUtils.COMMAND_LINE_VERSION)
            || cmd.hasOption(CommandLineUtils.COMMAND_LINE_VERSION_SHORT)) {
      System.out.println(TimeClockUtils.getVersion(cla$$, "timeclockj"));
      System.exit(0);
    }
    if (cmd.hasOption(CommandLineUtils.COMMAND_LINE_LICENSE)
            || cmd.hasOption(CommandLineUtils.COMMAND_LINE_LICENSE_SHORT)) {
      try {
        InputStream is = CommandLineUtils.class.getResourceAsStream("/license.txt");
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String line = null;
        while ((line = in.readLine()) != null) {
          System.out.println(line);
        }
        in.close();
        is.close();
        System.exit(0);
      } catch (IOException ioex) {
        System.out.println("Error locating license; see GNU Public License "
                + "at http://www.gnu.org/licenses/gpl.html");
        System.exit(-1);
      }
    }
    if (cmd.hasOption(CommandLineUtils.COMMAND_LINE_LIST)
            || cmd.hasOption(CommandLineUtils.COMMAND_LINE_LIST_SHORT)) {
      File f = CommandLineUtils.getFile(cmd);
      try {
        TimeClockUtils.parseFile(f);
      } catch (IOException ioex) {
        System.err.println(ioex.getMessage());
      }
      String[] projects = ClockManager.getInstance().getProjects();
      System.out.println("Projects:");
      for (int i = 0; i < projects.length; i++) {
        System.out.println("\t" + projects[i]);
      }
      if (projects.length == 0) {
        System.out.println("\t(No projects)");
      }
      System.exit(0);
    }

  }

  /**
   * 
   * @param cmd
   * @return
   */
  public static File getFile(CommandLine cmd) {
    File timelog = null;
    if (cmd.hasOption(CommandLineUtils.COMMAND_LINE_FILE)
            || cmd.hasOption(CommandLineUtils.COMMAND_LINE_FILE_SHORT)) {
      timelog = new File(cmd.getOptionValue(CommandLineUtils.COMMAND_LINE_FILE));
    } else {
      timelog = new File(System.getProperty("user.home")
              + File.separator + ".timelog");
    }
    return timelog;
  }
}
