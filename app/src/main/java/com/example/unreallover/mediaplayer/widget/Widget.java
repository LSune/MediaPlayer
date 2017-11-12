package com.example.unreallover.mediaplayer.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;

import android.content.Context;
import android.content.Intent;

import android.widget.RemoteViews;
import android.widget.TextView;

import com.example.unreallover.mediaplayer.R;
import com.example.unreallover.mediaplayer.Utils.MyApplication;

import java.util.List;



/**
 * Created by Unreal Lover on 2017/11/12.
 */

public class Widget extends AppWidgetProvider {
    int current;
    List musicList;
    TextView song;
    TextView artist;

//    @Override
//    public void onReceive(Context context, Intent intent) {
////        song = (TextView)
//        if (intent.getSerializableExtra(SER_KEY) != null) {
//            current = intent.getExtras().getInt("ID");
//            musicList = (List<Music>) intent.getSerializableExtra(SER_KEY);
//        }
//
//    }



    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        for(int i = 0; i < appWidgetIds.length; i++){

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            Intent last_intent = new Intent("CTL_ACTION");
            last_intent.putExtra("control" ,2);
            PendingIntent last_pendingIntent = PendingIntent.getBroadcast(context, 0, last_intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.appwidget_button_last, last_pendingIntent);

            Intent play_intent = new Intent("CTL_ACTION");
            play_intent.putExtra("control" ,1);
            PendingIntent play_pendingIntent = PendingIntent.getBroadcast(context, 0, play_intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.appwidget_button_play, play_pendingIntent);

            Intent next_intent = new Intent("CTL_ACTION");
            next_intent.putExtra("control" ,3);
            PendingIntent next_pendingIntent = PendingIntent.getBroadcast(context, 0, next_intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.appwidget_button_next, next_pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


}
