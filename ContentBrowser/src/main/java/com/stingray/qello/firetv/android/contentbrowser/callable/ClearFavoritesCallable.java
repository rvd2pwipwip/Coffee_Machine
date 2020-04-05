package com.stingray.qello.firetv.android.contentbrowser.callable;

import com.stingray.qello.firetv.android.async.SvodCallable;

public class ClearFavoritesCallable extends SvodCallable<Void> {
    private final static String ENDPOINT = "/v1/profile/favorites";
    private final static String TAG = ClearFavoritesCallable.class.getSimpleName();

    @Override
    public Void call() {
        String url = String.format(ENDPOINT);

        Response response = delete(url);
        if (response.getCode() != 204) {
            throw new RuntimeException(String.format("Failed to update like status from [%s]. Response: [%s]", url, response.getBody()));
        }

        return null;
    }
}
