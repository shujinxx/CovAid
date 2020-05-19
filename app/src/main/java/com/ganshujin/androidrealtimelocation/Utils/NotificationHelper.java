package com.ganshujin.androidrealtimelocation.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ganshujin.androidrealtimelocation.R;

public class NotificationHelper extends ContextWrapper {
    private static final String GSJ_CHANNEL_ID = "com.ganshujin.androidrealtimelocation";
    private static final String GSJ_CHANNEL_NAME = "Realtime2019";

    private NotificationManager manager;


    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel gsjChannel = new NotificationChannel(GSJ_CHANNEL_ID,GSJ_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        gsjChannel.enableLights(false);
        gsjChannel.enableVibration(true);
        gsjChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(gsjChannel);
    }

    public NotificationManager getManager() {
        if(manager==null)
            manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getRealtimeTrackingNotification(String title, String content, Uri defaultSound) {
        return new Notification.Builder(getApplicationContext(), GSJ_CHANNEL_ID)
        .setContentTitle(title)
        .setContentText(content)
        .setSound(defaultSound)
        .setAutoCancel(false)
        .setSmallIcon(R.mipmap.ic_launcher_round);
    }
}
