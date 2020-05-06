package com.stingray.qello.firetv.android.async;

import android.util.Log;

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
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.stingray.qello.firetv.android.async.EnvironmentConstants.BASE_AVC_URL;

public abstract class SvodCallable<T> extends BaseCommunicator implements Callable<T> {
    private static final String TAG = SvodCallable.class.getName();
    private static final String BASE_URL = BASE_AVC_URL;

    private String createUrl(String url) {
        return BASE_URL + url;
    }

    @Deprecated
    protected String get(String path) throws IOException {
        return performGet(path).getBody();
    }

    protected Response performGet(String path, String accessToken) {
        Preferences.setString(PreferencesConstants.ACCESS_TOKEN, accessToken);
        return performGet(path);
    }

    protected Response performGet(String path) {
        return performWithTokenRefresh(() -> {
            HttpURLConnection urlConnection = createUrlConnection(path, "GET");
            return new Response(urlConnection.getResponseCode(), getResponseBody(urlConnection), urlConnection.getURL().toString());
        });
    }

    protected Response post(String path, String jsonBody) {
        return post(path, jsonBody, Collections.emptyMap());
    }

    protected Response post(String path, String jsonBody, Map<String, String> additionalHeaders) {

        return performWithTokenRefresh(() -> {
            HttpURLConnection urlConnection = createUrlConnection(path, "POST");

            for (Map.Entry<String, String> entry : additionalHeaders.entrySet()) {
                urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            try (OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            return new Response(urlConnection.getResponseCode(), getResponseBody(urlConnection), urlConnection.getURL().toString());
        });
    }

    protected Response delete(String path) {
        return performWithTokenRefresh(() -> {
            HttpURLConnection urlConnection =  createUrlConnection(path, "DELETE");
            return new Response(urlConnection.getResponseCode(), getResponseBody(urlConnection), urlConnection.getURL().toString());
        });
    }

    private HttpURLConnection createUrlConnection(String path, String method) throws IOException {
        HttpURLConnection urlConnection;
        URL url = new URL(createUrl(path));
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod(method);
        urlConnection.setRequestProperty("x-client-id", CLIENT_ID);
        urlConnection.setRequestProperty("Content-Type", "application/json");

        String accessToken = Preferences.getString(PreferencesConstants.ACCESS_TOKEN);
        if (!accessToken.isEmpty()) {
            urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);
        }

        return urlConnection;
    }

    private Response performWithTokenRefresh(Callable<Response> responseCallable) {
        try {
            Response response = null;
            do {
                String accessToken = Preferences.getString(PreferencesConstants.ACCESS_TOKEN);
                long accessTokenExpiryDate = Preferences.getLong(PreferencesConstants.ACCESS_TOKEN_EXPIRED_TIME);
                boolean isAccessTokenExpired = new Date().after(new Date(accessTokenExpiryDate));
                if (accessToken.isEmpty()) {
                    response = responseCallable.call();
                } else {
                    if (isAccessTokenExpired) {
                        refreshTokenOrPerformLogout();
                    } else {
                        response = responseCallable.call();

                        if (response.getCode() == 401) {
                            refreshTokenOrPerformLogout();
                            response = null;
                        }
                    }
                }
            } while (response == null);

            return response;
        } catch (Exception e) {
            Log.w(TAG, "Failed to get response. Returning default response", e);
            return new Response();
        }
    }

    private void refreshTokenOrPerformLogout() {
        String refreshToken = Preferences.getString(PreferencesConstants.REFRESH_TOKEN);
        boolean performLogout = true;

        if (refreshToken != null) {
            TokenRequestBody tokenRequestBody = new TokenRequestBody(refreshToken, CLIENT_ID);
            TokenResponse tokenResponse = new TokenCallable(tokenRequestBody).call();
            if (tokenResponse != null) {
                Preferences.setString(PreferencesConstants.ACCESS_TOKEN, tokenResponse.getAccessToken());
                long accessTokenExpiryDate = new Date().getTime() + tokenResponse.getExpiresInMS();
                Preferences.setLong(PreferencesConstants.ACCESS_TOKEN_EXPIRED_TIME, accessTokenExpiryDate);
                performLogout = false;
            }
        }

        if (performLogout) {
            Preferences.setLoggedOutState();
            EventBus.getDefault().post(new AuthenticationStatusUpdateEvent(false));
        }
    }
}
