package com.stingray.qello.firetv.inapppurchase.communication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stingray.qello.firetv.android.async.SvodCallable;
import com.stingray.qello.firetv.android.utils.SvodObjectMapperProvider;
import com.stingray.qello.firetv.inapppurchase.communication.requestmodel.PostSubscriptionRequest;

public class PostSubscriptionCallable extends SvodCallable<Void> {
    private final static String ENDPOINT = "/v1/subscription";
    private final static String TAG = PostSubscriptionCallable.class.getSimpleName();
    private ObjectMapper objectMapper = new SvodObjectMapperProvider().get();

    private final PostSubscriptionRequest request;

    public PostSubscriptionCallable(PostSubscriptionRequest request) {
        this.request = request;
    }

    @Override
    public Void call() {

        try {
            String payload = objectMapper.writeValueAsString(request);
            Response response = post(ENDPOINT, payload);
            if (response.getCode() != 204) {
                throw new RuntimeException(String.format("Unexpected response code. Failed to save subscription. [%s]", response.getBody()));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to create request payload");
        }

        return null;
    }
}
