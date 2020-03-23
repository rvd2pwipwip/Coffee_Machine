package com.amazon.android.contentbrowser.explorepage;

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

public class GenreFilterCallable extends SvodCallable<ContentContainerExt> {
    private final static String ENDPOINT = "/v1/browse-pages/searchpage/sections/%s";
    private final static String TAG = GenreFilterCallable.class.getSimpleName();
    private final static String NAME_FORMAT = "GenreFilter%s";

    private final ContentTranslator contentTranslator = new ContentTranslator();
    private final ContentContainerExtFactory contentContainerExtFactory = new ContentContainerExtFactory();

    private static Recipe RECIPE;
    private static DynamicParser PARSER;

    private boolean initializationFailed = false;
    private String genreId;

    public GenreFilterCallable(String genreId) {
        try {
        if (RECIPE == null && PARSER == null) {
            RECIPE = new ConcertItemRecipe().getRecipe();
            PARSER = new DynamicParser();
            // Register content translator in case parser recipes use translation.
            PARSER.addTranslatorImpl(contentTranslator.getName(), contentTranslator);
        }

        this.genreId = genreId;

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

        String containerName = String.format(NAME_FORMAT, genreId);
        try {
            String url = String.format(ENDPOINT, genreId);
            String jsonResponse = get(url);

            return contentContainerExtFactory.create(containerName, jsonResponse, PARSER, RECIPE, contentTranslator);
        } catch (Exception e) {
            Log.e(TAG, String.format("Failed to get concerts for genreid [%s]", ENDPOINT),e);
            return new ContentContainerExt(new SvodMetadata(), ContentContainer.newInstance(containerName));
        }
    }
}
