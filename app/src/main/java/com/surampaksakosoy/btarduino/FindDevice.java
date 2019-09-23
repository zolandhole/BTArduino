package com.surampaksakosoy.btarduino;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

public class FindDevice extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private static final String TAG = "FindDevice";
    private static final BroadcastReceiver broadcastreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_device);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isDiscovering()){
            Log.e(TAG, "onCreate: + discovering bluetooth");
        } else if (!bluetoothAdapter.isDiscovering()){
            Log.e(TAG, "onCreate: + bluetooth not discover");
        }
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    }
}
