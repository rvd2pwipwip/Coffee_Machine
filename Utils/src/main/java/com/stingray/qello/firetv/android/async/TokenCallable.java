package com.stingray.qello.firetv.android.async;

import android.util.Base64;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.stingray.qello.firetv.android.async.requestmodel.TokenRequestBody;
import com.stingray.qello.firetv.android.async.requestmodel.TokenResponse;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

public class TokenCallable extends ULCallable<TokenResponse> {
    private final static TypeReference<Map<String, String>> MAP_STRING_STRING = new TypeReference<Map<String, String>>() {};
    private final static String ENDPOINT = "/oauth/token";
    private final static String TAG = TokenCallable.class.getSimpleName();
    private final static Map<String, String> additionalHeaders =
            Collections.singletonMap("Authorization", "Basic " +  Base64.encodeToString((CLIENT_ID + ":").getBytes(StandardCharsets.UTF_8), Base64.DEFAULT));

    private ObjectMapper objectMapper =  new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

    private TokenRequestBody requestBody;

    public TokenCallable(TokenRequestBody requestBody) {
        requestBody.setClientId(CLIENT_ID);
        this.requestBody = requestBody;
    }

    @Override
    public TokenResponse call() {
        try {
            Map<String, String> params = objectMapper.convertValue(requestBody, MAP_STRING_STRING);
            Response response = post(ENDPOINT, params, additionalHeaders);

            if (response.getCode() == 200) {
                return objectMapper.readValue(response.getBody(), TokenResponse.class);
            } else {
                throw new Exception(String.format("Bad response code [%s]", response.getCode()));
            }
        } catch (Exception e) {
            Log.e(TAG, String.format("Failed to call endpoint [%s]", ENDPOINT), e);
            return null;
        }
    }
}
