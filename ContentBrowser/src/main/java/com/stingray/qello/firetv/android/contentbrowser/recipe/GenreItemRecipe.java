package com.stingray.qello.firetv.android.contentbrowser.recipe;

import com.stingray.qello.firetv.android.recipe.Recipe;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GenreItemRecipe {
    private final Recipe recipe;

    public GenreItemRecipe() throws JsonProcessingException {
        Map<String, Object> recipeMap = new HashMap<>();
        recipeMap.put("cooker", "DynamicParser");
        recipeMap.put("format", "json");
        recipeMap.put("model", "com.stingray.qello.firetv.android.model.content.Genre");
        recipeMap.put("translator", "GenreTranslator");
        recipeMap.put("modelType", "array");
        recipeMap.put("query", "$.data[?(@.data_type == 'GENRE')]");
        recipeMap.put("matchList", Arrays.asList(
                "id@mId",
                "data/title@mTitle",
                "data_type@mAssetType",
                "data/images/THUMBNAIL_PORTRAIT@mCardImageUrl"
                )
        );

        ObjectMapper objectMapper = new ObjectMapper();
        this.recipe = Recipe.newInstance(objectMapper.writeValueAsString(recipeMap));
    }

    public Recipe getRecipe() {
        return recipe;
    }
}
