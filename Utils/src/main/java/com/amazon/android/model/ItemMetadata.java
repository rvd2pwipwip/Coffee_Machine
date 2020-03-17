package com.amazon.android.model;

public class ItemMetadata {
    private SvodMetadata metadata;

    private ItemMetadata() {
    }

    public ItemMetadata(SvodMetadata metadata) {
        this.metadata = metadata;
    }

    public SvodMetadata getMetadata() {
        return metadata;
    }
}
