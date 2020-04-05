package com.stingray.qello.firetv.android.contentbrowser.callable;

import com.stingray.qello.firetv.android.async.SvodCallable;

public class ClearBrowsePageCallable extends SvodCallable<Void> {
    private final static String ENDPOINT = "/v1/profile/%s";
    private final static String TAG = ClearBrowsePageCallable.class.getSimpleName();

    private String section;

    public ClearBrowsePageCallable(String section) {
        this.section = section;
    }

    @Override
    public Void call() {
        String url = String.format(ENDPOINT, section);

        Response response = delete(url);
        if (response.getCode() != 204) {
            throw new RuntimeException(String.format("Failed to update like status from [%s]. Response: [%s]", url, response.getBody()));
        }

        return null;
    }
}
