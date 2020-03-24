package com.stingray.qello.firetv.android.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

public class SvodObjectMapperProvider {
    private static ObjectMapper objectMapper;

    public ObjectMapper get() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper()
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        }

        return objectMapper;
    }
}
