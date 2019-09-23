package com.surampaksakosoy.btarduino;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DeviceList extends AppCompatActivity {

    private static final String TAG = "DeviceList";

//    UI
    Button btnPaired;
    ListView deviceList;

//    BLUETOOTH
    private BluetoothAdapter bluetoothAdapter = null;
    public static String EXTRA_ADDRESS = "Device ";

//    UTIL
    public int REQUEST_CODE_BT_ON = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        btnPaired = findViewById(R.id.button);
        deviceList = findViewById(R.id.listView);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null){
            Toast.makeText(this, "Smartphone tidak didukung Bluetooth", Toast.LENGTH_SHORT).show();
            finish();
        } else if (!bluetoothAdapter.isEnabled()){
            Intent intentTurnOnBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intentTurnOnBT,REQUEST_CODE_BT_ON);
        }

        btnPaired.setOnClickListener(v -> {
            Intent intent = new Intent(DeviceList.this, FindDevice.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        pairedDeviceList();
    }

    private void pairedDeviceList() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        List<String> list = new ArrayList<>();

        if (pairedDevices.size() > 0){
            for (BluetoothDevice bt : pairedDevices){
                list.add(bt.getName() + " " + bt.getAddress());
            }
        } else {
            Toast.makeText(this, "Paired bluetooth tidak ditemukan", Toast.LENGTH_SHORT).show();
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, list);
        deviceList.setAdapter(adapter);
        deviceList.setOnItemClickListener(listenerOnItemClick);
    }

    private AdapterView.OnItemClickListener listenerOnItemClick = (parent, view, position, id) -> {
        String info = ((TextView) view).getText().toString();
        Log.e(TAG, "onItemClick: " + info);
        Intent intent = new Intent(DeviceList.this, AndraActivity.class);
        intent.putExtra(EXTRA_ADDRESS, info);
        startActivity(intent);
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_BT_ON){
            if (resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Aplikasi ini membutuhkan koneksi bluetooth", Toast.LENGTH_SHORT).show();
                finish();
            } else if (resultCode == RESULT_OK){
                pairedDeviceList();
            }
        }
    }
}
