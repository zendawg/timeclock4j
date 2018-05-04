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
package org.statefive.timeclockj.android.v2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.regex.Pattern;
import org.statefive.timeclockj.ClockException;
import org.statefive.timeclockj.ClockInOutEnum;
import org.statefive.timeclockj.ClockLine;
import org.statefive.timeclockj.ClockLineFactory;
import org.statefive.timeclockj.ClockManager;
import org.statefive.timeclockj.ClockModel;
import org.statefive.timeclockj.ReverseLineReader;
import org.statefive.timeclockj.TimeClockUtils;

public class ClockActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        Observer, FileParseListener {

  public static final String LAST_MODIFIED =
          ClockActivity.class.getSimpleName() + ".LAST_MODIFIED";
  public static final String PROEJCTS =
          ClockActivity.class.getSimpleName() + ".PROJECTS";
  private static final int TIME_DIALOG_ID = 0;
  private static final int DATE_DIALOG_ID = -1;
//  private static final int DESCRIPTION_DIALOG_ID = 2;
  private Button buttonClock;
  private CheckBox checkBoxSpecifyDate;
  private CheckBox checkBoxSpecifyTime;
  private CheckBox checkBoxDescription;
  private TextView textViewClockDetails;
  private TextView textViewClockStatus;
  private Spinner spinnerClockIn;
  /**
   * Location of the time clock file.
   */
  private File fileTimeClock;
  /**
   *
   */
  private ClockModel clockModel;
  /**
   * Last modification time of the time clock file.
   */
  private long lastModified;
  /**
   * Last project that was selected, by the user.
   */
  private String lastSelectedProject = ClockManager.DEFAULT_PROJECT_STRING;
  /**
   * List of all projects that have been added so far.
   */
  private Collection<String> newProjectList = new ArrayList<String>();
  /**
   * Determines if preferences have been changed or not.
   */
  private boolean preferencesChanged = false;
  /**
   * Store state for last clock time, last clock line.
   */
  private SharedPreferences prefs;
  /**
   * For speed purposes, we read the last correct clock line from the file and
   * use that as the last clock, so the user does not have to wait (while the
   * clock file, if changed, gets parsed on a different thread).
   */
  private ClockLine line = null;
  /**
   * Whilst parsing the file, we cannot clock-in and-out as we want since the
   * clock manager's status will get interrupted; we need to add clock lines in
   * the order they occur, after parsing has finished. This array holds clocks
   * for such purposes, and when the parsing has finished, the array emptied.
   */
  private List<ClockLine> clocksWhileParsing = new ArrayList<ClockLine>();

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sp, String string) {
    System.out.println("Preferences changed");
    preferencesChanged = true;
  }

  /**
   *
   * @param status
   */
  @Override
  public void fileParseUpdate(Status status) {
    if (status == Status.FINISHED) {
      Utils.getInstance().setParseAsyncTask(null);
      updateClockInList();
      buttonClock.setEnabled(true);
    } else if (status == Status.RUNNING) {
      updateClockInList();
    }
    initialiseButtons();
  }

  @Override
  protected void onPause() {
    super.onPause();
// repeated code! See ReportActivity
    Editor prefsEditor = prefs.edit();
    System.out.println("saving lastModified=" + lastModified);
    prefsEditor.putLong(ClockActivity.LAST_MODIFIED, lastModified);
    StringBuilder projects = new StringBuilder();
    for (String p : ClockManager.getInstance().getProjects()) {
      projects.append(p + "\n");
    }
    prefsEditor.putString(ClockActivity.PROEJCTS,
            projects.substring(0, projects.length()));
    prefsEditor.apply();
  }

  /**
   * Called when any UI component updates the clock model that contains
   * information about clock date, time, project and description.
   *
   * @param o the updater.
   *
   * @param arg the updater.
   */
  @Override
  public void update(Observable o, Object arg) {
    this.updateClockDetails();
  }

  private class ClockInSelector implements AdapterView.OnItemSelectedListener {

    private Context context;

    public ClockInSelector(Context context) {
      this.context = context;
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
      if (pos == spinnerClockIn.getCount() - 1) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this.context);

        alert.setTitle("Enter New Project Name");

        // Set an EditText view to get user input
        final EditText input = new EditText(this.context);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int whichButton) {
            String project = input.getText().toString().trim();
            Pattern p = Pattern.compile("[0-9a-zA-Z\\s]*");
            if (p.matcher(project).matches()
                    && project != null
                    && !("".equals(project.trim()))) {
              newProjectList.add(project);
              updateClockInList();
            } else {
              spinnerClockIn.setSelection(0, true);
              showDialog("Invalid Project", "Projects must consist of "
                      + "alphanumeric characters and spaces.");
            }
          }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int whichButton) {
            spinnerClockIn.setSelection(0);
            System.out.println("Cancelled");
            // Canceled.
          }
        });

        alert.show();
      } else if (pos > -1) {
        clockModel.setProject(spinnerClockIn.getSelectedItem().toString());
      }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }
  }
  private TimePickerDialog.OnTimeSetListener mTimeSetListener =
          new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
              clockModel.setCalendarTime(getCalendarTime(null, hour, minute));
            }
          };
  private AlertDialog.OnClickListener mAlertClickListener =
          new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface di, int i) {
            }
          };
  private DatePickerDialog.OnDateSetListener mDateSetListener =
          new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int yy,
                    int mm, int dd) {
              clockModel.setCalendarDate(getCalendarDate(yy, mm, dd));
            }
          };

  /**
   *
   */
  @Override
  public void onResume() {
    super.onResume();
    System.out.println("ON_RESUME: CLOCK ACTIVITY");
    checkTimeStamp();
    initialiseButtons();
  }

  /**
   * Checks to see if the last modified time is different to the current
   * modification time of the time clock file. If the times are different, the
   * file is re-loaded.
   */
  private void checkTimeStamp() {

    lastModified = prefs.getLong(ClockActivity.LAST_MODIFIED, lastModified);
    try {
      fileTimeClock = Utils.getInstance().getLocalTimeClockFile(this);
      System.out.println("timelog file is " + fileTimeClock.getAbsolutePath());
      if (!fileTimeClock.exists()) {
        fileTimeClock.createNewFile();
      }
      System.out.println("Task: "
              + Utils.getInstance().getParseFileAsyncTask());
      ParseFileAsyncTask task = Utils.getInstance().getParseFileAsyncTask();
      if (task != null) {
        // don't start off two threads - there's already one in the pipeline:
        // since it's parsing, set the clock to disabled:

        buttonClock.setEnabled(false);
        task.addFileParseListener(this);
        System.out.println("Giving up check because thread is still active");
      } else {
        System.out.println("t1=" + lastModified);
        System.out.println("t2=" + fileTimeClock.lastModified());
        // TODO - need to research this - after a cetain length of time,
        // if the application is not used, the project list is GC'd and
        // when starting the application again, the clock manager has no entries.
        // The file has not been modified either, the result is an empty list
        // of projects
        if (lastModified != fileTimeClock.lastModified()
                || ClockManager.getInstance().getProjects().length == 0
                && fileTimeClock.length() > 0) {
          buttonClock.setEnabled(false);
          this.line = null;
          try {
            getLastLine();
          } catch (Exception ex) {
            // no worries - means the file's just been created (and has no
            // data), or the file doesn't exist yet
          }
          lastModified = fileTimeClock.lastModified();
          task = new ParseFileAsyncTask();
          //        buttonClock.setEnabled(false);
          task.addFileParseListener(this);
          Utils.getInstance().setParseAsyncTask(task);
          task.execute(fileTimeClock);
          if (this.preferencesChanged) {
            this.loadPreferences();
          }
        }
      }
    } catch (IOException ioex) {
      showDialog("I/O Exception", "An I/O error occurred: " + ioex.getMessage());
      ioex.printStackTrace();
    }
  }

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    clockModel = new ClockModel();
    clockModel.setProject(ClockManager.getInstance().getCurrentProject());
    prefs = PreferenceManager.getDefaultSharedPreferences(this);
//    lastModified = prefs.getLong(ClockActivity.LAST_MODIFIED, lastModified);
//    System.out.println("Read lastModified=" + lastModified);
//    if (line != null) {
//      System.out.println("last clock=" + line.toString());
//    }
    prefs.registerOnSharedPreferenceChangeListener(this);

    setContentView(R.layout.activity_clock);

    Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
    System.out.println("Toolbar :: " + toolbar);
    setSupportActionBar(toolbar);

    textViewClockDetails = (TextView) this.findViewById(R.id.textViewClockDetails);
    textViewClockStatus = (TextView) this.findViewById(R.id.textViewClockStatus);
    buttonClock = (Button) this.findViewById(R.id.buttonClock);
    checkBoxDescription = (CheckBox) this.findViewById(R.id.checkBoxDescription);
    checkBoxSpecifyDate = (CheckBox) this.findViewById(R.id.checkBoxSpecifyDate);
    checkBoxSpecifyTime = (CheckBox) this.findViewById(R.id.checkBoxSpecifyTime);
    spinnerClockIn = (Spinner) findViewById(R.id.spinnerClockInList);

    checkBoxSpecifyDate.setOnCheckedChangeListener(
            new OnCheckedChangeListener() {
              @Override
              public void onCheckedChanged(CompoundButton cb, boolean bln) {

                if (checkBoxSpecifyDate.isChecked()) {
//                  year = Calendar.getInstance().get(Calendar.YEAR);
//                  month = Calendar.getInstance().get(Calendar.MONTH);
//                  day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                  showDialog(DATE_DIALOG_ID);
                } else {
                  clockModel.setCalendarDate(null);
                }
              }
            });
    checkBoxSpecifyTime.setOnCheckedChangeListener(
            new OnCheckedChangeListener() {
              @Override
              public void onCheckedChanged(CompoundButton cb, boolean bln) {

                if (checkBoxSpecifyTime.isChecked()) {
//                  hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
//                  minute = Calendar.getInstance().get(Calendar.MINUTE);
                  showDialog(TIME_DIALOG_ID);
                } else {
                  clockModel.setCalendarTime(null);
                }
              }
            });
    checkBoxDescription.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (!checkBoxDescription.isChecked()) {
          clockModel.setDescription(null);
          return;
        }
        AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());

        alert.setTitle("Enter Description");

        // Set an EditText view to get user input
        final EditText input = new EditText(view.getContext());
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int whichButton) {
            if ("".equals(input.getText().toString().trim())) {
              checkBoxDescription.setChecked(false);
              clockModel.setDescription(null);
            } else {
              String description = input.getText().toString().trim();
              clockModel.setDescription(description);
            }
          }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int whichButton) {
//            ClockActivity.this.description = null;
            checkBoxDescription.setChecked(false);
            clockModel.setDescription(null);
          }
        });

        alert.show();
      }
    });
    buttonClock.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View arg0) {
        Calendar calDate = Calendar.getInstance();
        Calendar calTime = Calendar.getInstance();
        if (checkBoxSpecifyDate.isChecked()) {
          calDate = clockModel.getCalendarDate();
        }
        if (checkBoxSpecifyTime.isChecked()) {
          calTime = clockModel.getCalendarTime();
        }
        clockModel.setCalendarDateTime(calDate, calTime);
        if (ClockManager.getInstance().isClockedIn()) {
          if (checkBoxSpecifyDate.isChecked() || checkBoxSpecifyTime.isChecked()) {
            clockOut(clockModel.getDescription(), clockModel.getCalendarDateTime().getTime().getTime());
          } else {
            clockOut(clockModel.getDescription(), null);
          }
        } else {
          if (checkBoxSpecifyDate.isChecked() || checkBoxSpecifyTime.isChecked()) {
            clockIn(clockModel.getProject(), clockModel.getCalendarDateTime().getTime().getTime());
          } else {
            clockIn(clockModel.getProject(), null);
          }
          initialiseButtons();
        }
      }
    });

    try {
      loadPreferences();
      try {
        getLastLine();
      } catch (NullPointerException npex) {
        // NOT worried about this issue - it's a known issue and means 
        // the last line cannot be read
      }
      System.out.println("ON CREATE CLOCK ACTIVITY");
      checkTimeStamp();

      ClockInSelector clockInSelector = new ClockInSelector(this);
      spinnerClockIn.setOnItemSelectedListener(clockInSelector);
      initialiseButtons();
    } catch (IOException ioex) {
      showDialog("I/O Exception", ioex.getMessage());
      ioex.printStackTrace();
    } catch (Exception ex) {
      ex.printStackTrace();
      showDialog(
              "Exception", ex.getMessage());
    }
    makeNotification();
    clockModel.addObserver(this);
  }

  /**
   * Gets the last line of the clock file - this is so that the user can see
   * straight away, rather than having to wait for the file to be loaded to find
   * out what the last clock is.
   *
   * @throws IOException if there was any I/O problem reading the file.
   */
  private void getLastLine() throws IOException {
    ReverseLineReader r = new ReverseLineReader(fileTimeClock);
    boolean clocked = false;
    ClockLine lastLine = null;
    while (!clocked) {
      lastLine = new ClockLineFactory().getClockLine(r.readLine());
      if (lastLine != null) {
        line = lastLine;
        clocked = true;
      }
    }
  }

  /**
   *
   * @param year
   * @param month
   * @param day
   * @return
   */
  private Calendar getCalendarDate(int year, int month, int day) {
    Calendar c = Calendar.getInstance();
    c.set(Calendar.YEAR, year);
    c.set(Calendar.MONTH, month);
    c.set(Calendar.DAY_OF_MONTH, day);
    return c;
  }

  /**
   * @param cal
   * @param hour
   * @param minute
   * @return
   */
  private Calendar getCalendarTime(Calendar cal, int hour, int minute) {
    Calendar c = null;
    if (cal == null) {
      c = Calendar.getInstance();
    } else {
      c = cal;
    }
    c.set(Calendar.HOUR_OF_DAY, hour);
    c.set(Calendar.MINUTE, minute);
    c.set(Calendar.SECOND, 0);
    return c;
  }

  @Override
  protected Dialog onCreateDialog(int id) {
    switch (id) {
      case TIME_DIALOG_ID:
        int hour = Calendar.getInstance().get(Calendar.HOUR);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                mTimeSetListener, hour, minute, false);
        timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int button) {
            if (button == DialogInterface.BUTTON_NEGATIVE) {
              checkBoxSpecifyTime.setChecked(false);
            }
          }
        });
        return timePickerDialog;
      case DATE_DIALOG_ID:
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                mDateSetListener, year, month, day);
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int button) {
            if (button == DialogInterface.BUTTON_NEGATIVE) {
              checkBoxSpecifyDate.setChecked(false);
            }
          }
        });

        return datePickerDialog;
//      case DESCRIPTION_DIALOG_ID:
//        AlertDialog.Builder alert = new AlertDialog.Builder(this.getApplicationContext());
//
//        alert.setTitle("Enter Description");
//
//        // Set an EditText view to get user input
//        final EditText input = new EditText(this.getApplicationContext());
//        alert.setView(input);
//
//        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//
//          @Override
//          public void onClick(DialogInterface dialog, int button) {
//            if (button == DialogInterface.BUTTON_POSITIVE) {
//              ClockActivity.this.description = input.getText().toString().trim();
//              clockOut(description, null);
//            }
//          }
//        });
//
//        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//
//          @Override
//          public void onClick(DialogInterface dialog, int button) {
//            if (button == DialogInterface.BUTTON_NEGATIVE) {
//              checkBoxDescription.setChecked(false);
//            }
//          }
//        });
//
//        return alert.create();
    }
    return null;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.menu_main, menu);
      return true;
  }

  /**
   *
   * @param item
   * @return
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_sync) {
      Intent i = new Intent(getApplicationContext(), SyncDropboxActivity.class);
      ClockActivity.this.startActivity(i);
    }
    return true;
  }

  /**
   *
   * @throws IOException
   */
  private void loadPreferences() throws IOException {
    fileTimeClock = Utils.getInstance().getLocalTimeClockFile(this);
    System.out.println("Filename: " + fileTimeClock.getAbsolutePath());
    if (!fileTimeClock.exists()) {

      fileTimeClock.createNewFile();
      System.out.println("File created: " + fileTimeClock.getAbsolutePath());
    }
    lastModified = fileTimeClock.lastModified();
    this.preferencesChanged = false;
  }



  /**
   *
   *
   * @param description
   * @param time
   */
  private void clockOut(String description, Long time) {
    try {
      if (time == null) {
        line = TimeClockUtils.clockOut(fileTimeClock, description);
      } else {
        line = TimeClockUtils.clockOut(fileTimeClock, description, time);
      }
      initialiseButtons();
      makeNotification();
      Editor prefsEditor = prefs.edit();
      System.out.println("saving lastModified="
              + Utils.getInstance().getLocalTimeClockFile(this).lastModified());
      lastModified =
              Utils.getInstance().getLocalTimeClockFile(this).lastModified();
      prefsEditor.putLong(ClockActivity.LAST_MODIFIED, lastModified);
      prefsEditor.apply();
    } catch (ClockException cex) {
      showDialog("ClockException", cex.getMessage());
    } catch (IOException ioex) {
      showDialog("I/O Exception", ioex.getMessage());
    }
  }

  /**
   *
   * @param projectName
   */
  private void clockIn(String projectName, Long time) {
    try {
      lastSelectedProject = projectName;
      String projectToClock = projectName;
      if (ClockManager.DEFAULT_PROJECT_STRING.equals(projectName)) {
        projectToClock = null;
      }
      if (time == null) {
//          if (Utils.getInstance().getParseFileAsyncTask() == null) {
        line = TimeClockUtils.clockIn(fileTimeClock, projectToClock);
//          } else {
//            // we're parsing, add to list then commit afterward:
//            ClockLine cl = new ClockLine();
//            cl.setText(projectName);
//            cl.setClockDate(new Date(Calendar.getInstance().getTimeInMillis()));
//            cl.setClockStatus(ClockInOutEnum.IN);
//            this.clocksWhileParsing.add(cl);
//          }
      } else {
        line = TimeClockUtils.clockIn(fileTimeClock, projectToClock, time);
      }
      initialiseButtons();

      Editor prefsEditor = prefs.edit();
      System.out.println("saving lastModified="
              + Utils.getInstance().getLocalTimeClockFile(this).lastModified());
      lastModified =
              Utils.getInstance().getLocalTimeClockFile(this).lastModified();
      prefsEditor.putLong(ClockActivity.LAST_MODIFIED, lastModified);
      prefsEditor.apply();
      makeNotification();
    } catch (ClockException cex) {
      showDialog("ClockException", cex.getMessage());
    } catch (IOException ioex) {
      showDialog("I/O Exception", ioex.getMessage());
    }
  }

  /**
   *
   */
  private void makeNotification() {
//    // Get a reference to the NotificationManager:
//    String ns = Context.NOTIFICATION_SERVICE;
//    NotificationManager mNotificationManager =
//            (NotificationManager) getSystemService(ns);
//    // Instantiate the Notification:
//
//    int icon = 0;
//    CharSequence tickerText = null;
//    CharSequence contentText = null;
//
//    boolean clockedIn = ClockManager.getInstance().isClockedIn();
//
//    if (clockedIn) {
//      tickerText = "Clocked In";
//      contentText = "Clocked in to '"
//              + ClockManager.getInstance().getCurrentProject() + "'";
//      icon = R.drawable.clock_add;
//    } else {
//      tickerText = "Clocked Out";
//      contentText = "You are currently not clocked in";
//      icon = R.drawable.clock_stop;
//    }
//    long when = System.currentTimeMillis();
//
//    Notification notification = new Notification(icon, tickerText, when);
//    // Define the Notification's expanded message and Intent:
//    Context context = getApplicationContext();
//    CharSequence contentTitle = "Droid Clock";
//
//    Intent notificationIntent = new Intent(this, ClockActivity.class);
//    PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//
//    notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
//    // Pass the Notification to the NotificationManager:
//
//    mNotificationManager.notify(DROID_CLOCK_ID, notification);
  }

  private void initialiseButtons() {
    String currentProject = getCurrentProject();
    boolean clockedIn = isClockedIn();
    if (buttonClock.isEnabled()) {
      if (currentProject != null) {
        textViewClockStatus.setText("Status: Currently clocked-in to '"
                + currentProject + "'");
        textViewClockStatus.setTextColor(Color.GREEN);
      } else {
        textViewClockStatus.setText("Status: Not currently clocked-in");
        textViewClockStatus.setTextColor(Color.RED);
      }
    } else {
      textViewClockStatus.setText("Currently parsing timeclock file");
      textViewClockStatus.setTextColor(Color.RED);
    }
    if (clockedIn) {
      buttonClock.setText("Clock Out");
      checkBoxDescription.setVisibility(View.VISIBLE);
      spinnerClockIn.setVisibility(View.INVISIBLE);
    } else {
      clockModel.setDescription(null);
      checkBoxDescription.setVisibility(View.INVISIBLE);
      spinnerClockIn.setVisibility(View.VISIBLE);
      buttonClock.setText("Clock In");
    }
    checkBoxDescription.setChecked(false);
    checkBoxSpecifyDate.setChecked(false);
    checkBoxSpecifyTime.setChecked(false);
    updateClockInList();
    textViewClockDetails.setText(
            this.getClockDetails(currentProject, clockedIn));
  }

  /**
   * Gets the current project. If the last valid clock line was a clock-in, this
   * is used as the current project. Otherwise, the current project is obtained
   * from the clock manager.
   *
   * @return the last project clocked via the clock file, obtained either
   * directly from the file or from the clock manager.
   */
  private String getCurrentProject() {
    String currentProject = ClockManager.getInstance().getCurrentProject();
//    System.out.println("clock line=" + line);
    if (line != null) {
//      System.out.println("clock status=" + line.getClockStatus());
//      System.out.println("clock text=" + line.getText());
      if (ClockInOutEnum.IN.equals(line.getClockStatus())) {
        currentProject = line.getText();
        if (currentProject == null) {
          // it's the defaut project
          currentProject = ClockManager.DEFAULT_PROJECT_STRING;
        }
      }
    }
    return currentProject;
  }

  /**
   *
   */
  private boolean isClockedIn() {
    boolean clockedIn = false;
    if (line != null) {
      clockedIn = line.getClockStatus() == ClockInOutEnum.IN;
    } else {
      clockedIn = ClockManager.getInstance().isClockedIn();
    }
    return clockedIn;
  }

  /**
   * Sets the details clock text below the clock-in/-out button.
   *
   *
   */
  private void updateClockDetails() {
    this.textViewClockDetails.setText(this.getClockDetails(getCurrentProject(),
            isClockedIn()));
  }

  /**
   * Gets text showing information about clock-in/-out, and what date/time is
   * set for clocking next (after the current clock-in/out).
   *
   *
   *
   * @return human-readable text about the action of the next clock to occur.
   */
  private String getClockDetails(String currentProject, boolean clockedIn) {
    StringBuilder clockDetails = new StringBuilder("Clock-");
    if (clockedIn) {
      clockDetails.append("out of '");
      clockDetails.append(currentProject);
      clockDetails.append("'");
    } else {
      clockDetails.append("in to '");
      Object project = spinnerClockIn.getSelectedItem();
      if (project == null) {
        project = spinnerClockIn.getItemAtPosition(0);
      }
      clockDetails.append(project);
      clockDetails.append("'");
    }
    clockDetails.append(" with ");
    Calendar cal = clockModel.getCalendarDate();
    if (checkBoxSpecifyDate.isChecked()) {
      clockDetails.append("date ");
      clockDetails.append(ClockManager.getInstance().formatDate(
              cal.getTime()).substring(0, 11));
    } else {
      clockDetails.append("the current date");
    }
    clockDetails.append(" and ");
    if (checkBoxSpecifyTime.isChecked()) {
      clockDetails.append("time ");
      clockDetails.append(ClockManager.getInstance().formatDate(
              clockModel.getCalendarTime().getTime()).substring(11, 16));
    } else {
      if (checkBoxSpecifyDate.isChecked()) {
        clockDetails.append("the ");
      }
      clockDetails.append("current time");
    }
    if (checkBoxDescription.isChecked()) {
      clockDetails.append(" with description '");
      clockDetails.append(clockModel.getDescription());
      clockDetails.append("'");
    }
    return clockDetails.toString();
  }

  /**
   *
   */
  private void updateClockInList() {
    Collection<String> clockInList = new ArrayList<String>();
    String[] projectArray = ClockManager.getInstance().getProjects();
    if (Utils.getInstance().getParseFileAsyncTask() != null) {

      SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
      String savedProjects = p.getString(ClockActivity.PROEJCTS, null);
      if (savedProjects != null) {
        projectArray = savedProjects.split("\n");
//        System.out.println("Loaded old project list: " + savedProjects);
      }
    }
    List projects = Arrays.asList(projectArray);

    if (!projects.contains(ClockManager.DEFAULT_PROJECT_STRING)) {
      clockInList.add(ClockManager.DEFAULT_PROJECT_STRING);
    }
    String projectToSelect = lastSelectedProject;
    clockInList.addAll(projects);
    if (newProjectList.size() > 0) {
      clockInList.addAll(newProjectList);
      projectToSelect = newProjectList.iterator().next();
    }
    Collections.sort((List) clockInList);
    clockInList.add("New Project...");
//    if (newItems == null) {
//      spinnerClockIn.setSelection(pos);
//    }
    ArrayAdapter<String> aa = new ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_item,
            clockInList.toArray(new String[]{}));
    aa.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item);
    spinnerClockIn.setAdapter(aa);

    List<String> l = (List) clockInList;

    for (int i = 0; i
            < l.size(); i++) {
      String lookup = l.get(i);
      if (projectToSelect != null && projectToSelect.equals(lookup)) {
        spinnerClockIn.setSelection(i, true);
        break;
      } else {
        spinnerClockIn.setSelection(0, true);
      }
    }
    newProjectList.clear();
  }

  /**
   *
   * @param exception
   * @param message
   */
  private void showDialog(String exception, String message) {
    new AlertDialog.Builder(this).setTitle(exception).setMessage(message).setNeutralButton("Close",
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface arg0, int arg1) {
              }
            }).show();
  }

  @Override
  public void onItemSelected(AdapterView<?> parent,
          View v, int position, long id) {
//    selection.setText(items[position]);
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {
//    selection.setText("");
  }
}
