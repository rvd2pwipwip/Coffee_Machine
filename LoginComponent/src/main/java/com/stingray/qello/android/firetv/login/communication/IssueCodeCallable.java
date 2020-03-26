package com.stingray.qello.android.firetv.login.communication;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stingray.qello.android.firetv.login.communication.requestmodel.IssueCodeRequestBody;
import com.stingray.qello.android.firetv.login.communication.requestmodel.IssueCodeResponse;

import java.util.Map;

public class IssueCodeCallable extends ULCallable<IssueCodeResponse> {
    private final static TypeReference<Map<String, String>> MAP_STRING_STRING = new TypeReference<Map<String, String>>() {};
    private final static String ENDPOINT = "/oauth/issueCode";
    private final static String TAG = IssueCodeCallable.class.getSimpleName();

    private ObjectMapper objectMapper =  new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private IssueCodeRequestBody requestBody;

    public IssueCodeCallable(IssueCodeRequestBody requestBody) {
        requestBody.setClientId(CLIENT_ID);
        this.requestBody = requestBody;
    }

    @Override
    public IssueCodeResponse call() {
        try {
            Map<String, String> params = objectMapper.convertValue(requestBody, MAP_STRING_STRING);
            Response response = post(ENDPOINT, params);

            if (response.getCode() == 200) {
                return objectMapper.readValue(response.getBody(), IssueCodeResponse.class);
            } else {
                throw new Exception(String.format("Bad response code [%s]", response.getCode()));
            }
        } catch (Exception e) {
            Log.e(TAG, String.format("Failed to call endpoint [%s]", ENDPOINT), e);
            return null;
        }
    }
}
