package com.tutorial.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * File Cache.
 *
 * Created by lihg on 13-7-5.
 */
public class FileCache {
    private File rootDir;
    private Map<String, File> nameFiles; // <filename, File>

    public FileCache(Context context) {
        // Find the dir to save images
        if (IOUtils.isExternalStorageWritable()) {
            // External Storage (外部存储)
//            String filename = "Music Showcase";
            // This directory may not currently be accessible
            File path = Environment.getExternalStorageDirectory(); // /mnt/sdcard
            // where the user will typically place and manage their own files.
//            path = Environment.getExternalStoragePublicDirectory(
//                    Environment.DIRECTORY_PICTURES); // public files
            rootDir = new File(path, "Music Showcase");

            Log.d(LogUtils.LOG_ID, "External Storage can write");
        } else {
            // Internal Storage (内部存储)
            // internal directory for app
            rootDir = context.getCacheDir(); // app's temporary cache files
//            context.getFilesDir(); // app files
        }

        nameFiles = Collections.synchronizedMap(
                new HashMap<String, File>());
        if (rootDir.exists()) {
            Log.d(LogUtils.LOG_ID, rootDir.getAbsolutePath());

            File[] files = rootDir.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    nameFiles.put(file.getName(), file);
                }
            }
        } else {
            if (!rootDir.mkdirs()) {
                Log.e(LogUtils.LOG_ID, rootDir + " directory not created");
            }
        }
    }

    public File getFile(String filename) {
        if (nameFiles.containsKey(filename)) {
            return nameFiles.get(filename);
        } else {
            File file = new File(rootDir, filename);
            nameFiles.put(filename, file);
            return file;
        }
    }

/*    public void clear() {
        File[] files = rootDir.listFiles();
        if (files == null)
            return;
        for (File file : files) {
            file.delete();
        }
    }*/
}
