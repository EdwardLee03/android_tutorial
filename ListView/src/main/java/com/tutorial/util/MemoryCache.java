package com.tutorial.util;

import android.graphics.Bitmap;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Memory Cache.
 *
 * Created by lihg on 13-7-5.
 */
public class MemoryCache {
    private Map<String, SoftReference<Bitmap>> cache; // <filename, Bitmap>

    public MemoryCache() {
        cache = Collections.synchronizedMap(
                new HashMap<String, SoftReference<Bitmap>>());
    }

    public Bitmap get(String id) {
        if (!cache.containsKey(id))
            return null;

        SoftReference<Bitmap> ref = cache.get(id);
        return ref.get();
    }

    public void put(String id, Bitmap bitmap) {
        cache.put(id, new SoftReference<Bitmap>(bitmap));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("MemoryCache[ ");
        sb.append("size: ").append(cache.size()).append(", ")
          .append("keys: ").append(cache.keySet().toString())
          .append(" ]");
        return sb.toString();
    }

    public void clear() {
        cache.clear();
    }
}
