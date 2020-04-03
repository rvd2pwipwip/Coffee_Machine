package com.stingray.qello.android.firetv.login.communication;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stingray.qello.firetv.android.async.SvodCallable;
import com.stingray.qello.firetv.android.model.svod.SvodUserInfo;
import com.stingray.qello.firetv.android.utils.SvodObjectMapperProvider;

import java.io.IOException;

public class SvodUserInfoCallable extends SvodCallable<SvodUserInfo> {
    private final static String ENDPOINT = "/v1/profile/user-info";
    private final static String TAG = SvodUserInfoCallable.class.getSimpleName();
    private ObjectMapper objectMapper = new SvodObjectMapperProvider().get();

    private final String accessToken;

    public SvodUserInfoCallable(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public SvodUserInfo call() throws IOException {
        Response response = get(ENDPOINT, accessToken);
        if (response.getCode() != 200) {
            Log.e(TAG, String.format("User info called failed with code [%s]: [%s]", response.getCode(), response.getBody()));
            return null;
        }
        return objectMapper.readValue(response.getBody(), SvodUserInfo.class);
    }
}
