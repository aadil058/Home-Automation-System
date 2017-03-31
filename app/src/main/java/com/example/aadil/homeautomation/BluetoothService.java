package com.example.aadil.homeautomation;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

public class BluetoothService extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter = null;
    private OutputStream outputStream;
    public static boolean isConnected;
    private BluetoothDevice bluetoothDevice;
    IntentFilter filter = null;


    BluetoothService() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.isConnected = false;
    }


   public void init() {
       if (bluetoothAdapter != null) {
           if (bluetoothAdapter.isEnabled()) {

               Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
               if(bondedDevices.size() > 0) {
                   Object[] devices = (Object []) bondedDevices.toArray();
                   BluetoothDevice device = null;
                   for(int i = 0; i < devices.length; ++i) {
                       if(((BluetoothDevice) devices[i]).getName().equals("HC-05")) {
                           device = (BluetoothDevice) devices[i];
                           bluetoothDevice = device;
                           break;
                       }
                   }
                   ParcelUuid[] uuids = device.getUuids();
                   BluetoothSocket socket = null;
                   try {
                       socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                       socket.connect();
                       filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
                       isConnected = true;
                       outputStream = socket.getOutputStream();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
               else
                   System.out.println("No bonded device");
           } else
               System.out.println("Bluetooth not active");
       }
   }

    public void register(IntentFilter filter) {
        Receiver receiver = new Receiver();
        registerReceiver(receiver, filter);
    }

    public void setConnected(boolean value) {
        isConnected = value;
    }

    public void write(String s) {
        try {
            System.out.println("BS Write: "+s);
            outputStream.write(s.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return isConnected;
    }
}
