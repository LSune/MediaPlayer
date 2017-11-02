package com.example.unreallover.mediaplayer;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.unreallover.mediaplayer.Adapter.PlayListAdapter;
import com.example.unreallover.mediaplayer.Entities.Music;
import com.example.unreallover.mediaplayer.Utils.Constant;
import com.example.unreallover.mediaplayer.Utils.MyApplication;
import com.example.unreallover.mediaplayer.Utils.ScanUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static com.example.unreallover.mediaplayer.Utils.MyApplication.*;

public class PlayListActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        List<Music> musicList = (List<Music>) new ScanUtils().Scanmusic();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerv_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        PlayListAdapter adapter = new PlayListAdapter(musicList);
        recyclerView.setAdapter(adapter);
    }
}
