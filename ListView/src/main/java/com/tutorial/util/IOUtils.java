package com.tutorial.util;

import android.os.Environment;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * IO Utility class.
 *
 * Created by lihg on 13-7-5.
 */
public class IOUtils {
    /**
     * Checks if external storage is available for read and write.
     *
     * @return true: if the External Storage can read and write.
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private static final int EOF = -1;
    private static final int DEFAULT_BUFFER_SIZE = 1024; // 1KB

    public static void copy(InputStream input, OutputStream output) {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int count;
        try {
            while ((count = input.read(buffer, 0, DEFAULT_BUFFER_SIZE)) != EOF) {
                output.write(buffer, 0, count);
            }
        } catch (IOException ioe) {
            Log.e(LogUtils.LOG_ID, ioe.getMessage());
        }
    }
}
