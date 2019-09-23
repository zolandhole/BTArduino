package com.surampaksakosoy.btarduino.utils;


import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

public class RepeatListener implements View.OnTouchListener {

    private Handler handler = new Handler();

    private int initilaInterval;
    private final int normalInterval;
    private final View.OnClickListener clickListener;
    private View touchedView;

    private Runnable handleRunnable = new Runnable() {
        @Override
        public void run() {
            if (touchedView.isEnabled()){
                handler.postDelayed(this, normalInterval);
                clickListener.onClick(touchedView);
            } else {
                handler.removeCallbacks(handleRunnable);
                touchedView.setPressed(false);
                touchedView = null;
            }
        }
    };

    public RepeatListener(int initilaInterval, int normalInterval, View.OnClickListener clickListener){
        if (clickListener == null){
            throw new IllegalArgumentException("null runnable");
        }
        if (initilaInterval < 0 || normalInterval < 0){
            throw new IllegalArgumentException("negative interval");
        }

        this.initilaInterval = initilaInterval;
        this.normalInterval = normalInterval;
        this.clickListener = clickListener;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                handler.removeCallbacks(handleRunnable);
                handler.postDelayed(handleRunnable, initilaInterval);
                touchedView = v;
                touchedView.setPressed(true);
                clickListener.onClick(v);
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handler.removeCallbacks(handleRunnable);
                touchedView.setPressed(false);
                touchedView = null;
                return true;
        }
        return false;
    }
}
