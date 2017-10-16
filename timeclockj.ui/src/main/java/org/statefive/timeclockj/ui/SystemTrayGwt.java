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
import dorkbox.systemTray.Menu;
import dorkbox.systemTray.MenuItem;
import dorkbox.systemTray.SystemTray;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import org.statefive.timeclockj.ui.listener.SpecifyTimeReportListener;
import org.statefive.timeclockj.ui.listener.ReloadFileListener;
import org.statefive.timeclockj.ui.listener.ProjectReportListener;
import org.statefive.timeclockj.ui.listener.ProjectAllReportsListener;
import org.statefive.timeclockj.ui.listener.ClockInToProjectListener;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.plaf.PopupMenuUI;
import org.statefive.timeclockj.ClockManager;
import org.statefive.timeclockj.ui.listener.AboutListener;
import org.statefive.timeclockj.ui.listener.ClockInWithOptionsListener;
import org.statefive.timeclockj.ui.listener.ClockOutListener;
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
public class SystemTrayGwt extends AbstractSystemTray {

    private SystemTray systemTray;
    private MenuItem menuItemCLockOut;
    private MenuItem menuItemCLockOutWithOptions;
    private MenuItem menuItemReportCurrentProject;
    private MenuItem menuItemReportAllProjects;
    private MenuItem menuItemReload;
    private Menu menuItemReportSpecificProject;
    private MenuItem menuItemSpecifyReportTime;
    private Menu menuItemCLockIn;
    private MenuItem menuItemCLockInWithOptions;
    private Menu menuItemReport;
    private MenuItem menuItemHelp;
    private MenuItem menuItemAbout;
    private MenuItem menuItemExit;
    private List<MenuItem> items = new ArrayList<>();
    private ActionListener callback;

    public SystemTrayGwt(File file) {
        super(file);
    }

    /**
     * Determines if the system supports system tray.
     *
     * @return {@code true} if system trays are supported; {@code false}
     * otherwise.
     */
    public static boolean isSupported() {
        return true;
    }

    public ActionListener getCallback() {
        return callback;
    }

    /**
     *
     * @param caption
     * @param message
     * @param messageType
     * @param point
     */
    @Override
    public void displayMessage(String caption, String message,
            IconMessageTypeEnum messageType, Point point) {
        try {
            Thread.sleep(500);
            final JFrame frame = new JFrame(caption);
            frame.getContentPane().setLayout(new BorderLayout());
            final JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout());
            panel.add(new JLabel(message), BorderLayout.CENTER);
            frame.getContentPane().add(panel);
            frame.setUndecorated(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            int x = ((int) point.getX());
            int y = ((int) point.getY());
            frame.setLocation(x, y);
            frame.pack();
            frame.setVisible(true);
            Thread.sleep(3000);
            frame.dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     *
     * @param status
     */
    @Override
    public void setStatus(String status) {
        systemTray.setStatus(status);
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
        this.refreshClockInMenuItems();
        menuItemCLockOut.setEnabled(itemClockOut);
        menuItemCLockOutWithOptions.setEnabled(itemClockOut);
        menuItemCLockOut.setText(clockOutText);
        menuItemCLockIn.setEnabled(itemClockIn);
        menuItemCLockInWithOptions.setEnabled(itemClockIn);
        menuItemReportCurrentProject.setText(currentProjectText);
        menuItemReportCurrentProject.setEnabled(itemReportCurrentProject);
    }

    /**
     * Set the system tray to use the specified icon.
     * 
     * @param image non-{@code null} image to use.
     */
    @Override
    public void setTrayIconImage(Image image) {
        systemTray.setImage(image);
    }

    /**
     * Add a new project to the reporting menu with the specified name.
     * 
     * @param name non-{@code null} project name to add to the reporting
     * menu item set.
     */
    @Override
    public void addNewProjectReportMenuItem(String name) {
        ProjectReportListener prl = new ProjectReportListener();
        prl.setSystemTray(this);
        MenuItem menuItem = new MenuItem(name, prl);
        menuItemReportSpecificProject.add(menuItem);
    }

    /**
     * Utility for loading the system tray.
     *
     * @throws Exception
     */
    @Override
    public void systemtray() throws Exception {

//        if (SystemTray.isSupported()) {
        SystemTray.SWING_UI = new CustomSwingUI();
        JPopupMenu jpopup = new JPopupMenu();
        jpopup.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                System.out.println(e.getPoint().getX() + " " + e.getPoint().getY());
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                System.out.println(e.getPoint().getX() + " " + e.getPoint().getY());
            }

        });
        jpopup.add("Test");
        PopupMenuUI popup = SystemTray.SWING_UI.getMenuUI(jpopup, new Menu());
        systemTray = SystemTray.get();
        if (systemTray == null) {
            throw new RuntimeException("Unable to load SystemTray!");
        }

        refreshComponents();
    }

    /**
     * Update the system tray, updating necessary components.
     */
    @Override
    public void refreshClockInMenuItems() {
        
        this.setTrayIconImage(this.getImageForFile("timeclockj-in.png"));
        systemTray.getMenu().clear();

        if (ClockManager.getInstance().isClockedIn()) {
            setStatus("Clocked in to '"
                    + ClockManager.getInstance().getCurrentProject() + "'");
        } else {
            setStatus("Not clocked in");
        }
        exitListener = new ExitListener();
        menuItemExit = new MenuItem("Exit",
                this.getImageForFile("door_out.png"), exitListener);
        reloadFileListener = new ReloadFileListener();
        menuItemReload = new MenuItem("Reload File",
                this.getImageForFile("arrow_rotate_clockwise.png"), reloadFileListener);
        reloadFileListener.setSystemTray(this);
        clockOutListener = new ClockOutListener();
        menuItemCLockOut = new MenuItem("Clock Out",
                this.getImageForFile("timeclockj-out.png"), clockOutListener);
        clockOutListener.setSystemTray(this);
        clockOutWithDescriptionListener = new ClockOutWithDescriptionListener();
        clockOutWithDescriptionListener.setSystemTray(this);
        menuItemCLockOutWithOptions
                = new MenuItem("Clock Out With Options...",
                        this.getImageForFile("timeclockj-out.png"),
                        clockOutWithDescriptionListener);
        clockInToProjectListener = new ClockInToProjectListener(
                ClockManager.getInstance().getCurrentProject());
        menuItemCLockIn = new Menu("Clock In",
                this.getImageForFile("timeclockj-in.png"), clockInToProjectListener);
        clockInWithOptionsListener = new ClockInWithOptionsListener();
        clockInWithOptionsListener.setSystemTray(this);
        menuItemCLockInWithOptions = new MenuItem("Clock In With Options...",
                this.getImageForFile("timeclockj-in.png"),
                clockInWithOptionsListener);

        // ############
        menuItemReport = new Menu("Report",
                this.getImageForFile("report.png"));
        aboutListener = new AboutListener();
        aboutListener.setSystemTray(this);
        menuItemAbout = new MenuItem("About",
                this.getImageForFile("information.png"), aboutListener);
        helpListener = new HelpListener();
        menuItemHelp = new MenuItem("Help Contents",
                this.getImageForFile("book_go.png"), helpListener);

        menuItemCLockOut = systemTray.getMenu().add(menuItemCLockOut);
        menuItemCLockOutWithOptions = systemTray.getMenu().add(menuItemCLockOutWithOptions);
        menuItemCLockIn = systemTray.getMenu().add(menuItemCLockIn);
        menuItemCLockInWithOptions = systemTray.getMenu().add(menuItemCLockInWithOptions);
        systemTray.getMenu().add(new JSeparator());
        menuItemReport = systemTray.getMenu().add(menuItemReport);

        systemTray.getMenu().add(new JSeparator());
        menuItemReload = systemTray.getMenu().add(menuItemReload);
        systemTray.getMenu().add(new JSeparator());

        menuItemHelp = systemTray.getMenu().add(menuItemHelp);

        menuItemAbout = systemTray.getMenu().add(menuItemAbout);
        systemTray.getMenu().add(new JSeparator());
        menuItemExit = systemTray.getMenu().add(menuItemExit);

        menuItemReportSpecificProject = new Menu("Project",
                this.getImageForFile("time_add.png"));

        ProjectReportListener prl = new ProjectReportListener();
        prl.setSystemTray(this);
        MenuItem menuItem = new MenuItem(ClockManager.DEFAULT_PROJECT_STRING, prl);
        menuItemReportSpecificProject.add(menuItem);
        for (String project : ClockManager.getInstance().getProjects()) {
            if (!ClockManager.DEFAULT_PROJECT_STRING.equals(project)) {
                // added default project in previous step:
                prl = new ProjectReportListener();
                prl.setSystemTray(this);
                menuItem = new MenuItem(project, prl);
                menuItemReportSpecificProject.add(menuItem);
            }
        }
        List menuItems = getProjectReportMenuItems();
        for (Object o : menuItems) {
            MenuItem mi = (MenuItem) o;
            menuItemCLockIn.add(mi);
        }
        specifyTimeReportListener = new SpecifyTimeReportListener();
        specifyTimeReportListener.setSystemTray(this);
        menuItemSpecifyReportTime = new MenuItem("Specify Report Time",
                this.getImageForFile("time_go.png"), specifyTimeReportListener);
        projectReportListener = new ProjectReportListener();
        projectReportListener.setSystemTray(this);
        menuItemReportCurrentProject = new MenuItem("Current Project",
                this.getImageForFile("time.png"), projectReportListener);
        projectAllReportsListener = new ProjectAllReportsListener();
        projectAllReportsListener.setSystemTray(this);
        menuItemReportAllProjects = new MenuItem("All Projects",
                this.getImageForFile("application_cascade.png"), projectAllReportsListener);

        menuItemReportCurrentProject = menuItemReport.add(menuItemReportCurrentProject);
        menuItemReportAllProjects = menuItemReport.add(menuItemReportAllProjects);
        menuItemSpecifyReportTime = menuItemReport.add(menuItemSpecifyReportTime);
        menuItemReportSpecificProject = menuItemReport.add(menuItemReportSpecificProject);
        callback = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("-- ACTION PERFORMED -- ");
                Point p = MouseInfo.getPointerInfo().getLocation();
                System.out.println("-- " + p.getX() + "/" + p.getY() + " -- ");
                SystemTrayGwt.this.setPoint(p);
            }
        };
        systemTray.getMenu().setCallback(callback);
    }

    /**
     * Launch a browser with the specified page.
     * <p>
     * Adapted from
     * https://stackoverflow.com/questions/5226212/how-to-open-the-default-webbrowser-using-java
     * <p>
     * 
     * @param uri non-{@code null} URI to open.
     */
    @Override
    public void launchBrowser(URI uri) {
        Runtime rt = Runtime.getRuntime();
        String url = uri.toString();
        String[] browsers = {"epiphany", "firefox", "mozilla", "konqueror",
            "netscape", "opera", "links", "lynx"};

        StringBuffer cmd = new StringBuffer();
        for (int i = 0; i < browsers.length; i++) {
            if (i == 0) {
                cmd.append(String.format("%s \"%s\"", browsers[i], url));
            } else {
                cmd.append(String.format(" || %s \"%s\"", browsers[i], url));
            }
        }
        // If the first didn't work, try the next browser and so on
        try {
            rt.exec(new String[]{"sh", "-c", cmd.toString()});
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(),
                    "Unable to launch browser.", JOptionPane.ERROR_MESSAGE);
        }
        displayMessage("Project Report",
                "New report opening in browser",
                IconMessageTypeEnum.INFO, this.getPoint());
    }

    /**
     * Get the specified image from the underlying Java resource stream jar
     * file.
     * 
     * @param name non-{@code null} name of the image resource to retrieve.
     * 
     * @return an image
     */
    private Image getImageForFile(String name) {
        Image image = null;
        String path = "/icons/" + name;
        try {
            InputStream in = SystemTrayGwt.class.getResourceAsStream(path);
            image = ImageIO.read(in);
        } catch (IOException ioex) {
            ioex.printStackTrace();
        }
        return image;
    }

    /**
     * Get the menu items for all of the project report items; this is the list
     * that hangs off the "Project" item and displays: all projects
     * (including the default project) and a menu item for a new project.
     * 
     * @return a non-empty list of items that will be added to the
     * "Report" menu item.
     */
    private List<MenuItem> getProjectReportMenuItems() {
        if (!items.isEmpty()) {
            for (MenuItem item : items) {
                menuItemCLockIn.remove(item);
            }
        }
        items = new ArrayList();
        ClockInToProjectListener listener
                = new ClockInToProjectListener(ClockManager.DEFAULT_PROJECT_STRING);
        listener.setSystemTray(this);
        MenuItem menuItemDefaultProject = new MenuItem(
                ClockManager.DEFAULT_PROJECT_STRING, listener);
        items.add(menuItemDefaultProject);
        for (String project : ClockManager.getInstance().getProjects()) {
            if (!ClockManager.DEFAULT_PROJECT_STRING.equals(project)) {
                // just added default project in previous step
                ClockInToProjectListener l = new ClockInToProjectListener(project);
                l.setSystemTray(this);
                MenuItem menuItem = new MenuItem(project, l);
                items.add(menuItem);
            }
        }
        newProjectListener = new NewProjectListener();
        newProjectListener.setSystemTray(this);
        MenuItem menuItemNewProject = new MenuItem("New Project...",
                this.getImageForFile("clock_add.png"), newProjectListener);
        items.add(menuItemNewProject);
        return items;
    }
}
