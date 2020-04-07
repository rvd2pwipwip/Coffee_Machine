package com.stingray.qello.android.firetv.login.communication;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stingray.qello.android.firetv.login.communication.requestmodel.ForgotPasswordRequestBody;
import com.stingray.qello.android.firetv.login.communication.requestmodel.ForgotPasswordResponse;
import com.stingray.qello.firetv.android.async.ULCallable;

import java.io.IOException;
import java.util.Map;

public class ForgotPasswordCallable extends ULCallable<Void> {
    private final static TypeReference<Map<String, String>> MAP_STRING_STRING = new TypeReference<Map<String, String>>() {
    };
    private final static String ENDPOINT = "/user/forgotPassword";
    private final static String FORGOT_PASSWORD_LINK = BASE_CLIENT_URL + "/welcome/forgotpassword?captchaKey=";
    private final static String TAG = ForgotPasswordCallable.class.getSimpleName();

    private ObjectMapper objectMapper = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private ForgotPasswordRequestBody requestBody;

    public ForgotPasswordCallable(ForgotPasswordRequestBody requestBody) {
        requestBody.setClientId(CLIENT_ID);
        requestBody.setResetPasswordUri(FORGOT_PASSWORD_LINK);
        this.requestBody = requestBody;
    }

    @Override
    public Void call() throws IOException, EmailDoesntExistException, BadParameterException {

        Map<String, String> params = objectMapper.convertValue(requestBody, MAP_STRING_STRING);
        Response response = post(ENDPOINT, params);

        if (response.getCode() != 400) {
            throw new BadParameterException(ENDPOINT, requestBody.toString());
        }

        ForgotPasswordResponse forgotPasswordResponse = objectMapper.readValue(response.getBody(), ForgotPasswordResponse.class);

        if (forgotPasswordResponse.getEmailExists() == null || !forgotPasswordResponse.getEmailExists()) {
            throw new EmailDoesntExistException(requestBody.getEmail());
        }

        return null;
    }

    public static class EmailDoesntExistException extends Exception {
        EmailDoesntExistException(String email) {
            super(String.format("Email [%s] doesn't exist", email));
        }
    }
}
