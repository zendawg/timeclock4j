/*
 * Copyright (c) 2010 Evenflow, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package org.statefive.timeclockj.android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;

public class DropboxActivity extends Activity implements FileParseListener,
        DropboxListener {

  private static final String TAG = "DropboxActivity";
  final static public String ACCOUNT_PREFS_NAME =
          DropboxActivity.class.getName() + ".prefs";
//  final static public String ACCESS_KEY_NAME = "ACCESS_KEY";
//  final static public String ACCESS_SECRET_NAME = "ACCESS_SECRET";
  private boolean mLoggedIn;
  private boolean authInProgress;
  private Button mSubmit;
  private Button buttonPush;
  private Button buttonPull;
  private TextView mText;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    System.out.println("------------------------------");
    System.out.println("      DROPBOX ACTIVITY: CREATE");
    System.out.println("------------------------------");
    DropboxUpdateTool.getInstance().addListener(this);
    setContentView(R.layout.dropboxactivity);

    ParseFileAsyncTask task = Utils.getInstance().getParseFileAsyncTask();
    if (task != null) {
      // we're in the process of still loading the original file -
      // so inform us when it's done:
      task.addFileParseListener(this);
    }
    mSubmit = (Button) findViewById(R.id.login_submit);
    buttonPush = (Button) findViewById(R.id.buttonPush);
    buttonPull = (Button) findViewById(R.id.buttonPull);
    mSubmit = (Button) findViewById(R.id.login_submit);
    mText = (TextView) findViewById(R.id.text);
    String info = "Name:\n"
            + "E-mail:\n"
            + "User ID:\n"
            + "Quota: ";
    mText.setText(info);
    mSubmit.setEnabled(false);
    buttonPush.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        buttonPull.setEnabled(false);
        buttonPush.setEnabled(false);
        pushTimeClockFile();
      }
    });
    buttonPull.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        buttonPull.setEnabled(false);
        buttonPush.setEnabled(false);
        pullTimeClockFile();
      }
    });

    mSubmit.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
//        checkLoggedIn();
        System.out.println("################### " + mLoggedIn);
        if (mLoggedIn) {
          logout();
        } else {
          login();
        }
      }
    });
    buttonPull.setEnabled(false);
    buttonPush.setEnabled(false);

  }

  @Override
  protected void onStart() {
    super.onStart();
    System.out.println("------------------------------");
    System.out.println("      DROPBOX ACTIVITY: ON START");
    System.out.println("------------------------------");
//    checkLoggedIn();
    if (!mLoggedIn) {
      Thread dropboxAuthActivity = new Thread() {
        @Override
        public void run() {
          Intent intent = new Intent(getBaseContext(), DropboxAuthActivity.class);
          startActivity(intent);
        }
      };
      dropboxAuthActivity.start();
      System.out.println("------------------------------");
      System.out.println("      DROPBOX ACTIVITY: check logged in finished");
      System.out.println("------------------------------");
    }
  }

  /**
   *
   */
  @Override
  protected void onResume() {
    super.onResume();
  }

  public void dropboxLogin(boolean loggedIn, DropboxAPI.Account account) {
    System.out.println("[[[[[[[[[[[[ logged in = " + loggedIn);
    setLoggedIn(loggedIn);
    displayAccountInfo(account);
  }

  public void dropboxPushPullUpdate(boolean pullInProgress) {
    buttonPull.setEnabled(!pullInProgress);
    buttonPush.setEnabled(!pullInProgress);
  }

  public void dropboxAuthorising(boolean authorising) {
    System.out.println("[[[[[[[[[[[[ authorising = " + authorising);
    this.authInProgress = authorising;
    if (mLoggedIn) {
      mSubmit.setEnabled(!this.authInProgress);
    }
  }

  /**
   * Convenience function to change UI state based on being logged in
   */
  public void setLoggedIn(boolean loggedIn) {
    System.out.println("(((((((((((( Setting logged in : " + loggedIn);
    mLoggedIn = loggedIn;
    if (mLoggedIn) {
      mSubmit.setText("Log Out of Dropbox");
    } else {
      mSubmit.setText("Log In to Dropbox");
//      api = null;
    }
    buttonPull.setEnabled(loggedIn);
    buttonPush.setEnabled(loggedIn);
  }

  /**
   *
   * @param status
   */
  @Override
  public void fileParseUpdate(Status status) {
    if (mLoggedIn && status == Status.FINISHED) {
      buttonPush.setEnabled(true);
      buttonPull.setEnabled(true);
    }
  }

  /**
   *
   * @param msg
   */
  public void showToast(String msg) {
    Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
    error.show();
  }

  /**
   * Displays some useful info about the account, to demonstrate that we've
   * successfully logged in
   *
   * @param account
   */
  public void displayAccountInfo(DropboxAPI.Account account) {
    if (account != null) {
      long bytes = account.quota;
      int unit = 1024;
      String info = "Name: " + account.displayName + "\n"
              + "User ID: " + account.uid + "\n"
              + "Quota: " + Utils.humanReadableByteCount(account.quota, false);
      mText.setText(info);
    } else {
      String info = "Name:\n"
              + "E-mail:\n"
              + "User ID:\n"
              + "Quota: ";
      mText.setText(info);
    }
  }

  /**
   *
   */
  private void logout() {
    DropboxUpdateTool.getInstance().getApi().getSession().unlink();

    DropboxUpdateTool.getInstance().clearKeys(getSharedPreferences(
            DropboxUpdateTool.ACCOUNT_PREFS_NAME, 0));
    setLoggedIn(false);
  }

  private void login() {
    System.out.println("````````````````` login() called");
    Thread dropboxAuthActivity = new Thread() {
      @Override
      public void run() {
        Intent intent = new Intent(getBaseContext(), DropboxAuthActivity.class);
        startActivity(intent);
      }
    };
    System.out.println("````````````````` thread about to start");
    dropboxAuthActivity.start();
    System.out.println("````````````````` started");
  }

  /**
   *
   */
  private void pullTimeClockFile() {

    Thread toRun = new Thread() {
      @Override
      public void run() {
        Intent intent = new Intent(getBaseContext(), DropboxPullActivity.class);
        startActivity(intent);
      }
    };
    toRun.start();
  }

  /**
   *
   */
  private void pushTimeClockFile() {

    Thread toRun = new Thread() {
      @Override
      public void run() {
        Intent intent = new Intent(getBaseContext(), DropboxPushActivity.class);
        startActivity(intent);
      }
    };
    toRun.start();
  }
}
