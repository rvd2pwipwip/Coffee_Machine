package com.stingray.qello.firetv.android.contentbrowser.callable;

import android.util.Log;

import com.stingray.qello.firetv.android.async.SvodCallable;
import com.stingray.qello.firetv.android.contentbrowser.ContentContainerExtFactory;
import com.stingray.qello.firetv.android.contentbrowser.recipe.ConcertItemRecipe;
import com.stingray.qello.firetv.android.contentbrowser.recipe.HistoryItemRecipe;
import com.stingray.qello.firetv.android.model.SvodMetadata;
import com.stingray.qello.firetv.android.model.content.ContentContainer;
import com.stingray.qello.firetv.android.model.content.ContentContainerExt;
import com.stingray.qello.firetv.android.model.translators.ContentContainerTranslator;
import com.stingray.qello.firetv.android.model.translators.ContentTranslator;
import com.stingray.qello.firetv.android.recipe.Recipe;
import com.stingray.qello.firetv.dynamicparser.DynamicParser;

import java.util.List;
import java.util.Map;

public class HistoryCallable extends SvodCallable<ContentContainerExt> {
    private final static String ENDPOINT = "/v1/browse-pages/history";
    private final static String TAG = HistoryCallable.class.getSimpleName();

    private final ContentTranslator contentTranslator = new ContentTranslator();
    private final ContentContainerTranslator containerTranslator = new ContentContainerTranslator();
    private final ContentContainerExtFactory contentContainerExtFactory = new ContentContainerExtFactory();

    private static Recipe RECIPE;
    private static DynamicParser PARSER;

    private boolean initializationFailed = false;

    public HistoryCallable() {
        try {
        if (RECIPE == null && PARSER == null) {
            RECIPE = new HistoryItemRecipe().getRecipe();
            PARSER = new DynamicParser();
            // Register content translator in case parser recipes use translation.
            PARSER.addTranslatorImpl(contentTranslator.getName(), contentTranslator);
            PARSER.addTranslatorImpl(containerTranslator.getName(), containerTranslator);
        }


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

        try {
            String url = String.format(ENDPOINT);
            String jsonResponse = get(url);

            return contentContainerExtFactory.create("History", jsonResponse, PARSER, RECIPE, contentTranslator);
        } catch (Exception e) {
            Log.e(TAG, String.format("Failed to view more for [%s]", ENDPOINT),e);
            return new ContentContainerExt(new SvodMetadata(), ContentContainer.newInstance("History"));
        }
    }
}
