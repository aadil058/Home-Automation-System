package com.example.aadil.homeautomation;


import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_ACL_CONNECTED.equalsIgnoreCase(action) )   {
            System.out.println("Connected");
        }

        if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equalsIgnoreCase(action))  {
            System.out.println("Disconnected");
            BluetoothService.isConnected = false;
        }
    }
}
