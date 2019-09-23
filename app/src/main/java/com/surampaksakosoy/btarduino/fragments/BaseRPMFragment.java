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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.surampaksakosoy.btarduino.R;
import com.surampaksakosoy.btarduino.utils.RepeatListener;

import java.util.Objects;

import de.nitri.gauge.Gauge;

import static com.surampaksakosoy.btarduino.fragments.MainFragment.initialInterval;
import static com.surampaksakosoy.btarduino.fragments.MainFragment.normalInterval;

public class BaseRPMFragment extends Fragment implements View.OnClickListener {

    private ListenerBaseRPMFragment listener;
    private TextView tv_timingb, tv_timingc, tv_timingd, tv_timingf, tv_timingg, tv_timingh, tv_rpmpuls, tv_revcut, tv_revlimit;
    private Gauge gaugeRPM;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String pesan = intent.getStringExtra("PESAN");
            char location = pesan.charAt(0);
            String value = pesan.substring(pesan.lastIndexOf(" ") + 1);
            switch (location){
                case '[': tv_timingb.setText(value); break;
                case ']': tv_timingc.setText(value); break;
                case 'a': tv_timingd.setText(value); break;
                case 's': tv_timingf.setText(value); break;
                case 'd': tv_timingg.setText(value); break;
                case 'f': tv_timingh.setText(value); break;
                case 'p': tv_rpmpuls.setText(value); break;
                case 'j':
                    int gaugeVal = Integer.parseInt(value);
                    if (gaugeVal > 16000){
                        gaugeVal = 16000;
                    }
                    gaugeRPM.moveToValue(gaugeVal);
                    gaugeRPM.setLowerText(value);
                    break;
                case '#':
                    gaugeRPM.moveToValue(0);
                    gaugeRPM.setLowerText("-");
                    break;
                case 'h': tv_revcut.setText(value); break;
                case 'g': tv_revlimit.setText(value); break;
            }
        }
    };

    public BaseRPMFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_rpm, container, false);
        initView(view);
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity()).getApplicationContext()).registerReceiver(broadcastReceiver, new IntentFilter("incoming"));
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView(View view) {
        Button btn_timingbm = view.findViewById(R.id.btn_timingbm);
        btn_timingbm.setOnTouchListener(new RepeatListener(initialInterval,normalInterval, v -> listener.onInputBasedRPM("e")));
        Button btn_timingbp = view.findViewById(R.id.btn_timingbp);
        btn_timingbp.setOnTouchListener(new RepeatListener(initialInterval, normalInterval, v -> listener.onInputBasedRPM("r")));
        tv_timingb = view.findViewById(R.id.tv_timingb);

        Button btn_timingcm = view.findViewById(R.id.btn_timingcm);
        btn_timingcm.setOnTouchListener(new RepeatListener(initialInterval, normalInterval, v -> listener.onInputBasedRPM("t")));
        Button btn_timingcp = view.findViewById(R.id.btn_timingcp);
        btn_timingcp.setOnTouchListener(new RepeatListener(initialInterval, normalInterval, v -> listener.onInputBasedRPM("y")));
        tv_timingc = view.findViewById(R.id.tv_timingc);

        Button btn_timingdm = view.findViewById(R.id.btn_timingdm);
        btn_timingdm.setOnTouchListener(new RepeatListener(initialInterval, normalInterval, v -> listener.onInputBasedRPM("u")));
        Button btn_timingdp = view.findViewById(R.id.btn_timingdp);
        btn_timingdp.setOnTouchListener(new RepeatListener(initialInterval, normalInterval, v -> listener.onInputBasedRPM("i")));
        tv_timingd = view.findViewById(R.id.tv_timingd);

        Button btn_timingfm = view.findViewById(R.id.btn_timingfm);
        btn_timingfm.setOnTouchListener(new RepeatListener(initialInterval, normalInterval, v -> listener.onInputBasedRPM("d")));
        Button btn_timingfp = view.findViewById(R.id.btn_timingfp);
        btn_timingfp.setOnTouchListener(new RepeatListener(initialInterval, normalInterval, v -> listener.onInputBasedRPM("f")));
        tv_timingf = view.findViewById(R.id.tv_timingf);

        Button btn_timinggm = view.findViewById(R.id.btn_timinggm);
        btn_timinggm.setOnTouchListener(new RepeatListener(initialInterval, normalInterval, v -> listener.onInputBasedRPM("g")));
        Button btn_timinggp = view.findViewById(R.id.btn_timinggp);
        btn_timinggp.setOnTouchListener(new RepeatListener(initialInterval, normalInterval, v -> listener.onInputBasedRPM("h")));
        tv_timingg = view.findViewById(R.id.tv_timingg);

        Button btn_timinghm = view.findViewById(R.id.btn_timinghm);
        btn_timinghm.setOnTouchListener(new RepeatListener(initialInterval, normalInterval, v -> listener.onInputBasedRPM("n")));
        Button btn_timinghp = view.findViewById(R.id.btn_timinghp);
        btn_timinghp.setOnTouchListener(new RepeatListener(initialInterval, normalInterval, v -> listener.onInputBasedRPM("m")));
        tv_timingh = view.findViewById(R.id.tv_timingh);

        Button btn_rpmpulsm = view.findViewById(R.id.btn_rpmpulsm);
        btn_rpmpulsm.setOnTouchListener(new RepeatListener(initialInterval, normalInterval, v -> listener.onInputBasedRPM(",")));
        Button btn_rpmpulsp = view.findViewById(R.id.btn_rpmpulsp);
        btn_rpmpulsp.setOnTouchListener(new RepeatListener(initialInterval, normalInterval, v -> listener.onInputBasedRPM(".")));
        tv_rpmpuls = view.findViewById(R.id.tv_rpmpuls);

        gaugeRPM = view.findViewById(R.id.gaugeRPM);

        Button btn_readRPM = view.findViewById(R.id.btn_readrpm); btn_readRPM.setOnClickListener(this);
        Button btn_stopRPM = view.findViewById(R.id.btn_stoprpm); btn_stopRPM.setOnClickListener(this);

        Button btn_revcutm = view.findViewById(R.id.btn_revcutm);
        btn_revcutm.setOnTouchListener(new RepeatListener(initialInterval, normalInterval, v -> listener.onInputBasedRPM("[")));
        Button btn_revcutp = view.findViewById(R.id.btn_revcutp);
        btn_revcutp.setOnTouchListener(new RepeatListener(initialInterval, normalInterval, v -> listener.onInputBasedRPM("]")));
        tv_revcut = view.findViewById(R.id.tv_revcut);

        Button btn_revlimitm = view.findViewById(R.id.btn_revlimitm);
        btn_revlimitm.setOnTouchListener(new RepeatListener(initialInterval, normalInterval, v -> listener.onInputBasedRPM("<")));
        Button btn_revlimitp = view.findViewById(R.id.btn_revlimitp);
        btn_revlimitp.setOnTouchListener(new RepeatListener(initialInterval, normalInterval, v -> listener.onInputBasedRPM(">")));
        tv_revlimit = view.findViewById(R.id.tv_revlimit);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.btn_timingbm: listener.onInputBasedRPM("e"); break;
//            case R.id.btn_timingbp: listener.onInputBasedRPM("r"); break;

//            case R.id.btn_timingcm: listener.onInputBasedRPM("t"); break;
//            case R.id.btn_timingcp: listener.onInputBasedRPM("y"); break;

//            case R.id.btn_timingdm: listener.onInputBasedRPM("u"); break;
//            case R.id.btn_timingdp: listener.onInputBasedRPM("i"); break;

//            case R.id.btn_timingfm: listener.onInputBasedRPM("d"); break;
//            case R.id.btn_timingfp: listener.onInputBasedRPM("f"); break;

//            case R.id.btn_timinggm: listener.onInputBasedRPM("g"); break;
//            case R.id.btn_timinggp: listener.onInputBasedRPM("h"); break;

//            case R.id.btn_timinghm: listener.onInputBasedRPM("n"); break;
//            case R.id.btn_timinghp: listener.onInputBasedRPM("m"); break;

//            case R.id.btn_rpmpulsm: listener.onInputBasedRPM(","); break;
//            case R.id.btn_rpmpulsp: listener.onInputBasedRPM("."); break;

            case R.id.btn_readrpm: listener.onInputBasedRPM("@"); break;
            case R.id.btn_stoprpm: listener.onInputBasedRPM("#"); break;

//            case R.id.btn_revcutm: listener.onInputBasedRPM("["); break;
//            case R.id.btn_revcutp: listener.onInputBasedRPM("]"); break;

//            case R.id.btn_revlimitm: listener.onInputBasedRPM("<"); break;
//            case R.id.btn_revlimitp: listener.onInputBasedRPM(">"); break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ListenerBaseRPMFragment) {
            listener = (ListenerBaseRPMFragment) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ListenerBasedRPMFragment");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface ListenerBaseRPMFragment {
        void onInputBasedRPM(CharSequence input);
    }

//    public void updateData(CharSequence newData){
//        Log.e(TAG, "updateData: " + newData);
//    }
}
