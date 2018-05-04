package org.statefive.timeclockj.android.v2;

import android.content.Context;
import android.os.AsyncTask;


import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class PullTimelogTask extends AsyncTask<Void, Void, File> {

    private final DbxClientV2 mDbxClient;
    private final Callback mCallback;
    private final Context mContext;
    private Exception mException;

    public interface Callback {
        void onFilePulled(File result);

        void onError(Exception e);
    }

    PullTimelogTask(Context context, DbxClientV2 dbxClient, Callback callback) {
        mContext = context;
        mDbxClient = dbxClient;
        mCallback = callback;
    }

    @Override
    protected void onPostExecute(File result) {
        super.onPostExecute(result);

        if (mException != null) {
            mCallback.onError(mException);
        } else {
            mCallback.onFilePulled(result);
        }
    }

    @Override
    protected File doInBackground(Void... params) {
        try {
            ListFolderResult result = mDbxClient.files().listFolder("");
            for (Metadata metadata : result.getEntries()) {
                if (mContext.getResources().getString(R.string.timeclockj_timelog).equals(metadata.getName())) {

                    // Download the file.
                    if (metadata instanceof FileMetadata) {
                        return downloadFile((FileMetadata) metadata);
                    }
                    else {
                        // directory of same name!
                    }
                }
            }


        } catch (DbxException e) {
            mException = e;
        }

        return null;
    }

    File downloadFile(FileMetadata metadata) {
        try {
            // overwrite the local file - this is dangerous if interrupted!
            File file = Utils.getInstance().getLocalTimeClockFile(mContext);

            OutputStream outputStream = new FileOutputStream(file);

            mDbxClient.files().download(metadata.getPathLower(), metadata.getRev())
                    .download(outputStream);
            return file;
        }
        catch (DbxException | IOException e) {
            mException = e;
        }

        return null;
    }
}
