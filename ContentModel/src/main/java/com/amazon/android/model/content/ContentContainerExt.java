package com.amazon.android.model.content;

import com.amazon.android.model.SvodMetadata;

public class ContentContainerExt {
    private final SvodMetadata metadata;
    private final ContentContainer contentContainer;

    public ContentContainerExt() {
        this.metadata = new SvodMetadata();
        this.contentContainer = ContentContainer.newInstance("");
    }

    public ContentContainerExt(SvodMetadata metadata, ContentContainer contentContainer) {
        this.metadata = metadata;
        this.contentContainer = contentContainer;
    }

    public SvodMetadata getMetadata() {
        return metadata;
    }

    public ContentContainer getContentContainer() {
        return contentContainer;
    }
}
