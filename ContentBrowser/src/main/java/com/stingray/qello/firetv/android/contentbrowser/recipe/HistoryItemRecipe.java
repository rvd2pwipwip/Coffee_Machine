package com.stingray.qello.firetv.android.contentbrowser.recipe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stingray.qello.firetv.android.recipe.Recipe;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HistoryItemRecipe {
    private final Recipe recipe;

    public HistoryItemRecipe() throws JsonProcessingException {
        Map<String, Object> recipeMap = new HashMap<>();
        recipeMap.put("cooker", "DynamicParser");
        recipeMap.put("format", "json");
        recipeMap.put("model", "com.stingray.qello.firetv.android.model.content.Content");
        recipeMap.put("translator", "ContentTranslator");
        recipeMap.put("modelType", "array");
        recipeMap.put("query", "$.data[*].data[?(@.data_type == 'VIDEO')]");
        recipeMap.put("matchList", Arrays.asList(
                "data/title@mTitle",
                "data/artists@mSubtitle",
                "id@mId",
                "data_type@mAssetType",
                "data/short_description@mDescription",
                "data/images/0/url@mUrl",
                "data/images/0/url@mCardImageUrl",
                "data/images/1/url@mBackgroundImageUrl",
                "id@mChannelId")
        );

        ObjectMapper objectMapper = new ObjectMapper();
        this.recipe = Recipe.newInstance(objectMapper.writeValueAsString(recipeMap));
    }

    public Recipe getRecipe() {
        return recipe;
    }
}
