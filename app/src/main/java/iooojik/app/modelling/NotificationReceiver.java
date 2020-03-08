package iooojik.app.modelling;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {

    // идентификатор уведомления
    private static final int NOTIFY_ID = 101;

    @Override
    public void onReceive(Context context, Intent intent) {

        // получение NotificationManager
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Идентификатор канала
        String CHANNEL_ID = "Уведомления";
        //получаем версию sdk
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



        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_round) //иконка
                        .setContentTitle(newArgs.getString("title")) //имя
                        .setContentText(newArgs.getString("shortNote")) //описание
                        .setShowWhen(true)  //показывать дату
                        .setAutoCancel(false)   //автоматическое "отключение" заметки из статус-бара
                        .setPriority(2); //приоритет

        notificationManager.notify(NOTIFY_ID, builder.build());
        }
}
