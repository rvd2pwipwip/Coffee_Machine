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

public class RelatedContentCallable extends SvodCallable<ContentContainerExt> {
    private final static String ENDPOINT = "/v1/content-pages/%s/sections/related_content?limit=%s&offset=0";
    private final static String NAME_FORMAT = "Related Content %s";
    private final static String TAG = RelatedContentCallable.class.getSimpleName();
    private final static ContentContainerExtFactory contentContainerExtFactory = new ContentContainerExtFactory();

    private final ContentTranslator contentTranslator = new ContentTranslator();

    private static Recipe RECIPE;
    private static DynamicParser PARSER;

    private boolean initializationFailed = false;
    private String contentId;
    private int limit;

    public RelatedContentCallable(String contentId, int limit) {
        try {
            if (RECIPE == null || PARSER == null) {
                RECIPE = new ConcertItemRecipe().getRecipe();
                PARSER = new DynamicParser();
                // Register content translator in case parser recipes use translation.
                PARSER.addTranslatorImpl(contentTranslator.getName(), contentTranslator);
            }

            this.contentId = contentId;
            this.limit = limit;
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
            String url = String.format(ENDPOINT, contentId, limit);
            String jsonResponse = get(url);

            return contentContainerExtFactory.create(containerName, jsonResponse, PARSER, RECIPE, contentTranslator);

        } catch (Exception e) {
            Log.e(TAG, String.format("Failed to get related content from [%s]", containerName),e);
            return new ContentContainerExt(new SvodMetadata(), ContentContainer.newInstance(containerName));
        }
    }
}
