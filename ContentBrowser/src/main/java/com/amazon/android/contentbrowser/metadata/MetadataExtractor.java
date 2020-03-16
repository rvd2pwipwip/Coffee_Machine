package com.amazon.android.contentbrowser.metadata;

import com.amazon.android.model.ItemMetadata;
import com.amazon.android.model.SvodMetadata;
import com.amazon.android.utils.SvodObjectMapperProvider;
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
