package com.stingray.qello.android.firetv.login.communication;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stingray.qello.android.firetv.login.communication.requestmodel.AmazonLoginRequestBody;
import com.stingray.qello.android.firetv.login.communication.requestmodel.LoginResponse;
import com.stingray.qello.firetv.android.async.ULCallable;

import java.io.IOException;
import java.util.Map;

public class AmazonLoginCallable extends ULCallable<LoginResponse> {
    private final static TypeReference<Map<String, String>> MAP_STRING_STRING = new TypeReference<Map<String, String>>() {};
    private final static String ENDPOINT = "/user/amazonLogin";
    private final static String CREATE_PASSWORD_LINK = "https://login-test.stingray.com/createPasswordAndLink";
    private final static String TAG = AmazonLoginCallable.class.getSimpleName();

    private ObjectMapper objectMapper =  new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private AmazonLoginRequestBody requestBody;

    public AmazonLoginCallable(AmazonLoginRequestBody requestBody) {
        requestBody.setClientId(CLIENT_ID);
        requestBody.setSetAmazonPasswordUri(CREATE_PASSWORD_LINK);
        this.requestBody = requestBody;
    }

    @Override
    public LoginResponse call() {
        try {
            Map<String, String> params = objectMapper.convertValue(requestBody, MAP_STRING_STRING);
            Response response = post(ENDPOINT, params);
            return objectMapper.readValue(response.getBody(), LoginResponse.class);
        } catch (IOException e) {
            Log.e(TAG, String.format("Failed to call endpoint [%s]", ENDPOINT), e);
            return null;
        }
    }
}
