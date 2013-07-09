package com.tutorial;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.tutorial.util.FileCache;
import com.tutorial.util.IOUtils;
import com.tutorial.util.LogUtils;
import com.tutorial.util.MemoryCache;

//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Asynchronous Image Loader.
 *
 * Created by lihg on 13-7-5.
 */
public class ImageLoader {
    private MemoryCache memoryCache;
    private FileCache fileCache;
    private Map<ImageView, String> imageViews; // <ImageView, url>
    private ExecutorService executorService;

    public ImageLoader(Context context) {
        memoryCache = new MemoryCache();
        imageViews = Collections.synchronizedMap(
                new WeakHashMap<ImageView, String>());
        fileCache = new FileCache(context);
        executorService = Executors.newFixedThreadPool(5); // 4 cores for Android OS generally
    }

    final int stubId = R.drawable.no_image;

    public void displayImage(String url, ImageView imageView) {
        Bitmap bitmap = memoryCache.get(getFileName(url));
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            // 标识图像还未加载到本地
            imageView.setImageResource(stubId);
            synchronizedLoadPhoto(url, imageView);
        }
        imageViews.put(imageView, url);
    }

    private void synchronizedLoadPhoto(String url, ImageView imageView) {
        PhotoLoadTask task = new PhotoLoadTask(url, imageView);
        executorService.submit(new PhotosLoader(task));
    }

    /*
     * Task for the Queue.
     */
    private class PhotoLoadTask {
        private String url;
        private ImageView imageView;

        public PhotoLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }
    }

    private class PhotosLoader implements Runnable {
        private PhotoLoadTask photoLoadTask;

        PhotosLoader(PhotoLoadTask photoLoadTask) {
            this.photoLoadTask = photoLoadTask;
        }

        @Override
        public void run() {
            if (isImageViewReuse(photoLoadTask))
                return;

            Bitmap bitmap = getBitmap(photoLoadTask.url);
            memoryCache.put(getFileName(photoLoadTask.url), bitmap);
            Log.d(LogUtils.LOG_ID, memoryCache.toString());

            if (isImageViewReuse(photoLoadTask))
                return;
            BitMapShower shower = new BitMapShower(bitmap, photoLoadTask);
            Activity activity = (Activity) photoLoadTask.imageView.getContext();
            if (activity != null) {
                Log.d(LogUtils.LOG_ID, "run on UI thread");
                activity.runOnUiThread(shower); // UI thread
            }
        }
    }

    private boolean isImageViewReuse(PhotoLoadTask photoLoadTask) {
        String url = imageViews.get(photoLoadTask.imageView);
        return url == null || !url.equals(photoLoadTask.url);
    }

    private Bitmap getBitmap(String imageUrl) {
        File file = fileCache.getFile(getFileName(imageUrl));

        // from SD cache
        Bitmap bitmap = decodeFile(file);
        if (bitmap != null)
            return bitmap;

        // from Web
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(10000);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            InputStream in = conn.getInputStream();
            OutputStream out = new FileOutputStream(file);
            IOUtils.copy(in, out);
            in.close();
            out.close();
            conn.disconnect();

            // Use HttpClient is too slowly!
////            HttpClient httpClient = AndroidHttpClient.newInstance("Android");
//            HttpClient httpClient = new DefaultHttpClient();
//            HttpGet get = new HttpGet(imageUrl);
//            HttpResponse response = httpClient.execute(get);
//            HttpEntity entity = response.getEntity();
//            byte[] imageData = EntityUtils.toByteArray(entity);
//            OutputStream out = new FileOutputStream(file);
//            IOUtils.copy(imageData, out);
//            out.close();

            bitmap = decodeFile(file);

            return bitmap;
        } catch (MalformedURLException mue) {
            Log.e(LogUtils.LOG_ID, "URL is not valid: " + imageUrl);
        } catch (IOException e) {
            Log.e(LogUtils.LOG_ID, "Can't open connection: " + imageUrl);
        }
        return null;
    }

    // decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File file) {
        if (! file.exists())
            return null;


        try {
            // 1, decode image size
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream( new FileInputStream(file), null, options);

            // 2, find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 140;
            int width = options.outWidth;
            int height = options.outHeight;
            int scale = 1;
            while (width >= REQUIRED_SIZE && height >= REQUIRED_SIZE) {
                width >>= 1;
                height >>= 1;
                scale <<= 1;
            }

            // 3, decode with inSampleSize
            options = new BitmapFactory.Options();
            options.inSampleSize = scale;
            return BitmapFactory.decodeStream( new FileInputStream(file), null, options);
        } catch (FileNotFoundException e) {
            Log.e(LogUtils.LOG_ID, "File not found: " + file.getName());
        }
        return null;
    }

    /**
     * Used to display bitmap in the UI thread.
     */
    class BitMapShower implements Runnable {
        private Bitmap bitmap;
        private PhotoLoadTask photoLoadTask;

        public BitMapShower(Bitmap bitmap, PhotoLoadTask photoLoadTask) {
            this.bitmap = bitmap;
            this.photoLoadTask = photoLoadTask;
        }

        @Override
        public void run() {
            if (isImageViewReuse(photoLoadTask))
                return;

            Log.d(LogUtils.LOG_ID, "BitMap show: " + photoLoadTask.url);

            if (bitmap != null)
                photoLoadTask.imageView.setImageBitmap(bitmap);
            else
                photoLoadTask.imageView.setImageResource(stubId); // 标识图像加载失败
        }
    }

    public void clearCache() {
        memoryCache.clear();
//        fileCache.clear();
    }

    /**
     * Gets the last file name for the URL.
     *
     * @param url image URI
     * @return file name for the URL
     */
    private static String getFileName(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }
}
