package com.stingray.qello.firetv.android.contentbrowser.metadata;

import com.stingray.qello.firetv.android.model.ItemMetadata;
import com.stingray.qello.firetv.android.model.SvodMetadata;
import com.stingray.qello.firetv.android.utils.SvodObjectMapperProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MetadataExtractor {
    private final ObjectMapper objectMapper;

    public MetadataExtractor() {
        this.objectMapper = new SvodObjectMapperProvider().get();
    }

    public SvodMetadata extractAtFirstLevel(String itemJsonString) throws JsonProcessingException {
        ItemMetadata itemMetadata = objectMapper.readValue(itemJsonString, ItemMetadata.class);
        return itemMetadata.getMetadata();
    }
}
