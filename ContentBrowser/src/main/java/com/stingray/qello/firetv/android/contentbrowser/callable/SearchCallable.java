package com.stingray.qello.firetv.android.contentbrowser.callable;

import android.util.Log;

import com.stingray.qello.firetv.android.async.SvodCallable;
import com.stingray.qello.firetv.android.contentbrowser.ContentContainerExtFactory;
import com.stingray.qello.firetv.android.contentbrowser.recipe.ConcertItemRecipe;
import com.stingray.qello.firetv.android.model.SvodMetadata;
import com.stingray.qello.firetv.android.model.content.ContentContainer;
import com.stingray.qello.firetv.android.model.content.ContentContainerExt;
import com.stingray.qello.firetv.android.model.translators.ContentTranslator;
import com.stingray.qello.firetv.android.recipe.Recipe;
import com.stingray.qello.firetv.dynamicparser.DynamicParser;

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
            String jsonResponse = get(url);

            return contentContainerExtFactory.create(containerName, jsonResponse, PARSER, RECIPE, contentTranslator);

        } catch (Exception e) {
            Log.e(TAG, String.format("Failed to get search results from [%s]", ENDPOINT), e);
            return new ContentContainerExt(new SvodMetadata(), ContentContainer.newInstance(containerName));
        }
    }
}
