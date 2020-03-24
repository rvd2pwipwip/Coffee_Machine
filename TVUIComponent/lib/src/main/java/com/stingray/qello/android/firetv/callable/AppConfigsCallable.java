package com.stingray.qello.android.firetv.callable;

import android.util.Log;

import com.stingray.qello.firetv.android.async.SvodCallable;

import java.util.HashMap;
import java.util.Map;

public class AppConfigsCallable extends SvodCallable<Map<String,String>> {
    private final static String TAG = AppConfigsCallable.class.getSimpleName();
    private final String ENDPOINT = "v1/app-configs";


    @Override
    public Map<String, String> call() throws Exception {
        Map<String, String> appConfigs = new HashMap<>();
        try {
            String jsonResponse = get(ENDPOINT);

            Log.i(TAG, String.format("Received response: %s", jsonResponse));


        } catch (Exception e) {
            Log.e(TAG, String.format("Failed to get genres from [%s]", ENDPOINT),e);
        }

        return appConfigs;
    }


}
