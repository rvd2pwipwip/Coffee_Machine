package com.stingray.qello.firetv.android.contentbrowser.recipe;

import com.stingray.qello.firetv.android.recipe.Recipe;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TrackItemRecipe {
    private final Recipe recipe;

    public TrackItemRecipe() throws JsonProcessingException {
        Map<String, Object> recipeMap = new HashMap<>();
        recipeMap.put("cooker", "DynamicParser");
        recipeMap.put("format", "json");
        recipeMap.put("model", "com.stingray.qello.firetv.android.model.content.Track");
        recipeMap.put("translator", "TrackTranslator");
        recipeMap.put("modelType", "array");
        recipeMap.put("query", "$.data[?(@.data_type == 'TRACK')]");
        recipeMap.put("matchList", Arrays.asList(
                "data/title@mTitle",
                "data/artists@mSubtitle",
                "id@mId",
                "data_type@mAssetType",
                "id@mChannelId",
                "data/duration@mDuration",
                "data/parent_id@mParentId",
                "data/is_public@mIsPublic"
                )
        );

        ObjectMapper objectMapper = new ObjectMapper();
        this.recipe = Recipe.newInstance(objectMapper.writeValueAsString(recipeMap));
    }

    public Recipe getRecipe() {
        return recipe;
    }
}
