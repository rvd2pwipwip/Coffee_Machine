package com.amazon.android.contentbrowser.genre;

import android.util.Log;

import com.amazon.android.async.SvodCallable;
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

public class GenreFilterCallable extends SvodCallable<ContentContainerExt> {
    private final static String ENDPOINT = "/v1/browse-pages/searchpage/sections/%s";
    private final static String TAG = GenreFilterCallable.class.getSimpleName();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ContentTranslator contentTranslator = new ContentTranslator();
    private final MetadataExtractor metadataExtractor = new MetadataExtractor();

    private static Recipe RECIPE;
    private static DynamicParser PARSER;

    private final String genreId;

    public GenreFilterCallable(String genreId) {

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
                RECIPE = Recipe.newInstance(objectMapper.writeValueAsString(recipeMap));
            } catch (JsonProcessingException e) {
                // Do nothing lol
            }

            PARSER = new DynamicParser();
            // Register content translator in case parser recipes use translation.
            PARSER.addTranslatorImpl(contentTranslator.getName(), contentTranslator);
        }

        this.genreId = genreId;
    }

    @Override
    public ContentContainerExt call() {
        ContentContainer contentContainer = ContentContainer.newInstance(genreId);
        SvodMetadata svodMetadata = new SvodMetadata();
        try {
            String url = String.format(ENDPOINT, genreId);
            String jsonResponse = getData(url);

            Log.i(TAG, String.format("Received response: %s", jsonResponse));

            List<Map<String, Object>> cookedJson = PARSER.parseInput(RECIPE, jsonResponse, null);

            for (Map<String, Object> objectMap: cookedJson) {
                Content content = (Content) PARSER.translateMapToModel(RECIPE, new NoOpRecipeCallbacks(), objectMap);

                if (contentTranslator.validateModel(content)) {
                    contentContainer.addContent(content);
                }
            }


            svodMetadata = metadataExtractor.extractAtFirstLevel(jsonResponse);

        } catch (Exception e) {
            Log.e(TAG, String.format("Failed to get concerts for genreid [%s]", ENDPOINT),e);
        }

        return new ContentContainerExt(svodMetadata, contentContainer);
    }
}
