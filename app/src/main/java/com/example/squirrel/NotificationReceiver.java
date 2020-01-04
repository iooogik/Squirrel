package com.example.squirrel;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.util.Objects;

public class NotificationReceiver extends BroadcastReceiver {

    // Идентификатор уведомления
    private static final int NOTIFY_ID = 101;
    // Идентификатор канала
    private static String CHANNEL_ID = "Cat channel";

    @Override
    public void onReceive(Context context, Intent intent) {

                NotificationManager notificationManager =
                (NotificationManager) Objects.requireNonNull(context).
                        getSystemService(Context.NOTIFICATION_SERVICE);

        assert notificationManager != null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Уведомление",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Уведомление заметки");
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(false);

            notificationManager.createNotificationChannel(channel);
        }

        Bundle arguments = intent.getExtras();

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(arguments.getString("title"))
                        .setContentText(arguments.getString("text"))
                        .setShowWhen(false);

        notificationManager.notify(NOTIFY_ID, builder.build());

    }
}
