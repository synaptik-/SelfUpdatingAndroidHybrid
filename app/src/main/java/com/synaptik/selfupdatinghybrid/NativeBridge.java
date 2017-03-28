package com.synaptik.selfupdatinghybrid;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.webkit.JavascriptInterface;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class NativeBridge {
    private static final String TAG = NativeBridge.class.getSimpleName();

    private Context mContext;
    private File mDataDir;

    public NativeBridge(Context context) {
        this.mContext = context;

        this.mDataDir = this.mContext.getFilesDir();
    }

    @JavascriptInterface
    public void deleteUpdates() {
        Log.i(TAG, "deleteUpdates");
        File[] files = mDataDir.listFiles();
        for (File f : files) {
            f.delete();
        }
    }

    @JavascriptInterface
    public void downloadUpdates() {
        Log.i(TAG, "downloadUpdates()");

        Uri updateUri = Uri.parse("https://www.dropbox.com/s/v9q7jtu1yvairmg/assets.zip?dl=1");
        File f = fetchUpdate(updateUri);
        if (f != null) {
            unzipUpdateFile(f);
            f.delete();
        }
    }

    @JavascriptInterface
    public boolean isUpdateAvailable(String inputVersion) {
        Log.i(TAG, "isUpdateAvailable(" + inputVersion + ")");
        boolean result = false;

        // TODO - You could fetch a remote file that contains the version of the latest update and compare the two values
        if (inputVersion != null && inputVersion.equals("1.0")) {
            result = true;
        }

        return result;
    }

    private File fetchUpdate(Uri uri) {
        File result = null;
        try {
            URL u = new URL(uri.toString());
            InputStream is = u.openStream();

            DataInputStream dis = new DataInputStream(is);

            byte[] buffer = new byte[1024];
            int length;

            result = new File(this.mDataDir.getCanonicalPath() + "/update.zip");
            FileOutputStream fos = new FileOutputStream(result);
            while ((length = dis.read(buffer))>0) {
                fos.write(buffer, 0, length);
            }

            Log.i(TAG, "Update file downloaded! " + uri.toString());

        } catch (Exception ex) {
            Log.e(TAG, "Unable to download update file. " + uri.toString(), ex);
        }

        return result;
    }

    private void unzipUpdateFile(File f) {
        FileInputStream fis = null;
        ZipInputStream zis = null;
        String zipFile = null;
        try {
            zipFile = f.getCanonicalPath();
            String filename;
            File outputFile;
            fis = new FileInputStream(zipFile);
            zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry zipEntry;
            byte[] buffer = new byte[1024];
            int count;

            while ((zipEntry = zis.getNextEntry()) != null) {
                filename = zipEntry.getName();
                outputFile = new File(this.mDataDir.getCanonicalPath() + "/" + filename);

                if (zipEntry.isDirectory()) {
                    outputFile.mkdirs();
                } else {
                    FileOutputStream fout = new FileOutputStream(outputFile);

                    while ((count = zis.read(buffer)) != -1) {
                        fout.write(buffer, 0, count);
                    }

                    fout.close();
                    zis.closeEntry();

                    Log.d(TAG, "Update file written to " + outputFile.getCanonicalPath());
                }
            }
        } catch(Exception ex) {
            Log.e(TAG, "Unable to unzip file: " + zipFile, ex);
        } finally {
            if (zis != null) {
                try {
                    zis.closeEntry();
                    zis.close();
                } catch (IOException ex) {
                    // ignore
                }
            }
        }
    }
}
