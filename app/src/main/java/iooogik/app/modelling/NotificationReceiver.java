package iooogik.app.modelling;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {

    // идентификатор уведомления
    private static final int NOTIFY_ID = 101;
    protected Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        // получение NotificationManager
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        assert notificationManager != null;

        // Идентификатор канала
        String CHANNEL_ID = "Уведомления";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Уведомления",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Уведомления заметок");
            channel.enableLights(true); // возможно ли использование LED - индикатора
            channel.setLightColor(Color.BLUE);// цвет LED - индикатора на смартфоне
            channel.enableVibration(true); // есть ли вибрация при уведомлении

            notificationManager.createNotificationChannel(channel);
        }

        Bundle newArgs = intent.getExtras();

        newArgs.putInt("button ID", newArgs.getInt("button ID"));

        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.putExtras(newArgs);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);



        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(newArgs.getString("title"))
                        .setContentText(newArgs.getString("shortNote"))
                        .setShowWhen(true)
                        .setAutoCancel(false)
                        .setContentIntent(pendingIntent)
                        .setPriority(2);

        notificationManager.notify(NOTIFY_ID, builder.build());
        }
}
