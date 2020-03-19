package com.amazon.android.contentbrowser.explorepage;

import android.util.Log;

import com.amazon.android.async.SvodCallable;
import com.amazon.android.contentbrowser.recipe.GenreItemRecipe;
import com.amazon.android.model.content.Genre;
import com.amazon.android.model.translators.GenreTranslator;
import com.amazon.android.recipe.NoOpRecipeCallbacks;
import com.amazon.android.recipe.Recipe;
import com.amazon.dynamicparser.DynamicParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExplorePageCallable extends SvodCallable<List<Genre>> {
    private final static String ENDPOINT = "/v1/browse-pages/EXPLORE_PAGE";
    private final static String TAG = ExplorePageCallable.class.getSimpleName();
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final static GenreTranslator genreTranslator = new GenreTranslator();

    private static Recipe RECIPE;
    private static DynamicParser PARSER;

    private boolean initializationFailed = false;

    public ExplorePageCallable() {
        try {
            if (RECIPE == null && PARSER == null) {
                RECIPE = new GenreItemRecipe().getRecipe();
                PARSER = new DynamicParser();
                // Register content translator in case parser recipes use translation.
                PARSER.addTranslatorImpl(genreTranslator.getName(), genreTranslator);
            }
        } catch (Exception e) {
            Log.e(TAG, String.format("Failed to initialize [%s]", TAG), e);
            initializationFailed = true;
        }
    }

    @Override
    public List<Genre> call() {
        List<Genre> genres = new ArrayList<>();

        if (initializationFailed) {
            return genres;
        }

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
