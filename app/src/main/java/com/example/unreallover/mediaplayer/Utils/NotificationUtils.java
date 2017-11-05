package com.example.unreallover.mediaplayer.Utils;

import java.util.HashMap;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;

import com.example.unreallover.mediaplayer.Entities.Music;
import com.example.unreallover.mediaplayer.MainActivity;
import com.example.unreallover.mediaplayer.R;
import com.example.unreallover.mediaplayer.Services.MusicService;

public class NotificationUtils {

    private Context mContext;
    private NotificationManager mNotificationManager = null;
    private Map<Integer, Notification> mNotifications = null;

    public NotificationUtils(Context context) {
        this.mContext = context;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifications = new HashMap<Integer, Notification>();
    }

    public void showNotification(Music music) {

        if(!mNotifications.containsKey(music.getId())){
            Notification notification = new Notification();
            notification.tickerText = music.gettitle() + "正在播放";
            notification.when = System.currentTimeMillis();
            notification.icon = R.mipmap.ic_launcher;
            notification.flags = Notification.FLAG_AUTO_CANCEL;


            Intent intent = new Intent(mContext, MainActivity.class);
            PendingIntent pd = PendingIntent.getActivity(mContext, 0, intent, 0);
            notification.contentIntent = pd;

//			OpenFileUtil.openFile(music.getFileName(),mContext);

            RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.activity_notification_utils);

            Intent intentStart = new Intent("CTL_ACTION");

            intentStart.putExtra("control" ,1);

            PendingIntent piStart = PendingIntent.getBroadcast(MyApplication.getContext(), 0,
                    intentStart, 0);

            remoteViews.setOnClickPendingIntent(R.id.start_button, piStart);


            remoteViews.setTextViewText(R.id.file_textview, music.gettitle());

            notification.contentView = remoteViews;

            mNotificationManager.notify((int) music.getId(), notification);

//            mNotifications.put((int) music.getId(), notification);
        }
    }


    public void cancelNotification(int id) {
        mNotificationManager.cancel(id);
        mNotifications.remove(id);
    }

//    public void updataNotification(int id, long progress,double rate) {
//        Notification notification = mNotifications.get(id);
//
//        if (notification != null) {
//            CalculateUtil calculateUtil = new CalculateUtil(rate);
//            notification.contentView.setProgressBar(R.id.progressBar2, 100, (int) progress, false);
//            notification.contentView.setTextViewText(R.id.rate2,String.format("%.0f", calculateUtil.Rate()).toString()+""+calculateUtil.Unit()+"/s");
//            mNotificationManager.notify(id, notification);
//        }
//    }
}