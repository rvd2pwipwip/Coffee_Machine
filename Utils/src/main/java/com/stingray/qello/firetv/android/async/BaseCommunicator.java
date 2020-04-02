package com.stingray.qello.firetv.android.async;

import android.util.Log;

import com.stingray.qello.firetv.android.utils.Helpers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class BaseCommunicator {
    private static final String TAG = BaseCommunicator.class.getName();
    protected static final String CLIENT_ID = "nM3NxVg5GP3yDVp1";

    protected static class Response {
        private final int code;
        private final String body;

        public Response() {
            this.code = -1;
            this.body = "";
        }

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


    protected String getResponseBody(HttpURLConnection urlConnection) {
        InputStream inputStream;

        try {
            String responseCode = String.valueOf(urlConnection.getResponseCode());

            if (responseCode.startsWith("4") || responseCode.startsWith("5")) {
                inputStream = urlConnection.getErrorStream();
            } else {
                inputStream = urlConnection.getInputStream();
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed to get connection inputStream. Fall-backing to connection error stream", e);
            inputStream = urlConnection.getErrorStream();
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Helpers.getDefaultAppCharset()), 8)) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            Log.w(TAG, "Failed to read connection response body. Fall-backing to empty string", e);
            return "";
        }
    }
}
