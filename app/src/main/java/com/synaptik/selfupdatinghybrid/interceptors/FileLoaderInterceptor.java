package com.synaptik.selfupdatinghybrid.interceptors;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.webkit.WebResourceResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileLoaderInterceptor extends BaseInterceptor {
    private static final String TAG = FileLoaderInterceptor.class.getSimpleName();

    public FileLoaderInterceptor(Context context) {
        super(context);
    }

    @Override
    public boolean canHandle(Uri uri) {
        return uri.getHost().equals("app");
    }

    @Override
    public WebResourceResponse handle(Uri uri) {
        WebResourceResponse result = null;
        try {
            String fileExt = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExt);
            if (mimeType == null) {
                mimeType = "text/plain";
            }
            // +1 to remove the preceeding '/' (AssetManager doesn't like it)
            String file = uri.getPath().substring(1);

            InputStream fileStream = fetchFile(file);
            result = new WebResourceResponse(mimeType, "utf8", fileStream);
        } catch (IOException ex) {
            Log.e(TAG, "Unable to open Uri: " + uri, ex);
        }

        return result;
    }

    /**
     * This method will return the requested file by first looking for it in the 'updates' folder
     * in the applications writeable directory. If it doesn't exist, it will fall back to the
     * bundled assets folder.
     */
    private InputStream fetchFile(String file) throws IOException {
        InputStream result = null;

        String dataPath = this.mContext.getFilesDir().getCanonicalPath() + "/" + file;
        File dataFile = new File(dataPath);
        if (dataFile.exists()) {
            Log.d(TAG, "Loading from data folder: " + dataPath);
            result = new FileInputStream(dataFile);
        } else {
            Log.d(TAG, "Loading from assets folder: " + file);
            result = this.mContext.getAssets().open(file);
        }
        return result;
    }
}
