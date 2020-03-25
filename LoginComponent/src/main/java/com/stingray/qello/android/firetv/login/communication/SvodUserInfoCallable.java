package com.stingray.qello.android.firetv.login.communication;

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
        String response = get(ENDPOINT, accessToken);
        return objectMapper.readValue(response, SvodUserInfo.class);
    }
}
