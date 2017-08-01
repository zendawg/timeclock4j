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
  private File timelog;
  private File reportDirectory;
  private TrayIcon trayIcon;
  private PopupMenu popup;
  private MenuItem menuItemCLockOut;
  private MenuItem menuItemCLockOutWithOptions;
  private MenuItem menuItemReportCurrentProject;
  private MenuItem menuItemReportAllProjects;
  private MenuItem menuItemReload;
  private MenuItem menuItemSpecifyReportTime;
  private Menu menuItemCLockIn;
  private MenuItem menuItemCLockInWithOptions;
  private Menu menuItemReport;
  private Menu menuItemReportSpecificProject;
  private ClockModel clockModel;

  @Override
  public void update(Observable o, Object arg) {
//    throw new UnsupportedOperationException("Not supported yet.");
  }
  private int interval = DEFAULT_INTERVAL;
  private boolean userClockChange = false;

  public TimeClockJApp(File file) {
    clockModel = new ClockModel();
    this.timelog = file;
    if (!file.exists()) {
      try {
        file.createNewFile();
        trayIcon.displayMessage("File I/O",
                "File " + file.getAbsolutePath()
                + " does not exist; created successfully",
                TrayIcon.MessageType.INFO);
      } catch (IOException ioex) {
        trayIcon.displayMessage("I/O Error",
                "Unable to create file " + file.getAbsolutePath(),
                TrayIcon.MessageType.ERROR);
      }
    }
  }

  /**
   * Determines if the system supports system tray.
   *
   * @return {@code true} if system trays are supported; {@code false}
   * otherwise.
   */
  public static boolean isSupported() {
    return SystemTray.isSupported();
  }

  public int getInterval() {
    return interval;
  }

  public void setInterval(int interval) {
    if (interval > 0) {
      this.interval = interval;
    }
  }

  public void setReportDirectory(File reportDir) throws SecurityException {
    this.reportDirectory = reportDir;
    if (!reportDir.exists()) {
      reportDir.mkdirs();
    }
  }

  public File getReportDirectory() {
    return this.reportDirectory;
  }

  public void watch() {
    Thread t = new Thread(new Runnable() {

      @Override
      public void run() {
        long lastModified = timelog.lastModified();
        while (true) {
          if (timelog.lastModified() > lastModified) {
            lastModified = timelog.lastModified();
            try {
              if (!userClockChange) {
                TimeClockUtils.reloadFile(timelog);
                trayIcon.displayMessage("File I/O",
                        "File changed on disk; changes reloaded",
                        TrayIcon.MessageType.INFO);
                refreshComponents();
              }
              userClockChange = false;
            } catch (IOException ioex) {
              JOptionPane.showMessageDialog(null, ioex.getMessage(),
                      "I/O Error", JOptionPane.ERROR_MESSAGE);
            } catch (ClockException cex) {
              JOptionPane.showMessageDialog(null, cex.getMessage(),
                      "Clock Error", JOptionPane.ERROR_MESSAGE);
            }
          }
          try {
            Thread.sleep(interval * 1000);
          } catch (InterruptedException iex) {
          }
        }
      }
    });
    t.start();
  }

  /**
   * Utility for loading the system tray.
   *
   * @throws Exception
   */
  public void systemtray() throws Exception {

    if (SystemTray.isSupported()) {

      final SystemTray tray = SystemTray.getSystemTray();
      String path = "/icons/timeclockj-in.png";
      InputStream in = TimeClockJApp.class.getResourceAsStream(path);
      Image image = ImageIO.read(in);

      MouseListener mouseListener = new MouseListener() {

        @Override
        public void mouseClicked(MouseEvent e) {
//          System.out.println("Tray Icon - Mouse clicked!");
        }

        @Override
        public void mouseEntered(MouseEvent e) {
//          System.out.println("Tray Icon - Mouse entered!");
        }

        @Override
        public void mouseExited(MouseEvent e) {
//          System.out.println("Tray Icon - Mouse exited!");
        }

        @Override
        public void mousePressed(MouseEvent e) {
//          System.out.println("Tray Icon - Mouse pressed!");
        }

        @Override
        public void mouseReleased(MouseEvent e) {
//          System.out.println("Tray Icon - Mouse released!");
//          popup.show(e.getComponent(), e.getX(), e.getY());
        }
      };

      ActionListener exitListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          System.out.println("Exiting...");
          System.exit(0);
        }
      };

      ActionListener aboutListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          JFrame frameAbout = new JFrame("About timeclockj");
          frameAbout.getContentPane().setLayout(new BorderLayout());
          frameAbout.getContentPane().add(getAboutPanel());
          frameAbout.pack();
          frameAbout.setSize(450, 350);
          Utils.centerFrame(frameAbout);
          frameAbout.setVisible(true);
        }
      };
      ActionListener clockOutListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          try {
            TimeClockUtils.clockOut(timelog, null);
            userClockChange = true;
            refreshComponents();
          } catch (IOException ioex) {
            JOptionPane.showMessageDialog(null,
                    ioex.getMessage(), "I/O Error",
                    JOptionPane.ERROR_MESSAGE);
          } catch (ClockException cex) {
            JOptionPane.showMessageDialog(null,
                    "The project is not clocked-in.", "Error Clocking Out",
                    JOptionPane.ERROR_MESSAGE);
          }
        }
      };
      ActionListener clockOutWithDescriptionListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          // show dialog with date, time & description options
          JPanel panelOptions = new ClockOptionsPanel().getClockOutPanel(clockModel);
          JOptionPane optionPane = new JOptionPane();
          JButton buttonOK = new JButton("OK");
          JButton buttonCancel = new JButton("Cancel");
          optionPane.setMessage(panelOptions);
          optionPane.setOptions(new JButton[]{buttonOK, buttonCancel});

          final JDialog dialog = optionPane.createDialog(null, "Clock-out of '"
                  + ClockManager.getInstance().getCurrentProject() + "'");
          buttonOK.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
              try {
                doClock();
              } catch (IOException ioex) {
                JOptionPane.showMessageDialog(null,
                        ioex.getMessage(), "I/O Error",
                        JOptionPane.ERROR_MESSAGE);
              } catch (ClockException cex) {
                JOptionPane.showMessageDialog(null,
                        "The project is not clocked-in.", "Error Clocking Out",
                        JOptionPane.ERROR_MESSAGE);
              } finally {
                dialog.dispose();
              }
            }
          });
          buttonCancel.addActionListener(
                  new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                      dialog.dispose();
                    }
                  });
          optionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
          dialog.setVisible(true);
        }
      };
      ActionListener clockInWithOptionsListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          // show dialog with date, time & project choice options
          JButton buttonOK = new JButton("OK");
          JButton buttonCancel = new JButton("Cancel");
          JPanel panelOptions = new ClockOptionsPanel().getClockInPanel(clockModel);
          JOptionPane optionPane = new JOptionPane();
          optionPane.setMessage(panelOptions);
          optionPane.setOptions(new JButton[]{buttonOK, buttonCancel});
          optionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
          final JDialog dialog = optionPane.createDialog(null, "Clock In");

          buttonOK.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
              try {
                doClock();

                MenuItem menuItem = new MenuItem(ClockManager.getInstance().getCurrentProject());
                menuItem.addActionListener(new ProjectReportListener());
              } catch (IOException ioex) {
                JOptionPane.showMessageDialog(null,
                        ioex.getMessage(), "I/O Error",
                        JOptionPane.ERROR_MESSAGE);
              } catch (ClockException cex) {
                JOptionPane.showMessageDialog(null,
                        "The project is not clocked-in.", "Error Clocking Out",
                        JOptionPane.ERROR_MESSAGE);
              } finally {
                dialog.dispose();
              }
            }
          });
          buttonCancel.addActionListener(
                  new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                      dialog.dispose();
                    }
                  });
          dialog.setVisible(
                  true);
        }
      };

      popup = new PopupMenu();
      MenuItem defaultItem = new MenuItem("Exit");
      defaultItem.addActionListener(exitListener);
      menuItemReload = new MenuItem("Reload File");
      menuItemReload.addActionListener(new ReloadFileListener());
      menuItemCLockOut = new MenuItem("Clock Out");
      menuItemCLockOut.addActionListener(clockOutListener);
      menuItemCLockOutWithOptions =
              new MenuItem("Clock Out With Options...");
      menuItemCLockOutWithOptions.addActionListener(
              clockOutWithDescriptionListener);
      menuItemCLockIn = new Menu("Clock In");
      menuItemCLockInWithOptions = new MenuItem("Clock In With Options...");
      menuItemCLockInWithOptions.addActionListener(clockInWithOptionsListener);

      // ############
      popup.add(menuItemCLockOut);
      popup.add(menuItemCLockOutWithOptions);
      popup.add(menuItemCLockIn);
      popup.add(menuItemCLockInWithOptions);

      popup.addSeparator();
      menuItemReport = new Menu("Report");
      menuItemReportSpecificProject = new Menu("Project");

      MenuItem menuItem = new MenuItem(ClockManager.DEFAULT_PROJECT_STRING);
      menuItemReportSpecificProject.add(menuItem);
      menuItem.addActionListener(new ProjectReportListener());
      for (String project : ClockManager.getInstance().getProjects()) {
        if (!ClockManager.DEFAULT_PROJECT_STRING.equals(project)) {
          // added default project in previous step:
          menuItem = new MenuItem(project);
          menuItemReportSpecificProject.add(menuItem);
          menuItem.addActionListener(new ProjectReportListener());
        }
      }
      menuItemSpecifyReportTime = new MenuItem("Specify Report Time");
      menuItemSpecifyReportTime.addActionListener(new SpecifyTimeReportListener());
      menuItemReportCurrentProject = new MenuItem("Current Project");
      menuItemReportCurrentProject.addActionListener(new ProjectReportListener());
      menuItemReportAllProjects = new MenuItem("All Projects");
      menuItemReportAllProjects.addActionListener(new ProjectAllReportsListener());

      menuItemReport.add(menuItemReportCurrentProject);
      menuItemReport.add(menuItemReportSpecificProject);
      menuItemReport.add(menuItemReportAllProjects);
      menuItemReport.add(menuItemSpecifyReportTime);

      popup.add(menuItemReport);
      popup.addSeparator();
      popup.add(menuItemReload);
      popup.addSeparator();

      MenuItem menuItemHelp = new MenuItem("Help Contents");
      menuItemHelp.addActionListener(new HelpListener());
      popup.add(menuItemHelp);

      MenuItem menuItemAbout = new MenuItem("About");
      popup.add(menuItemAbout);
      menuItemAbout.addActionListener(aboutListener);
      popup.addSeparator();
      popup.add(defaultItem);

      trayIcon = new TrayIcon(image, "Tray Demo", popup);

      trayIcon.setImageAutoSize(true);
      trayIcon.addMouseListener(mouseListener);

      try {
        tray.add(trayIcon);
        refreshComponents();
      } catch (AWTException e) {
        System.err.println("TrayIcon could not be added.");
      }

    } else {
      //  System Tray is not supported
      System.err.println("TrayIcon is not supported.");
    }
  }

  private void doClock() throws IOException, ClockException {
    Calendar calDate = clockModel.getCalendarDate();
    Calendar calTime = clockModel.getCalendarTime();
    if (calDate == null) {
      calDate = clockModel.getCalendarDate();
    }
    if (calTime == null) {
      calTime = clockModel.getCalendarTime();
    }
    clockModel.setCalendarDateTime(calDate, calTime);
    try {
      if (ClockManager.getInstance().isClockedIn()) {
        if (calDate != null || calTime != null) {
          clockModel.setCalendarDateTime(calDate, calTime);
          TimeClockUtils.clockOut(timelog, clockModel.getDescription(), clockModel.getCalendarDateTime().getTime().getTime());
        } else {
          TimeClockUtils.clockOut(timelog, clockModel.getDescription());
        }
      } else {
        if (calDate != null || calTime != null) {
          TimeClockUtils.clockIn(timelog, clockModel.getProject(), clockModel.getCalendarDateTime().getTime().getTime());
        } else {
          TimeClockUtils.clockIn(timelog, clockModel.getProject());
        }
      }
    } finally {
      refreshComponents();
    }
  }

  private void refreshComponents() {
    if (ClockManager.getInstance().isClockedIn()) {
      String currentProject = ClockManager.getInstance().getCurrentProject();
      List<ClockPeriod> times = ClockManager.getInstance().getClockPeriods(
                      currentProject);
      ClockPeriod lastClockTime = times.get(times.size() -1 );
      String dateTime = ClockManager.getInstance().formatDate(
              new Date(lastClockTime.getClockInTime()));
      String[] formattedDateTime = dateTime.split(" ");
      trayIcon.setToolTip("Current project: "
              + currentProject + " since " + formattedDateTime[1].substring(
                      0, 5)
              + ", " + formattedDateTime[0]
      );
      menuItemCLockOut.setEnabled(true);
      menuItemCLockOutWithOptions.setEnabled(true);
      menuItemCLockOut.setLabel("Clock Out '" + currentProject + "'");
      menuItemCLockIn.setEnabled(false);
      menuItemCLockInWithOptions.setEnabled(false);
      menuItemReportCurrentProject.setLabel(currentProject);
      menuItemReportCurrentProject.setEnabled(true);
      setIcon("/icons/timeclockj-out.png");

    } else {
      trayIcon.setToolTip("Currently not clocked-in to any projects.");
      menuItemCLockOut.setEnabled(false);
      menuItemCLockOutWithOptions.setEnabled(false);
      menuItemCLockOut.setLabel("Clock Out");
      menuItemCLockIn.setEnabled(true);
      menuItemCLockInWithOptions.setEnabled(true);
      menuItemReportCurrentProject.setLabel("Current Project");
      menuItemReportCurrentProject.setEnabled(false);
      setIcon("/icons/timeclockj-in.png");
    }
    refreshClockInMenuItems();
  }

  private void refreshClockInMenuItems() {

    menuItemCLockIn.removeAll();

    MenuItem menuItemDefaultProject = new MenuItem(ClockManager.DEFAULT_PROJECT_STRING);
    ClockInToProjectListener listener =
            new ClockInToProjectListener(null);
    menuItemDefaultProject.addActionListener(listener);
    menuItemCLockIn.add(menuItemDefaultProject);


    for (String project : ClockManager.getInstance().getProjects()) {
      if (!ClockManager.DEFAULT_PROJECT_STRING.equals(project)) {
        // just added default project in previous step
        MenuItem menuItem = new MenuItem(project);
        listener =
                new ClockInToProjectListener(project);
        menuItem.addActionListener(listener);
        menuItemCLockIn.add(menuItem);


      }
    }
    MenuItem menuItemNewProject = new MenuItem("New Project...");
    menuItemCLockIn.add(menuItemNewProject);

    ActionListener newProjectListener = new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          String project = JOptionPane.showInputDialog("Enter project name");
          if (project != null) {
            TimeClockUtils.clockIn(timelog, project);
            refreshComponents();

            MenuItem menuItem = new MenuItem(project);
            menuItemReportSpecificProject.add(menuItem);
            menuItem.addActionListener(new ProjectReportListener());
          }
        } catch (IOException ioex) {
          JOptionPane.showMessageDialog(null,
                  ioex.getMessage(), "I/O Error",
                  JOptionPane.ERROR_MESSAGE);


        } catch (ClockException cex) {
          JOptionPane.showMessageDialog(null,
                  "The project is not clocked-in.", "Error Clocking Out",
                  JOptionPane.ERROR_MESSAGE);
        }
      }
    };
    menuItemNewProject.addActionListener(newProjectListener);
  }

  /**
   * 
   * @param path
   */
  private void setIcon(String path) {
    InputStream in = TimeClockJApp.class.getResourceAsStream(path);
    try {
      Image image = ImageIO.read(in);
      trayIcon.setImage(image);
    } catch (IOException ioex) {
    }
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
      TimeClockJApp app = new TimeClockJApp(timelog);
      TimeClockUtils.parseFile(timelog);

      try {
        if (cmd.hasOption(COMMAND_LINE_REPORT_DIR)
                || cmd.hasOption(COMMAND_LINE_REPORT_DIR_SHORT)) {
          app.setReportDirectory(new File(cmd.getOptionValue(COMMAND_LINE_REPORT_DIR)));
        } else {
          app.setReportDirectory(new File(
                  new File(System.getProperty("user.home")), ".timeclockj/reports"));
        }
      } catch (SecurityException sex) {
        System.err.println("Report directory `"
                + app.getReportDirectory().getAbsolutePath() + "' could not be created. "
                + "Is this a file permissions problem?");
      }
      if (cmd.hasOption(COMMAND_LINE_INTERVAL)
              || cmd.hasOption(COMMAND_LINE_INTERVAL_SHORT)) {
        app.setInterval(Integer.parseInt(cmd.getOptionValue(COMMAND_LINE_INTERVAL)));
      }
      if (cmd.hasOption(COMMAND_LINE_RELOAD_ON_CHANGE)
              || cmd.hasOption(COMMAND_LINE_RELOAD_ON_CHANGE_SHORT)) {
        app.watch();
      }
      app.systemtray();
    } catch (ParseException pex) {
      System.err.println(pex.getMessage());


    } catch (Exception ex) {
      System.err.println(ex.getMessage());
    }
  }

  private class HelpListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
      JHelp helpViewer = null;
      try {
        // Get the classloader of this class.
        ClassLoader cl = TimeClockJApp.class.getClassLoader();
        // Use the findHelpSet method of HelpSet to create a URL referencing the helpset file.
        // Note that in this example the location of the helpset is implied as being in the same
        // directory as the program by specifying "jhelpset.hs" without any directory prefix,
        // this should be adjusted to suit the implementation.
        URL url = HelpSet.findHelpSet(cl, "doc/jhelpset.hs");
        // Create a new JHelp object with a new HelpSet.
        helpViewer = new JHelp(new HelpSet(cl, url));
        // Set the initial entry point in the table of contents.
        helpViewer.setCurrentID("Top");
      } catch (Exception ex) {
        System.err.println("API Help Set not found: " + ex.getMessage());
      }

      // Create a new frame.
      JFrame frame = new JFrame();
      // Set it's size.

      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      frame.setSize(screenSize.width - 200, screenSize.height - 200);
      // Add the created helpViewer to it.
      frame.getContentPane().add(helpViewer);
      // Set a default close operation.
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      Utils.centerFrame(frame);
      // Make the frame visible.
      frame.setVisible(true);

    }
  }

  private class ClockInToProjectListener implements ActionListener {

    private String project;

    public ClockInToProjectListener(String project) {
      this.project = project;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      try {
        TimeClockUtils.clockIn(timelog, project);
        userClockChange = true;
        refreshComponents();
      } catch (IOException ioex) {
        JOptionPane.showMessageDialog(null,
                ioex.getMessage(), "I/O Error",
                JOptionPane.ERROR_MESSAGE);
      } catch (ClockException cex) {
        JOptionPane.showMessageDialog(null,
                "The project is not clocked-in.", "Error Clocking Out",
                JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private class ReloadFileListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
      try {
        TimeClockUtils.reloadFile(timelog);
        trayIcon.displayMessage("File I/O",
                "File successfully reloaded",
                TrayIcon.MessageType.INFO);
        refreshComponents();
      } catch (IOException ioex) {
        trayIcon.displayMessage("I/O Error",
                ioex.getMessage(),
                TrayIcon.MessageType.ERROR);
      } catch (ClockException cex) {
        trayIcon.displayMessage("Clock Error",
                cex.getMessage(),
                TrayIcon.MessageType.ERROR);
      }
    }
  }
  JEditorPane textPane;

  private class ProjectAllReportsListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
      writeReports(ClockManager.getInstance().getProjects(), null, null);
    }
  }

  private class ProjectReportListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
      // TODO repeated code! See ProjectReportsAllListener must be cleaned up!
      MenuItem menuItem = (MenuItem) e.getSource();
      String project = menuItem.getLabel();
      writeReports(new String[]{project}, null, null);
    }
  }

  private class SpecifyTimeReportListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
      // show dialog with date, time & description options
      final ReportTimePanel panel = new ReportTimePanel();
      JPanel panelOptions = panel.getReportTimePanel();
      JOptionPane optionPane = new JOptionPane();
      JButton buttonOK = new JButton("OK");
      JButton buttonCancel = new JButton("Cancel");
      optionPane.setMessage(panelOptions);
      optionPane.setOptions(new JButton[]{buttonOK, buttonCancel});

      final JDialog dialog = optionPane.createDialog(null,
              "Specify Report Time");
      buttonOK.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          try {
            String project = panel.getSelectedProject();
            writeReports(new String[]{project}, panel.getStartDate(),
                    panel.getEndDate());
          } finally {
            dialog.dispose();
          }
        }
      });
      buttonCancel.addActionListener(
              new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                  dialog.dispose();
                }
              });
      optionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
      dialog.setVisible(true);
    }
  }

  private void writeReports(String[] reports, Date start, Date end) {

    File report = null;


    try {
      report = File.createTempFile("timeclockj-report.", ".html",
              getReportDirectory());
      FileOutputStream fos = new FileOutputStream(report);
      fos.write(
              new ReportOutput().getHtmlOutput(reports, start, end).getBytes());
      fos.flush();
      fos.close();
      URI uri = report.toURI();
      java.awt.Desktop.getDesktop().browse(uri);
      trayIcon.displayMessage("Project Report",
              "New report opening in browser",
              TrayIcon.MessageType.INFO);


    } catch (IOException ioex) {
      if (report != null) {
        ioex.printStackTrace();
        trayIcon.displayMessage("Error",
                "Report unable to open in browser: saved in "
                + report.getAbsolutePath(), TrayIcon.MessageType.ERROR);


      } else {
        ioex.printStackTrace();
        trayIcon.displayMessage("Error",
                "Report unable to open in browser.",
                TrayIcon.MessageType.ERROR);


      }
    }
  }

  public JPanel getAboutPanel() {
    JPanel panelAbout = new JPanel(new BorderLayout());
    panelAbout.setPreferredSize(new Dimension(400, 300));
    JTabbedPane tabbedPane = new JTabbedPane();
    JEditorPane editorPaneAbout = new JEditorPane("text/html", "");
    editorPaneAbout.setText(AboutHelper.getAboutHtml().replace(AboutHelper.DOLLAR_VERSION,
            TimeClockUtils.getVersion(TimeClockJApp.class, null)));
    editorPaneAbout.addHyperlinkListener(
            new HyperlinkListener() {

              @Override
              public void hyperlinkUpdate(HyperlinkEvent e) {
                try {
                  if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    java.awt.Desktop.getDesktop().browse(URI.create(
                            "http://www.statefive.org"));
                    trayIcon.displayMessage("State Five",
                            "www.statefive.org opening in browser",
                            TrayIcon.MessageType.INFO);
                  }
                } catch (IOException ioex) {
                  // TODO throw
                }
              }
            });
    editorPaneAbout.setEditable(
            false);
    tabbedPane.addTab(
            "About", editorPaneAbout);
    tabbedPane.addTab(
            "License", getLicensePanel());
    tabbedPane.addTab(
            "Contributors", getContributorPanel());
//    tabbedPane.addTab("Contributors", new JPanel());
//    tabbedPane.addTab("Third Party", new JPanel());
    panelAbout.add(tabbedPane);




    return panelAbout;
  }
//  /http://packjacket.sourceforge.net/

  /**
   * Gets the panel showing the license for this software.
   *
   * @return a panel, if it could be loaded; <tt>null</tt> otherwise.
   */
  public JPanel getLicensePanel() {
    JPanel panelLicense = new JPanel(new BorderLayout());
    JTextArea textAreaLicense = new JTextArea(20, 40);
    InputStream is = TimeClockJApp.class.getResourceAsStream("/license.txt");
    BufferedInputStream bis = new BufferedInputStream(is);
    StringBuilder buffer = new StringBuilder();
    int read = -1;
    try {
      while ((read = bis.read()) != -1) {
        buffer.append((char) read);
      }
      textAreaLicense.setText(buffer.toString());
      textAreaLicense.setEditable(false);
      textAreaLicense.setCaretPosition(0);
    } catch (IOException ioex) {
      ioex.printStackTrace();
    }

    final String uri = "http://www.gnu.org/licenses/gpl.html";
    JLabel labelLicenseUri = new JLabel(
            "<html><body><p><br><p align=\"justify\"><a href=\"" + uri + "\">"
            + " View in browser</a> (requires internet connection)</p><br></body></html>");

    labelLicenseUri.addMouseListener(new MouseListener() {

      @Override
      public void mouseClicked(MouseEvent e) {
      }

      @Override
      public void mousePressed(MouseEvent e) {
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        try {
          java.awt.Desktop.getDesktop().browse(URI.create(uri));
          trayIcon.displayMessage("License Information",
                  "GPL License V3 has been opened in a browser",
                  TrayIcon.MessageType.INFO);
        } catch (IOException ioex) {
          ioex.printStackTrace();
        }
      }

      @Override
      public void mouseEntered(MouseEvent e) {
      }

      @Override
      public void mouseExited(MouseEvent e) {
      }
    });

    panelLicense.add(BorderLayout.NORTH, labelLicenseUri);
    panelLicense.add(BorderLayout.CENTER,
            new JScrollPane(textAreaLicense));




    return panelLicense;
  }

  /**
   * Gets the panel showing the license for this software.
   *
   * @return a panel, if it could be loaded; <tt>null</tt> otherwise.
   */
  public JPanel getContributorPanel() {
    JPanel panelLicense = new JPanel(new BorderLayout());
    JEditorPane editorPaneContributors = new JEditorPane("text/html", "");
    editorPaneContributors.setEditable(false);
    editorPaneContributors.setText(
            AboutHelper.getContributorHtml());
    editorPaneContributors.addHyperlinkListener(new HyperlinkListener() {

      @Override
      public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
          try {
            java.awt.Desktop.getDesktop().browse(e.getURL().toURI());
            trayIcon.displayMessage("Third Party Software",
                    "URL " + e.getURL().toString() + " has been opened in a browser.",
                    TrayIcon.MessageType.INFO);


          } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Could not open the specified URL.", "URL Error",
                    JOptionPane.ERROR_MESSAGE);


          }
        }
      }
    });
    panelLicense.add(BorderLayout.CENTER,
            new JScrollPane(editorPaneContributors));


    return panelLicense;

  }
}
