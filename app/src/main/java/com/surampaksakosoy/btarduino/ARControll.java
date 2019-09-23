package com.surampaksakosoy.btarduino;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.surampaksakosoy.btarduino.utils.BluetoothConnectionService;

import java.io.IOException;
import java.util.UUID;

import de.nitri.gauge.Gauge;
import pl.pawelkleczkowski.customgauge.CustomGauge;

public class ARControll extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ARControll";

    private String dataIntent = null, address = null;
    TextView textViewMonitor, textviewBt_device;
    TextView textViewMode, textViewtiming, textViewbliptime, textViewontofft, textViewabtabft,
    textViewtrt1, textViewrpmpuls, textViewtimingb, textViewtimingc, textViewtimingd,
    textViewtimingf, textViewtimingg, textViewtimingh, textViewrevcut, textViewrevlimit;

    private Gauge gauge;
    private CustomGauge gaugebar;

    private ProgressDialog progressDialog;

    BluetoothAdapter bluetoothAdapter = null;
    BluetoothSocket bluetoothSocket = null;
    private boolean isBTConnected = false;
    BluetoothConnectionService bluetoothConnectionService;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            String PESAN = intent.getStringExtra("PESAN");
            String BTDC = intent.getStringExtra("btdc");
            if (BTDC != null){
                if (BTDC.equals("BTDC")){
                    exitActivity();
                }
            }

            if (PESAN != null){
                Log.e(TAG, "onReceive: " + PESAN);
                textViewMonitor.setText(PESAN);
                char location = PESAN.charAt(0);
                String value = PESAN.substring(PESAN.lastIndexOf(" ") + 1);
                switch (location){
                    case 'q':
                        textViewMode.setText("Mode " + value.toUpperCase()); break;
                    case 'w':
                        textViewtiming.setText(value); break;
                    case 'e':
                        textViewbliptime.setText(value); break;
                    case 'r':
                        textViewontofft.setText(value); break;
                    case 't':
                        textViewontofft.setText(textViewontofft.getText() + " / " + value); break;
                    case 'y':
                        textViewabtabft.setText(value); break;
                    case 'u':
                        textViewabtabft.setText(textViewabtabft.getText() + " / " + value); break;
                    case 'i':
                        textViewtrt1.setText(value); break;
                    case 'p':
                        textViewrpmpuls.setText(value); break;
                    case '[':
                        textViewtimingb.setText(value); break;
                    case ']':
                        textViewtimingc.setText(value); break;
                    case 'a':
                        textViewtimingd.setText(value); break;
                    case 's':
                        textViewtimingf.setText(value); break;
                    case 'd':
                        textViewtimingg.setText(value); break;
                    case 'f':
                        textViewtimingh.setText(value); break;
                    case 'g':
                        textViewrevcut.setText(value); break;
                    case 'h':
                        textViewrevlimit.setText(value); break;
                    case 'l':
                        Log.e(TAG, "onReceive: " + Float.parseFloat(value));
                        gaugebar.setValue((int) Float.parseFloat(value));  break;
                    case 'j':
                        try {
                                int gaugeVal = Integer.parseInt(value);
                                if (gaugeVal > 16000){
                                    gaugeVal = 16000;
                                }
                                gauge.moveToValue(10000);
                                gauge.setLowerText("10000");
                            } catch (NumberFormatException e){
                            Log.e(TAG, "onReceive: Read RPM " + e.getMessage());
                            }
                        break;
                    case 'k':
                        try {
                            gauge.moveToValue(0);
                            gauge.setLowerText("-");
                        } catch (NumberFormatException e){
                            Log.e(TAG, "onReceive: Read RPM " + e.getMessage());
                        }
                        break;
                }
            }
        }
    };

    static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        dataIntent = intent.getStringExtra(DeviceList.EXTRA_ADDRESS);
        address = dataIntent.substring(dataIntent.length() - 17);

        setContentView(R.layout.activity_arcontroll);
        textViewMonitor = findViewById(R.id.textviewmonitor);
        textviewBt_device = findViewById(R.id.bt_device);
        textviewBt_device.setText(dataIntent);

        loadinit();



        Button btnLoad = findViewById(R.id.btn_load);
        btnLoad.setOnClickListener(this);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("INCOMING"));
        new ConnectBT().execute();
        bluetoothConnectionService = new BluetoothConnectionService();

    }

    private void loadinit() {
        textViewMode = findViewById(R.id.txtMode);
        textViewtiming = findViewById(R.id.txt_timing);
        textViewbliptime = findViewById(R.id.txt_bliptime);
        textViewontofft = findViewById(R.id.txt_ontofft);
        textViewabtabft = findViewById(R.id.txt_abtabft);
        textViewtrt1 = findViewById(R.id.trt1);
        textViewrpmpuls = findViewById(R.id.txt_rpmpuls);
        textViewtimingb = findViewById(R.id.txt_timingb);
        textViewtimingc = findViewById(R.id.txt_timingc);
        textViewtimingd = findViewById(R.id.txt_timingd);
        textViewtimingf = findViewById(R.id.txt_timingf);
        textViewtimingg = findViewById(R.id.txt_timingg);
        textViewtimingh = findViewById(R.id.txt_timingh);
        textViewrevcut = findViewById(R.id.txt_revcut);
        textViewrevlimit = findViewById(R.id.txt_revlimit);

        gauge = findViewById(R.id.gauge);
        gaugebar = findViewById(R.id.gaugebar);
        gaugebar.setValue(0);

        Button btntimingp = findViewById(R.id.btn_timingp);
        btntimingp.setOnClickListener(this);
        Button btntimingm = findViewById(R.id.btn_timingm);
        btntimingm.setOnClickListener(this);

        Button btnbliptimep = findViewById(R.id.btn_bliptimep);
        btnbliptimep.setOnClickListener(this);
        Button btnbliptimem = findViewById(R.id.btn_bliptimem);
        btnbliptimem.setOnClickListener(this);

        Button btnontofftp = findViewById(R.id.btn_ontofftp);
        btnontofftp.setOnClickListener(this);
        Button btnontofftm = findViewById(R.id.btn_ontofftm);
        btnontofftm.setOnClickListener(this);

        Button btnabtabftp = findViewById(R.id.btn_abtabftp);
        btnabtabftp.setOnClickListener(this);
        Button btnabtabftm = findViewById(R.id.btn_abtabftm);
        btnabtabftm.setOnClickListener(this);

        Button btntrt1p = findViewById(R.id.btn_trt1p);
        btntrt1p.setOnClickListener(this);
        Button btntrt1m = findViewById(R.id.btn_trt1m);
        btntrt1m.setOnClickListener(this);

        Button btnrpmpulsp = findViewById(R.id.btn_rpmpulsp);
        btnrpmpulsp.setOnClickListener(this);
        Button btnrpmpulsm = findViewById(R.id.btn_rpmpulsm);
        btnrpmpulsm.setOnClickListener(this);

        Button btntimingbp = findViewById(R.id.btn_timingbp);
        btntimingbp.setOnClickListener(this);
        Button btntimingbm = findViewById(R.id.btn_timingbm);
        btntimingbm.setOnClickListener(this);

        Button btntimingcp = findViewById(R.id.btn_timingcp);
        btntimingcp.setOnClickListener(this);
        Button btntimingcm = findViewById(R.id.btn_timingcm);
        btntimingcm.setOnClickListener(this);

        Button btntimingdp = findViewById(R.id.btn_timingdp);
        btntimingdp.setOnClickListener(this);
        Button btntimingdm = findViewById(R.id.btn_timingdm);
        btntimingdm.setOnClickListener(this);

        Button btntimingfp = findViewById(R.id.btn_timingfp);
        btntimingfp.setOnClickListener(this);
        Button btntimingfm = findViewById(R.id.btn_timingfm);
        btntimingfm.setOnClickListener(this);

        Button btntiminggp = findViewById(R.id.btn_timinggp);
        btntiminggp.setOnClickListener(this);
        Button btntiminggm = findViewById(R.id.btn_timinggm);
        btntiminggm.setOnClickListener(this);

        Button btntiminghp = findViewById(R.id.btn_timinghp);
        btntiminghp.setOnClickListener(this);
        Button btntiminghm = findViewById(R.id.btn_timinghm);
        btntiminghm.setOnClickListener(this);

        Button btnrevcutp = findViewById(R.id.btn_revcutp);
        btnrevcutp.setOnClickListener(this);
        Button btnrevcutm = findViewById(R.id.btn_revcutm);
        btnrevcutm.setOnClickListener(this);

        Button btnrevlimitp = findViewById(R.id.btn_revlimitp);
        btnrevlimitp.setOnClickListener(this);
        Button btnrevlimitm = findViewById(R.id.btn_revlimitm);
        btnrevlimitm.setOnClickListener(this);

        Button btnreadrpm = findViewById(R.id.btn_readrpm);
        btnreadrpm.setOnClickListener(this);
        Button btnstoprpm = findViewById(R.id.btn_stoprpm);
        btnstoprpm.setOnClickListener(this);

        Button dis_btn = findViewById(R.id.dis_btn);
        dis_btn.setOnClickListener(this);

        Button btn_readsensor = findViewById(R.id.btn_readsensor);
        btn_readsensor.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_load: sendData("6"); break;
            case R.id.btn_timingp: sendData("w"); break;
            case R.id.btn_timingm: sendData("q"); break;

            case R.id.btn_bliptimep: sendData(")"); break;
            case R.id.btn_bliptimem: sendData("("); break;

            case R.id.btn_ontofftp: sendData("8"); break;
            case R.id.btn_ontofftm: sendData("7"); break;

            case R.id.btn_abtabftp: sendData("o"); break;
            case R.id.btn_abtabftm: sendData("p"); break;

            case R.id.btn_trt1p: sendData("2"); break;
            case R.id.btn_trt1m: sendData("1"); break;

            case R.id.btn_rpmpulsp: sendData("."); break;
            case R.id.btn_rpmpulsm: sendData(","); break;

            case R.id.btn_timingbp: sendData("r"); break;
            case R.id.btn_timingbm: sendData("e"); break;

            case R.id.btn_timingcp: sendData("y"); break;
            case R.id.btn_timingcm: sendData("t"); break;

            case R.id.btn_timingdp: sendData("i"); break;
            case R.id.btn_timingdm: sendData("u"); break;

            case R.id.btn_timingfp: sendData("f"); break;
            case R.id.btn_timingfm: sendData("d"); break;

            case R.id.btn_timinggp: sendData("h"); break;
            case R.id.btn_timinggm: sendData("g"); break;

            case R.id.btn_timinghp: sendData("m"); break;
            case R.id.btn_timinghm: sendData("n"); break;

            case R.id.btn_revcutp: sendData(">"); break;
            case R.id.btn_revcutm: sendData("<"); break;

            case R.id.btn_revlimitp: sendData("]"); break;
            case R.id.btn_revlimitm: sendData("["); break;

            case R.id.btn_readrpm: sendData("@"); break;
            case R.id.btn_stoprpm: sendData("#"); break;

            case R.id.btn_readsensor: sendData("$"); break;

            case R.id.dis_btn: disconnectBT(); break;
        }
    }

//    private void setDataBar()

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    private void disconnectBT() {
        if (bluetoothSocket != null){
            try {
                Thread.currentThread().interrupt();
                bluetoothSocket.close();
                finish();
            } catch (IOException e) {
                msg("Failed to connect, Please try again");
            }
        }
    }



    @SuppressLint("StaticFieldLeak")
    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(ARControll.this,"Connecting ...","Please Wait!");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.e(TAG, "doInBackground: " + address);
            try {
                if (bluetoothSocket == null || !isBTConnected){
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
                    bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(uuid);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    bluetoothSocket.connect();
                    new BluetoothConnectionService.ConnectedThread(bluetoothSocket, ARControll.this).start();
                }
            } catch (IOException e){
                ConnectSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!ConnectSuccess){
                msg("Connectiong failed, Please try again.");
                finish();
            } else {
                msg("Connected with " + dataIntent);
                isBTConnected = true;
            }
            progressDialog.dismiss();
        }
    }
    
    private void sendData(String string){
        if (bluetoothSocket!= null){
            try {
                Log.e(TAG, "sendData: " + string);
                bluetoothSocket.getOutputStream().write(string.getBytes());
            } catch (IOException e) {
                Log.e(TAG, "sendData: " + string);
                Log.e(TAG, "sendData: " + e.getMessage());
                exitActivity();
            }
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

    private void msg(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
