package com.example.unreallover.mediaplayer.Services;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.IBinder;
import android.util.Log;

import com.example.unreallover.mediaplayer.Entities.Music;
import com.example.unreallover.mediaplayer.MainActivity;
import com.example.unreallover.mediaplayer.Utils.Constant;

import static com.example.unreallover.mediaplayer.MainActivity.CTL_ACTION;
import static com.example.unreallover.mediaplayer.Utils.Constant.CIRCLE;
import static com.example.unreallover.mediaplayer.Utils.Constant.RANDOM;
import static com.example.unreallover.mediaplayer.Utils.Constant.SELF;
import static com.example.unreallover.mediaplayer.Utils.Constant.SER_KEY;

public class MusicService extends Service
{
    MyReceiver serviceReceiver;
    List<Music> musicList;
    int range;
    int token;
    int progress;
    AssetManager am;
    int mode;
    MediaPlayer mPlayer;
    // 当前的状态，Constant.M_NONE代表没有播放；Constant.M_PLAYING代表正在播放；Constant.M_PAUSE代表暂停
    int status = Constant.M_NONE;
    int moDe = CIRCLE;
    // 记录当前正在播放的音乐
    int current = 0;
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
    @Override
    public void onCreate()
    {
        progress = 0;
        super.onCreate();
        am = getAssets();
        // 创建BroadcastReceiver
        serviceReceiver = new MyReceiver();
        // 创建IntentFilter
        IntentFilter filter = new IntentFilter();
        filter.addAction(CTL_ACTION);
        registerReceiver(serviceReceiver, filter);
        mPlayer = new MediaPlayer();

    }
    public class MyReceiver extends BroadcastReceiver
    {
        int control;
        @Override
        public void onReceive(final Context context, Intent intent)
        {
            control = intent.getIntExtra("control", -1);
//            control = Integer.parseInt(intent.getAction());
//            if (intent.getAction().equals("control"))
//            {
//                control =1;
//            }
            mode = intent.getIntExtra("mode", -1);
            float proGress = intent.getFloatExtra("progress", -1);
            if (proGress != -1) {
                float toto = proGress * mPlayer.getDuration() / 100;
                mPlayer.seekTo((int) toto);

            }

            if(intent.getSerializableExtra(SER_KEY)!=null) {
                current = intent.getExtras().getInt("ID");
                musicList = (List<Music>) intent.getSerializableExtra(SER_KEY);
                current = intent.getExtras().getInt("ID");
                Log.i("onStart", "onStart: " + musicList.get(current).gettitle());
                range = musicList.size();

                // 创建MediaPlayer

                mPlayer.reset();
                status = Constant.M_PLAYING;
                Intent sendIntent = new Intent(MainActivity.UPDATE_ACTION);
                sendIntent.putExtra("update", status);
                sendBroadcast(sendIntent);

                prepareAndPlay(musicList.get(current).getpath());
                // 为MediaPlayer播放完成事件绑定监听器
                mPlayer.setOnCompletionListener(new OnCompletionListener() // ①
                {

                    @Override
                    public void onCompletion(MediaPlayer mp)
                    {
                        switch (moDe)
                        {
                            case CIRCLE:
                                {
                                    current++;
                                    if (current >= range)
                                    {
                                        current = 0;
                                    }
                                }
                                break;
                            case RANDOM:
                                {
                                    current = new Random().nextInt(musicList.size());
                                }
                                break;
                            case SELF:
                                {

                                }
                                break;
                        }

                        //发送广播通知Activity更改文本框
                        Intent sendIntent = new Intent(MainActivity.UPDATE_ACTION);
                        sendIntent.putExtra("current", current);
                        // 发送广播，将被Activity组件中的BroadcastReceiver接收到
                        sendBroadcast(sendIntent);
                        // 准备并播放音乐
                        token = 0;
                        prepareAndPlay(musicList.get(current).getpath());
                    }
                });
            }

            switch (mode) {
                case CIRCLE:
                    moDe = RANDOM;
                    break;
                case RANDOM:
                    moDe = SELF;
                    break;
                case SELF:
                    moDe = CIRCLE;
                    break;
            }
            switch (control) {
                // 播放或暂停
                case 1:
                    // 原来处于没有播放状态
                    if (status == Constant.M_NONE) {
                        // 准备并播放音乐
                        if (musicList != null) {
                            prepareAndPlay(musicList.get(current).getpath());
                            status = Constant.M_PLAYING;
                        }
                    }
                    // 原来处于播放状态
                    else if (status == Constant.M_PLAYING) {
                        // 暂停
                        mPlayer.pause();
                        // 改变为暂停状态
                        status = Constant.M_PAUSE;
                    }
                    // 原来处于暂停状态
                    else if (status == Constant.M_PAUSE) {
                        // 播放
                        mPlayer.start();
                        // 改变状态
                        status = Constant.M_PLAYING;
                    }
                    break;
                // 停止声音
                case 2:
                    // 如果原来正在播放或暂停
                    if (status == Constant.M_PLAYING || status == Constant.M_PAUSE) {
                        // 停止播放
                        mPlayer.stop();
                        status = Constant.M_NONE;
                    }
            }
            // 广播通知Activity更改图标、文本框
            Intent sendIntent = new Intent(MainActivity.UPDATE_ACTION);
            sendIntent.putExtra("update", status);
            sendIntent.putExtra("current", current);
            sendIntent.putExtra("moDe", moDe);
            // 发送广播，将被Activity组件中的BroadcastReceiver接收到
            sendBroadcast(sendIntent);
        }
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.d("Service", "onStartCommand: "+(intent==null));
//        if(intent==null){
//            return super.onStartCommand(intent, flags, startId);
//        }
//        else
//        {
//            current = intent.getAction();
//        }
//        return super.onStartCommand(intent, flags, startId);
//    }

    private void prepareAndPlay(String music)
    {
        token = 0;
        try
        {
            // 打开指定音乐文件
//            AssetFileDescriptor afd = am.openFd(music);

            // 使用MediaPlayer加载指定的声音文件。
//            mPlayer.setDataSource(afd.getFileDescriptor(),
//                    afd.getStartOffset(), afd.getLength());
//            mPlayer.setDataSource(music.getpath(), 0, (int)music.getduration());
            mPlayer.reset();
            mPlayer.setDataSource(music);
            // 准备声音
            mPlayer.prepare();
            // 播放
            mPlayer.start();
            countThread();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    public void countThread ()
    {
        token = 1;

        new Thread(new Runnable() {
            @Override
            public void run() {

                String TAG = "TAG";
                Intent intent = new Intent(MainActivity.UPDATE_ACTION);
                long time = System.currentTimeMillis();

                while (progress<=100&& token ==1 )
                {
                    if (System.currentTimeMillis() - time > 1000
                            ) {

                        progress = (int) (mPlayer.getCurrentPosition()* 100 / musicList.get(current).getduration() );

                        time = System.currentTimeMillis();
                        Log.i(TAG, "run: "+progress);
                        intent.putExtra("finish", progress);
                        sendBroadcast(intent);
                    }
                }

            }
        }).start();
    }
}

