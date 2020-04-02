package com.stingray.qello.firetv.inapppurchase.communication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stingray.qello.firetv.android.async.SvodCallable;
import com.stingray.qello.firetv.android.model.svod.SvodUserInfo;
import com.stingray.qello.firetv.android.utils.SvodObjectMapperProvider;

import java.io.IOException;
import java.util.List;

public class SubscriptionsCallable extends SvodCallable<SubscriptionsResponse> {
    private final static String ENDPOINT = "/v1/subscription/offers?list-all=true";
    private final static String TAG = SubscriptionsCallable.class.getSimpleName();
    private ObjectMapper objectMapper = new SvodObjectMapperProvider().get();

    @Override
    public SubscriptionsResponse call() throws IOException {
        String response = get(ENDPOINT);

        throw new IOException();
        //return objectMapper.readValue(response, SubscriptionsResponse.class);
    }
}
