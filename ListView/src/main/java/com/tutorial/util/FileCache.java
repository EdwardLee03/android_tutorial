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
    private File cacheDir;
    private Map<String, File> nameFiles; // <filename, File>

    public FileCache(Context context) {
        // Find the dir to save cached images
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(Environment.getExternalStorageDirectory(), "Music Showcase");
        } else {
            cacheDir = context.getCacheDir();
        }

        nameFiles = Collections.synchronizedMap(
                new HashMap<String, File>());
        if (cacheDir != null) {
            if (cacheDir.exists()) {
                Log.d(LogUtils.LOG_ID, cacheDir.getAbsolutePath());

                File[] files = cacheDir.listFiles();
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        nameFiles.put(file.getName(), file);
                    }
                }
            } else {
                cacheDir.mkdirs();
            }
        }
    }

    public File getFile(String filename) {
        if (nameFiles.containsKey(filename)) {
            return nameFiles.get(filename);
        } else {
            File file = new File(cacheDir, filename);
            nameFiles.put(filename, file);

            Log.d(LogUtils.LOG_ID, file.getAbsolutePath());

            return file;
        }
    }

//    public void clear() {
//        File[] files = cacheDir.listFiles();
//        if (files == null)
//            return;
//        for (File file : files) {
//            file.delete();
//        }
//    }
}
