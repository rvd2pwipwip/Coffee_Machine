package com.stingray.qello.android.firetv.login.communication;

import com.amazon.android.utils.Helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

public abstract class ULCallable<T> implements Callable<T> {
    private static final String TAG = ULCallable.class.getName();
    private static final String BASE_URL = "https://ulogin-proxy-test.stingray.com/loginapi";

    protected static final String CLIENT_ID = "mBasxFOpteXOYwc9";

    private String createUrl(String url) {
        return BASE_URL + url;
    }

    protected String get(String path) throws IOException {
        URL url = new URL(createUrl(path));
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("x-client-id", CLIENT_ID);

        return getResponse(urlConnection);
    }

    protected String post(String path, String body) throws IOException {
        URL url = new URL(createUrl(path));

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        urlConnection.setRequestProperty("x-client-id", CLIENT_ID);

        try(OutputStream os = urlConnection.getOutputStream()) {
            byte[] input = body.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return getResponse(urlConnection);
    }


    private String getResponse(HttpURLConnection urlConnection) throws IOException {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), Helpers.getDefaultAppCharset()), 8)) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
    }
}
