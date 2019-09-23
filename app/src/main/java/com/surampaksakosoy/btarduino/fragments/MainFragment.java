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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.surampaksakosoy.btarduino.R;
import com.surampaksakosoy.btarduino.utils.RepeatListener;

import java.util.Objects;

import de.nitri.gauge.Gauge;

import static com.surampaksakosoy.btarduino.R.drawable.button;

public class MainFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "MainFragment";
    private ListenerMainFragment listener;
    private TextView textView_mode, txt_bliptime, txt_timing, txt_ontofft, txt_trt1, txt_abtabft;
    private Button btn_modefreerpm;
    private Button btn_modebasedrpm;
    private Button btn_modebypass;
    private Button btn_playblip;
    private Button btn_limitsensor;
    private Button btn_limitminus;
    private Gauge gaugeSensor;
    static final int initialInterval = 200;
    static final int normalInterval = 200;


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        @Override
        public void onReceive(Context context, Intent intent) {
            String pesan = intent.getStringExtra("PESAN");
            char location = pesan.charAt(0);
            String value = pesan.substring(pesan.lastIndexOf(" ") + 1);
            switch (location){
                case 'q':
                    switch (value) {
                        case "c": modeFreeRPM(); break;
                        case "v": modeBasedRPM(); break;
                        case "b": modeByPass(); break; } break;
                case 'w': txt_timing.setText(value); break;
                case 'e': txt_bliptime.setText(value); break;
                case 'r': txt_ontofft.setText(value); break;
                case 'y': txt_abtabft.setText(value); break;
                case 'i':
                    Double persentase = (((double)Integer.parseInt(value)-80)/(600-80))*100;
                    txt_trt1.setText(String.format("%.2f",persentase) + " %");
                    break;
                case 'l':
                    float floatSensor = Float.parseFloat(value);
                    if (floatSensor > 15.00){
                        floatSensor = (float) 15.00;
                    } else if (floatSensor < -15.00){
                        floatSensor = (float) -15.00;
                    }
                    if (!txt_ontofft.getText().toString().equals("n/a")){
                        float limitSensor = Float.parseFloat(txt_ontofft.getText().toString());

                        if (floatSensor >= limitSensor){
                            btn_limitsensor.setVisibility(View.VISIBLE);
                        } else {
                            btn_limitsensor.setVisibility(View.GONE);
                        }
                    }

                    if (!txt_abtabft.getText().toString().equals("n/a")){
                        float limitSensor = Float.parseFloat(txt_abtabft.getText().toString());
                        if (floatSensor <= limitSensor){
                            btn_limitminus.setVisibility(View.VISIBLE);
                        } else {
                            btn_limitminus.setVisibility(View.GONE);
                        }
                    }

                    gaugeSensor.moveToValue(floatSensor);
                    gaugeSensor.setLowerText(value);
                    break;
                case ':':
                    gaugeSensor.moveToValue(0);
                    gaugeSensor.setLowerText("-");
                    btn_limitsensor.setVisibility(View.GONE);
                    break;
            }
        }
    };

    public interface ListenerMainFragment{
        void onInputMain(CharSequence input);
    }

    public MainFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_main, container, false);
        Log.e(TAG, "onCreateView: Main Fragment");
        initView(view);

        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity()).getApplicationContext()).registerReceiver(broadcastReceiver, new IntentFilter("incoming"));
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView(View view) {
        textView_mode = view.findViewById(R.id.text_mode);
        btn_modefreerpm = view.findViewById(R.id.btn_modefreerpm); btn_modefreerpm.setOnClickListener(this);
        btn_modebasedrpm = view.findViewById(R.id.btn_modebasedrpm); btn_modebasedrpm.setOnClickListener(this);
        btn_modebypass = view.findViewById(R.id.btn_modebypass); btn_modebypass.setOnClickListener(this);

        Button btn_timingm = view.findViewById(R.id.btn_timingm);
        btn_timingm.setOnTouchListener(new RepeatListener(initialInterval, normalInterval, v -> listener.onInputMain("q")));

        Button btn_timingp = view.findViewById(R.id.btn_timingp);
        btn_timingp.setOnTouchListener(new RepeatListener(initialInterval,normalInterval, v -> listener.onInputMain("w")));


        Button btn_bliptimem = view.findViewById(R.id.btn_bliptimem);
        btn_bliptimem.setOnTouchListener(new RepeatListener(initialInterval,normalInterval, v -> listener.onInputMain("(")));

        Button btn_bliptimep = view.findViewById(R.id.btn_bliptimep);
        btn_bliptimep.setOnTouchListener(new RepeatListener(initialInterval,normalInterval, v -> listener.onInputMain(")")));

        Button btn_qssensim = view.findViewById(R.id.btn_ontofftm);
        btn_qssensim.setOnTouchListener(new RepeatListener(initialInterval,normalInterval, v -> listener.onInputMain("7")));

        Button btn_qssensip = view.findViewById(R.id.btn_ontofftp);
        btn_qssensip.setOnTouchListener(new RepeatListener(initialInterval,normalInterval, v -> listener.onInputMain("8")));

        Button btn_blipsensim = view.findViewById(R.id.btn_abtabftm);
        btn_blipsensim.setOnTouchListener(new RepeatListener(initialInterval,normalInterval, v -> listener.onInputMain("o")));

        Button btn_blipsensip = view.findViewById(R.id.btn_abtabftp);
        btn_blipsensip.setOnTouchListener(new RepeatListener(initialInterval,normalInterval, v -> listener.onInputMain("p")));

        Button btn_bliplevelm = view.findViewById(R.id.btn_trt1m);
        btn_bliplevelm.setOnTouchListener(new RepeatListener(initialInterval,normalInterval, v -> listener.onInputMain("1")));

        Button btn_bliplevelp = view.findViewById(R.id.btn_trt1p);
        btn_bliplevelp.setOnTouchListener(new RepeatListener(initialInterval,normalInterval, v -> listener.onInputMain("2")));

        Button btn_readsensor = view.findViewById(R.id.btn_readsensor); btn_readsensor.setOnClickListener(this);
        btn_limitsensor = view.findViewById(R.id.btn_limitsensor); btn_limitsensor.setVisibility(View.GONE);
        btn_limitminus = view.findViewById(R.id.btn_limitminus); btn_limitminus.setVisibility(View.GONE);
        Button btn_stopsensor = view.findViewById(R.id.btn_stopsensor);
        btn_stopsensor.setOnClickListener(this);

        Button btn_autowarmup = view.findViewById(R.id.btn_autowarmup); btn_autowarmup.setOnClickListener(this);
        btn_playblip = view.findViewById(R.id.btn_playblip);
        btn_playblip.setOnTouchListener((v, event) -> {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN: listener.onInputMain("^"); btn_playblip.setBackgroundResource(button);
                    return true;
                case MotionEvent.ACTION_UP: listener.onInputMain("&"); btn_playblip.setBackgroundResource(android.R.drawable.btn_default);
                    return true;
            }
            return false;
        });

        Button btn_cut = view.findViewById(R.id.btn_cut); btn_cut.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN){
                listener.onInputMain("4"); btn_cut.setBackgroundResource(button);
                return true;
            } else {
                btn_cut.setBackgroundResource(android.R.drawable.btn_default);
            }
            return false;
        });

        Button btn_blip = view.findViewById(R.id.btn_blip); btn_blip.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN){
                listener.onInputMain("5"); btn_blip.setBackgroundResource(button);
                return true;
            } else {
                btn_blip.setBackgroundResource(android.R.drawable.btn_default);
            }
            return false;
        });

        txt_timing = view.findViewById(R.id.txt_timing);
        txt_bliptime = view.findViewById(R.id.txt_bliptime);
        txt_ontofft = view.findViewById(R.id.txt_ontofft);
        txt_trt1 = view.findViewById(R.id.txt_trt1);
        txt_abtabft = view.findViewById(R.id.txt_abtabft);

        gaugeSensor = view.findViewById(R.id.gaugeSensor);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_modefreerpm: listener.onInputMain("c"); listener.onInputMain("6"); break;
            case R.id.btn_modebasedrpm: listener.onInputMain("v"); listener.onInputMain("6"); break;
            case R.id.btn_modebypass: listener.onInputMain("b"); listener.onInputMain("6"); break;
//            case R.id.btn_timingp: listener.onInputMain("w"); break;
//            case R.id.btn_bliptimem: listener.onInputMain("("); break;
//            case R.id.btn_bliptimep: listener.onInputMain(")"); break;
//            case R.id.btn_ontofftm: listener.onInputMain("7"); break;
//            case R.id.btn_ontofftp: listener.onInputMain("8"); break;
//            case R.id.btn_abtabftm: listener.onInputMain("o"); break;
//            case R.id.btn_abtabftp: listener.onInputMain("p"); break;
//            case R.id.btn_trt1m: listener.onInputMain("1"); break;
//            case R.id.btn_trt1p: listener.onInputMain("2"); break;
            case R.id.btn_readsensor: listener.onInputMain("$"); break;
            case R.id.btn_stopsensor: listener.onInputMain(":"); break;
            case R.id.btn_autowarmup: listener.onInputMain("3"); break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainFragment.ListenerMainFragment){
            listener = (ListenerMainFragment) context;
        } else {
            throw new RuntimeException(context.toString() + " Must implement ListenerMainFragment");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

//    public void updateData(CharSequence newData){
//        Log.e(TAG, "updateData: " + newData);
//    }

    private void modeByPass() {
        textView_mode.setText(R.string.mode_bypass);
        btn_modefreerpm.setEnabled(true);
        btn_modefreerpm.setBackgroundResource(button);
        btn_modebasedrpm.setEnabled(true);
        btn_modebasedrpm.setBackgroundResource(button);
        btn_modebypass.setEnabled(false);
        btn_modebypass.setBackgroundResource(R.drawable.roundedbutton);
    }

    private void modeBasedRPM() {
        textView_mode.setText(R.string.mode_based);
        btn_modefreerpm.setEnabled(true);
        btn_modefreerpm.setBackgroundResource(button);
        btn_modebasedrpm.setEnabled(false);
        btn_modebasedrpm.setBackgroundResource(R.drawable.roundedbutton);
        btn_modebypass.setEnabled(true);
        btn_modebypass.setBackgroundResource(button);
    }

    private void modeFreeRPM(){
        textView_mode.setText(R.string.mode_rpm);
        btn_modefreerpm.setEnabled(false);
        btn_modefreerpm.setBackgroundResource(R.drawable.roundedbutton);
        btn_modebasedrpm.setEnabled(true);
        btn_modebasedrpm.setBackgroundResource(button);
        btn_modebypass.setEnabled(true);
        btn_modebypass.setBackgroundResource(button);
    }
}
