package com.stingray.qello.firetv.inapppurchase.communication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stingray.qello.firetv.android.async.SvodCallable;
import com.stingray.qello.firetv.android.utils.SvodObjectMapperProvider;
import com.stingray.qello.firetv.inapppurchase.communication.requestmodel.SubscriptionsResponse;

import java.io.IOException;

public class GetSubscriptionsCallable extends SvodCallable<SubscriptionsResponse> {
    private final static String ENDPOINT = "/v1/subscription/offers?list-all=false";
    private final static String TAG = GetSubscriptionsCallable.class.getSimpleName();
    private ObjectMapper objectMapper = new SvodObjectMapperProvider().get();

    @Override
    public SubscriptionsResponse call() throws JsonProcessingException {
        Response response = performGet(ENDPOINT);

        if (response.getCode() != 200) {
            throw new RuntimeException(String.format("Unexpected response code. Failed to get subscription. [%s]", response.getBody()));
        }

        return objectMapper.readValue(response.getBody(), SubscriptionsResponse.class);
    }
}
