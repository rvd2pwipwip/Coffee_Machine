package com.stingray.qello.firetv.android.async;

import com.stingray.qello.firetv.android.async.requestmodel.TokenRequestBody;
import com.stingray.qello.firetv.android.async.requestmodel.TokenResponse;
import com.stingray.qello.firetv.android.event.AuthenticationStatusUpdateEvent;
import com.stingray.qello.firetv.android.ui.constants.PreferencesConstants;
import com.stingray.qello.firetv.android.utils.Preferences;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.Callable;

public abstract class SvodCallable<T> extends BaseCommunicator implements Callable<T> {
    private static final String TAG = SvodCallable.class.getName();
    private static final String BASE_URL = "https://svod-test.api.stingray.com";

    private String createUrl(String url) {
        return BASE_URL + url;
    }

    protected String get(String path) throws IOException {
        return get(path, Preferences.getString(PreferencesConstants.ACCESS_TOKEN));
    }

    protected String get(String path, String accessToken) throws IOException {
        HttpURLConnection urlConnection;
        Response response;
        boolean performRetry = false;

        do {
            URL url = new URL(createUrl(path));
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("x-client-id", CLIENT_ID);

            if (accessToken != null && !accessToken.isEmpty()) {
                urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
            }

            response = new Response(urlConnection.getResponseCode(), getResponseBody(urlConnection));

            if (response.getCode() == 401 && !performRetry) {
                String refreshToken = Preferences.getString(PreferencesConstants.REFRESH_TOKEN);
                boolean performLogout = true;

                if (refreshToken != null) {
                    TokenRequestBody tokenRequestBody = new TokenRequestBody(refreshToken, CLIENT_ID);
                    TokenResponse tokenResponse = new TokenCallable(tokenRequestBody).call();
                    if (tokenResponse != null) {
                        Preferences.setString(PreferencesConstants.ACCESS_TOKEN, tokenResponse.getAccessToken());
                        performRetry = true;
                        performLogout = false;
                    }
                }

                if (performLogout) {
                    Preferences.setLoggedOutState();
                    EventBus.getDefault().post(new AuthenticationStatusUpdateEvent(false));
                    performRetry = true;
                }
            } else {
                performRetry = false;
            }

        } while (performRetry);

        return response.getBody();
    }

    protected Response post(String path, String jsonBody, Map<String, String> additionalHeaders) throws IOException {
        URL url = new URL(createUrl(path));

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("x-client-id", CLIENT_ID);

        String accessToken = Preferences.getString(PreferencesConstants.ACCESS_TOKEN);

        if (accessToken != null && !accessToken.isEmpty()) {
            urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        }


        for (Map.Entry<String, String> entry : additionalHeaders.entrySet()) {
            urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
        }

        try (OutputStream os = urlConnection.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return new Response(urlConnection.getResponseCode(), getResponseBody(urlConnection));
    }
}
