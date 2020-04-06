package com.stingray.qello.android.firetv.callable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stingray.qello.firetv.android.async.SvodCallable;
import com.stingray.qello.firetv.android.utils.SvodObjectMapperProvider;

public class PostLogPlayActionCallable extends SvodCallable<Void> {
    private final static String ENDPOINT = "/v1/commands/log";
    private final static String TAG = PostLogPlayActionCallable.class.getSimpleName();
    private ObjectMapper objectMapper = new SvodObjectMapperProvider().get();

    private PostLogPlayActionRequest request;

    public PostLogPlayActionCallable(PostLogPlayActionRequest request) {
        this.request = request;
    }

    @Override
    public Void call() {

        try {
            String payload = objectMapper.writeValueAsString(request);
            Response response = post(ENDPOINT, payload);
            if (response.getCode() != 204) {
                throw new RuntimeException(String.format("Unexpected response code. Failed to log play action. [%s]", response.getBody()));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to create request payload");
        }

        return null;
    }
}
