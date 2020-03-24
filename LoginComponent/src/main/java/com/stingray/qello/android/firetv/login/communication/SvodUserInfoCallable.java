package com.stingray.qello.android.firetv.login.communication;

import android.util.Log;

import com.amazon.android.async.SvodCallable;
import com.amazon.android.model.svod.SvodUserInfo;
import com.amazon.android.utils.SvodObjectMapperProvider;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class SvodUserInfoCallable extends SvodCallable<SvodUserInfo> {
    private final static String ENDPOINT = "/v1/profile/user-info";
    private final static String TAG = SvodUserInfoCallable.class.getSimpleName();
    private ObjectMapper objectMapper = new SvodObjectMapperProvider().get();

    @Override
    public SvodUserInfo call() {
        try {
            String response = get(ENDPOINT);
            return objectMapper.readValue(response, SvodUserInfo.class);
        } catch (Exception e) {
            Log.e(TAG, String.format("Failed to call endpoint [%s]", ENDPOINT), e);
            return null;
        }
    }
}
