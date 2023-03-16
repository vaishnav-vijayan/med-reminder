package com.example.medmanager;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;


public class BlueNotify {

    public static BlueNotify blueNotify;
    public static OutputStream outputStream;
    public static InputStream inStream;
    private Context context;
    public BlueNotify(Context context){
        this.context=context;
        try {
            BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();

            if (blueAdapter != null) {
                if (blueAdapter.isEnabled()) {

                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(">>","bluetooth ------------");
                        Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();
                        if (bondedDevices.size() > 0) {
                            BluetoothDevice[] devices =bondedDevices.toArray(new BluetoothDevice[bondedDevices.size()]);
                            boolean detected=false;
                            for(BluetoothDevice device :  devices){
                                if(device.getName().equals("MedicineReminder")){
                                    detected=true;
                                    Toast.makeText(context, device.getName()+" detected", Toast.LENGTH_SHORT).show();
                                    ParcelUuid[] uuids = device.getUuids();
                                    BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                                    socket.connect();
                                    outputStream = socket.getOutputStream();
                                    inStream = socket.getInputStream();
                                }
                            }
                            Toast.makeText(context, (detected)?"Medicine remained detected":"Medicine remainder not paiered", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(context, "Bluetooth permission not granded", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e("error", "Bluetooth is disabled.");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context, "Error in bluetooth init", Toast.LENGTH_SHORT).show();
        }

    }


    public static void write(String s) throws IOException {
        outputStream.write(s.getBytes());
    }

    public static boolean run(){
        while (true) {
            try {
                if (inStream.read() =='9') return true;
                Log.d(">>>>>>","======not sdj");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
