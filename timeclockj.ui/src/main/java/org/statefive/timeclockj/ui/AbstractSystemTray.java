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

import org.statefive.timeclockj.ui.listener.HelpListener;
import org.statefive.timeclockj.ui.listener.ProjectReportListener;
import org.statefive.timeclockj.ui.listener.ClockInToProjectListener;
import java.awt.Image;
import java.awt.Point;
import java.awt.SystemTray;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.statefive.timeclockj.ClockException;
import org.statefive.timeclockj.ClockManager;
import org.statefive.timeclockj.ClockModel;
import org.statefive.timeclockj.ClockPeriod;
import org.statefive.timeclockj.TimeClockUtils;
import org.statefive.timeclockj.report.ReportOutput;
import org.statefive.timeclockj.ui.listener.AboutListener;
import org.statefive.timeclockj.ui.listener.ClockInWithOptionsListener;
import org.statefive.timeclockj.ui.listener.ClockOutListener;
import org.statefive.timeclockj.ui.listener.ClockOutWithDescriptionListener;
import org.statefive.timeclockj.ui.listener.ExitListener;
import org.statefive.timeclockj.ui.listener.NewProjectListener;
import org.statefive.timeclockj.ui.listener.ProjectAllReportsListener;
import org.statefive.timeclockj.ui.listener.ReloadFileListener;
import org.statefive.timeclockj.ui.listener.SpecifyTimeReportListener;

/**
 * Abstract base class for concrete implementations to follow.
 * <p>
 * Although most OSs will use the default Java system tray, some are not fully
 * supported, or supported at all; for example, support for the Java system
 * tray in recent versions of Ubuntu Linux do not work. This class is designed
 * to bridge the gap between Java and non-Java supported versions of system
 * trays.
 * </p>
 *
 * @author rich
 *
 */
public abstract class AbstractSystemTray {

    /**
     * CLI long option for relaoding time clock files when changed.
     */
    public final static String COMMAND_LINE_RELOAD_ON_CHANGE = "reload-on-change";
    /**
     * CLI short option for reload file on change.
     */
    public final static String COMMAND_LINE_RELOAD_ON_CHANGE_SHORT = "r";
    /**
     * CLI long option for directory to write reports to.
     */
    public final static String COMMAND_LINE_REPORT_DIR = "report-dir";
    /**
     * CLI short option for report directory.
     */
    public final static String COMMAND_LINE_REPORT_DIR_SHORT = "d";
    /**
     * CLI long option for how often to check the file for changes (seconds).
     */
    public final static String COMMAND_LINE_INTERVAL = "interval";
    /**
     * CLI short option for interval (seconds).
     */
    public final static String COMMAND_LINE_INTERVAL_SHORT = "i";
    /**
     * Default interval (seconds) to check for changes if none specified.
     */
    public final static int DEFAULT_INTERVAL = 1;
    /**
     * Interval in seconds to watch for changes to the time clock file.
     */
    private int interval = DEFAULT_INTERVAL;
    /**
     * 
     */
    private boolean userClockChange = false;
    /**
     * Time log file to read/write.
     */
    private File timelog;
    /**
     * Directory where to write time clock file reports to.
     */
    private File reportDirectory;
    /**
     * 
     */
    private ClockModel clockModel;
    /**
     * Point/location of the (approximate) location of the system tray icon.
     */
    private Point point;

    /**
     * Listener for the "About"  dialog.
     */
    protected AboutListener aboutListener;
    /**
     * Listener for clocking in to projects.
     */
    protected ClockInToProjectListener clockInToProjectListener;
    /**
     * Listener for clocking in with options.
     */
    protected ClockInWithOptionsListener clockInWithOptionsListener;
    /**
     * Listener for clocking out of projects.
     */
    protected ClockOutListener clockOutListener;
    /**
     * Listener for clocking out with options.
     */
    protected ClockOutWithDescriptionListener clockOutWithDescriptionListener;
    /**
     * Listener for quitting the application.
     */
    protected ExitListener exitListener;
    /**
     * Listener for showing the help application.
     */
    protected HelpListener helpListener;
    /**
     * Listener for creating new projects.
     */
    protected NewProjectListener newProjectListener;
    /**
     * Listener for producing reports for all projects.
     */
    protected ProjectAllReportsListener projectAllReportsListener;
    /**
     * Listener for producing a report for a specific project.
     */
    protected ProjectReportListener projectReportListener;
    /**
     * Listener for reloading clock files.
     */
    protected ReloadFileListener reloadFileListener;
    /**
     * Listener for producing reports for a given project at a specific time
     * period.
     */
    protected SpecifyTimeReportListener specifyTimeReportListener;

    /**
     * Create a new system tray with the specified clock file to read.
     * 
     * @param file non-{@code null} file in time clock format to parse; if the
     * file does not exist, it is created.
     */
    public AbstractSystemTray(File file) {
        clockModel = new ClockModel();
        this.timelog = file;
        if (!file.exists()) {
            try {
                file.createNewFile();
                this.displayMessage("File I/O",
                        "File " + file.getAbsolutePath()
                        + " does not exist; created successfully",
                        IconMessageTypeEnum.INFO, this.getPoint());
            } catch (IOException ioex) {
                JOptionPane.showMessageDialog(null, "I/O Error",
                        "Unable to create or read file " + file.getName(), 
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }
        aboutListener = new AboutListener();
        clockInToProjectListener = new ClockInToProjectListener("");
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
    
    /**
     * Main call to launch the system tray - implementor specific.
     */
    public abstract void systemtray() throws Exception;

    /**
     * For implementors to decide how to display a message when something
     * changes in the application. Implementors do not have to support
     * the implementation but should not throw an
     * {@code UnsupportedOperationException} but should instead do nothing.
     * 
     * @param caption non-{@code null} title of the message.
     * 
     * @param message non-{@code null} to display.
     * 
     * @param messageType the type of message to display.
     * 
     * @param point can be {@code null} depending on implementation; for system
     * trays that do not specify a location, this point can be used to determine
     * the location of the displayed message.
     */
    public abstract void displayMessage(String caption, String message,
            IconMessageTypeEnum messageType, Point point);

    /**
     * Set the status of the system tray - in AWT, this comes in the form
     * of a popup appearing at the location of the system tray that disappears
     * after a short while.
     * 
     * @param status non-{@code null} status message to display. 
     */
    public abstract void setStatus(String status);

    /**
     * Refresh menu items after a clock-in or out, setting menu items
     * appropriately and updating menu text.
     * 
     * @param itemClockOut if {@code true}, ensure the clock-out menu options
     * are enabled; otherwise, disable them.
     * 
     * @param clockOutText the text for the clock-out (if required); may be
     * {@code null}.
     * 
     * @param itemClockIn if {@code true}, ensure the clock-in menu options
     * are enabled; otherwise, disable them.
     * 
     * @param itemReportCurrentProject if {@code true}, ensure the current
     * project section of the reporting menu items is enabled; otherwise,
     * disable it.
     * 
     * @param currentProjectText the name of the current project.
     */
    public abstract void refreshMenuItems(boolean itemClockOut,
            String clockOutText, boolean itemClockIn,
            boolean itemReportCurrentProject, String currentProjectText);

    /**
     * Add a new project to the reporting menu with the specified name.
     * 
     * @param name non-{@code null} project name to add to the reporting
     * menu item set.
     */
    public abstract void addNewProjectReportMenuItem(String name);

    /**
     * Update the system tray, updating necessary components.
     */
    public abstract void refreshClockInMenuItems();

    /**
     * Set the system tray to use the specified icon.
     * 
     * @param image non-{@code null} image to use.
     */
    public abstract void setTrayIconImage(Image image);
    
    /**
     * Open a browser with the specified (existing) page.
     * 
     * @param uri non-{@code null} URI to open.
     */
    public abstract void launchBrowser(URI uri);

    /**
     * Get the interval in seconds for checking if there are changes in the
     * clock file.
     * 
     * @return the interval that has been set; if no call to
     * {@link #setInterval(int)} has been made, the default interval
     * {@link #DEFAULT_INTERVAL} is returned.
     */
    public int getInterval() {
        return interval;
    }

    /**
     * Set the interval.
     * 
     * @param interval positive (greater than 0) integer; anything less than
     * this is ignored and the interval remains unchanged.
     */
    public void setInterval(int interval) {
        if (interval > 0) {
            this.interval = interval;
        }
    }

    /**
     * Get the timelog file.
     * 
     * @return the file used for the timelog; may be {@code null} if not yet
     * set.
     */
    public File getTimelog() {
        return timelog;
    }

    /**
     * Set the timelog file to use.
     * 
     * @param timelog non-{@code null} file in timelog format.
     */
    public void setTimelog(File timelog) {
        this.timelog = timelog;
    }

    /**
     * Set the report directorty; will be created if it does not exist.
     * 
     * @param reportDir non-{@code null} directory to set.
     * 
     * @throws SecurityException 
     */
    public void setReportDirectory(File reportDir) throws SecurityException {
        this.reportDirectory = reportDir;
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }
    }

    /**
     * Get the report directory.
     * 
     * @return gets the report directory; will be {@code null} if not yet set.
     */
    public File getReportDirectory() {
        return this.reportDirectory;
    }

    /**
     * Determine if the user has made a change to the timelog file (as opposed
     * to a Dropbox/sync change).
     * 
     * @return {@code true} if the user made the change to the timelog file;
     * {@code false} otherwise.
     */
    public boolean isUserClockChange() {
        return userClockChange;
    }

    /**
     * Set if the clock file has been modified by the user or not.
     * 
     * @param userClockChange {@code true} if the user made a change to the
     * clock file; {@code false} otherwise.
     */
    public void setUserClockChange(boolean userClockChange) {
        this.userClockChange = userClockChange;
    }

    /**
     * 
     * @return 
     */
    public ClockModel getClockModel() {
        return clockModel;
    }

    /**
     * 
     * @param clockModel 
     */
    public void setClockModel(ClockModel clockModel) {
        this.clockModel = clockModel;
    }

    /**
     * Get the point near the location of the system tray; up to implementors
     * to decide how to use, only if they require it for implementation.
     * 
     * @return the point, if it has been set; {@code null} otherwise.
     */
    public Point getPoint() {
        return point;
    }

    /**
     * Sets the point; upda to implementors to decide on if it is required or
     * not.
     * 
     * @param point the non-{@code null} point to set.
     */
    public void setPoint(Point point) {
        this.point = point;
    }

    /**
     * New thread to watch the timelog fie and make changes appropriately.
     */
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
                                AbstractSystemTray.this.displayMessage("File I/O",
                                        "File changed on disk; changes reloaded",
                                        IconMessageTypeEnum.INFO,
                                        AbstractSystemTray.this.getPoint());
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
     * Perform a clock-in or -out depending on the status of the clock manager.
     * 
     * @throws IOException if there was a problem reading or writing the
     * timelog file.
     * 
     * @throws ClockException if a clock-in happens when already clocked-in,
     * or vice-versa.
     */
    public void doClock() throws IOException, ClockException {
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
            } else if (calDate != null || calTime != null) {
                TimeClockUtils.clockIn(timelog, clockModel.getProject(), clockModel.getCalendarDateTime().getTime().getTime());
            } else {
                TimeClockUtils.clockIn(timelog, clockModel.getProject());
            }
        } finally {
            try {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        refreshComponents();
                    }
                });

            } catch (Exception ex) {

            }
        }
    }

    /**
     * Refresh components based on if a clock-in or -out was performed.
     */
    public void refreshComponents() {
        if (ClockManager.getInstance().isClockedIn()) {
            String currentProject = ClockManager.getInstance().getCurrentProject();
            List<ClockPeriod> times = ClockManager.getInstance().getClockPeriods(
                    currentProject);
            ClockPeriod lastClockTime = times.get(times.size() - 1);
            String dateTime = ClockManager.getInstance().formatDate(
                    new Date(lastClockTime.getClockInTime()));
            String[] formattedDateTime = dateTime.split(" ");
//            this.setStatus("Current project: "
//                    + currentProject + " since " + formattedDateTime[1].substring(
//                            0, 5)
//                    + ", " + formattedDateTime[0]
//            );

            this.refreshMenuItems(true, "Clock Out '" + currentProject + "'",
                    false, true, currentProject);
            setIcon("/icons/timeclockj-out.png");

        } else {
//            this.setStatus("Currently not clocked-in to any projects.");
            this.refreshMenuItems(false, "Clock Out", true, false,
                    "Current Project");
            setIcon("/icons/timeclockj-in.png");
        }
//        refreshClockInMenuItems();
    }

    /**
     * Set the icon for the system tray.
     * 
     * @param path non-{@code null} path of an existing file icon.
     */
    private void setIcon(String path) {
        InputStream in = AbstractSystemTray.class.getResourceAsStream(path);
        try {
            Image image = ImageIO.read(in);
            setTrayIconImage(image);
        } catch (IOException ioex) {
        }
    }

    /**
     * Write the specified reports.
     * 
     * @param reports non-{@code null} array of reports to write - these must
     * be the textual names of projects already in the time clock file.
     * 
     * @param start start date to produce the reports for, if required; may be
     * {@code null}.
     * 
     * @param end end date to produce the reports for, if required; may be
     * {@code null}.
     */
    public void writeReports(String[] reports, Date start, Date end) {

        File report = null;

        try {
            report = File.createTempFile("timeclockj-report.", ".html",
                    getReportDirectory());
            FileOutputStream fos = new FileOutputStream(report);
            fos.write(
                    new ReportOutput().getHtmlOutput(reports, start, end).getBytes());
            fos.flush();
            fos.close();
            final URI uri = report.toURI();
            this.launchBrowser(uri);

        } catch (IOException ioex) {
            if (report != null) {
                ioex.printStackTrace();
                displayMessage("Error",
                        "Report unable to open in browser: saved in "
                        + report.getAbsolutePath(), IconMessageTypeEnum.ERROR,
                        this.getPoint());

            } else {
                ioex.printStackTrace();
                displayMessage("Error",
                        "Report unable to open in browser.",
                        IconMessageTypeEnum.ERROR, this.getPoint());

            }
        }
    }
}
