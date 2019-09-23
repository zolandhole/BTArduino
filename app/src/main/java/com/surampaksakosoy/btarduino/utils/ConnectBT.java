package com.surampaksakosoy.btarduino.utils;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.UUID;

public class ConnectBT extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "ConnectBT";

    private ProgressDialog progressDialog;
    private boolean connectSuccess = true;
    private WeakReference<Context> contextRef;

    private BluetoothAdapter bluetoothAdapter = null;
    private boolean isBTConnected = false;
    private String address;
    private UUID uuid;

    public ConnectBT(Context context, String address, UUID uuid){
        contextRef = new WeakReference<>(context);
        this.address = address;
        this.uuid = uuid;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.e(TAG, "onPreExecute: ");
        progressDialog = ProgressDialog.show(contextRef.get(), "Connecting ...", "Please Wait!");
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.e(TAG, "doInBackground: ");
            try {
                if (bluetoothAdapter == null || !isBTConnected) {
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
                    BluetoothSocket bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(uuid);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    bluetoothSocket.connect();
                    new BluetoothConnectionService.ConnectedThread(bluetoothSocket, contextRef.get()).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
                connectSuccess = false;
            }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.e(TAG, "onPostExecute: ");
        Intent intent = new Intent(contextRef.get(), BroadcastReceiverData.class);
        if (!connectSuccess){
            Toast.makeText(contextRef.get(), "Connection Failed, Please try again.", Toast.LENGTH_SHORT).show();
            intent.setAction("finish");
        } else {
            isBTConnected = true;
            intent.setAction("connected");
        }
        contextRef.get().sendBroadcast(intent);
        progressDialog.dismiss();
    }
}
