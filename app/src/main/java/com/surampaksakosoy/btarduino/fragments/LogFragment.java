package com.surampaksakosoy.btarduino.fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.surampaksakosoy.btarduino.R;
import com.surampaksakosoy.btarduino.adapters.logAdapter;
import com.surampaksakosoy.btarduino.models.logModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class LogFragment extends Fragment {

    private ListenerLogFragment listener;
    private logAdapter adapter;
    private List<logModel> logModels;
    private LinearLayoutManager linearLayoutManager;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String pesan = intent.getStringExtra("PESAN");
            char location = pesan.charAt(0);
            String value = pesan.substring(pesan.lastIndexOf(" ") + 1);
            switch (location){
                case 'q':
                    switch (value) {
                        case "c": tampilkanLog("Free RPM Mode"); break;
                        case "v": tampilkanLog("Based RPM Mode"); break;
                        case "b": tampilkanLog("Bypass"); break;
                    } break;
                case 'w': tampilkanLog("Kill Time: " + value); break;
                case 'e': tampilkanLog("Blip Time: " + value); break;
                case 'r': tampilkanLog("QS Sensitivity: " + value); break;
                case 'y': tampilkanLog("Blip Sensitivity: " + value); break;
                case 'i': tampilkanLog("Blip Level: " + value); break;
//                case 'l': tampilkanLog("Reading Sensor: " + value); break;
                case '[': tampilkanLog("Timing RPM 1750 - 3000: " + value); break;
                case ']': tampilkanLog("Timing RPM 3000 - 4500: " + value); break;
                case 'a': tampilkanLog("Timing RPM 4500 - 6000: " + value); break;
                case 's': tampilkanLog("Timing RPM 6000 - 7500: " + value); break;
                case 'd': tampilkanLog("Timing RPM 7500 - 10000: " + value); break;
                case 'f': tampilkanLog("Timing RPM 10000 - 16000: " + value); break;
                case 'p': tampilkanLog("RPMPULS: " + value); break;
                case 'o': tampilkanLog("Configuration Saved"); break;
                case ';': tampilkanLog("Launch Control"); break;
                case 'k': tampilkanLog("PITLANE"); break;
                case 'h': tampilkanLog("Rev Cut"); break;
                case 'Q': tampilkanLog("Cut Rest: " + value); break;
                case 'W': tampilkanLog("Rest C"); break;
                case 'E': tampilkanLog("Rest B"); break;
                case 'g': tampilkanLog("Blip Rest: " + value); break;
            }
            switch (pesan){
                case "4": tampilkanLog("TEST CUT"); break;
                case "^": tampilkanLog("PLAY BLIP"); break;
                case "3": tampilkanLog("AUTO WARMUP"); break;
            }
        }
    };

    private void tampilkanLog(String value) {
        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String strDate = dateFormat.format(date);
        logModel data =(new logModel(value, strDate));
        logModels.add(0,data);
        adapter.notifyItemInserted(0);
        linearLayoutManager.scrollToPosition(0);
    }

    public LogFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log, container, false);
        listener.onInputLog("log fragment");
        RecyclerView recyclerView = view.findViewById(R.id.log_recycler);
        logModels = new ArrayList<>();
        adapter = new logAdapter(logModels);
        linearLayoutManager = new LinearLayoutManager(Objects.requireNonNull(getActivity()).getApplicationContext());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity()).getApplicationContext()).registerReceiver(broadcastReceiver, new IntentFilter("incoming"));
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ListenerLogFragment) {
            listener = (ListenerLogFragment) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ListenerLogFragment");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface ListenerLogFragment {
        void onInputLog(CharSequence input);
    }

//    public void updateData(CharSequence newData){
//        Log.e(TAG, "updateData: " + newData);
//    }
}
