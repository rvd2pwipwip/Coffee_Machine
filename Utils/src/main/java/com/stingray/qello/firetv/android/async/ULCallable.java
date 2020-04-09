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
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.stingray.qello.firetv.android.async.UrlConstants.BASE_UL_API_URL;
import static com.stingray.qello.firetv.android.async.UrlConstants.BASE_UL_FE_URL;

public abstract class ULCallable<T> extends BaseCommunicator implements Callable<T> {
    private static final String TAG = ULCallable.class.getName();
    protected static final String BASE_API_URL = BASE_UL_API_URL + "/loginapi";
    protected static final String BASE_CLIENT_URL = BASE_UL_FE_URL;

    private String createUrl(String url) {
        return BASE_API_URL + url;
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

        return new Response(urlConnection.getResponseCode(), getResponseBody(urlConnection), url.toString());
    }


    public static class UnexpectedException extends Exception {
        public UnexpectedException(int code, String endpoint, String requestBody, String responseBody) {
            super(String.format("Unexpected error occurred when calling to endpoint [%s]. Code: [%s] RequestBody [%s] Response: [%s]", endpoint, code, requestBody, responseBody));
        }
    }
}
