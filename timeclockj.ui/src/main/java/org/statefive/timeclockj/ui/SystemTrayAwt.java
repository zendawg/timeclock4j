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
import org.statefive.timeclockj.ui.listener.SpecifyTimeReportListener;
import org.statefive.timeclockj.ui.listener.ReloadFileListener;
import org.statefive.timeclockj.ui.listener.ProjectReportListener;
import org.statefive.timeclockj.ui.listener.ProjectAllReportsListener;
import org.statefive.timeclockj.ui.listener.ClockInToProjectListener;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import org.statefive.timeclockj.ClockManager;
import org.statefive.timeclockj.ui.listener.AboutListener;
import org.statefive.timeclockj.ui.listener.ClockInWithOptionsListener;
import org.statefive.timeclockj.ui.listener.ClockOutWithDescriptionListener;
import org.statefive.timeclockj.ui.listener.ExitListener;
import org.statefive.timeclockj.ui.listener.NewProjectListener;

/**
 * TimeClock AWT application for the Java System Tray API, JSR 270.
 *
 * TODO this class has been quickly hacked! Needs a lot of tidying up...
 *
 * @author rich
 *
 */
public class SystemTrayAwt extends AbstractSystemTray {

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
    public SystemTrayAwt(File file) {
        super(file);
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
     * AWT system trays support message display out of the box - the message
     * appears near the system tray itself.
     * 
     * @param caption non-{@code null} caption to display.
     * @param message non-{@code null} message to display.
     * @param messageType the type of message to show.
     * @param point unused.
     */
    @Override
    public void displayMessage(String caption, String message,
            IconMessageTypeEnum messageType, Point point) {
        TrayIcon.MessageType mType = null;
        switch (messageType) {
            case ERROR:
                mType = TrayIcon.MessageType.ERROR;
                break;
            case INFO:
                mType = TrayIcon.MessageType.INFO;
                break;
            case NONE:
                mType = TrayIcon.MessageType.NONE;
                break;
            case WARNING:
                mType = TrayIcon.MessageType.WARNING;
                break;
        }
        trayIcon.displayMessage(caption, message, mType);
    }

    /**
     * Sets the status - in the case of AWT, a tooltip that appears when the
     * user hovers their mouse over the tray icon.
     * 
     * @param status non-{@code null} status to show.
     */
    @Override
    public void setStatus(String status) {
        trayIcon.setToolTip(status);
    }

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
    @Override
    public void refreshMenuItems(boolean itemClockOut,
            String clockOutText, boolean itemClockIn,
            boolean itemReportCurrentProject,
            String currentProjectText) {
        menuItemCLockOut.setEnabled(itemClockOut);
        menuItemCLockOutWithOptions.setEnabled(itemClockOut);
        menuItemCLockOut.setLabel(clockOutText);
        menuItemCLockIn.setEnabled(itemClockIn);
        menuItemCLockInWithOptions.setEnabled(itemClockIn);
        menuItemReportCurrentProject.setLabel(currentProjectText);
        menuItemReportCurrentProject.setEnabled(itemReportCurrentProject);
    }

    /**
     * Set the system tray to use the specified icon.
     * 
     * @param image non-{@code null} image to use.
     */
    @Override
    public void setTrayIconImage(Image image) {
        trayIcon.setImage(image);
    }
    
    /**
     * Add a new project to the reporting menu with the specified name.
     * 
     * @param name non-{@code null} project name to add to the reporting
     * menu item set.
     */
    @Override
    public void addNewProjectReportMenuItem(String name) {
        MenuItem menuItem = new MenuItem(name);
        menuItemReportSpecificProject.add(menuItem);
        menuItem.addActionListener(new ProjectReportListener());
    }

    /**
     * Utility for loading the system tray.
     *
     * @throws Exception
     */
    @Override
    public void systemtray() throws Exception {

        if (SystemTray.isSupported()) {

            final SystemTray tray = SystemTray.getSystemTray();
            String path = "/icons/timeclockj-in.png";
            InputStream in = SystemTrayAwt.class.getResourceAsStream(path);
            Image image = ImageIO.read(in);

            popup = new PopupMenu();
            MenuItem defaultItem = new MenuItem("Exit");
            exitListener = new ExitListener();
            defaultItem.addActionListener(exitListener);
            menuItemReload = new MenuItem("Reload File");
            menuItemReload.addActionListener(new ReloadFileListener());
            menuItemCLockOut = new MenuItem("Clock Out");
            menuItemCLockOut.addActionListener(clockOutListener);
            menuItemCLockOutWithOptions
                    = new MenuItem("Clock Out With Options...");
            clockOutWithDescriptionListener = new ClockOutWithDescriptionListener();
            clockOutWithDescriptionListener.setSystemTray(this);
            menuItemCLockOutWithOptions.addActionListener(clockOutWithDescriptionListener);
            menuItemCLockIn = new Menu("Clock In");
            menuItemCLockInWithOptions = new MenuItem("Clock In With Options...");
            clockInWithOptionsListener = new ClockInWithOptionsListener();
            clockInWithOptionsListener.setSystemTray(this);
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
            aboutListener = new AboutListener();
            menuItemAbout.addActionListener(aboutListener);
            popup.addSeparator();
            popup.add(defaultItem);

            trayIcon = new TrayIcon(image, "Tray Demo", popup);

            trayIcon.setImageAutoSize(true);

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

    /**
     * Update the system tray, updating necessary components.
     */
    @Override
    public void refreshClockInMenuItems() {

        menuItemCLockIn.removeAll();

        MenuItem menuItemDefaultProject = new MenuItem(ClockManager.DEFAULT_PROJECT_STRING);
        ClockInToProjectListener listener
                = new ClockInToProjectListener(null);
        menuItemDefaultProject.addActionListener(listener);
        menuItemCLockIn.add(menuItemDefaultProject);

        for (String project : ClockManager.getInstance().getProjects()) {
            if (!ClockManager.DEFAULT_PROJECT_STRING.equals(project)) {
                // just added default project in previous step
                MenuItem menuItem = new MenuItem(project);
                listener
                        = new ClockInToProjectListener(project);
                menuItem.addActionListener(listener);
                menuItemCLockIn.add(menuItem);

            }
        }
        MenuItem menuItemNewProject = new MenuItem("New Project...");
        menuItemCLockIn.add(menuItemNewProject);

        newProjectListener = new NewProjectListener();
        newProjectListener.setSystemTray(this);

        menuItemNewProject.addActionListener(newProjectListener);
    }

    /**
     * Open a browser with the specified (existing) page.
     * 
     * @param uri non-{@code null} URI to open.
     */
    @Override
    public void launchBrowser(URI uri) {

        try {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        java.awt.Desktop.getDesktop().browse(uri);
                        displayMessage("Project Report",
                                "New report opening in browser",
                                IconMessageTypeEnum.INFO,
                                SystemTrayAwt.this.getPoint());
                    } catch (IOException ioex) {
                        ioex.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {

        }
    }
}
