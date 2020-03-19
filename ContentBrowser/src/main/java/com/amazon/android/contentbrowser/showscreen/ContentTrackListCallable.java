package com.amazon.android.contentbrowser.showscreen;

import android.util.Log;

import com.amazon.android.async.SvodCallable;
import com.amazon.android.contentbrowser.ContentContainerExtFactory;
import com.amazon.android.contentbrowser.recipe.TrackSectionRecipe;
import com.amazon.android.model.SvodMetadata;
import com.amazon.android.model.content.Content;
import com.amazon.android.model.content.ContentContainer;
import com.amazon.android.model.content.ContentContainerExt;
import com.amazon.android.model.content.Track;
import com.amazon.android.model.translators.TrackTranslator;
import com.amazon.android.recipe.Recipe;
import com.amazon.dynamicparser.DynamicParser;

import java.util.ArrayList;
import java.util.List;

public class ContentTrackListCallable extends SvodCallable<List<Track>> {
    private final static String ENDPOINT = "/v1/content-pages/%s/sections/content_track_list";
    private final static String NAME_FORMAT = "Content Track List %s";
    private final static String TAG = ContentTrackListCallable.class.getSimpleName();
    private final static ContentContainerExtFactory contentContainerExtFactory = new ContentContainerExtFactory();

    private final TrackTranslator trackTranslator = new TrackTranslator();

    private static Recipe RECIPE;
    private static DynamicParser PARSER;

    private boolean initializationFailed = false;
    private String contentId;

    public ContentTrackListCallable(String contentId) {
        try {
            if (RECIPE == null || PARSER == null) {
                RECIPE = new TrackSectionRecipe().getRecipe();
                PARSER = new DynamicParser();
                // Register content translator in case parser recipes use translation.
                PARSER.addTranslatorImpl(trackTranslator.getName(), trackTranslator);
            }

            this.contentId = contentId;
        } catch (Exception e) {
            Log.e(TAG, String.format("Failed to initialize [%s]", TAG), e);
            initializationFailed = true;
        }
    }

    @Override
    public List<Track> call() {
        if (initializationFailed) {
            return new ArrayList<>();
        }

        String containerName = String.format(NAME_FORMAT, contentId);
        try {
            String url = String.format(ENDPOINT, contentId);
            String jsonResponse = getData(url);

            ContentContainerExt contentContainerExt = contentContainerExtFactory.create(containerName, jsonResponse, PARSER, RECIPE, trackTranslator);

            List<Track> tracks = new ArrayList<>();

            for (Content content: contentContainerExt.getContentContainer()) {
                tracks.add((Track) content);
            }

            return tracks;

        } catch (Exception e) {
            Log.e(TAG, String.format("Failed to get related content from [%s]", containerName),e);
            return new ArrayList<>();
        }
    }
}
