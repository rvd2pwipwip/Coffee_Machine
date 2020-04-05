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
            //"{\"id\": \"HISTORY\",\"data_type\": \"ITEM_LIST\",\"data_count\": 1,\"metadata\": {\t\"background_image_link\": \"https://d1w32pzqgdzaqx.cloudfront.net/qello_resource/my_history/page_background_mobile/375x667/jpg?type=single\"},\"data\": [\t{\t\t\"id\": \"HISTORY\",\t\t\"data_type\": \"ITEM_LIST\",\t\t\"data_count\": 1,\t\t\"metadata\": {\t\t\t\"display_name\": \"History\",\t\t\t\"show_if_empty\": true,\t\t\t\"has_view_more\": false,\t\t\t\"refreshable\": true,\t\t\t\"preset\": \"Grid\",\t\t\t\"background_image_link\": \"https://d1w32pzqgdzaqx.cloudfront.net/qello_resource/my_history/page_background_mobile/375x667/jpg?type=single\"\t\t},\t\t\"links\": {\t\t\t\"self\": \"null://browse-pages/history/sections/history\",\t\t\t\"next\": \"null://browse-pages/history/sections/history?limit=50&offset=50\"\t\t},\t\t\"data\": [\t\t\t{\t\t\t\t\"id\": \"2154855\",\t\t\t\t\"data_type\": \"VIDEO\",\t\t\t\t\"links\": {\t\t\t\t\t\"goto\": \"null://content-pages/2154855\"\t\t\t\t},\t\t\t\t\"data\": {\t\t\t\t\t\"title\": \"Live at Austin City Limits\",\t\t\t\t\t\"artists\": \"Jason Isbell\",\t\t\t\t\t\"subtitle\": \"Jason Isbell\",\t\t\t\t\t\"short_description\": \"On August 19, 2013, Jason Isbell and his band The 400 Unit, stepped out onto the stage in front of a live audience in Austin, TX to film his first appearance on the longest running original music series in the U.S., Austin City Limits.\",\t\t\t\t\t\"category\": \"Country\",\t\t\t\t\t\"duration\": 5331198,\t\t\t\t\t\"full_description\": \"Nashville, TN – On August 19, 2013, Jason Isbell and his band The 400 Unit, stepped out onto the stage in front of a live audience in Austin, TX to film his first appearance on the longest running original music series in the U.S., Austin City Limits.\\nIsbell performs songs from his critically acclaimed, award-winning album Southeastern along with songs from throughout his career, including fan favorites “Outfit” and “Decoration Day”. The set closed with a rousing rendition of the Rolling Stones’ “Can’t You Hear Me Knocking”, a live staple. \\nIn September, Isbell won Artist of the Year, Song of the Year (“Cover Me Up”) and Album of the Year for Southeastern. In October, Isbell reached a major milestone by selling out three consecutive nights at Nashville’s historic Ryman Auditorium.\",\t\t\t\t\t\"has_preview\": true,\t\t\t\t\t\"images\": [\t\t\t\t\t\t{\t\t\t\t\t\t\t\"type\": \"THUMBNAIL_PORTRAIT\",\t\t\t\t\t\t\t\"url\": \"https://d1w32pzqgdzaqx.cloudfront.net/qello/2154855/thumbnail_portrait/330x436/jpg?country_code=ca&type=single&image_id=89119350\"\t\t\t\t\t\t},\t\t\t\t\t\t{\t\t\t\t\t\t\t\"type\": \"SHOWSCREEN\",\t\t\t\t\t\t\t\"url\": \"https://d1w32pzqgdzaqx.cloudfront.net/qello/2154855/show_screen_phone/375x295/jpg?country_code=ca&type=single&image_id=92760087\"}],\"play_position\": 0}}]}]}";//
            return contentContainerExtFactory.create("History", jsonResponse, PARSER, RECIPE, contentTranslator);
        } catch (Exception e) {
            Log.e(TAG, String.format("Failed to view more for [%s]", ENDPOINT),e);
            return new ContentContainerExt(new SvodMetadata(), ContentContainer.newInstance("History"));
        }
    }
}
