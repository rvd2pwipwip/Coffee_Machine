package com.amazon.android.contentbrowser.search;

import android.util.Log;

import com.amazon.android.async.SvodCallable;
import com.amazon.android.contentbrowser.ContentContainerExtFactory;
import com.amazon.android.contentbrowser.metadata.MetadataExtractor;
import com.amazon.android.model.SvodMetadata;
import com.amazon.android.model.content.Content;
import com.amazon.android.model.content.ContentContainer;
import com.amazon.android.model.content.ContentContainerExt;
import com.amazon.android.model.translators.ContentTranslator;
import com.amazon.android.recipe.NoOpRecipeCallbacks;
import com.amazon.android.recipe.Recipe;
import com.amazon.dynamicparser.DynamicParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchCallable extends SvodCallable<ContentContainerExt> {
    private final static String ENDPOINT = "/v1/content-search?text=%s";
    private final static String NAME_FORMAT = "SearchResults%s";
    private final static String TAG = SearchCallable.class.getSimpleName();
    private final static ContentContainerExtFactory contentContainerExtFactory = new ContentContainerExtFactory();

    private final ContentTranslator contentTranslator = new ContentTranslator();

    private static Recipe RECIPE;
    private static DynamicParser PARSER;
    private final String query;

    public SearchCallable(String query) {

        if (RECIPE == null && PARSER == null) {
            Map<String, Object> recipeMap = new HashMap<>();
            recipeMap.put("cooker", "DynamicParser");
            recipeMap.put("format", "json");
            recipeMap.put("model", "com.amazon.android.model.content.Content");
            recipeMap.put("translator", "ContentTranslator");
            recipeMap.put("modelType", "array");
            recipeMap.put("query", "$.data[?(@.data_type == 'VIDEO')]");
            recipeMap.put("matchList", Arrays.asList(
                    "data/title@mTitle",
                    "data/artists@mSubtitle",
                    "id@mId",
                    "data/short_description@mDescription",
                    "data/images/0/url@mUrl",
                    "data/images/0/url@mCardImageUrl",
                    "data/images/1/url@mBackgroundImageUrl",
                    "id@mChannelId")
            );

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                RECIPE = Recipe.newInstance(objectMapper.writeValueAsString(recipeMap));
            } catch (JsonProcessingException e) {
                // Do nothing lol
            }

            PARSER = new DynamicParser();
            // Register content translator in case parser recipes use translation.
            PARSER.addTranslatorImpl(contentTranslator.getName(), contentTranslator);
        }

        this.query = query;
    }

    @Override
    public ContentContainerExt call() {
        String containerName = String.format(NAME_FORMAT, query);
        try {
            String url = String.format(ENDPOINT, query);
            String jsonResponse = getData(url);

            return contentContainerExtFactory.create(containerName, jsonResponse, PARSER, RECIPE, contentTranslator);

        } catch (Exception e) {
            Log.e(TAG, String.format("Failed to get search results from [%s]", ENDPOINT),e);
            return new ContentContainerExt(new SvodMetadata(), ContentContainer.newInstance(containerName));
        }
    }
}
