package com.amazon.android.contentbrowser.explorepage;

import android.util.Log;

import com.amazon.android.async.SvodCallable;
import com.amazon.android.model.content.Genre;
import com.amazon.android.model.translators.GenreTranslator;
import com.amazon.android.recipe.NoOpRecipeCallbacks;
import com.amazon.android.recipe.Recipe;
import com.amazon.dynamicparser.DynamicParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExplorePageCallable extends SvodCallable<List<Genre>> {
    private final static String ENDPOINT = "/v1/browse-pages/EXPLORE_PAGE";
    private final static String TAG = ExplorePageCallable.class.getSimpleName();
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final static GenreTranslator genreTranslator = new GenreTranslator();

    private static Recipe RECIPE;
    private static DynamicParser PARSER;

    public ExplorePageCallable() {
        if (RECIPE == null && PARSER == null) {

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
                    "data/images/0/url@mCardImageUrl"
                    )
            );

            try {
                RECIPE = Recipe.newInstance(OBJECT_MAPPER.writeValueAsString(recipeMap));
            } catch (JsonProcessingException e) {
                // Do nothing lol
            }

            PARSER = new DynamicParser();
            // Register content translator in case parser recipes use translation.
            PARSER.addTranslatorImpl(genreTranslator.getName(), genreTranslator);
        }
    }

    @Override
    public List<Genre> call() {

        List<Genre> genres = new ArrayList<>();
        try {
            String jsonResponse = getData(ENDPOINT);

            Log.i(TAG, String.format("Received response: %s", jsonResponse));

            List<Map<String, Object>> cookedJson = PARSER.parseInput(RECIPE, jsonResponse, null);

            for (Map<String, Object> objectMap: cookedJson) {
                Genre genre = (Genre) PARSER.translateMapToModel(RECIPE, new NoOpRecipeCallbacks(), objectMap);

                if (genreTranslator.validateModel(genre)) {
                    genres.add(genre);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, String.format("Failed to get genres from [%s]", ENDPOINT),e);
        }

        return genres;
    }
}
