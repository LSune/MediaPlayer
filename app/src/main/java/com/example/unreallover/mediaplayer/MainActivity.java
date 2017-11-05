package com.example.unreallover.mediaplayer;

import android.Manifest;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.unreallover.mediaplayer.Entities.Music;
import com.example.unreallover.mediaplayer.Services.MusicService;
import com.example.unreallover.mediaplayer.Utils.Constant;
import com.example.unreallover.mediaplayer.Utils.NotificationUtils;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import static com.example.unreallover.mediaplayer.Utils.Constant.CIRCLE;
import static com.example.unreallover.mediaplayer.Utils.Constant.PERMISSIONS_STORAGE;
import static com.example.unreallover.mediaplayer.Utils.Constant.RANDOM;
import static com.example.unreallover.mediaplayer.Utils.Constant.REQUEST_EXTERNAL_STORAGE;
import static com.example.unreallover.mediaplayer.Utils.Constant.SELF;
import static com.example.unreallover.mediaplayer.Utils.Constant.SER_KEY;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{



    // 获取界面中显示歌曲标题、作者文本框
    TextView title, artist;
    // 播放/暂停、停止按钮
    ImageButton play,mode,timec;

    NotificationUtils mNotificationUtil;

    int token = 0;

    float mPercent;

    int current;

    private SeekBar seekBar;
    int moDe;
    ActivityReceiver activityReceiver;

    // 定义音乐的播放状态，Constant.M_NONE代表没有播放；Constant.M_PLAYING代表正在播放；Constant.M_PAUSE代表暂停
    int status = Constant.M_NONE;

    List<Music> musicList;
    int iD;
    public static final String CTL_ACTION =
            "CTL_ACTION";
    public static final String UPDATE_ACTION =
            "UPDATE_ACTION";

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
                    final TextView show = (TextView) findViewById(R.id.show);
                    show.setText((String)msg.obj);
        }
    };
// 在这里可以进行 UI 操作
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        verifyStoragePermissions(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = (SeekBar) findViewById(R.id.seekbar);
        seekBar.setProgress(0);
        seekBar.setMax(100);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        moDe = CIRCLE;
                // 获取程序界面界面中的两个按钮
        play = (ImageButton) this.findViewById(R.id.play);
        mode = (ImageButton) this.findViewById(R.id.play_mode);
        timec = (ImageButton) this.findViewById(R.id.timer);
        title = (TextView) findViewById(R.id.title);
        artist = (TextView) findViewById(R.id.artist);
        // 为两个按钮的单击事件添加监听器
        play.setOnClickListener(this);
        mode.setOnClickListener(this);
        timec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(FirstActivity.this, SecondActivity.class);
//                startActivity(intent);
            }
        });

        activityReceiver = new ActivityReceiver();
        // 创建IntentFilter
        IntentFilter filter = new IntentFilter();
        // 指定BroadcastReceiver监听的Action
        filter.addAction(UPDATE_ACTION);
        Intent intent = new Intent(this, MusicService.class);
        // 注册BroadcastReceiver
        registerReceiver(activityReceiver, filter);
        startService(intent);


        musicList = (List<Music>) getIntent().getSerializableExtra(SER_KEY);
        if(musicList!=null) {
            iD = getIntent().getExtras().getInt("ID");
            Log.i("onStart", "onStart: " + musicList.get(iD).gettitle());

            Intent iintent = new Intent(CTL_ACTION);
            iintent.putExtra("ID",iD);
            Bundle mBundle = new Bundle();
            mBundle.putSerializable(SER_KEY, (Serializable) musicList);
            iintent.putExtras(mBundle);
            // 启动后台Service
            sendBroadcast(iintent);
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {



            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser) { //必须这个判断，是否为用户拉动导致的进度变更，否则会造成播放卡顿现象
                    mPercent = (float) progress * 100 / (float) seekBar.getMax();
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Intent intent = new Intent("CTL_ACTION");
                intent.putExtra("progress" ,mPercent);
                sendBroadcast(intent);
            }


        });

        //为“设置时间”按钮绑定监听器
        timec.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View source)
            {
                token =0;

                Calendar c = Calendar.getInstance();
                // 创建一个TimePickerDialog实例，并把它显示出来
                new TimePickerDialog(MainActivity.this,
                        // 绑定监听器
                        new TimePickerDialog.OnTimeSetListener()
                        {


                            @Override
                            public void onTimeSet(TimePicker tp, final int hourOfDay,
                                                  final int minute)
                            {
                                token=1;

                                new Thread(new Runnable() {

                                    @Override
                                    public void run() {
                                        Log.i("QAQ", "run: "+"    ");
                                        long time = System.currentTimeMillis()/1000;
                                        long ddl = time+hourOfDay*60*60+minute*60;
                                        while (token == 1) {
                                            if (time<ddl) {
                                                int hour = (int)(ddl-System.currentTimeMillis()/1000)/(60*60);
                                                int min = (int)((ddl-System.currentTimeMillis()/1000-hour*60*60))/60+1;
                                                Message message = Message.obtain();
                                                message.obj = ("剩余：" + hour + "时" + min + "分");
                                                handler.sendMessage(message); // 将 Message 对象发送出去
                                                time = System.currentTimeMillis()/1000;
                                            }
                                            else {
                                                Message message = Message.obtain();
                                                message.obj = ("已完成");
                                                handler.sendMessage(message);
                                                Intent intent = new Intent("CTL_ACTION");
                                                intent.putExtra("control", 1);
                                                sendBroadcast(intent);
                                                break;
                                            }
                                        }
                                    }
                                }).start();

                            }
                        }
                        //设置初始时间
                        , c.get(Calendar.HOUR_OF_DAY)
                        , c.get(Calendar.MINUTE)
                        //true表示采用24小时制
                        , true).show();
            }
        });

    }

    @Override
    public void onStart(){
        super.onStart();


    }

    @Override
    public void onStop(){
        super.onStop();
        if(musicList != null) {
            mNotificationUtil = new NotificationUtils(MainActivity.this);
            mNotificationUtil.showNotification(musicList.get(current));
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
            int finish = intent.getIntExtra("finish", -1);
            Log.i("finish", "onReceive: "+finish);
            if (finish!=-1) {
                seekBar.setProgress(finish);
            }
            moDe = intent.getIntExtra("moDe",-1);
            // 获取Intent中的current消息，current代表当前正在播放的歌曲

            int curret = intent.getIntExtra("current", -1);
            if (curret != -1 && mNotificationUtil!=null) {
                mNotificationUtil.cancelNotification(current);
                current = curret;
            }
            if (current >= 0 && musicList != null)
            {
                title.setText(musicList.get(current).gettitle());
                artist.setText(musicList.get(current).getartist());
            }
            switch (moDe)
            {
                case CIRCLE:
                    mode.setImageResource(R.drawable.loop_all);
                    break;
                case SELF:
                    mode.setImageResource(R.drawable.loop_one);
                    break;
                case RANDOM:
                    mode.setImageResource(R.drawable.radom);
                    break;
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

        switch (source.getId())
        {
            case R.id.play_mode:
                intent.putExtra("mode" ,moDe);
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
