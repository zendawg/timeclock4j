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
package org.statefive.timeclockj.ui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.statefive.timeclockj.ClockManager;

import org.statefive.timeclockj.ClockModel;

/**
 *
 * @author rich
 */
public class ClockOptionsPanel {

  private ClockModel clockModel;

  public JPanel getClockInPanel(final ClockModel model) {
    this.clockModel = model;
    JPanel panelClockInOptions = new JPanel();
    panelClockInOptions.setLayout(new GridLayout(3, 3));
    addDateTimeComponents(panelClockInOptions);
    final Vector projects = getProjects();
    final JComboBox comboBoxProjects = new JComboBox(projects);
    comboBoxProjects.addItemListener(new ItemListener() {

      @Override
      public void itemStateChanged(ItemEvent ie) {

        System.out.println("Index: " + comboBoxProjects.getSelectedIndex());
        if (comboBoxProjects.getSelectedIndex() == (projects.size() - 1)) {
          String project = JOptionPane.showInputDialog("Enter project name");
          model.setProject(project);
          projects.add(1, project);
          comboBoxProjects.setSelectedIndex(1);
        } else {
          if (comboBoxProjects.getSelectedIndex() == 0) {
            System.out.println("Setting default project");
            model.setProject(null);
          } else {
            model.setProject(comboBoxProjects.getSelectedItem().toString());
          }
        }
      }
    });
    // null means 0th element, ie. default project
    clockModel.setProject(null);
    panelClockInOptions.add(new JLabel("Project:"));
    panelClockInOptions.add(comboBoxProjects);
    return panelClockInOptions;
  }

  public JPanel getClockOutPanel(ClockModel model) {
    this.clockModel = model;
    JPanel panelClockInOptions = new JPanel();
    panelClockInOptions.setLayout(new GridLayout(3, 2));
    final JCheckBox checkBoxDescription = new JCheckBox("Description:");
    final JTextField textFieldDescription = new JTextField(12);
    textFieldDescription.setEnabled(false);
    addDateTimeComponents(panelClockInOptions);
    panelClockInOptions.add(checkBoxDescription);
    panelClockInOptions.add(textFieldDescription);

    checkBoxDescription.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("Action performed!");
        setComponentStates(checkBoxDescription, textFieldDescription);
      }
    });
    textFieldDescription.addFocusListener(new FocusAdapter() {

      public void focusLost(FocusEvent e) {
        if (textFieldDescription.getText().length() > 0) {
          clockModel.setDescription(textFieldDescription.getText());
        } else {
          clockModel.setDescription(null);
        }
      }
    });
    return panelClockInOptions;
  }

  void addDateTimeComponents(JPanel panel) {

    final JCheckBox checkBoxDate = new JCheckBox("Date (yyyy-mm-dd)");
    final JTextField textFieldDate = new JTextField(12);
    final JCheckBox checkBoxTime = new JCheckBox("Time (hh:mm)");
    final JTextField textFieldTime = new JTextField(12);
    textFieldDate.setEnabled(false);
    textFieldTime.setEnabled(false);
    panel.add(checkBoxDate);
    panel.add(textFieldDate);
    panel.add(checkBoxTime);
    panel.add(textFieldTime);

    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    dateFormat.setLenient(false);
    timeFormat.setLenient(false);

    textFieldDate.addFocusListener(new FocusAdapter() {

      @Override
      public void focusLost(FocusEvent e) {
        if (!e.isTemporary()) {
          try {
            Date d = dateFormat.parse(textFieldDate.getText());
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            clockModel.setCalendarDate(cal);
          } catch (ParseException pex) {
            JOptionPane.showMessageDialog(checkBoxTime, "Format is yyyy-mm-dd",
                    "Invalid Date", JOptionPane.ERROR_MESSAGE);
            checkBoxDate.setSelected(false);
            clockModel.setCalendarDate(null);
          }
        }
      }
    });

    textFieldTime.addFocusListener(new FocusAdapter() {

      @Override
      public void focusLost(FocusEvent e) {
        if (!e.isTemporary()) {
          try {
            Date d = timeFormat.parse(textFieldTime.getText());
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.set(Calendar.SECOND, 0);
            clockModel.setCalendarTime(cal);
          } catch (Exception pex) {
            JOptionPane.showMessageDialog(null, "Format is HH:mm",
                    "Invalid Time", JOptionPane.ERROR_MESSAGE);
            checkBoxTime.setSelected(false);
            clockModel.setCalendarTime(null);

          }
        }
      }
    });
    textFieldTime.setText(timeFormat.format(Calendar.getInstance().getTime()));
    textFieldDate.setText(dateFormat.format(Calendar.getInstance().getTime()));

    checkBoxDate.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        setComponentStates(checkBoxDate, textFieldDate);
        if (!checkBoxDate.isSelected()) {
          clockModel.setCalendarDate(null);
        }
      }
    });

    checkBoxTime.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        setComponentStates(checkBoxTime, textFieldTime);
        if (!checkBoxTime.isSelected()) {
          clockModel.setCalendarTime(null);
        }
      }
    });
  }

  /**
   * 
   * @param checkBox
   * @param textField
   */
  void setComponentStates(JCheckBox checkBox, JTextField textField) {

    if (checkBox.isSelected()) {
      textField.setEnabled(true);
      textField.requestFocus();
    } else {
      textField.setEnabled(false);
    }
  }

  /**
   * Gets all projects from the clock manager; also, the default
   * project is given at the start of the list and the 'New Project'
   * option added at the end.
   * 
   * @return a list of projects, including the default and an option to
   * add a new project.
   */
  private Vector getProjects() {
    final Vector projects = new Vector();
    projects.add(ClockManager.DEFAULT_PROJECT_STRING);
    for (int i = 0; i < ClockManager.getInstance().getProjects().length; i++) {
      String project = ClockManager.getInstance().getProjects()[i];
      if (!ClockManager.DEFAULT_PROJECT_STRING.equals(project)) {
        projects.add(project);
      }
    }
    projects.add("New Project...");
    return projects;
  }
}
