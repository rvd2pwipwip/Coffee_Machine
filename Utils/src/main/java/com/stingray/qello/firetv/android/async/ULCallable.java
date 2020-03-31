package com.stingray.qello.firetv.android.async;

import android.text.TextUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public abstract class ULCallable<T> extends BaseCommunicator implements Callable<T> {
    private static final String TAG = ULCallable.class.getName();
    private static final String BASE_URL = "https://ulogin-proxy-test.stingray.com/loginapi";

    private String createUrl(String url) {
        return BASE_URL + url;
    }

    protected String get(String path) throws IOException {
        URL url = new URL(createUrl(path));
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("x-client-id", CLIENT_ID);

        return getResponseBody(urlConnection);
    }

    protected Response post(String path, Map<String, String> formParams) throws IOException {
        return post(path, formParams, Collections.emptyMap());
    }

    protected Response post(String path, Map<String, String> formParams, Map<String, String> additionalHeaders) throws IOException {

        List<String> urlEncodedParams = new ArrayList<>();
        for (Map.Entry<String, String> entry: formParams.entrySet()) {
            urlEncodedParams.add(entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
        }

        URL url = new URL(createUrl(path));

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        urlConnection.setRequestProperty("x-client-id", CLIENT_ID);

        for (Map.Entry<String, String> entry: additionalHeaders.entrySet()) {
            urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
        }

        try(OutputStream os = urlConnection.getOutputStream()) {
            byte[] input =  TextUtils.join("&", urlEncodedParams).getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return new Response(urlConnection.getResponseCode(), getResponseBody(urlConnection));
    }
}
