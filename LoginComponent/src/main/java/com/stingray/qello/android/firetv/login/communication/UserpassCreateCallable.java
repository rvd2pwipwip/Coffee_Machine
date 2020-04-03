package com.stingray.qello.android.firetv.login.communication;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stingray.qello.android.firetv.login.communication.requestmodel.LoginResponse;
import com.stingray.qello.android.firetv.login.communication.requestmodel.UserpassCreateRequestBody;
import com.stingray.qello.android.firetv.login.communication.requestmodel.UserpassLoginRequestBody;
import com.stingray.qello.firetv.android.async.ULCallable;

import java.io.IOException;
import java.util.Map;

public class UserpassCreateCallable extends ULCallable<LoginResponse> {
    private final static String ENDPOINT = "/user/userpassCreate";
    private final static String TAG = UserpassCreateCallable.class.getSimpleName();

    private ObjectMapper objectMapper =  new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private UserpassCreateRequestBody requestBody;

    public UserpassCreateCallable(UserpassCreateRequestBody requestBody) {
        requestBody.setClientId(CLIENT_ID);
        this.requestBody = requestBody;
    }

    @Override
    public LoginResponse call() {
        try {
            Map<String, String> params = objectMapper.convertValue(requestBody, new TypeReference<Map<String, String>>() {});
            Response response = post(ENDPOINT, params);
            return objectMapper.readValue(response.getBody(), LoginResponse.class);
        } catch (IOException e) {
            Log.e(TAG, String.format("Failed to call endpoint [%s]", ENDPOINT), e);
            return null;
        }
    }
}
