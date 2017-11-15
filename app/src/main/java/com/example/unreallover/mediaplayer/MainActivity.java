package com.example.unreallover.mediaplayer;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.unreallover.mediaplayer.Entities.LrcContent;
import com.example.unreallover.mediaplayer.Entities.Music;
import com.example.unreallover.mediaplayer.Services.MusicService;
import com.example.unreallover.mediaplayer.Utils.Constant;
import com.example.unreallover.mediaplayer.Utils.LrcProcess;
import com.example.unreallover.mediaplayer.Utils.NotificationUtils;
import com.example.unreallover.mediaplayer.Views.LrcView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.unreallover.mediaplayer.Utils.Constant.CIRCLE;
import static com.example.unreallover.mediaplayer.Utils.Constant.PERMISSIONS_STORAGE;
import static com.example.unreallover.mediaplayer.Utils.Constant.RANDOM;
import static com.example.unreallover.mediaplayer.Utils.Constant.REQUEST_EXTERNAL_STORAGE;
import static com.example.unreallover.mediaplayer.Utils.Constant.SELF;
import static com.example.unreallover.mediaplayer.Utils.Constant.SER_KEY;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    int target = 1;

    public static LrcView lrcView;
    public static SeekBar seekBar;
    public static TextView nowtime;

    RelativeLayout change;

    private LrcProcess mLrcProcess; //歌词处理
    private List<LrcContent> lrcList = new ArrayList<LrcContent>(); //存放歌词列表对象
    private int index = 0;          //歌词检索值

    // 获取界面中显示歌曲标题、作者文本框
    TextView title, artist,totaltime;
    // 播放/暂停、停止按钮
    ImageButton play,mode,timec,pre,next;

    NotificationUtils mNotificationUtil;

    private ObjectAnimator discObjectAnimator,neddleObjectAnimator;

    int token = 0;

    float mPercent;

    int current;


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
        pre = (ImageButton) this.findViewById(R.id.preview);
        next = (ImageButton) this.findViewById(R.id.next);
        title = (TextView) findViewById(R.id.title);
        artist = (TextView) findViewById(R.id.artist);
        change = (RelativeLayout) findViewById(R.id.change);
        nowtime = (TextView) findViewById(R.id.pass_time);
        totaltime = (TextView) findViewById(R.id.main_time);

        lrcView = (LrcView) findViewById(R.id.lrcShowView);
        // 为两个按钮的单击事件添加监听器
        play.setOnClickListener(this);
        mode.setOnClickListener(this);
        pre.setOnClickListener(this);
        next.setOnClickListener(this);
        timec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(FirstActivity.this, SecondActivity.class);
//                startActivity(intent);
            }
        });
        Initview();
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

            moDe = intent.getIntExtra("moDe",-1);
            int curret = intent.getIntExtra("current", -1);
            if (curret != -1){
                current = curret;
            }

            // 获取Intent中的current消息，current代表当前正在播放的歌曲
            if (curret != -1 && mNotificationUtil!=null) {
                mNotificationUtil.cancelNotification(current);
                current = curret;
            }
            if (curret >= 0 && musicList != null)
            {

                current = curret;
                title.setText(musicList.get(current).gettitle());
                artist.setText(musicList.get(current).getartist());
                int min = (int)musicList.get(current).getduration()/(60*1000);
                int sec = (int)(musicList.get(current).getduration()-min*60*1000)/1000;
                totaltime.setText((min<10?"0"+min:min)+":"+(sec<10?"0"+sec:sec));
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
                    discObjectAnimator.start();
                    neddleObjectAnimator.start();
                    status = Constant.M_PLAYING;
                    break;
                // 控制系统进入暂停状态
                case Constant.M_PAUSE:
                    // 暂停状态下设置使用播放图标
                    play.setImageResource(R.drawable.play);
                    // 设置当前状态
                    discObjectAnimator.cancel();
                    neddleObjectAnimator.reverse();
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

        switch (source.getId())
        {
            case R.id.preview:
                intent.putExtra("control", 2);
                break;
        }

        switch (source.getId())
        {
            case R.id.next:
                intent.putExtra("control", 3);
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

    public void Initview(){

        //最外部的半透明边线
        OvalShape ovalShape0 = new OvalShape();
        ShapeDrawable drawable0 = new ShapeDrawable(ovalShape0);
        drawable0.getPaint().setColor(0x10000000);
        drawable0.getPaint().setStyle(Paint.Style.FILL);
        drawable0.getPaint().setAntiAlias(true);

        //黑色唱片边框
        RoundedBitmapDrawable drawable1 = RoundedBitmapDrawableFactory.create(getResources(),
                BitmapFactory.decodeResource(getResources(), R.drawable.disc));
        drawable1.setCircular(true);
        drawable1.setAntiAlias(true);

        //内层黑色边线
        OvalShape ovalShape2 = new OvalShape();
        ShapeDrawable drawable2 = new ShapeDrawable(ovalShape2);
        drawable2.getPaint().setColor(Color.BLACK);
        drawable2.getPaint().setStyle(Paint.Style.FILL);
        drawable2.getPaint().setAntiAlias(true);

        //最里面的图像
        RoundedBitmapDrawable drawable3 = RoundedBitmapDrawableFactory.create(getResources(),
                BitmapFactory.decodeResource(getResources(), R.drawable.chicken));
        drawable3.setCircular(true);
        drawable3.setAntiAlias(true);

        Drawable[] layers = new Drawable[4];
        layers[0] = drawable0;
        layers[1] = drawable1;
        layers[2] = drawable2;
        layers[3] = drawable3;

        LayerDrawable layerDrawable = new LayerDrawable(layers);

        int width = 10;
        //针对每一个图层进行填充，使得各个圆环之间相互有间隔，否则就重合成一个了。
        //layerDrawable.setLayerInset(0, width, width, width, width);
        layerDrawable.setLayerInset(1, width , width, width, width );
        layerDrawable.setLayerInset(2, width * 11, width * 11, width * 11, width * 11);
        layerDrawable.setLayerInset(3, width * 12, width * 12, width * 12, width * 12);

        final View discView = findViewById(R.id.myView);
        discView.setBackgroundDrawable(layerDrawable);

        final ImageView needleImage= (ImageView) findViewById(R.id.needle);

        discObjectAnimator = ObjectAnimator.ofFloat(discView, "rotation", 0, 360);
        discObjectAnimator.setDuration(20000);
        //使ObjectAnimator动画匀速平滑旋转
        discObjectAnimator.setInterpolator(new LinearInterpolator());
        //无限循环旋转
        discObjectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        discObjectAnimator.setRepeatMode(ValueAnimator.INFINITE);


        neddleObjectAnimator = ObjectAnimator.ofFloat(needleImage, "rotation", 0, 25);
        needleImage.setPivotX(0);
        needleImage.setPivotY(0);
        neddleObjectAnimator.setDuration(800);
        neddleObjectAnimator.setInterpolator(new LinearInterpolator());

        if (change == null){
            Log.i("change is nulllllllllllllllllllll", "Initview: ");
        }


        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (target == 1) {
                    discView.setVisibility(View.INVISIBLE);
                    needleImage.setVisibility(View.INVISIBLE);
                    lrcView.setVisibility(View.VISIBLE);
                    target = 0;
                }
                else{
                    discView.setVisibility(View.VISIBLE);
                    needleImage.setVisibility(View.VISIBLE);
                    lrcView.setVisibility(View.INVISIBLE);
                    target = 1;
                }
            }
        });
    }
}
