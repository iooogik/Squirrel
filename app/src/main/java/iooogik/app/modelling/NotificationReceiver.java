package iooogik.app.modelling;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;


import java.util.Objects;

public class NotificationReceiver extends BroadcastReceiver {

    // Идентификатор уведомления
    private static final int NOTIFY_ID = 101;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager =
                (NotificationManager) Objects.requireNonNull(StandartNote.view.getContext()).
                        getSystemService(Context.NOTIFICATION_SERVICE);

        assert notificationManager != null;

        // Идентификатор канала
        String CHANNEL_ID = "Уведомления";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Уведомления",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Уведомления заметок");
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(true);

            notificationManager.createNotificationChannel(channel);
        }

        Bundle newArgs = intent.getExtras();

        Intent resultIntent = new Intent(StandartNote.view.getContext(), Notes.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(StandartNote.view.getContext());
        stackBuilder.addParentStack(Notes.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);

        assert newArgs != null;
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(StandartNote.view.getContext(), CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(newArgs.getString("title"))
                        .setContentText(newArgs.getString("shortNote"))
                        .setShowWhen(true)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager.notify(NOTIFY_ID, builder.build());
        }
}
