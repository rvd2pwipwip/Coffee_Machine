package com.stingray.qello.firetv.android.tv.tenfoot.utils;

import com.stingray.qello.firetv.android.contentbrowser.ContentBrowser;
import com.stingray.qello.firetv.android.model.content.Content;
import com.stingray.qello.firetv.android.model.content.ContentContainer;
import com.stingray.qello.firetv.android.model.content.ViewMore;
import com.stingray.qello.firetv.android.tv.tenfoot.R;
import com.stingray.qello.firetv.android.tv.tenfoot.presenter.CardPresenter;
import com.stingray.qello.firetv.android.ui.constants.PreferencesConstants;
import com.stingray.qello.firetv.android.utils.Preferences;
import com.stingray.qello.firetv.utils.DateAndTimeHelper;

import android.app.Activity;
import android.content.Context;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.util.Log;

import java.util.List;

/**
 * This class contains common functionality between different browse fragment layouts.
 */
public class BrowseHelper {

    private static String TAG = BrowseHelper.class.getSimpleName();

    /**
     * Saves the browse activity state to be reloaded later, if necessary.
     *
     * @param activity The browse activity.
     */
    public static void saveBrowseActivityState(Activity activity) {

        // Update the last activity preference value if an activity restore is not currently in
        // progress.
        if (activity.getIntent() == null ||
                !activity.getIntent().getBooleanExtra(ContentBrowser.RESTORE_ACTIVITY, false)) {

            Preferences.setString(PreferencesConstants.LAST_ACTIVITY,
                                  ContentBrowser.CONTENT_HOME_SCREEN);
            Preferences.setLong(PreferencesConstants.TIME_LAST_SAVED,
                                DateAndTimeHelper.getCurrentDate().getTime());
        }
    }

    /**
     * Loads the content from the root content container into the rows adapter.
     *
     * @param activity    The activity.
     * @param rowsAdapter The rows adapter.
     */
    public static void loadRootContentContainer(Activity activity, ArrayObjectAdapter rowsAdapter) {

        ContentContainer rootContentContainer = ContentBrowser.getInstance(activity)
                                                              .getRootContentContainer();

        CardPresenter cardPresenter = new CardPresenter();

        for (ContentContainer contentContainer : rootContentContainer.getContentContainers()) {

            HeaderItem header = new HeaderItem(0, contentContainer.getName());
            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);

            for (ContentContainer innerContentContainer : contentContainer.getContentContainers()) {
                listRowAdapter.add(innerContentContainer);
            }

            for (Content content : contentContainer.getContents()) {
                listRowAdapter.add(content);
                //TODO remove after testing
                listRowAdapter.add(content);
                //TODO remove after testing
                listRowAdapter.add(content);
            }

            //TODO REMOVE after testing
            if(contentContainer.hasViewMore() || contentContainer.getName().equals("Top Picks For Dad")) {
                listRowAdapter.add(new ViewMore(contentContainer.getId(), contentContainer.getName()));
            }

            rowsAdapter.add(new ListRow(header, listRowAdapter));
        }
    }

    /**
     * Updates the recent row in the rows adapter. To be displayed before the settings row.
     *
     * @param activity      The activity.
     * @param recentListRow The recent list row.
     * @param rowsAdapter   The browse fragment rows adapter.
     * @return The updated list row.
     */
    public static ListRow updateContinueWatchingRow(Activity activity, ListRow recentListRow,
                                                    ArrayObjectAdapter rowsAdapter) {

        if (ContentBrowser.getInstance(activity).getContentLoader().isContentLoaded()) {

            List<Content> recentContent = ContentBrowser.getInstance(activity)
                                                        .getRecentContent();

            int maxItems = ContentBrowser.getInstance(activity).getMaxNumberOfRecentItems();

            return updateListRow(activity.getApplicationContext(), rowsAdapter, recentListRow,
                                 recentContent, rowsAdapter.size() - 1, R.string.recent_row,
                                 maxItems);

        }
        return null;
    }

    /**
     * Updates the watchlist row in the rows adapter. To be displayed before the settings row and
     * the recent row (if present).
     *
     * @param activity         The activity.
     * @param watchlistListRow The watchlist list row.
     * @param recentListRow    The recent list row.
     * @param rowsAdapter      The rows adapter of the browse fragment.
     * @return The updated watchlist list row.
     */
    public static ListRow updateWatchlistRow(Activity activity, ListRow watchlistListRow,
                                             ListRow recentListRow, ArrayObjectAdapter
                                                     rowsAdapter) {

        if (ContentBrowser.getInstance(activity).getContentLoader().isContentLoaded()) {

            List<Content> watchlistItems = ContentBrowser.getInstance(activity)
                                                         .getWatchlistContent();

            // This row should be inserted before the settings row and the recent row if present.
            int rowIndex = recentListRow == null ? rowsAdapter.size() - 1 : rowsAdapter.size() - 2;
            Log.d(TAG, "Inserting watchlist row at index " + rowIndex);
            return updateListRow(activity.getApplicationContext(), rowsAdapter, watchlistListRow,
                                 watchlistItems, rowIndex, R.string.watchlist_row,
                                 watchlistItems.size());
        }
        return null;
    }

    /**
     * Updates the given row of the rows adapter.
     *
     * @param context        The context.
     * @param rowsAdapter    The rows adapter of the browse fragment.
     * @param listRow        The list row to update.
     * @param content        The content for the row.
     * @param rowIndex       The index in which the list row should be added into the rows adapter.
     * @param headerStringId The string resource id for the row's header.
     * @param maxItems       The maximum number of content to put in the row.
     * @return The updated list row.
     */
    private static ListRow updateListRow(Context context, ArrayObjectAdapter rowsAdapter,
                                         ListRow listRow, List<Content> content, int rowIndex,
                                         int headerStringId, int maxItems) {

        if (listRow != null) {
            rowsAdapter.remove(listRow);
            rowIndex--;
        }
        // Create the new row.
        listRow = createListRow(context, content, headerStringId, maxItems);
        if (listRow != null) {
            rowsAdapter.add(rowIndex, listRow);
        }
        return listRow;
    }

    /**
     * Creates a list row of content with the given header string.
     *
     * @param context        The context.
     * @param contents       The contents for the list row.
     * @param headerStringId The string resource id for the row's header.
     * @param maxItems       The maximum number of content to put in the row.
     * @return A list row.
     */
    private static ListRow createListRow(Context context, List<Content> contents,
                                         int headerStringId, int maxItems) {

        // Only create the row if the content list is not empty.
        if (!contents.isEmpty()) {
            CardPresenter cardPresenter = new CardPresenter();
            HeaderItem header = new HeaderItem(0, context.getResources().getString(headerStringId));

            ArrayObjectAdapter recentRowAdapter = new ArrayObjectAdapter(cardPresenter);

            if (contents.size() == maxItems) {
                recentRowAdapter.addAll(0, contents);
            }
            else {
                for (int i = 0; i < contents.size() && i < maxItems; i++) {
                    recentRowAdapter.add(contents.get(i));
                }
            }
            return new ListRow(header, recentRowAdapter);
        }
        return null;
    }
}
