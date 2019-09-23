package com.surampaksakosoy.btarduino;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.surampaksakosoy.btarduino.adapters.pagerAdapter;
import com.surampaksakosoy.btarduino.fragments.BaseRPMFragment;
import com.surampaksakosoy.btarduino.fragments.LogFragment;
import com.surampaksakosoy.btarduino.fragments.MainFragment;
import com.surampaksakosoy.btarduino.utils.BluetoothConnectionService;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class AndraActivity extends AppCompatActivity
        implements MainFragment.ListenerMainFragment, BaseRPMFragment.ListenerBaseRPMFragment, LogFragment.ListenerLogFragment{

    private static final String TAG = "AndraActivity";
    private String bluetoothName;
    private String bluetoothAddress;
    private BluetoothSocket bluetoothSocket = null;
    private BluetoothAdapter bluetoothAdapter = null;
    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private boolean isBTConnected;
    private ProgressDialog progressDialog;
    BluetoothConnectionService bluetoothConnectionService;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String param = intent.getAction();
            assert param != null;
            if (param.equals("btdisconnected")){
                exitActivity();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        bluetoothName = intent.getStringExtra(DeviceList.EXTRA_ADDRESS);
        bluetoothAddress = bluetoothName.substring(bluetoothName.length() - 17);
        setContentView(R.layout.activity_andra);
        toolbarBusiness();
        viewPagerBusiness();
        registerReceiver(broadcastReceiver, new IntentFilter("btdisconnected"));
        new ConnectionBluetooth().execute();
        bluetoothConnectionService = new BluetoothConnectionService();
    }

    private void toolbarBusiness() {
//        String firstCharacter = bluetoothName.split(" ")[0];
        Toolbar toolbar = findViewById(R.id.toolbarFragment);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        Button btn_disconnect = findViewById(R.id.btn_disconnect);
        Button btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(v -> sendData("s"));
        Button btn_load = findViewById(R.id.btn_load);
        btn_load.setOnClickListener(v -> sendData("6"));
        btn_disconnect.setOnClickListener(v -> disconnetBT());
    }

    private void disconnetBT() {
        sendData(":");
        sendData("#");
        if (bluetoothSocket != null){
            try {
                Thread.currentThread().interrupt();
                bluetoothSocket.close();
                finish();
            } catch (IOException e) {
                Toast.makeText(this, "Failed to connect, Please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void viewPagerBusiness() {
        TabLayout tabLayout = findViewById(R.id.tablayoutFragment);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_motorcycle));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_av_timer));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_event_note));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = findViewById(R.id.pagerAdapter);
        final PagerAdapter adapter = new pagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Objects.requireNonNull(tab.getIcon()).setColorFilter(Color.parseColor("#00979D"), PorterDuff.Mode.SRC_IN);
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Objects.requireNonNull(tab.getIcon()).setColorFilter(Color.parseColor("#bfbfbf"), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void sendData(String string){
        if (bluetoothSocket != null){
            try {
                Log.e(TAG, "sendData: " + string);
                bluetoothSocket.getOutputStream().write(string.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                exitActivity();
            }
        } else {
            Log.e(TAG, "sendData: FAILED");
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        Thread.currentThread().interrupt();
    }

    @Override
    public void onInputBasedRPM(CharSequence input) {
        sendData(input.toString());
    }

    @Override
    public void onInputLog(CharSequence input) {
        CharSequence activeFragment = input;
    }

    @Override
    public void onInputMain(CharSequence input) {
        sendData(input.toString());
    }

    @SuppressLint("StaticFieldLeak")
    private class ConnectionBluetooth extends AsyncTask<Void,Void,Void> {

        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog
                    .show(AndraActivity.this, "Connecting...", "Please Wait!");
        }

        @Override
        protected Void doInBackground(Void... voids) {
                try {
                    if (bluetoothAdapter == null || isBTConnected) {
                        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(bluetoothAddress);
                        bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(uuid);
                        bluetoothAdapter.cancelDiscovery();
                        bluetoothSocket.connect();
                        new BluetoothConnectionService
                                .ConnectedThread(bluetoothSocket, AndraActivity.this).start();
                    }
                } catch (IOException e) {
                    ConnectSuccess = false;
                }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!ConnectSuccess){
                Toast
                        .makeText(AndraActivity.this,
                                "Connection Failed, Please try  again!",
                                Toast.LENGTH_SHORT)
                        .show();
                exitActivity();
            } else {
                Log.e(TAG, "onPostExecute: Connected with " + bluetoothName);
                isBTConnected = true;
                sendData("6");
            }
            progressDialog.dismiss();
        }
    }

    private void exitActivity(){
        Thread.currentThread().interrupt();
        try {
            bluetoothSocket.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        finish();
    }
}
