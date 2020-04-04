package com.stingray.qello.firetv.android.contentbrowser.callable;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.stingray.qello.firetv.android.async.SvodCallable;
import com.stingray.qello.firetv.android.contentbrowser.ContentContainerExtFactory;
import com.stingray.qello.firetv.android.contentbrowser.recipe.ConcertItemRecipe;
import com.stingray.qello.firetv.android.model.content.ContentContainerExt;
import com.stingray.qello.firetv.android.model.translators.ContentTranslator;
import com.stingray.qello.firetv.android.recipe.Recipe;
import com.stingray.qello.firetv.android.utils.PathHelper;
import com.stingray.qello.firetv.dynamicparser.DynamicParser;
import com.stingray.qello.firetv.dynamicparser.IParser;

public class BrowsePageCallable extends SvodCallable<ContentContainerExt> {
    private final static String ENDPOINT = "/v1/browse-pages/%s/sections/%s";
    private final static String NAME_FORMAT = "Browse Page: page [%s] section [%s]";
    private final static String TAG = BrowsePageCallable.class.getSimpleName();
    private final static ContentContainerExtFactory contentContainerExtFactory = new ContentContainerExtFactory();

    private final ContentTranslator contentTranslator = new ContentTranslator();

    private static Recipe RECIPE;
    private static DynamicParser PARSER;

    private boolean initializationFailed = false;
    private String page;
    private String section;

    public BrowsePageCallable(String page, String section) {
        try {
            if (RECIPE == null || PARSER == null) {
                RECIPE = new ConcertItemRecipe().getRecipe();
                PARSER = new DynamicParser();
                // Register content translator in case parser recipes use translation.
                PARSER.addTranslatorImpl(contentTranslator.getName(), contentTranslator);
            }

            this.page = page;
            this.section = section;
        } catch (Exception e) {
            Log.e(TAG, String.format("Failed to initialize [%s]", TAG), e);
            initializationFailed = true;
        }
    }

    @Override
    public ContentContainerExt call() throws PathHelper.MalformedInjectionStringException, DynamicParser.ParserNotFoundException, IParser.InvalidQueryException, IParser.InvalidDataException, JsonProcessingException {
        if (initializationFailed) {
            return new ContentContainerExt();
        }

        String containerName = String.format(NAME_FORMAT, page, section);

        String url = String.format(ENDPOINT, page, section);
        Response response = performGet(url);

        if (response.getCode() != 200) {
            throw new RuntimeException(String.format("Call to browse page with endpoint [%s]. Response [%s]", url, response.getBody()));
        }
        return contentContainerExtFactory.create(containerName, response.getBody(), PARSER, RECIPE, contentTranslator);

    }
}
