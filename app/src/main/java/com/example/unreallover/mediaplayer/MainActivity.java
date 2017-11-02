package com.example.unreallover.mediaplayer;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unreallover.mediaplayer.Adapter.PlayListAdapter;
import com.example.unreallover.mediaplayer.Entities.Music;
import com.example.unreallover.mediaplayer.Services.MusicService;
import com.example.unreallover.mediaplayer.Utils.Constant;
import com.example.unreallover.mediaplayer.Utils.MyApplication;

import java.io.Serializable;
import java.util.List;

import static com.example.unreallover.mediaplayer.Utils.Constant.PERMISSIONS_STORAGE;
import static com.example.unreallover.mediaplayer.Utils.Constant.REQUEST_EXTERNAL_STORAGE;
import static com.example.unreallover.mediaplayer.Utils.Constant.SER_KEY;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{



    // 获取界面中显示歌曲标题、作者文本框
    TextView title, artist;
    // 播放/暂停、停止按钮
    ImageButton play;
    ActivityReceiver activityReceiver;

    // 定义音乐的播放状态，Constant.M_NONE代表没有播放；Constant.M_PLAYING代表正在播放；Constant.M_PAUSE代表暂停
    int status = Constant.M_NONE;
    List<Music> musicList;
    int iD;
    public static final String CTL_ACTION =
            "CTL_ACTION";
    public static final String UPDATE_ACTION =
            "UPDATE_ACTION";
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        verifyStoragePermissions(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 获取程序界面界面中的两个按钮
        play = (ImageButton) this.findViewById(R.id.play);
        title = (TextView) findViewById(R.id.title);
        artist = (TextView) findViewById(R.id.artist);
        // 为两个按钮的单击事件添加监听器
        play.setOnClickListener(this);

        activityReceiver = new ActivityReceiver();
        // 创建IntentFilter
        IntentFilter filter = new IntentFilter();
        // 指定BroadcastReceiver监听的Action
        filter.addAction(UPDATE_ACTION);
        Intent intent = new Intent(this, MusicService.class);
        // 注册BroadcastReceiver
        registerReceiver(activityReceiver, filter);
        startService(intent);
    }

    @Override
    public void onStart(){
        super.onStart();

        musicList = (List<Music>) getIntent().getSerializableExtra(SER_KEY);
        if(musicList!=null) {
            iD = getIntent().getExtras().getInt("ID");
            Log.i("onStart", "onStart: " + musicList.get(iD).gettitle());

            Intent intent = new Intent(CTL_ACTION);
            intent.putExtra("ID",iD);
            Bundle mBundle = new Bundle();
            mBundle.putSerializable(SER_KEY, (Serializable) musicList);
            intent.putExtras(mBundle);
            // 启动后台Service
            sendBroadcast(intent);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    // 自定义的BroadcastReceiver，负责监听从Service传回来的广播
    public class ActivityReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            // 获取Intent中的update消息，update代表播放状态
            int update = intent.getIntExtra("update", -1);
            // 获取Intent中的current消息，current代表当前正在播放的歌曲
            int current = intent.getIntExtra("current", -1);
            if (current >= 0 && musicList != null)
            {
                title.setText(musicList.get(current).gettitle());
                artist.setText(musicList.get(current).getartist());
            }
            switch (update)
            {
                case Constant.M_NONE:
                    play.setImageResource(R.drawable.play);
                    status = Constant.M_NONE;
                    break;
                // 控制系统进入播放状态
                case Constant.M_PLAYING:
                    // 播放状态下设置使用暂停图标
                    play.setImageResource(R.drawable.pause);
                    // 设置当前状态
                    status = Constant.M_PLAYING;
                    break;
                // 控制系统进入暂停状态
                case Constant.M_PAUSE:
                    // 暂停状态下设置使用播放图标
                    play.setImageResource(R.drawable.play);
                    // 设置当前状态
                    status = Constant.M_PAUSE;
                    break;
            }
        }
    }

    public void onClick(View source)
    {
        // 创建Intent
        Intent intent = new Intent("CTL_ACTION");
        switch (source.getId())
        {
            // 按下播放/暂停按钮
            case R.id.play:
                intent.putExtra("control", 1);
                break;

        }
        // 发送广播，将被Service组件中的BroadcastReceiver接收到
        sendBroadcast(intent);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.list:
                Intent intent = new Intent(this,PlayListActivity.class);
                startActivity(intent);
                Toast.makeText(this, "You clicked list", Toast.LENGTH_SHORT).
                        show();
                break;

            default:
        }
        return true;
    }

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
