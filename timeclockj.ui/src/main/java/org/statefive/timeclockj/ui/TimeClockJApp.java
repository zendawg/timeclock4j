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
package org.statefive.timeclockj.ui;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.help.HelpSet;
import javax.help.JHelp;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang3.SystemUtils;
import org.statefive.timeclockj.ClockException;
import org.statefive.timeclockj.ClockManager;
import org.statefive.timeclockj.ClockModel;
import org.statefive.timeclockj.ClockPeriod;
import org.statefive.timeclockj.TimeClockUtils;
import org.statefive.timeclockj.report.ReportOutput;
import org.statefive.timeclockj.cli.CommandLineUtils;

/**
 * TimeClock AWT application for the Java System Tray API, JSR 270.
 *
 * TODO this class has been quickly hacked! Needs a lot of tidying up...
 *
 * @author rich
 *
 */
public class TimeClockJApp implements Observer {

  public final static String COMMAND_LINE_RELOAD_ON_CHANGE = "reload-on-change";
  public final static String COMMAND_LINE_RELOAD_ON_CHANGE_SHORT = "r";
  public final static String COMMAND_LINE_REPORT_DIR = "report-dir";
  public final static String COMMAND_LINE_REPORT_DIR_SHORT = "d";
  public final static String COMMAND_LINE_INTERVAL = "interval";
  public final static String COMMAND_LINE_INTERVAL_SHORT = "i";
  public final static int DEFAULT_INTERVAL = 1;
  private AbstractSystemTray systemTray;
  private ClockModel clockModel;

  @Override
  public void update(Observable o, Object arg) {
//    throw new UnsupportedOperationException("Not supported yet.");
  }


  public static void main(String[] args) {
    Options options = CommandLineUtils.getCommonOptions();

    options.addOption(COMMAND_LINE_REPORT_DIR_SHORT,
            COMMAND_LINE_REPORT_DIR, true,
            "Use the specified directory to write reports to; will be created if "
            + "it does not exist. Defaults to the user's home directory in a file "
            + "named `.timeclockj/reports'");

    options.addOption(COMMAND_LINE_INTERVAL_SHORT,
            COMMAND_LINE_INTERVAL, true,
            "Set time (seconds) for time to wait before checking if file has "
            + "changed. Default is 1. Use with --" + COMMAND_LINE_RELOAD_ON_CHANGE);

    options.addOption(COMMAND_LINE_RELOAD_ON_CHANGE_SHORT,
            COMMAND_LINE_RELOAD_ON_CHANGE, false,
            "If file has changed, reload. Use with --" + COMMAND_LINE_INTERVAL);

    CommandLineParser parser = new PosixParser();


    try {
      CommandLine cmd = parser.parse(options, args);
      String header = "Launches the timeclockj AWT system tray. Options:";
      CommandLineUtils.doBasicOptions(
              "timeclockj-gui [OPTIONS]",
              header, options, cmd, TimeClockJApp.class);
      File timelog = CommandLineUtils.getFile(cmd);
      if (!timelog.exists()) {
        timelog.createNewFile();
      }
      AbstractSystemTray systemTray = null;
      if (SystemUtils.IS_OS_LINUX) {
          systemTray = new SystemTrayGwt(timelog);
      } else {
          systemTray = new SystemTrayAwt(timelog);
      }
      TimeClockUtils.parseFile(timelog);

      try {
        if (cmd.hasOption(COMMAND_LINE_REPORT_DIR)
                || cmd.hasOption(COMMAND_LINE_REPORT_DIR_SHORT)) {
          systemTray.setReportDirectory(new File(cmd.getOptionValue(COMMAND_LINE_REPORT_DIR)));
        } else {
          systemTray.setReportDirectory(new File(
                  new File(System.getProperty("user.home")), ".timeclockj/reports"));
        }
      } catch (SecurityException sex) {
        System.err.println("Report directory `"
                + systemTray.getReportDirectory().getAbsolutePath() + "' could not be created. "
                + "Is this a file permissions problem?");
      }
      if (cmd.hasOption(COMMAND_LINE_INTERVAL)
              || cmd.hasOption(COMMAND_LINE_INTERVAL_SHORT)) {
        systemTray.setInterval(Integer.parseInt(cmd.getOptionValue(COMMAND_LINE_INTERVAL)));
      }
      if (cmd.hasOption(COMMAND_LINE_RELOAD_ON_CHANGE)
              || cmd.hasOption(COMMAND_LINE_RELOAD_ON_CHANGE_SHORT)) {
        systemTray.watch();
      }
      systemTray.systemtray();
    } catch (ParseException pex) {
      System.err.println(pex.getMessage());


    } catch (Exception ex) {
        ex.printStackTrace();
      System.err.println(ex.getMessage());
    }
  }
}
