package com.example.medmanager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.example.medmanager.App.CHANNEL_ID;


import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Set;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    static MediaPlayer Player;

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
               .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle( intent.getStringExtra("medName"))
                .setContentText(intent.getStringExtra("medName")+" for "+intent.getStringExtra("userName"))
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat.from(context).notify(new Random().nextInt(), builder.build());


        Intent i = new Intent();
        i.setClassName("com.example.medmanager", "com.example.medmanager.AlarmActivity");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("box",intent.getStringExtra("box"));
        i.putExtra("medTime",intent.getStringExtra("medTime"));
        context.startActivity(i);

        }

    }
