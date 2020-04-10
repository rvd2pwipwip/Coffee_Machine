package com.stingray.qello.firetv.android.async;

import java.util.HashMap;
import java.util.Map;

public class UrlConstants {
    private final static EnvironmentSettings ENV_SETTINGS = getMap()
            // Change this to set urls
            .get(Environment.STAGE);

    final static String BASE_AVC_URL = ENV_SETTINGS.baseAVCUrl;
    final static String BASE_UL_API_URL = ENV_SETTINGS.baseULAPIUrl;
    final static String BASE_UL_FE_URL = ENV_SETTINGS.baseULFEUrl;

    public final static String HOMEPAGE_LOAD_URL = BASE_AVC_URL + "/v1/browse-pages/homepage";


    private static Map<Environment, EnvironmentSettings> getMap() {
        Map<Environment, EnvironmentSettings> envMap = new HashMap<>();
        envMap.put(Environment.STAGE, new EnvironmentSettings("https://svod-stage.api.stingray.com",
                "https://ulogin-proxy-stage.stingray.com", "https://login-stage.stingray.com"));

        envMap.put(Environment.TEST, new EnvironmentSettings("https://svod-test.api.stingray.com",
                "https://ulogin-proxy-test.stingray.com", "https://login-test.stingray.com"));

        return envMap;
    }

    private static class EnvironmentSettings {
        private final String baseAVCUrl;
        private final String baseULAPIUrl;
        private final String baseULFEUrl;

        public EnvironmentSettings(String baseAVCUrl, String baseULAPIUrl, String baseULFEUrl) {
            this.baseAVCUrl = baseAVCUrl;
            this.baseULAPIUrl = baseULAPIUrl;
            this.baseULFEUrl = baseULFEUrl;
        }
    }

    private enum Environment {STAGE, TEST};
}