package com.tutorial;

import android.os.Bundle;
import android.app.Activity;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.tutorial.util.LogUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MusicActivity extends Activity {
    private static final String SONG_URL = "http://api.androidhive.info/music/music.xml";

    private LazyAdapter adapter;

    private static final boolean DEVELOPER_MODE = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (DEVELOPER_MODE) {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                    .detectDiskReads()
//                    .detectDiskWrites()
//                    .detectNetwork()   // or .detectAll() for all detectable problems
//                    .penaltyLog()
//                    .build());
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                    .detectLeakedSqlLiteObjects()
//                    .detectLeakedClosableObjects()
//                    .penaltyLog()
//                    .penaltyDeath()
//                    .build());

            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .build());
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        List<Map<String, String>> songList = new ArrayList<Map<String, String>>();
        XMLParser parser = new XMLParser();
        String xml = parser.getXmlFromUrl(SONG_URL);
        Document doc = parser.getDomElement(xml);
        NodeList nodeList = doc.getElementsByTagName(Song.NODE_NAME);
        for (int i = 0, len = nodeList.getLength(); i < len; i++) {
            Map<String, String> song = new TreeMap<String, String>();
            Element e = (Element) nodeList.item(i);
            // add each child node to Map key => value
            song.put(Song.ID, parser.getValue(e, Song.ID));
            song.put(Song.TITLE, parser.getValue(e, Song.TITLE));
            song.put(Song.ARTIST, parser.getValue(e, Song.ARTIST));
            song.put(Song.DURATION, parser.getValue(e, Song.DURATION));
            song.put(Song.THUMB_URL, parser.getValue(e, Song.THUMB_URL));

            // add a song to List
            songList.add(song);
        }
        Log.d(LogUtils.LOG_ID, songList.toString());

        ListView listView = (ListView) findViewById(R.id.list);
        // get an adapter by passing xml data list
        adapter = new LazyAdapter(this, songList);
//        adapter = new LazyAdapter(this.getApplicationContext(), songList);
        listView.setAdapter(adapter);

        // click event for single list row
        listView.setOnItemClickListener(itemClickListener);
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Map<String, String> song = (Map<String, String>) adapter.getItem(position);
            if (song != null) {
                Log.d(LogUtils.LOG_ID, song.toString());

                ImageView thumbImage = (ImageView) view.findViewById(R.id.thumb_image);
                ImageLoader imageLoader = adapter.getImageLoader();
                imageLoader.displayImage(song.get(Song.THUMB_URL), thumbImage);
                adapter.notifyDataSetInvalidated();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // release resource
        adapter.clear();
    }
}
