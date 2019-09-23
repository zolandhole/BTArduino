package com.surampaksakosoy.btarduino.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BroadcastReceiverData extends BroadcastReceiver {

    private static final String TAG = "BroadcastReceiverData";

    @Override
    public void onReceive(Context context, Intent intent) {
        String param =intent.getAction();
        Log.e(TAG, "onReceive: " + param);
        context.sendBroadcast(new Intent(param));
    }
}
