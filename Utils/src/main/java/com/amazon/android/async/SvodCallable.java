package com.amazon.android.async;


import com.amazon.android.utils.NetworkUtils;

import java.io.IOException;
import java.util.concurrent.Callable;

public abstract class SvodCallable<T> implements Callable<T> {
    private static final String BASE_URL = "https://svod-stage.api.stingray.com";

    private String createUrl(String url) {
        return BASE_URL + url;
    }

    protected String getData(String url) throws IOException {
        return NetworkUtils.getDataLocatedAtUrl(createUrl(url));
    }
}
