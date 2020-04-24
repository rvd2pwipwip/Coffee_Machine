package com.stingray.qello.firetv.android.async;

import java.util.HashMap;
import java.util.Map;

public class UrlConstants {
    private final static EnvironmentSettings ENV_SETTINGS = getMap()
            // Change this to set urls
            .get(Environment.TEST);

    final static String BASE_AVC_URL = ENV_SETTINGS.baseAVCUrl;
    final static String BASE_UL_API_URL = ENV_SETTINGS.baseULAPIUrl;
    final static String BASE_UL_FE_URL = ENV_SETTINGS.baseULFEUrl;
    final static String WEB_URL = ENV_SETTINGS.webUrl;

    public final static String HOMEPAGE_LOAD_URL = BASE_AVC_URL + "/v1/browse-pages/homepage";


    private static Map<Environment, EnvironmentSettings> getMap() {
        Map<Environment, EnvironmentSettings> envMap = new HashMap<>();
        envMap.put(Environment.STAGE, new EnvironmentSettings("https://svod-stage.api.stingray.com",
                "https://ulogin-proxy-stage.stingray.com", "https://login-stage.stingray.com", "https://qello-stage.stingray.com"));

        envMap.put(Environment.TEST, new EnvironmentSettings("https://svod-test.api.stingray.com",
                "https://ulogin-proxy-test.stingray.com", "https://login-test.stingray.com", "https://qello-test.stingray.com"));

        envMap.put(Environment.DEV, new EnvironmentSettings("https://svod-dev.api.stingray.com",
                "https://ulogin-proxy-dev.stingray.com", "https://login-dev.stingray.com", "https://qello-dev.stingray.com"));

        return envMap;
    }

    private static class EnvironmentSettings {
        private final String baseAVCUrl;
        private final String baseULAPIUrl;
        private final String baseULFEUrl;
        private final String webUrl;

        public EnvironmentSettings(String baseAVCUrl, String baseULAPIUrl, String baseULFEUrl, String webUrl) {
            this.baseAVCUrl = baseAVCUrl;
            this.baseULAPIUrl = baseULAPIUrl;
            this.baseULFEUrl = baseULFEUrl;
            this.webUrl = webUrl;
        }
    }

    private enum Environment {STAGE, TEST, DEV}
}
