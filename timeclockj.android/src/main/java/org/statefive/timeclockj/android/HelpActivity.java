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
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author rich
 */
public class HelpActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    WebView webview = new WebView(this);
    webview.setWebViewClient(new WebViewClient() {

      public boolean shouldOverrideUrlLloading(WebView w, String url) {
        System.out.println(url);
        return true;
      }
    });
//    webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
    webview.getSettings().setBuiltInZoomControls(true);
//    webview.getSettings().setDefaultFontSize(9);
    webview.getSettings().setTextSize(TextSize.NORMAL);
    webview.getSettings().setSupportZoom(true);
//    webview.getSettings().setDefaultZoom(ZoomDensity.FAR);
    webview.getSettings().setUseWideViewPort(true);
    setContentView(webview);

//    webview.loadUrl("file:///android_asset/docs/html/timeclockj.html");
//    Url("file:///android_asset/docs/html/timeclockj.html"
    try {
      InputStream is = getAssets().open("timeclockj-for-Android.html");
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader br = new BufferedReader(isr);
      String html = "";
      String currentLine;
      while ((currentLine = br.readLine()) != null) {
        html += currentLine;
      }
      // if it's portrait orientation, show warning:
      if (Configuration.ORIENTATION_PORTRAIT ==
              getResources().getConfiguration().orientation) {
        new AlertDialog.Builder(this).setTitle("Screen Width").setMessage(
          "Pages are" +
          " best viewed in wide-screen (i.e. with the phone sideways)").setNeutralButton("Close",
          new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {
            }
          }).show();
      }
      webview.loadDataWithBaseURL("file:///android_asset/", html,
        "text/html", "UTF-8", null);
    } catch (IOException ioex) {

      new AlertDialog.Builder(this).setTitle("Error").setMessage(ioex.getMessage()).setNeutralButton("Close",
        new DialogInterface.OnClickListener() {

          public void onClick(DialogInterface arg0, int arg1) {
          }
        }).show();
    }
  }
}
