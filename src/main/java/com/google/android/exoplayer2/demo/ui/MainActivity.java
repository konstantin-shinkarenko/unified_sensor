package com.google.android.exoplayer2.demo.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.conviva.sdk.ConvivaAnalytics;
import com.google.android.exoplayer2.demo.R;
import com.google.android.exoplayer2.demo.helper.ConvivaHelper;
import com.google.android.exoplayer2.demo.utils.VideoItem;
import com.google.android.exoplayer2.demo.utils.VideoMetadata;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.list_layout);

        ListView listView = findViewById(R.id.ads_list);
        VideoItemAdapter mAdapter = new VideoItemAdapter(this,
                R.layout.video_item, getVideoItems());
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                VideoMetadata videoMetadata = VideoMetadata.APP_VIDEOS.get(i);

                Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                intent.setData(Uri.parse(videoMetadata.videoUrl));
                intent.putExtra(PlayerActivity.EXTENSION_EXTRA, "");
                intent.putExtra(PlayerActivity.AD_TAG_URI_EXTRA, "");
                intent.putExtra(PlayerActivity.AD_TAG_URI, videoMetadata.adTagUrl);
                intent.setAction(PlayerActivity.ACTION_VIEW);
                startActivity(intent);
            }
        });

        ConvivaHelper.init(getApplicationContext());
    }

    private List<VideoItem> getVideoItems() {
        final List<VideoItem> videoItems = new ArrayList<>();

        for (int i = 0; i < VideoMetadata.APP_VIDEOS.size(); i++) {
            VideoMetadata videoMetadata = VideoMetadata.APP_VIDEOS.get(i);
            videoItems.add(new VideoItem(videoMetadata.title,
                    videoMetadata.thumbnail));
        }
        return videoItems;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConvivaAnalytics.release();
    }
}
