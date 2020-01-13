package my.iooogik.Book;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
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
    private static String CHANNEL_ID = "Уведомления";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager =
                (NotificationManager) Objects.requireNonNull(StandartNote.view.getContext()).
                        getSystemService(Context.NOTIFICATION_SERVICE);

        assert notificationManager != null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Уведомление",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Уведомление заметок");
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(false);

            notificationManager.createNotificationChannel(channel);
        }

        Bundle newArgs = intent.getExtras();

        Intent resultIntent = new Intent(StandartNote.view.getContext(), MainActivity.class);
        /*
        Bundle args = new Bundle();
        args.putInt("buttonID", newArgs.getInt("btnID"));
        args.putString("button name", newArgs.getString("btnName"));
        resultIntent.putExtras(args);
        */
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(StandartNote.view.getContext());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(StandartNote.view.getContext(), CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(newArgs.getString("title"))
                        .setContentText(newArgs.getString("shortNote"))
                        .setShowWhen(true)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        notificationManager.notify(NOTIFY_ID, builder.build());
        Toast.makeText(StandartNote.view.getContext(), "notification", Toast.LENGTH_LONG).show();

    }
}
