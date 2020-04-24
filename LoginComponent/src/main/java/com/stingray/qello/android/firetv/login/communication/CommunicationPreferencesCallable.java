package com.stingray.qello.android.firetv.login.communication;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stingray.qello.android.firetv.login.communication.requestmodel.CommunicationPreferencesRequestBody;
import com.stingray.qello.firetv.android.async.ULCallable;

import java.util.Map;

public class CommunicationPreferencesCallable extends ULCallable<Void> {
    private final static String ENDPOINT = "/marketing/communicationPreferences";
    private final static String TAG = CommunicationPreferencesCallable.class.getSimpleName();

    private ObjectMapper objectMapper =  new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private CommunicationPreferencesRequestBody requestBody;

    public CommunicationPreferencesCallable(CommunicationPreferencesRequestBody requestBody) {
        requestBody.setClientId(CLIENT_ID);
        this.requestBody = requestBody;
    }

    @Override
    public Void call() throws Exception {
        Map<String, String> params = objectMapper.convertValue(requestBody, new TypeReference<Map<String, String>>() {});
        Response response = post(ENDPOINT, params);

        if (!String.valueOf(response.getCode()).startsWith("2")) {
            throw new UnexpectedException(response.getCode(), response.getUrl(), params.toString(), response.getBody());
        }

        return null;
    }
}
