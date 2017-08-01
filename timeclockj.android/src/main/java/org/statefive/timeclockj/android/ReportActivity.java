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
package org.statefive.timeclockj.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.statefive.timeclockj.ClockManager;
import org.statefive.timeclockj.report.ReportOutput;

/**
 *
 * @author rich
 */
public class ReportActivity extends Activity implements FileParseListener {

  public static final String PROJECT_NAMES =
          "org.statefive.droidclock.project_names";
  private static final int DATE_START_DIALOG_ID = 0;
  private static final int DATE_END_DIALOG_ID = 1;
  RadioGroup radioGroup;
//  RadioButton radiobuttonCurrentProject;
  RadioButton radiobuttonChooseProject;
  RadioButton radiobuttonAllProjects;
  CheckBox checkBoxWriteReports;
  CheckBox checkBoxViewReports;
  CheckBox checkBoxSpecifyStartDate;
  CheckBox checkBoxSpecifyEndDate;
  Spinner spinnerChooseProject;
  Button buttonGo;
  private Date dateStart;
  private Date dateEnd;

  /**
   * 
   * @param status 
   * 
   * @since 1.1
   */
  @Override
  public void fileParseUpdate(Status status) {
    if (status == Status.FINISHED) {
      ArrayAdapter<String> aa = new ArrayAdapter<String>(this,
              android.R.layout.simple_spinner_item,
              ClockManager.getInstance().getProjects());
      aa.setDropDownViewResource(
              android.R.layout.simple_spinner_dropdown_item);
      spinnerChooseProject.setAdapter(aa);
      buttonGo.setEnabled(true);
      Utils.getInstance(this).setParseAsyncTask(null);
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    System.out.println("onCreate(Bundle) : ReportActivity");
    setContentView(R.layout.reportactivity);

    spinnerChooseProject = (Spinner) findViewById(R.id.spinnerChooseProject);
    ArrayAdapter<String> aa = new ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_item,
            ClockManager.getInstance().getProjects());
    aa.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item);
    spinnerChooseProject.setAdapter(aa);
//    spinnerChooseProject.setEnabled(false);

    radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
//    radiobuttonCurrentProject = (RadioButton) findViewById(R.id.radioButtonCurrentProject);
    radiobuttonChooseProject = (RadioButton) findViewById(R.id.radioButtonChooseProject);
    radiobuttonAllProjects = (RadioButton) findViewById(R.id.radioButtonAllProjects);
    boolean clockedIn = ClockManager.getInstance().isClockedIn();
//    radiobuttonCurrentProject.setEnabled(clockedIn);
    radiobuttonChooseProject.setChecked(true);
//    radiobuttonCurrentProject.setChecked(clockedIn);
    System.out.println("Set component state - clocked-in: " + clockedIn);
    checkBoxViewReports = (CheckBox) findViewById(R.id.checkBoxViewReports);
    checkBoxWriteReports = (CheckBox) findViewById(R.id.checkBoxWriteReports);
    checkBoxViewReports.setChecked(true);
    checkBoxWriteReports.setChecked(false);
    checkBoxSpecifyStartDate = (CheckBox) findViewById(R.id.checkBoxStart);
    checkBoxSpecifyEndDate = (CheckBox) findViewById(R.id.checkBoxEnd);

    checkBoxSpecifyStartDate.setOnCheckedChangeListener(
            new OnCheckedChangeListener() {

              @Override
              public void onCheckedChanged(CompoundButton cb, boolean bln) {

                if (checkBoxSpecifyStartDate.isChecked()) {
//                  year = Calendar.getInstance().get(Calendar.YEAR);
//                  month = Calendar.getInstance().get(Calendar.MONTH);
//                  day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                  showDialog(DATE_START_DIALOG_ID);
                } else {
//                  clockModel.setCalendarDate(null);
                  dateStart = null;
                }
              }
            });
    checkBoxSpecifyEndDate.setOnCheckedChangeListener(
            new OnCheckedChangeListener() {

              @Override
              public void onCheckedChanged(CompoundButton cb, boolean bln) {

                if (checkBoxSpecifyEndDate.isChecked()) {
//                  year = Calendar.getInstance().get(Calendar.YEAR);
//                  month = Calendar.getInstance().get(Calendar.MONTH);
//                  day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                  showDialog(DATE_END_DIALOG_ID);
                } else {
//                  clockModel.setCalendarDate(null);
                  dateEnd = null;
                }
              }
            });
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    boolean enableWriting = prefs.getBoolean(
            this.getResources().getString(R.string.timeclockj_sdcard), true);
    System.out.println("Enable writing: " + enableWriting);
    checkBoxWriteReports.setEnabled(enableWriting);
    if (!enableWriting) {
      checkBoxWriteReports.setOnTouchListener(new OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent me) {
          showDialog("SD Card", "SD Card option must be selected; Edit "
                  + "timeclockj settings and ensure data is stored on the SD card");
          return true;
        }
      });
    }
//    if (clockedIn) {
//      radiobuttonCurrentProject.setText("Current Project ('"
//        + ClockManager.getInstance().getCurrentProject() + "')");
//    } else {
//      radiobuttonCurrentProject.setText("Current Project");
//      radiobuttonChooseProject.setChecked(true);
//      spinnerChooseProject.setEnabled(true);
//    }
    buttonGo = (Button) findViewById(R.id.buttonGo);
    buttonGo.setOnClickListener(new OnClickListener() {

      public void onClick(View arg0) {
        if (!checkBoxViewReports.isChecked()
                && !checkBoxWriteReports.isChecked()) {
          showDialog("Checkbox Options", "Ensure at least one of the options "
                  + "'Write to file' or 'View in browser' is checked");
        } else {
          generateReports();
        }
      }
    });
    initialiseComponentStates();

    System.out.println(Utils.getInstance().getLocalTimeClockFile(this).length());
    System.out.println(ClockManager.getInstance().getProjects().length);
    if (Utils.getInstance().getLocalTimeClockFile(this).length() < 1
            && ClockManager.getInstance().getProjects().length == 0) {
      showDialog("No projects - no reports", "It looks like you've never "
              + "used TimeClockJ, or your timelog file is missing. "
              + "Clocking-in to a project will enable report generation.");
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    System.out.println("onPause() : ReportActivity");
    SharedPreferences prefs =
            PreferenceManager.getDefaultSharedPreferences(this);
    Editor prefsEditor = prefs.edit();
    File timeclock = Utils.getInstance().getLocalTimeClockFile(this);
    long lastModified = timeclock.lastModified();
    System.out.println("saving lastModified=" + lastModified);
    prefsEditor.putLong(ClockActivity.LAST_MODIFIED, lastModified);
    StringBuilder projects = new StringBuilder();
    for (String p : ClockManager.getInstance().getProjects()) {
      projects.append(p + "\n");
    }
    prefsEditor.putString(ClockActivity.PROEJCTS,
            projects.substring(0, projects.length()));
    prefsEditor.commit();
  }

  /**
   * Danger! Repeated code in this method! TODO refactor required
   */
  @Override
  public void onResume() {
    super.onResume();
    System.out.println("onResume : ReportActivity");
//    boolean clockedIn = ClockManager.getInstance().isClockedIn();
//    radiobuttonCurrentProject.setEnabled(clockedIn);
//    radiobuttonCurrentProject.setChecked(clockedIn);
//    if (clockedIn) {
//      radiobuttonCurrentProject.setText("Current Project ('"
//        + ClockManager.getInstance().getCurrentProject() + "')");
//    } else {
//      radiobuttonCurrentProject.setText("Current Project");
//      radiobuttonChooseProject.setChecked(true);
//      spinnerChooseProject.setEnabled(true);
//    }
    ArrayAdapter<String> aa = new ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_item,
            ClockManager.getInstance().getProjects());
    aa.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item);
    spinnerChooseProject.setAdapter(aa);
    initialiseComponentStates();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    int base = 1;
    menu.add(base, base, base, "Setup").setIcon(R.drawable.options);
    return true;
  }

  /**
   *
   * @param item
   * @return
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == 1) {
      Intent i = new Intent(getApplicationContext(), TimeClockJConfigActivity.class);
      ReportActivity.this.startActivity(i);
    }
    return true;
  }

  private void showDialog(String exception, String message) {
    new AlertDialog.Builder(this).setTitle(exception).setMessage(message).setNeutralButton("Close",
            new DialogInterface.OnClickListener() {

              public void onClick(DialogInterface arg0, int arg1) {
              }
            }).show();
  }

  private void initialiseComponentStates() {
//    radiobuttonCurrentProject.setOnClickListener(new OnClickListener() {
//
//      public void onClick(View arg0) {
//        buttonGo.setEnabled(true);
//        spinnerChooseProject.setEnabled(false);
//      }
//    });
    radiobuttonChooseProject.setOnClickListener(new OnClickListener() {

      public void onClick(View arg0) {
        spinnerChooseProject.setEnabled(true);
      }
    });
    radiobuttonAllProjects.setOnClickListener(new OnClickListener() {

      public void onClick(View arg0) {
        spinnerChooseProject.setEnabled(false);
      }
    });

    if (ClockManager.getInstance().getProjects().length == 0) {
      // there've been no clock-ins - *ever*
      buttonGo.setEnabled(false);
      spinnerChooseProject.setEnabled(false);
      radiobuttonAllProjects.setEnabled(false);
      radiobuttonChooseProject.setEnabled(false);
    } else {
      // enable it all, user can now make choices:
      buttonGo.setEnabled(true);
      spinnerChooseProject.setEnabled(true);
      radiobuttonAllProjects.setEnabled(true);
      radiobuttonChooseProject.setEnabled(true);
    }

    // check that someone else isn't performing a sync:
    ParseFileAsyncTask task = Utils.getInstance(this).getParseFileAsyncTask();
    if (task != null && task.getStatus() != Status.FINISHED) {
      buttonGo.setEnabled(false);
      task.addFileParseListener(this);
// TODO this is repeated code
      Toast toast = Toast.makeText(getApplicationContext(),
              "Currently parsing timeclock file; report generation will be"
              + " enabled when parsing is complete.", Toast.LENGTH_SHORT);
      toast.show();
    } else {

      File timeclock = Utils.getInstance().getLocalTimeClockFile(this);
      long lastModified = timeclock.lastModified();

      SharedPreferences prefs =
              PreferenceManager.getDefaultSharedPreferences(this);
      long lastModifiedPref = prefs.getLong(ClockActivity.LAST_MODIFIED, -1);
      System.out.println("LAST MODIFIED PREF: " + lastModifiedPref);
      System.out.println("LAST MODIFIED: " + lastModified);
      // it's been modified and no-one else has created a sync task - sp create
      // one and update the user:
      if ((lastModified != lastModifiedPref
              && Utils.getInstance(this).getParseFileAsyncTask() == null)
              || (ClockManager.getInstance().getProjects().length == 0
              && timeclock.length() > 0)) {
        // udpate the new value:
        Editor prefsEditor = prefs.edit();
        System.out.println("saving lastModified=" + lastModified);
        prefsEditor.putLong(ClockActivity.LAST_MODIFIED, lastModified);
        prefsEditor.commit();
  //        if (fileTimeClock.length() > 1024) {
  //          Toast toast = Toast.makeText(getApplicationContext(),
  //                  "Currently parsing timeclock file; clocking-in and "
  //                  + "out will be enabled when parsing is complete.",
  //                  Toast.LENGTH_SHORT);
  //          toast.show();
  //        }
        if (ClockManager.getInstance().getProjects().length == 0
                && timeclock.length() > 0) {
          Toast toast = Toast.makeText(getApplicationContext(),
                  "What happened? No projects but a full file...",
                  Toast.LENGTH_SHORT);
          toast.show();

        }
        task = new ParseFileAsyncTask();
        Utils.getInstance(this).setParseAsyncTask(task);
        task.addFileParseListener(this);
        task.execute(timeclock);
        buttonGo.setEnabled(false);
        System.out.println("PREFERENCES HAVE CHANGED FOR REPORT ACTIVITY");
        Toast toast = Toast.makeText(getApplicationContext(),
                "Currently parsing timeclock file; report generation will be"
                + " enabled when parsing is complete.", Toast.LENGTH_SHORT);
        toast.show();
      }
    }
  }

  /**
   * 
   * @param projectNames
   */
  public void generateReports(String[] projectNames) {
    if (checkBoxWriteReports.isChecked()) {

      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
      String timelogReportDir = prefs.getString(
              this.getResources().getString(R.string.timeclockj_report_dir),
              "timeclockj-reports");
      String timelogDir = prefs.getString(
              this.getResources().getString(R.string.timeclockj_timelog_dir),
              "timeclockj");
      if (timelogDir != null) {
        timelogReportDir = timelogDir + "/" + timelogReportDir;
      } else {
        timelogReportDir = "/" + timelogReportDir;
      }
      File root = Environment.getExternalStorageDirectory();
      try {
        String projects = "all-projects";
        if (projectNames != null && projectNames.length == 1) {
          projects = projectNames[0];
        }
        File htmlReportDir = new File(root, timelogReportDir);
        if (!htmlReportDir.exists()) {
          htmlReportDir.mkdirs();
        }
        File htmlReport = File.createTempFile(projects + ".", ".html", htmlReportDir);
        FileWriter fw = new FileWriter(htmlReport);
        fw.write(new ReportOutput().getHtmlOutput(projectNames,
                dateStart, dateEnd));
        fw.flush();
        fw.close();
        showDialog("I/O Success", "Reports successfully created.");
      } catch (IOException ioex) {
        showDialog("I/O Error", ioex.getMessage());
      }
    }

    if (checkBoxViewReports.isChecked()) {
      Intent i = new Intent(getApplicationContext(),
              ViewWebContentActivity.class);

//      dateStart = new Date(2011-1900, 11, 1);
//      dateEnd = new Date(2011-1900, 11, 10);
      i.putExtra(ViewWebContentActivity.WEB_CONTENT,
              new ReportOutput().getHtmlOutput(projectNames,
              dateStart, dateEnd));
      ReportActivity.this.startActivity(i);
    }
  }

  @Override
  protected Dialog onCreateDialog(int id) {
    switch (id) {
      case DATE_START_DIALOG_ID:
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                mDateStartSetListener, year, month, day);
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

          @Override
          public void onClick(DialogInterface dialog, int button) {
            if (button == DialogInterface.BUTTON_NEGATIVE) {
//              checkBoxSpecifyDate.setChecked(false);
            }
          }
        });

        return datePickerDialog;
      case DATE_END_DIALOG_ID:
        year = Calendar.getInstance().get(Calendar.YEAR);
        month = Calendar.getInstance().get(Calendar.MONTH);
        day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(this,
                mDateEndSetListener, year, month, day);
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

          @Override
          public void onClick(DialogInterface dialog, int button) {
            if (button == DialogInterface.BUTTON_NEGATIVE) {
//              checkBoxSpecifyDate.setChecked(false);
            }
          }
        });

        return datePickerDialog;
    }
    return null;
  }
  private DatePickerDialog.OnDateSetListener mDateStartSetListener =
          new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int yy,
                    int mm, int dd) {
              dateStart = new Date(yy - 1900, mm, dd);
//              clockModel.setCalendarDate(getCalendarDate(yy, mm, dd));
            }
          };
  private DatePickerDialog.OnDateSetListener mDateEndSetListener =
          new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int yy,
                    int mm, int dd) {
              dateEnd = new Date(yy - 1900, mm, dd);
//              clockModel.setCalendarDate(getCalendarDate(yy, mm, dd));
            }
          };

  /**
   * 
   */
  private void generateReports() {
    int id = radioGroup.getCheckedRadioButtonId();
    String[] projectNames = null;
    if (id == R.id.radioButtonChooseProject) {
      int pos = spinnerChooseProject.getSelectedItemPosition();
      String projectName = spinnerChooseProject.getItemAtPosition(pos).toString();
      projectNames = new String[]{projectName};
    } else if (id == R.id.radioButtonAllProjects) {
      projectNames = ClockManager.getInstance().getProjects();
    }
//    else if (id == R.id.radioButtonCurrentProject) {
//      String projectName = ClockManager.getInstance().getCurrentProject();
//      System.out.println("project: " + projectName);
//      projectNames = new String[]{projectName};
//    }
    generateReports(projectNames);
  }
}
