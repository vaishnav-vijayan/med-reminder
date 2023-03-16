package com.example.medmanager;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.StrictMode;

public class App extends Application {
  public static final String CHANNEL_ID = "channel";

  public App() {
//    if(BuildConfig.DEBUG)
//      StrictMode.enableDefaults();
  }
  @Override
  public void onCreate() {
    super.onCreate();

    createNotificationChannels();
  }

  private void createNotificationChannels() {
    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
      NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"MedManager notifications", NotificationManager.IMPORTANCE_HIGH);
      channel.setDescription("Medmanager notifications appear here");

      NotificationManager manager = getSystemService(NotificationManager.class);
      manager.createNotificationChannel(channel);
    }
  }
}
