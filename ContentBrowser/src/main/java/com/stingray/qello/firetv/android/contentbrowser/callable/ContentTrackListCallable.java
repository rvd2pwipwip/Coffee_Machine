package com.stingray.qello.firetv.android.contentbrowser.callable;

import android.util.Log;

import com.stingray.qello.firetv.android.async.SvodCallable;
import com.stingray.qello.firetv.android.contentbrowser.ContentContainerExtFactory;
import com.stingray.qello.firetv.android.contentbrowser.recipe.TrackItemRecipe;
import com.stingray.qello.firetv.android.model.content.Content;
import com.stingray.qello.firetv.android.model.content.ContentContainerExt;
import com.stingray.qello.firetv.android.model.content.Track;
import com.stingray.qello.firetv.android.model.translators.TrackTranslator;
import com.stingray.qello.firetv.android.recipe.Recipe;
import com.stingray.qello.firetv.dynamicparser.DynamicParser;

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
                RECIPE = new TrackItemRecipe().getRecipe();
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
            String jsonResponse = get(url);

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
