package iooogik.app.modelling;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import iooogik.app.modelling.notes.Notes;

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

        //Переменная для работы с БД
        Database mDBHelper;
        SQLiteDatabase mDb;

        mDBHelper = new Database(context);
        mDBHelper.openDataBase();
        mDb = mDBHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("isNotifSet", 0);

        mDb.update("Notes", contentValues, "_id="+ newArgs.getInt("btnId"), null);

        Notes.NOTES_ADAPTER.notifyDataSetChanged();

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
