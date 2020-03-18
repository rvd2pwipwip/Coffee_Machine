package com.amazon.android.contentbrowser.recipe;

import com.amazon.android.recipe.Recipe;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TrackSectionRecipe {
    private final Recipe recipe;

    public TrackSectionRecipe() throws JsonProcessingException {
        Map<String, Object> recipeMap = new HashMap<>();
        recipeMap.put("cooker", "DynamicParser");
        recipeMap.put("format", "json");
        recipeMap.put("model", "com.amazon.android.model.content.Track");
        recipeMap.put("translator", "TrackTranslator");
        recipeMap.put("modelType", "array");
        recipeMap.put("query", "$.data[?(@.data_type == 'TRACK')]");
        recipeMap.put("matchList", Arrays.asList(
                "data/title@mTitle",
                "data/artists@mSubtitle",
                "id@mId",
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
