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

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;

public class LoginAsyncTask extends AsyncTask<Void, Void, Integer> {

  private static final String TAG = "LoginAsyncTask";
  private DropboxAPI<AndroidAuthSession> api;
  private AndroidAuthSession session;
  private DropboxActivity activity;
  String mUser;
  Context context;
  String mPassword;
  String mErrorMessage = "";
  DropboxActivity mDropboxSample;
  DropboxAPI.Account mAccount;
  AppKeyPair appKeys;

  // Will just log in
  public LoginAsyncTask(DropboxActivity activity, String key, String secret) {
    super();
    appKeys = new AppKeyPair(key, secret);
    session = new AndroidAuthSession(appKeys, Session.AccessType.DROPBOX);
    api = new DropboxAPI<AndroidAuthSession>(session);
    this.activity = activity;
  }

  @Override
  protected Integer doInBackground(Void... params) {
    Integer status = 0;
    try {
        System.out.println("============== DO IN BACKGROUND");
      String[] keys = null;//activity.getKeys();
      if (keys != null) {
        api.getSession().setAccessTokenPair(new AccessTokenPair(keys[0], keys[1]));
      } else {
        api.getSession().startAuthentication(activity);
      }
      if (api != null && api.getSession().authenticationSuccessful()) {
        try {
          activity.displayAccountInfo(api.accountInfo());
          api.getSession().finishAuthentication();
          System.out.println("============== auth finished");
        } catch (DropboxException dex) {
          dex.printStackTrace();
        }
      }
//      activity.setDropboxApi(api);

    } catch (Exception e) {
      Log.e(TAG, "Error in logging in.", e);
    }
    return status;
  }

  @Override
  protected void onPostExecute(Integer result) {
//    Log.e(LoginAsyncTask.class.getName(), "result: " + result);
//    System.out.println("============== POST EXECUTE");
//    System.out.println("============== API=" + api);
//    
//    if (api != null && api.getSession().authenticationSuccessful()) {
//      System.out.println("============== success=" + api.getSession().authenticationSuccessful());
//      try {
//        // MANDATORY call to complete auth.
//        // Sets the access token on the session
//        api.getSession().finishAuthentication();
//
//        // Provide your own storeKeys to persist the access token pair
//        // A typical way to store tokens is using SharedPreferences
//        activity.setLoggedIn(true);
//        try {
//          activity.displayAccountInfo(api.accountInfo());
//          activity.storeKeys(appKeys.key, appKeys.secret);
//        } catch (DropboxException dex) {
//          dex.printStackTrace();
//        }
//      } catch (IllegalStateException e) {
//        Log.i("DbAuthLog", "Error authenticating", e);
//      }
//    } else {
//      System.out.println("============== success=false");
//    }
  }
  
}
