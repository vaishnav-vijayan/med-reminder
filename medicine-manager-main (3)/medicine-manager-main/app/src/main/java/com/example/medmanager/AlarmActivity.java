package com.example.medmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_alarm);

    TextView userName, medName, medTime, medQty;
    Button tookMed, snooze;

    userName = findViewById(R.id.alarm_user_name);
    medName = findViewById(R.id.alarm_med_name);
    medTime = findViewById(R.id.alarm_med_time);
    medQty = findViewById(R.id.alarm_med_quantity);

    tookMed = findViewById(R.id.alarm_took);
    snooze = findViewById(R.id.alarm_snooze);

    Intent intent = getIntent();
    medName.setText(intent.getStringExtra("medName"));
    medTime.setText(String.format("Time: %s",intent.getStringExtra("medTime")));
    medQty.setText(String.format("Box: %s",intent.getStringExtra("box")));

//    int val=intent.getIntExtra("box",0);
    userName.setText(intent.getStringExtra("userName"));
    MediaPlayer Player = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
    Player.start();

    if(BlueNotify.blueNotify==null)  BlueNotify.blueNotify=new BlueNotify(this);
    try{
              Toast.makeText(this, intent.getStringExtra("box"), Toast.LENGTH_SHORT).show();
      BlueNotify.outputStream.write(intent.getStringExtra("box").getBytes(StandardCharsets.UTF_8));
      new Thread(){
        public void run(){
          if(BlueNotify.run()) {
            Player.stop();
            finish();
          }
        }
      }.start();
    }catch (Exception e){
      e.printStackTrace();
    }


    tookMed.setOnClickListener(v->{
      Player.stop();
      Player.release();
      try{BlueNotify.outputStream.write('f');}catch (Exception e){e.printStackTrace();}
      finish();
    } );

    snooze.setOnClickListener(v->{
        Player.stop();
        Player.release();
      try{BlueNotify.outputStream.write('f');}catch (Exception e){e.printStackTrace();}
      AlarmManager alarmManager = (AlarmManager) this.getSystemService(Activity.ALARM_SERVICE);
      Intent i = new Intent(this, AlarmBroadcastReceiver.class);
      i.putExtra("medName",intent.getStringExtra("medName"));
      i.putExtra("medQty",intent.getStringExtra("medQty"));
      i.putExtra("medTime",intent.getStringExtra("medTime"));
      i.putExtra("userName",intent.getStringExtra("userName"));
      PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, i, 0);
      alarmManager.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis()+1000*60*15, pendingIntent);
      Toast.makeText(getApplicationContext(),"Reminder set after 15 minutes",Toast.LENGTH_LONG).show();
      finish();
    });
  }
}