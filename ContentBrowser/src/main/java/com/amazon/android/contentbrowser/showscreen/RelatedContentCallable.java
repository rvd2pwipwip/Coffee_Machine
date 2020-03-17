package com.amazon.android.contentbrowser.showscreen;

import android.util.Log;

import com.amazon.android.async.SvodCallable;
import com.amazon.android.contentbrowser.ContentContainerExtFactory;
import com.amazon.android.contentbrowser.recipe.ConcertSectionRecipe;
import com.amazon.android.model.SvodMetadata;
import com.amazon.android.model.content.ContentContainer;
import com.amazon.android.model.content.ContentContainerExt;
import com.amazon.android.model.translators.ContentTranslator;
import com.amazon.android.recipe.Recipe;
import com.amazon.dynamicparser.DynamicParser;

public class RelatedContentCallable extends SvodCallable<ContentContainerExt> {
    private final static String ENDPOINT = "/v1/content-pages/%s/sections/related_content?limit=6&offset=0";
    private final static String NAME_FORMAT = "Related Content %s";
    private final static String TAG = RelatedContentCallable.class.getSimpleName();
    private final static ContentContainerExtFactory contentContainerExtFactory = new ContentContainerExtFactory();

    private final ContentTranslator contentTranslator = new ContentTranslator();

    private static Recipe RECIPE;
    private static DynamicParser PARSER;

    private boolean initializationFailed = false;
    private String contentId;

    public RelatedContentCallable(String contentId) {
        try {
            if (RECIPE == null || PARSER == null) {
                RECIPE = new ConcertSectionRecipe().getRecipe();
                PARSER = new DynamicParser();
                // Register content translator in case parser recipes use translation.
                PARSER.addTranslatorImpl(contentTranslator.getName(), contentTranslator);
            }

            this.contentId = contentId;
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

        String containerName = String.format(NAME_FORMAT, contentId);
        try {
            String url = String.format(ENDPOINT, contentId);
            String jsonResponse = getData(url);

            return contentContainerExtFactory.create(containerName, jsonResponse, PARSER, RECIPE, contentTranslator);

        } catch (Exception e) {
            Log.e(TAG, String.format("Failed to get related content from [%s]", containerName),e);
            return new ContentContainerExt(new SvodMetadata(), ContentContainer.newInstance(containerName));
        }
    }
}
