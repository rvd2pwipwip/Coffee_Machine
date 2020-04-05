package com.stingray.qello.firetv.android.contentbrowser.callable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stingray.qello.firetv.android.async.SvodCallable;
import com.stingray.qello.firetv.android.contentbrowser.callable.model.Item;
import com.stingray.qello.firetv.android.contentbrowser.callable.model.SvodConcert;
import com.stingray.qello.firetv.android.utils.SvodObjectMapperProvider;

public class ContentInfoCallable extends SvodCallable<Item<Item<SvodConcert>>> {
    private final static String ENDPOINT = "/v1/content-pages/%s/sections/content_info";
    private final static String TAG = ContentInfoCallable.class.getSimpleName();

    private final ObjectMapper objectMapper = new SvodObjectMapperProvider().get();

    private String contentId;

    public ContentInfoCallable(String contentId) {
        this.contentId = contentId;
    }

    @Override
    public Item<Item<SvodConcert>> call() throws JsonProcessingException {
        String url = String.format(ENDPOINT, contentId);
        Response response = performGet(url);

        if (response.getCode() != 200) {
            throw new RuntimeException(String.format("Failed to get content info from [%s]. Response: [%s]", ENDPOINT, response.getBody()));
        }

        return objectMapper.readValue(response.getBody(), Item.NESTED_SINGLE_CONCERT_TYPE);
    }
}
