package com.stingray.qello.firetv.android.async;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentConstants {
    // Change this to set urls
    private final static Environment CURRENT_ENV =  Environment.TEST;
    private final static EnvironmentSettings ENV_SETTINGS = getMap().get(CURRENT_ENV);

    public final static String BASE_AVC_URL = ENV_SETTINGS.baseAVCUrl;
    public final static String SEGMENT_WRITE_KEY = ENV_SETTINGS.segmentWriteKey;

    final static String BASE_UL_API_URL = ENV_SETTINGS.baseULAPIUrl;
    final static String BASE_UL_FE_URL = ENV_SETTINGS.baseULFEUrl;
    final static String WEB_URL = ENV_SETTINGS.webUrl;

    private static Map<Environment, EnvironmentSettings> getMap() {
        Map<Environment, EnvironmentSettings> envMap = new HashMap<>();

        String segmentWriteKeyDev = "thKdc3YGs1K6r3DQ2qKaXQNGdrFN3cUt";

        envMap.put(Environment.PROD, new EnvironmentSettings(
                "https://svod.api.stingray.com",
                "https://ulogin-proxy-prod.stingray.com",
                "https://login.stingray.com",
                "https://qello.stingray.com",
                "<TODO>"));

        envMap.put(Environment.STAGE, new EnvironmentSettings(
                "https://svod-stage.api.stingray.com",
                "https://ulogin-proxy-stage.stingray.com",
                "https://login-stage.stingray.com",
                "https://qello-stage.stingray.com",
                "<TODO>"));

        envMap.put(Environment.TEST, new EnvironmentSettings(
                "https://svod-test.api.stingray.com",
                "https://ulogin-proxy-test.stingray.com",
                "https://login-test.stingray.com",
                "https://qello-test.stingray.com",
                segmentWriteKeyDev));

        envMap.put(Environment.DEV, new EnvironmentSettings(
                "https://svod-dev.api.stingray.com",
                "https://ulogin-proxy-dev.stingray.com",
                "https://login-dev.stingray.com",
                "https://qello-dev.stingray.com",
                segmentWriteKeyDev));

        return envMap;
    }

    private static class EnvironmentSettings {
        private final String baseAVCUrl;
        private final String baseULAPIUrl;
        private final String baseULFEUrl;
        private final String webUrl;
        private final String segmentWriteKey;

        public EnvironmentSettings(String baseAVCUrl, String baseULAPIUrl, String baseULFEUrl, String webUrl, String segmentWriteKey) {
            this.baseAVCUrl = baseAVCUrl;
            this.baseULAPIUrl = baseULAPIUrl;
            this.baseULFEUrl = baseULFEUrl;
            this.webUrl = webUrl;
            this.segmentWriteKey = segmentWriteKey;
        }
    }

    private enum Environment {PROD, STAGE, TEST, DEV}
}
