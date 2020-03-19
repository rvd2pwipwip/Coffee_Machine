package com.amazon.android.contentbrowser.search;

import android.util.Log;

import com.amazon.android.async.SvodCallable;
import com.amazon.android.contentbrowser.ContentContainerExtFactory;
import com.amazon.android.contentbrowser.recipe.ConcertItemRecipe;
import com.amazon.android.model.SvodMetadata;
import com.amazon.android.model.content.ContentContainer;
import com.amazon.android.model.content.ContentContainerExt;
import com.amazon.android.model.translators.ContentTranslator;
import com.amazon.android.recipe.Recipe;
import com.amazon.dynamicparser.DynamicParser;

public class SearchCallable extends SvodCallable<ContentContainerExt> {
    private final static String ENDPOINT = "/v1/content-search?text=%s";
    private final static String NAME_FORMAT = "SearchResults%s";
    private final static String TAG = SearchCallable.class.getSimpleName();
    private final static ContentContainerExtFactory contentContainerExtFactory = new ContentContainerExtFactory();

    private final ContentTranslator contentTranslator = new ContentTranslator();

    private static Recipe RECIPE;
    private static DynamicParser PARSER;

    private boolean initializationFailed = false;
    private String query;

    public SearchCallable(String query) {
        try {
            if (RECIPE == null || PARSER == null) {
                RECIPE = new ConcertItemRecipe().getRecipe();
                PARSER = new DynamicParser();
                // Register content translator in case parser recipes use translation.
                PARSER.addTranslatorImpl(contentTranslator.getName(), contentTranslator);
            }

            this.query = query;
        } catch (Exception e) {
            Log.e(TAG, String.format("Failed to initialize [%s]", TAG), e);
            initializationFailed = true;
        }
    }

    @Override
    public ContentContainerExt call() {
        if (initializationFailed) {
            return new ContentContainerExt();
        }

        String containerName = String.format(NAME_FORMAT, query);
        try {
            String url = String.format(ENDPOINT, query);
            String jsonResponse = getData(url);

            return contentContainerExtFactory.create(containerName, jsonResponse, PARSER, RECIPE, contentTranslator);

        } catch (Exception e) {
            Log.e(TAG, String.format("Failed to get search results from [%s]", ENDPOINT), e);
            return new ContentContainerExt(new SvodMetadata(), ContentContainer.newInstance(containerName));
        }
    }
}
