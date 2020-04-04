package com.stingray.qello.firetv.android.contentbrowser.callable.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.stingray.qello.firetv.android.model.SvodMetadata;

public class Item<T> {
    public static final TypeReference<Item<Item<SvodConcert>>> NESTED_SINGLE_CONCERT_TYPE = new TypeReference<Item<Item<SvodConcert>>>() {};

    private String id;
    private SvodMetadata metadata;
    private T data;

    // For deserialization
    public Item() { }

    public String getId() {
        return id;
    }

    public SvodMetadata getMetadata() {
        return metadata;
    }

    public T getData() {
        return data;
    }
}
