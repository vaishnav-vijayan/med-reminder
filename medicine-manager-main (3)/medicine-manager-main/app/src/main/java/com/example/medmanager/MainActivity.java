package com.example.medmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medmanager.mydatabase.MedicalDB;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.nio.charset.StandardCharsets;
import java.sql.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
//    list of users:
    public RecyclerView recyclerView;
//    database object:
    public MedicalDB DbHelper;
    public static TextView empty_view;
    public UserListAdapter userlistAdapter;
    public FloatingActionButton add_user_fab;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    public static MainActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Database instantiation:
        DbHelper = MedicalDB.getInstance(this.getApplicationContext());
        MainActivity activity=this;




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("ch", "hello", importance);
            channel.setDescription("description ");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT},233);
            }
        }
        BlueNotify.blueNotify = new BlueNotify(this);

//        try{
//            BlueNotify.outputStream.write("hello world".getBytes(StandardCharsets.UTF_8));
//
//        }catch (Exception e){e.printStackTrace();}

        empty_view = (TextView) findViewById(R.id.user_empty);

        Cursor user_list = DbHelper.getUserList(DbHelper.getWritableDatabase());
        if(user_list.getCount() ==0){
            empty_view.setText(R.string.empty_users);
        }

        recyclerView = (RecyclerView) findViewById(R.id.user_list);
        add_user_fab = (FloatingActionButton) findViewById(R.id.add_user);

        //Initialize layout manager (default = vertical) and set to the recycler view:
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        //Instantiate object of recycler view by its adapter:
        userlistAdapter = new UserListAdapter(getApplicationContext(),DbHelper);
        userlistAdapter.setUserData(DbHelper.getUserList(DbHelper.getWritableDatabase()));
        recyclerView.setAdapter(userlistAdapter);

        add_user_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTextDialog().show();
            }
        });



    }

    private AlertDialog myTextDialog() {
        View layout = View.inflate(this, R.layout.add_user_dialog, null);
        EditText savedText = ((EditText) layout.findViewById(R.id.add_username));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                DbHelper.addUser(DbHelper.getWritableDatabase(),savedText.getText().toString().trim());
                Cursor user_list = DbHelper.getUserList(DbHelper.getWritableDatabase());
                userlistAdapter.setUserData(user_list);
                userlistAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(userlistAdapter);
                empty_view.setText("");

            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setView(layout);
        return builder.create();
    }

    @Override
    protected void onResume() {
        super.onResume();
        userlistAdapter.setUserData(DbHelper.getUserList(DbHelper.getWritableDatabase()));
        userlistAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(userlistAdapter);
    }
}