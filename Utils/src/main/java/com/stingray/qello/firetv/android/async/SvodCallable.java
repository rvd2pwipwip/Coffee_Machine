package com.stingray.qello.firetv.android.async;

import com.stingray.qello.firetv.android.utils.Helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.Callable;

public abstract class SvodCallable<T> implements Callable<T> {
    private static final String TAG = SvodCallable.class.getName();
    private static final String BASE_URL = "https://svod-test.api.stingray.com";

    protected static final String CLIENT_ID = "mBasxFOpteXOYwc9";

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

    protected Response post(String path, String jsonBody, Map<String, String> additionalHeaders) throws IOException {
        URL url = new URL(createUrl(path));

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("x-client-id", CLIENT_ID);

        for (Map.Entry<String, String> entry : additionalHeaders.entrySet()) {
            urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
        }

        try (OutputStream os = urlConnection.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return new Response(urlConnection.getResponseCode(), getResponseBody(urlConnection));
    }


    private String getResponseBody(HttpURLConnection urlConnection) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), Helpers.getDefaultAppCharset()), 8)) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
    }

    protected static class Response {
        private final int code;
        private final String body;

        public Response(int code, String body) {
            this.code = code;
            this.body = body;
        }

        public int getCode() {
            return code;
        }

        public String getBody() {
            return body;
        }
    }
}
