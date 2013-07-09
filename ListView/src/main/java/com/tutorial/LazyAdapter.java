package com.tutorial;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Lazy Adapt for List View.
 *
 * Created by lihg on 13-7-6.
 */
public class LazyAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ImageLoader imageLoader;
    private List<Map<String, String>> data;

    public LazyAdapter(Context context, List<Map<String, String>> data) {
        inflater = LayoutInflater.from(context);
        imageLoader = new ImageLoader(context);
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.list_row, null);
        }

        if (view != null) {
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView artist = (TextView) view.findViewById(R.id.artist);
            TextView duration = (TextView) view.findViewById(R.id.duration);
            ImageView thumbImage = (ImageView) view.findViewById(R.id.thumb_image);

            // Setting all values in ListView
            Map<String, String> song = data.get(position);
            title.setText(song.get(Song.TITLE));
            artist.setText(song.get(Song.ARTIST));
            duration.setText(song.get(Song.DURATION));
            imageLoader.displayImage(song.get(Song.THUMB_URL), thumbImage);
            this.notifyDataSetChanged(); // Important!
        }

        return view;
    }

    public void clear() {
        imageLoader.clearCache();
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}