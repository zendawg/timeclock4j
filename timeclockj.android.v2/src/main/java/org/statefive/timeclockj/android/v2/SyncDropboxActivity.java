package org.statefive.timeclockj.android.v2;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dropbox.core.android.Auth;
import com.dropbox.core.examples.android.DropboxActivity;
import com.dropbox.core.examples.android.DropboxClientFactory;
import com.dropbox.core.examples.android.GetCurrentAccountTask;
import com.dropbox.core.examples.android.ListFolderTask;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;

import java.io.File;


public class SyncDropboxActivity extends DropboxActivity {

    boolean mRemoteFileExists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_dropbox);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        Button loginButton = (Button)findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.startOAuth2Authentication(SyncDropboxActivity.this, getString(R.string.app_key));
            }
        });

        // TODO add listeners to pull and push buttons
        // Don't allow navigation away while doing the pull or push
        // i.e. updating the file
        Button pullButton = (Button) findViewById(R.id.pull_button);
        pullButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start a spinner
                final TextView messageView = (TextView)findViewById(R.id.message_text);
                messageView.setVisibility(View.VISIBLE);
                messageView.setText("Downloading...");
                new PullTimelogTask(SyncDropboxActivity.this, DropboxClientFactory.getClient(),
                        new PullTimelogTask.Callback() {
                            @Override
                            public void onFilePulled(File result) {
                                // print a message; perhaps finish the activity
                                // close the spinner
                                messageView.setText("Pulled file");
                                (SyncDropboxActivity.this).finish();
                            }

                            @Override
                            public void onError(Exception e) {
                                // print a message
                                messageView.setText("Error downloading!");
                                // end the spinner
                            }
                        }).execute();
            }
        });

        Button pushButton = (Button) findViewById(R.id.push_button);
        pushButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView messageView = (TextView)findViewById(R.id.message_text);
                messageView.setVisibility(View.VISIBLE);
                messageView.setText("Uploading...");
                new PushTimelogTask(SyncDropboxActivity.this, DropboxClientFactory.getClient(),
                        new PushTimelogTask.Callback() {
                            @Override
                            public void onUploadComplete(FileMetadata result) {
                                // print a message; perhaps finish the activity
                                // close the spinner
                                messageView.setText("Pushed file");
                                (SyncDropboxActivity.this).finish();
                            }

                            @Override
                            public void onError(Exception e) {
                                messageView.setText("Error downloading!");
                                // end the spinner
                            }
                        }).execute();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (hasToken()) {
            findViewById(R.id.login_button).setVisibility(View.GONE);
            findViewById(R.id.email_text).setVisibility(View.VISIBLE);
            findViewById(R.id.name_text).setVisibility(View.VISIBLE);
            if (mRemoteFileExists) {
                findViewById(R.id.pull_button).setEnabled(true);
            } else {
                findViewById(R.id.pull_button).setEnabled(false);
            }
            // perhaps only allow push if the file has changed locally
            findViewById(R.id.push_button).setEnabled(true);
        } else {
            findViewById(R.id.login_button).setVisibility(View.VISIBLE);
            findViewById(R.id.email_text).setVisibility(View.GONE);
            findViewById(R.id.name_text).setVisibility(View.GONE);
            findViewById(R.id.pull_button).setEnabled(false);
            findViewById(R.id.push_button).setEnabled(false);
        }
        findViewById(R.id.message_text).setVisibility(View.INVISIBLE);
    }

    @Override
    protected void loadData() {
        new GetCurrentAccountTask(DropboxClientFactory.getClient(), new GetCurrentAccountTask.Callback() {
            @Override
            public void onComplete(FullAccount result) {
                ((TextView) findViewById(R.id.email_text)).setText(result.getEmail());
                ((TextView) findViewById(R.id.name_text)).setText(result.getName().getDisplayName());
            }

            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Failed to get account details.", e);
            }
        }).execute();

        // Check if there is a timelog file in the App's folder in Dropbox
        // Allow PULL if it exists
        new ListFolderTask(DropboxClientFactory.getClient(), new ListFolderTask.Callback() {
            @Override
            public void onDataLoaded(ListFolderResult result) {
                for (Metadata metadata : result.getEntries()) {
                    if (getString(R.string.timeclockj_timelog).equals(metadata.getName())) {
                        mRemoteFileExists = true;
                        findViewById(R.id.pull_button).setEnabled(true);
                        break;
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Failed to list folder.", e);
            }
        }).execute("");

    }
}
