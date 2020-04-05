package com.stingray.qello.firetv.android.contentbrowser.callable;

import com.stingray.qello.firetv.android.async.SvodCallable;

public class RemoveFromFavoriteCallable extends SvodCallable<Void> {
    private final static String ENDPOINT = "/v1/profile/favorites/%s";
    private final static String TAG = RemoveFromFavoriteCallable.class.getSimpleName();

    private String contentId;

    public RemoveFromFavoriteCallable(String contentId) {
        this.contentId = contentId;
    }

    @Override
    public Void call() {
        String url = String.format(ENDPOINT, contentId);

        Response response = delete(url);
        if (response.getCode() != 204) {
            throw new RuntimeException(String.format("Failed to update like status from [%s]. Response: [%s]", url, response.getBody()));
        }

        return null;
    }
}
