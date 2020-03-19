package com.amazon.android.contentbrowser.recipe;

import com.amazon.android.recipe.Recipe;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GenreSectionRecipe {
    private final Recipe recipe;

    public GenreSectionRecipe() throws JsonProcessingException {
        Map<String, Object> recipeMap = new HashMap<>();
        recipeMap.put("cooker", "DynamicParser");
        recipeMap.put("format", "json");
        recipeMap.put("model", "com.amazon.android.model.content.Genre");
        recipeMap.put("translator", "GenreTranslator");
        recipeMap.put("modelType", "array");
        recipeMap.put("query", "$.data[?(@.data_type == 'GENRE')]");
        recipeMap.put("matchList", Arrays.asList(
                "id@mId",
                "data/title@mTitle",
                "data_type@mAssetType",
                "data/images/0/url@mCardImageUrl"
                )
        );

        ObjectMapper objectMapper = new ObjectMapper();
        this.recipe = Recipe.newInstance(objectMapper.writeValueAsString(recipeMap));
    }

    public Recipe getRecipe() {
        return recipe;
    }
}
