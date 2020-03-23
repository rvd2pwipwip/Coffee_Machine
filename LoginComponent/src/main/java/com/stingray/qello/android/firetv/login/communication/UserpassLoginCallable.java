package com.stingray.qello.android.firetv.login.communication;

import android.text.TextUtils;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stingray.qello.android.firetv.login.communication.requestmodel.UserpassLoginRequestBody;
import com.stingray.qello.android.firetv.login.communication.requestmodel.UserpassLoginResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserpassLoginCallable extends ULCallable<UserpassLoginResponse> {
    private final static TypeReference<Map<String, String>> MAP_STRING_STRING = new TypeReference<Map<String, String>>() {};
    private final static String ENDPOINT = "/user/userpassLogin";
    private final static String TAG = UserpassLoginCallable.class.getSimpleName();

    private ObjectMapper objectMapper =  new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private UserpassLoginRequestBody requestBody;

    public UserpassLoginCallable(UserpassLoginRequestBody requestBody) {
        requestBody.setClientId(CLIENT_ID);
        this.requestBody = requestBody;
    }

    @Override
    public UserpassLoginResponse call() {
        try {
            Map<String, String> params = objectMapper.convertValue(requestBody, MAP_STRING_STRING);
            List<String> urlEncodedParams = new ArrayList<>();
            for (Map.Entry<String, String> entry: params.entrySet()) {
                urlEncodedParams.add(entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
            }
            String response = post(ENDPOINT, TextUtils.join("&", urlEncodedParams));
            return objectMapper.readValue(response, UserpassLoginResponse.class);
        } catch (IOException e) {
            Log.e(TAG, String.format("Failed to call endpoint [%s]", ENDPOINT), e);
            return null;
        }
    }
}
