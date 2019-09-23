package com.surampaksakosoy.btarduino.utils;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.InputStream;

public class BluetoothConnectionService {
    private static final String TAG = "BluetoothConnectionServ";

    public static class ConnectedThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private Context context;

        public ConnectedThread(BluetoothSocket socket, Context context) {
            Log.e(TAG, "CONNECTEDTHREAD - ConnectedThread: CONNECTED.");
            mmSocket = socket;
            InputStream tmpIn = null;

            try {
                tmpIn = mmSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "ConnectedThread: " + e.getMessage());
            }
            mmInStream = tmpIn;
            this.context = context;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while (true){
                Intent intent = new Intent(context, BroadcastReceiverData.class);
                try {
                    bytes = mmInStream.read(buffer);
                    String incomingMessage = new String(buffer,0,bytes);
                    intent.setAction("incoming");
                    intent.putExtra("PESAN", incomingMessage);
                    Log.e(TAG, "run: INCOMING: " + incomingMessage);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "CONNECTTHREAD - run - WHILE - CATCH: Error Reading InputStream " + e.getMessage());
                    intent.setAction("btdisconnected");
                    context.sendBroadcast(intent);
                    break;
                }
            }
        }
    }
}
