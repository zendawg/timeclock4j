/*
 * Copyright (C) 2017 State Five
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.statefive.timeclockj.ui.listener;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
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
import org.statefive.timeclockj.ui.AboutHelper;
import org.statefive.timeclockj.ui.AbstractSystemTray;
import org.statefive.timeclockj.ui.IconMessageTypeEnum;
import org.statefive.timeclockj.ui.Utils;

/**
 *
 * @author irch
 */
public class AboutListener implements ActionListener {

    private AbstractSystemTray systemTray;

    public AbstractSystemTray getSystemTray() {
        return systemTray;
    }

    public void setSystemTray(AbstractSystemTray systemTray) {
        this.systemTray = systemTray;
    }

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

    public JPanel getAboutPanel() {
        JPanel panelAbout = new JPanel(new BorderLayout());
        panelAbout.setPreferredSize(new Dimension(400, 300));
        JTabbedPane tabbedPane = new JTabbedPane();
        JEditorPane editorPaneAbout = new JEditorPane("text/html", "");
//        editorPaneAbout.setText(AboutHelper.getAboutHtml().replace(AboutHelper.DOLLAR_VERSION,
//                TimeClockUtils.getVersion(AbstractSystemTray.class, null)));
        editorPaneAbout.setText(AboutHelper.getAboutHtml());
        editorPaneAbout.addHyperlinkListener(
                new HyperlinkListener() {

            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    getSystemTray().launchBrowser(URI.create(
                            "http://www.statefive.org"));
                    systemTray.displayMessage("State Five",
                            "www.statefive.org opening in browser",
                            IconMessageTypeEnum.INFO, systemTray.getPoint());
                }
            }
        });
        editorPaneAbout.setEditable( false);
        tabbedPane.addTab("About", editorPaneAbout);
        tabbedPane.addTab("License", getLicensePanel());
        tabbedPane.addTab("Contributors", getContributorPanel());
//    tabbedPane.addTab("Contributors", new JPanel());
//    tabbedPane.addTab("Third Party", new JPanel());
        panelAbout.add(tabbedPane);

        return panelAbout;
    }

    /**
     * Gets the panel showing the license for this software.
     *
     * @return a panel, if it could be loaded; <tt>null</tt> otherwise.
     */
    public JPanel getLicensePanel() {
        JPanel panelLicense = new JPanel(new BorderLayout());
        JTextArea textAreaLicense = new JTextArea(20, 40);
        InputStream is = AbstractSystemTray.class.getResourceAsStream("/license.txt");
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
            // TODO show error dialog?
            ioex.printStackTrace();
        }

        final String uri = "http://www.gnu.org/licenses/gpl.html";
        JLabel labelLicenseUri = new JLabel(
                "<html><body><p><br><p align=\"justify\"><a href=\""
                + uri + "\">" + " View in browser</a> (requires internet"
                + " connection)</p><br></body></html>");
        labelLicenseUri.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        labelLicenseUri.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                getSystemTray().launchBrowser(URI.create(uri.toString()));
                systemTray.displayMessage("License Information",
                        "GPL License V3 has been opened in a browser",
                        IconMessageTypeEnum.INFO, systemTray.getPoint());
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
                        getSystemTray().launchBrowser(
                                URI.create(e.getURL().toURI().toString()));
                        systemTray.displayMessage("Third Party Software",
                                "URL " + e.getURL().toString()
                                + " has been opened in a browser.",
                                IconMessageTypeEnum.INFO, systemTray.getPoint());

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
