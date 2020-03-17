package com.amazon.android.contentbrowser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.widget.SparseArrayObjectAdapter;
import android.util.Log;

import com.amazon.android.async.AsyncCaller;
import com.amazon.android.contentbrowser.database.helpers.RecentDatabaseHelper;
import com.amazon.android.contentbrowser.database.helpers.WatchlistDatabaseHelper;
import com.amazon.android.contentbrowser.database.records.RecentRecord;
import com.amazon.android.contentbrowser.helper.AnalyticsHelper;
import com.amazon.android.contentbrowser.helper.AuthHelper;
import com.amazon.android.contentbrowser.helper.ErrorHelper;
import com.amazon.android.contentbrowser.helper.FontManager;
import com.amazon.android.contentbrowser.helper.LauncherIntegrationManager;
import com.amazon.android.contentbrowser.helper.PurchaseHelper;
import com.amazon.android.contentbrowser.recommendations.RecommendationManager;
import com.amazon.android.contentbrowser.search.SearchCallable;
import com.amazon.android.interfaces.ICancellableLoad;
import com.amazon.android.interfaces.IContentBrowser;
import com.amazon.android.model.Action;
import com.amazon.android.model.content.Content;
import com.amazon.android.model.content.ContentContainer;
import com.amazon.android.model.content.ContentContainerExt;
import com.amazon.android.model.content.constants.PreferencesConstants;
import com.amazon.android.model.event.ActionUpdateEvent;
import com.amazon.android.module.ModularApplication;
import com.amazon.android.navigator.Navigator;
import com.amazon.android.navigator.UINode;
import com.amazon.android.recipe.Recipe;
import com.amazon.android.search.ISearchAlgo;
import com.amazon.android.search.ISearchResult;
import com.amazon.android.search.SearchManager;
import com.amazon.android.ui.fragments.AlertDialogFragment;
import com.amazon.android.ui.fragments.ContactUsSettingsFragment;
import com.amazon.android.ui.fragments.FAQSettingsFragment;
import com.amazon.android.ui.fragments.LogoutSettingsFragment;
import com.amazon.android.utils.ErrorUtils;
import com.amazon.android.utils.LeanbackHelpers;
import com.amazon.android.utils.Preferences;
import com.amazon.utils.DateAndTimeHelper;
import com.amazon.utils.StringManipulation;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.amazon.android.contentbrowser.helper.LauncherIntegrationManager.getSourceOfContentPlayRequest;

/**
 * This class is the controller of the content browsing solution.
 */
public class ContentBrowser implements IContentBrowser, ICancellableLoad {

    private static final String TAG = ContentBrowser.class.getSimpleName();

    private static final boolean DEBUG_RECIPE_CHAIN = false;
    private static final String REQUEST_FROM_LAUNCHER = "REQUEST_FROM_LAUNCHER";
    public static final String CONTENT_WILL_UPDATE = "CONTENT_WILL_UPDATE";
    private static final String CONTENT_SPLASH_SCREEN = "CONTENT_SPLASH_SCREEN";
    public static final String CONTENT_HOME_SCREEN = "CONTENT_HOME_SCREEN";
    public static final String CONTENT_DETAILS_SCREEN = "CONTENT_DETAILS_SCREEN";
    public static final String CONTENT_SUBMENU_SCREEN = "CONTENT_SUBMENU_SCREEN";
    public static final String CONTENT_RENDERER_SCREEN = "CONTENT_RENDERER_SCREEN";
    public static final String ACCOUNT_CREATION_SCREEN = "ACCOUNT_CREATION_SCREEN";

    private static final String FREE_CONTENT = "free";
    private static final String SEARCH = "Search";
    private static final String HOME = "Home";
    private static final String MY_QELLO = "MyQello";

    public static final int CONTENT_ACTION_WATCH_NOW = 1;
    public static final int CONTENT_ACTION_WATCH_FROM_BEGINNING = 2;
    public static final int CONTENT_ACTION_RESUME = 3;
    public static final int CONTENT_ACTION_SUBSCRIPTION = 5;
    public static final int CONTENT_ACTION_DAILY_PASS = 6;
    public static final int CONTENT_ACTION_SEARCH = 7;
    public static final int CONTENT_ACTION_HOME = 8;
    public static final int CONTENT_ACTION_MY_QELLO = 9;
    public static final int CONTENT_ACTION_LOGIN_LOGOUT = 10;
    private static final int CONTENT_ACTION_ADD_WATCHLIST = 11;
    private static final int CONTENT_ACTION_REMOVE_WATCHLIST = 12;
    public static final int CONTENT_ACTION_ADD_TO_FAVORITES = 13;
    public static final int CONTENT_ACTION_REMOVE_FROM_FAVORITES = 14;

    private static final String DEFAULT_SEARCH_ALGO_NAME = "basic";
    public static final String RESTORE_ACTIVITY = "restore_last_activity";

    public static final long GRACE_TIME_MS = 5000; // 5 seconds.

    private final Context mAppContext;
    private static ContentBrowser sInstance;
    private static final Object sLock = new Object();
    private final EventBus mEventBus = EventBus.getDefault();
    private final SearchManager<ContentContainer, Content> mSearchManager = new SearchManager<>();
    private ICustomSearchHandler mICustomSearchHandler;
    private IRootContentContainerListener mIRootContentContainerListener;
    private Content mLastSelectedContent;
    private final Navigator mNavigator;
    private final List<Action> mWidgetActionsList = new ArrayList<>();
    private final List<Action> mGlobalContentActionList = new ArrayList<>();
    private final Map<Integer, List<IContentActionListener>> mContentActionListeners = new HashMap<>();
    private final Map<String, String> mPoweredByLogoUrlMap = new HashMap<>();
    private AuthHelper mAuthHelper;
    private PurchaseHelper mPurchaseHelper;
    private LauncherIntegrationManager mLauncherIntegrationManager;
    private boolean mSubscribed;
    private boolean mIAPDisabled;

    /**
     * When set to true, this flag will override the subscription flag for all the content.
     */
    private boolean mOverrideAllContentsSubscriptionFlag = false;

    /**
     * Composite subscription instance; single use only!!!.
     */
    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    /**
     * boolean to read launcher integration status.
     */
    private final boolean mLauncherIntegrationEnabled;

    /**
     * loginLogout action, content browser needs to keep the state updated of this action.
     */
    private Action mLoginAction;

    /**
     * Boolean indicating if {@link #onAllModulesLoaded()} has been called.
     */
    private boolean mModulesLoaded = false;

    /**
     * Content loader instance.
     */
    private ContentLoader mContentLoader;

    /**
     * Recommendation manager instance.
     */
    private RecommendationManager mRecommendationManager;
    
    /**
     * Returns AuthHelper instance.
     *
     * @return authHelper instance
     */
    public AuthHelper getAuthHelper() {

        return mAuthHelper;
    }

    /**
     * Returns if the loading request is cancelled or not.
     * For this class it will never be cancelled.
     *
     * @return True if loading is cancelled
     */
    public boolean isLoadingCancelled() {

        return false;
    }

    /**
     * Get a list of content that belong in the watchlist.
     *
     * @return List of content.
     */
    public List<Content> getWatchlistContent() {

        List<Content> contentList = new ArrayList<>();
        
        WatchlistDatabaseHelper databaseHelper = WatchlistDatabaseHelper.getInstance();
        if (databaseHelper != null) {


            List<String> contendIds = databaseHelper.getWatchlistContentIds(mAppContext);

            for (String contentId : contendIds) {

                Content content = mContentLoader.getRootContentContainer()
                                                .findContentById(contentId);
                if (content != null) {
                    contentList.add(content);
                }
                // The content is no longer valid so remove from database.
                else {
                    Log.d(TAG, "Content no longer valid");
                    databaseHelper.deleteRecord(mAppContext, contentId);
                }
            }
        }
        return contentList;
    }

    /**
     * Content action listener interface.
     */
    public interface IContentActionListener {

        /**
         * Called when an action happens on a content.
         *
         * @param activity Activity.
         * @param content  Content.
         * @param actionId Action id.
         */
        void onContentAction(Activity activity, Content content, int actionId);

        /**
         * Called when an action is completed.
         *
         * @param activity Activity.
         * @param content  Content.
         * @param actionId Action id.
         */
        void onContentActionCompleted(Activity activity, Content content, int actionId);
    }

    /**
     * Custom search handler interface.
     */
    public interface ICustomSearchHandler {

        /**
         * On search requested callback.
         *
         * @param query         Query string.
         * @param iSearchResult Search result listener.
         */
        void onSearchRequested(String query, ISearchResult iSearchResult);
    }

    /**
     * Root content container listener.
     */
    public interface IRootContentContainerListener {

        /**
         * Root content container populated callback.
         *
         * @param contentContainer Root content container reference.
         */
        void onRootContentContainerPopulated(ContentContainer contentContainer);
    }

    /**
     * Screen switch listener interface.
     */
    public interface IScreenSwitchListener {

        /**
         * On screen switch callback.
         *
         * @param extra Extra bundle.
         */
        void onScreenSwitch(Bundle extra);
    }

    /**
     * Screen switch Error listener interface.
     */
    public interface IScreenSwitchErrorHandler {

        /**
         * Authentication error callback.
         *
         * @param iScreenSwitchListener Screen switch listener interface implementation.
         */
        void onErrorHandler(IScreenSwitchListener iScreenSwitchListener);
    }

    /**
     * Constructor.
     *
     * @param activity The activity that is active when ContentBrowser is created.
     */
    private ContentBrowser(Activity activity) {

        mAppContext = activity.getApplicationContext();
        mNavigator = new Navigator(activity);
        mSubscribed = Preferences.getBoolean(PurchaseHelper.CONFIG_PURCHASE_VERIFIED);

        mContentLoader = ContentLoader.getInstance(mAppContext);

        mIAPDisabled = mAppContext.getResources().getBoolean(R.bool.is_iap_disabled);

        mLauncherIntegrationEnabled =
                mAppContext.getResources().getBoolean(R.bool.is_launcher_integration_enabled);

        mOverrideAllContentsSubscriptionFlag =
                mAppContext.getResources()
                           .getBoolean(R.bool.override_all_contents_subscription_flag);

        addWidgetsAction(createHomeAction());
        addWidgetsAction(createSearchAction());
        addWidgetsAction(createMyQelloAction());
        
        mSearchManager.addSearchAlgo(DEFAULT_SEARCH_ALGO_NAME, (ISearchAlgo<Content>) (query, content) -> content.searchInFields(query, new String[]{
                Content.TITLE_FIELD_NAME,
                Content.DESCRIPTION_FIELD_NAME
        }));

        mNavigator.setINavigationListener(new Navigator.INavigationListener() {

            @Override
            public void onSetTheme(Activity activity) {

            }

            @Override
            public void onScreenCreate(Activity activity, String screenName) {

                Log.d(TAG, " onScreenCreate for screen " + screenName + " activity " + activity +
                        " intent " + (activity != null ? activity.getIntent() : null));

                if (!mContentLoader.isContentLoaded() &&
                        (screenName == null || !screenName.equals(CONTENT_SPLASH_SCREEN))) {
                    Log.e(TAG, "Immature app, switching to splash");
                    initFromImmatureApp(activity);
                }
            }

            @Override
            public void onScreenGotFocus(Activity activity, String screenName) {

                Log.d(TAG, "onScreenGotFocus for screen " + screenName + " activity " + activity +
                        " intent " + (activity != null ? activity.getIntent() : null));

                if (screenName.equals(CONTENT_HOME_SCREEN)) {
                    if (mContentLoader.isContentReloadRequired() ||
                            !mContentLoader.isContentLoaded()) {
                        Log.d(TAG, "Are modules loaded? " + mModulesLoaded);
                        if (!mModulesLoaded) {
                            initFromImmatureApp(activity);
                        }
                        else {
                            reloadFeed(activity);
                        }

                    }
                    else if (activity != null &&
                            activity.getIntent().hasExtra(REQUEST_FROM_LAUNCHER) &&
                            activity.getIntent().getBooleanExtra(REQUEST_FROM_LAUNCHER, false)) {

                        activity.getIntent().putExtra(REQUEST_FROM_LAUNCHER, false);
                        switchToRendererScreen(activity.getIntent());
                    }
                    // If we're loading from after an app launch, try to restore the state.
                    else if (shouldRestoreLastActivity(activity)) {
                        activity.getIntent().putExtra(RESTORE_ACTIVITY, false);
                        restoreActivityState(screenName);
                    }
                }
                else if (screenName.equals(CONTENT_SPLASH_SCREEN)) {
                    Log.d(TAG, "runGlobalRecipes due to CONTENT_SPLASH_SCREEN focus");
                }
            }

            @Override
            public void onScreenLostFocus(Activity activity, String screenName) {

                Log.d(TAG, "onScreenLostFocus:" + screenName);
                if (mAuthHelper != null) {
                    mAuthHelper.cancelAllRequests();
                }
            }

            @Override
            public void onApplicationGoesToBackground() {

                Log.d(TAG, "onApplicationGoesToBackground:");
                if (mCompositeSubscription.hasSubscriptions()) {
                    Log.d(TAG, "mCompositeSubscription.unsubscribe");
                    mCompositeSubscription.unsubscribe();
                    // CompositeSubscription is a single use, create a new one for next round.
                    mCompositeSubscription = null;
                    mCompositeSubscription = new CompositeSubscription();
                }
                else {
                    Log.d(TAG, "onApplicationGoesToBackground has no subscriptions!!!");
                }
            }
        });
    }

    /**
     * Restores the last active activity that was saved before the app went to the background or
     * was closed.
     *
     * @param screenName The screen name of the activity to resume.
     */
    private void restoreActivityState(String screenName) {

        String lastActivity = Preferences.getString(com.amazon.android.ui.constants
                                                            .PreferencesConstants.LAST_ACTIVITY);
        String lastContent = Preferences.getString(PreferencesConstants.CONTENT_ID);

        Content content = mContentLoader.getRootContentContainer().findContentById(lastContent);
        Log.d(TAG, "Restoring to last activity: " + lastActivity + " with content: " + lastContent);
        // Switch the last activity if its not the current one.
        if (!StringManipulation.isNullOrEmpty(lastActivity) &&
                !lastActivity.equals(screenName) && content != null) {

            setLastSelectedContent(content);
            switchToScreen(lastActivity, content);
        }
    }

    /**
     * Listener method to listen for authentication updates, it sets the status of
     * loginLogoutAction action used by the browse activities
     *
     * @param authenticationStatusUpdateEvent Event for update in authentication status.
     */
    public void onAuthenticationStatusUpdateEvent(AuthHelper.AuthenticationStatusUpdateEvent
                                                          authenticationStatusUpdateEvent) {

        if (mLoginAction != null) {
            mLoginAction.setState(authenticationStatusUpdateEvent.isUserAuthenticated() ?
                                          LogoutSettingsFragment.TYPE_LOGOUT :
                                          LogoutSettingsFragment.TYPE_LOGIN);
        }

    }

    /**
     * Get instance, singleton method.
     *
     * @param activity The activity.
     * @return Content browser singleton instance.
     */
    public static ContentBrowser getInstance(Activity activity) {

        synchronized (sLock) {
            if (sInstance == null) {
                sInstance = new ContentBrowser(activity);
            }
            return sInstance;
        }
    }

    /**
     * Gets called after all modules are loaded.
     */
    public void onAllModulesLoaded() {

        FontManager.configureFonts(mAppContext, this);

        mAuthHelper = new AuthHelper(mAppContext, this);
        mAuthHelper.setupMvpdList();

        // Need to force the auth activity to be removed from the mRxLauncher object.
        // This handles the case of resuming app launch after home button press from
        // second-screen auth activity.
        mAuthHelper.handleOnActivityResult(AuthHelper.AUTH_ON_ACTIVITY_RESULT_REQUEST_CODE, 0,
                                           null);

        mPurchaseHelper = new PurchaseHelper(mAppContext, this);
        // Launcher integration requires the content authorization system initialized before this
        // manager is initialized. Otherwise it will incorrectly set that authorization is not
        // required for content playing. Hence initializing this after initializing AuthHelper
        // and PurchaseHelper.
        if (mLauncherIntegrationEnabled) {
            mLauncherIntegrationManager = new LauncherIntegrationManager(mAppContext, this);
        }

        mRecommendationManager = new RecommendationManager(mAppContext);
        // First reading of the database upon app launch. Created off of main thread.
        mRecommendationManager.cleanDatabase();
        
        // The app successfully loaded its modules so clear out the crash number.
        Preferences.setLong(ModularApplication.APP_CRASHES_KEY, 0);

        mModulesLoaded = true;
    }

    /**
     * Get navigator.
     *
     * @return Navigator.
     */
    public Navigator getNavigator() {

        return mNavigator;
    }

    /**
     * Gets the content loader instance.
     *
     * @return The content loader.
     */
    public ContentLoader getContentLoader() {

        return mContentLoader;
    }


    /**
     * Get root content container.
     *
     * @return Root content container.
     */
    public ContentContainer getRootContentContainer() {

        return mContentLoader.getRootContentContainer();
    }

    /**
     * Set last selected content.
     *
     * @param content Content.
     * @return Content browser instance.
     */
    public ContentBrowser setLastSelectedContent(Content content) {

        mLastSelectedContent = content;
        Preferences.setString(PreferencesConstants.CONTENT_ID, content.getId());
        return this;
    }

    /**
     * Get last selected content.
     *
     * @return Last selected content.
     */
    public Content getLastSelectedContent() {

        return mLastSelectedContent;
    }

    /**
     * Set last selected content container.
     *
     * @param contentContainer Last selected content container.
     * @return Content browser instance.
     */
    public ContentBrowser setLastSelectedContentContainer(ContentContainer contentContainer) {

        return this;
    }

    /**
     * Get the path for the light font.
     *
     * @return Light font path.
     */
    public String getLightFontPath() {

        return mNavigator.getNavigatorModel().getBranding().lightFont;
    }

    /**
     * Get the path for the bold font.
     *
     * @return Bold font path.
     */
    public String getBoldFontPath() {

        return mNavigator.getNavigatorModel().getBranding().boldFont;
    }

    /**
     * Get the path for the regular font.
     *
     * @return Regular font path.
     */
    public String getRegularFontPath() {

        return mNavigator.getNavigatorModel().getBranding().regularFont;
    }

    /**
     * Get the flag for showing related content.
     *
     * @return True if related content should be shown; false otherwise.
     */
    public boolean isShowRelatedContent() {

        return mNavigator.getNavigatorModel().getConfig().showRelatedContent;
    }

    /**
     * Get the flag for showing content from the same category if similar tags resulted in no
     * related content.
     *
     * @return True if the category's content should be used as default related content; false
     * otherwise.
     */
    private boolean isUseCategoryAsDefaultRelatedContent() {

        return mNavigator.getNavigatorModel().getConfig().useCategoryAsDefaultRelatedContent;
    }

    /**
     * Get the flag for enabling CEA-608 closed captions
     *
     * @return True if CEA-608 closed captions should be enabled and set as priority;
     * false otherwise
     */
    public boolean isEnableCEA608() {

        return mNavigator.getNavigatorModel().getConfig().enableCEA608;
    }

    /**
     * Get the flag for enabling the recent row on the browse screen.
     *
     * @return True if the recent row should be displayed; false otherwise.
     */
    public boolean isRecentRowEnabled() {

        return mNavigator.getNavigatorModel().getConfig().enableRecentRow;
    }

    /**
     * Get the maximum number of items to be displayed in the recent row on the browse screen.
     *
     * @return The max number of items.
     */
    public int getMaxNumberOfRecentItems() {

        return mNavigator.getNavigatorModel().getConfig().maxNumberOfRecentItems;
    }

    /**
     * Get the flag for enabling the watchlist row on the browse screen.
     *
     * @return True if the watchlist row should be displayed; false otherwise.
     */
    public boolean isWatchlistRowEnabled() {

        return mNavigator.getNavigatorModel().getConfig().enableWatchlistRow;
    }

    /**
     * Get powered by logo url by name.
     *
     * @param name Powered by logo name.
     * @return Powered by logo url.
     */
    public String getPoweredByLogoUrlByName(String name) {

        return mPoweredByLogoUrlMap.get(name);
    }

    /**
     * Add powered by logo url.
     *
     * @param name Powered by logo name.
     * @param url  Powered by logo ur.
     */
    public void addPoweredByLogoUrlByName(String name, String url) {

        mPoweredByLogoUrlMap.put(name, url);
    }

    /**
     * Add action to widget action list.
     *
     * @param action The action to add.
     */
    private void addWidgetsAction(Action action) {
        mWidgetActionsList.add(action);
    }

    /**
     * Creates search action.
     *
     * @return The search action.
     */
    private Action createSearchAction() {
        Action search = new Action(CONTENT_ACTION_SEARCH, SEARCH, R.drawable.explore_white);
        search.setId(ContentBrowser.CONTENT_ACTION_SEARCH);
        search.setAction(SEARCH);
        return search;
    }

    private Action createHomeAction() {
        Action search = new Action(CONTENT_ACTION_HOME, HOME, R.drawable.home_white);
        search.setId(ContentBrowser.CONTENT_ACTION_HOME);
        search.setAction(HOME);
        return search;
    }

    private Action createMyQelloAction() {
        Action search = new Action(CONTENT_ACTION_MY_QELLO, HOME, R.drawable.my_qello_white);
        search.setId(ContentBrowser.CONTENT_ACTION_MY_QELLO);
        search.setAction(MY_QELLO);
        return search;
    }

    /**
     * This method returns the list of actions that are being used.
     *
     * @return A list of actions used for the action widget adapter.
     */
    public ArrayList<Action> getWidgetActionsList() {
        return (ArrayList<Action>) mWidgetActionsList;
    }

    /**
     * @param content Content.
     * @return Recommended contents as a content container.
     */
    public ContentContainer getRecommendedListOfAContentAsAContainer(Content content) {

        ContentContainer recommendedContentContainer =
                new ContentContainer(mAppContext.getString(R.string.recommended_contents_header));

        for (Content c : mContentLoader.getRootContentContainer()) {
            if (content.hasSimilarTags(c) && !StringManipulation.areStringsEqual(c.getId(),
                                                                                 content.getId())) {
                recommendedContentContainer.addContent(c);
            }
        }

        // Use items from the same category as recommended contents
        // if there are no contents with similar tags and the config setting is set to true.
        if (recommendedContentContainer.getContents().isEmpty() &&
                isUseCategoryAsDefaultRelatedContent()) {

            ContentContainer parentContainer = getContainerForContent(content);

            if (parentContainer != null) {

                for (Content relatedContent : parentContainer.getContents()) {
                    if (!StringManipulation.areStringsEqual(content.getId(),
                                                            relatedContent.getId())) {
                        recommendedContentContainer.addContent(relatedContent);
                    }
                }

            }
            else {
                Log.w(TAG, "The content's container could not be found! " + content.toString());
            }
        }
        return recommendedContentContainer;
    }

    /**
     * Get the parent content container for the given content.
     *
     * @param content The content to use to find the container.
     * @return The content container of the given content.
     */
    private ContentContainer getContainerForContent(Content content) {

        // Container that contains the current content
        ContentContainer parentContainer = null;

        // Stack of all content containers from root container.
        Stack<ContentContainer> contentContainerStack = new Stack<>();

        contentContainerStack.push(mContentLoader.getRootContentContainer());

        while (!contentContainerStack.isEmpty()) {
            // Get a sub container.
            ContentContainer contentContainer = contentContainerStack.pop();

            for (Content c : contentContainer.getContents()) {
                if (StringManipulation.areStringsEqual(c.getId(), content.getId())) {
                    parentContainer = contentContainer;
                }
            }

            // Add all the sub containers.
            if (contentContainer.hasSubContainers()) {
                for (ContentContainer cc : contentContainer.getContentContainers()) {
                    contentContainerStack.push(cc);
                }
            }
        }
        return parentContainer;
    }

    /**
     * Search content.
     *
     * @param query         Query string.
     * @param iSearchResult Search result listener.
     */
    public void search(String query, ISearchResult iSearchResult) {

        if (mICustomSearchHandler != null) {
            mICustomSearchHandler.onSearchRequested(query, iSearchResult);
        }
        else {
            ContentContainerExt contentContainerExt = new AsyncCaller<>(new SearchCallable(query)).getResult();
            mSearchManager.syncSearch(DEFAULT_SEARCH_ALGO_NAME,
                    query,
                    iSearchResult,
                    contentContainerExt.getContentContainer(),
                    contentContainerExt.getMetadata());
        }
    }

    /**
     * Method to trigger the LogoutSettingsFragment on clicking loginLogout Action.
     *
     * @param activity       The activity on which fragment needs to be added.
     */
    public void loginLogoutActionTriggered(Activity activity) {

        mAuthHelper
                .isAuthenticated()
                .subscribe(isAuthenticatedResultBundle -> {
                    if (isAuthenticatedResultBundle.getBoolean(AuthHelper.RESULT)) {
                        new LogoutSettingsFragment()
                                .createFragment(activity,
                                                activity.getFragmentManager());
                    }
                    else {
                        mAuthHelper.authenticateWithActivity().subscribe(resultBundle -> {
                            if (resultBundle != null &&
                                    !resultBundle.getBoolean(AuthHelper.RESULT)) {
                                getNavigator().runOnUpcomingActivity(() -> mAuthHelper
                                        .handleErrorBundle(resultBundle));
                            }
                        });
                    }
                });
    }

    /**
     * Get content action list.
     *
     * @param content Content.
     * @return List of action for provided content.
     */
    public List<Action> getContentActionList(Content content) {

        List<Action> contentActionList = new ArrayList<>();

        boolean isSubscriptionNotRequired = !content.isSubscriptionRequired();
        if (isSubscriptionNotRequired && mOverrideAllContentsSubscriptionFlag) {
            isSubscriptionNotRequired = false;
        }

        if (mSubscribed || isSubscriptionNotRequired || mIAPDisabled) {

            // Check if the content is meant for live watching. Live content requires only a
            // watch now button.
            boolean liveContent = content.getExtraValue(Recipe.LIVE_FEED_TAG) != null &&
                    Boolean.valueOf(content.getExtraValue(Recipe.LIVE_FEED_TAG).toString());

            // Check database for stored playback position of content.
            if (!liveContent) {
                RecentRecord record = getRecentRecord(content);

                // Add "Resume" button if content playback is not complete.
                if (record != null && !record.isPlaybackComplete()) {
                    contentActionList.add(createActionButton(CONTENT_ACTION_RESUME,
                                                             R.string.resume_1,
                                                             R.string.resume_2));
                    // Add "Watch From Beginning" button to start content over.
                    contentActionList.add(createActionButton(CONTENT_ACTION_WATCH_FROM_BEGINNING,
                                                             R.string.watch_from_beginning_1,
                                                             R.string.watch_from_beginning_2));
                }
                // If the content has not been played yet, add the "Watch Now" button.
                else {
                    contentActionList.add(createActionButton(CONTENT_ACTION_WATCH_NOW,
                                                             R.string.watch_now_1,
                                                             R.string.watch_now_2));
                }

                // TODO Leo - Toggle remove/add depending if favorited
                contentActionList.add(createActionButton(CONTENT_ACTION_ADD_TO_FAVORITES,
                        R.string.add_to_favorites_1, R.string.add_to_favorites_2));

                if (isWatchlistRowEnabled()) {
                    addWatchlistAction(contentActionList, content.getId());
                }
            }
            else {
                contentActionList.add(createActionButton(CONTENT_ACTION_WATCH_NOW,
                                                         R.string.watch_now_1,
                                                         R.string.watch_now_2));
            }
        }
        else {
            contentActionList.add(createActionButton(CONTENT_ACTION_SUBSCRIPTION,
                                                     R.string.premium_1,
                                                     R.string.premium_2));

            contentActionList.add(createActionButton(CONTENT_ACTION_DAILY_PASS,
                                                     R.string.daily_pass_1,
                                                     R.string.daily_pass_2));
        }

        contentActionList.addAll(mGlobalContentActionList);

        return contentActionList;
    }

    /**
     * Create an action button.
     *
     * @param contentActionId The content action id.
     * @param stringId1       The id of the string to be displayed on the first line of text.
     * @param stringId2       The id of the string to be displayed on the second line of text.
     * @return The action.
     */
    private Action createActionButton(int contentActionId, int stringId1, int stringId2) {

        return new Action().setId(contentActionId)
                           .setLabel1(mAppContext.getResources().getString(stringId1))
                           .setLabel2(mAppContext.getResources().getString(stringId2));
    }

    /**
     * Adds the "Add to Watchlist" action if the content is not in the watchlist. If the content
     * is in the watchlist, the "Remove from Watchlist" action will be added instead.
     *
     * @param contentActionList The list of content actions.
     * @param id                The content id.
     */
    private void addWatchlistAction(List<Action> contentActionList, String id) {

        // If the content is already in the watchlist, add a remove button.
        if (isContentInWatchlist(id)) {
            contentActionList.add(createActionButton(CONTENT_ACTION_REMOVE_WATCHLIST,
                                                     R.string.watchlist_2,
                                                     R.string.watchlist_3));
        }
        // Add the add to watchlist button.
        else {
            contentActionList.add(createActionButton(CONTENT_ACTION_ADD_WATCHLIST,
                                                     R.string.watchlist_1,
                                                     R.string.watchlist_3));
        }
    }

    /**
     * Tests whether or not the given content id is in the watchlist.
     *
     * @param id The content id.
     * @return True if the watchlist contains the content; false otherwise.
     */
    private boolean isContentInWatchlist(String id) {
        
        WatchlistDatabaseHelper databaseHelper = WatchlistDatabaseHelper.getInstance();
        if (databaseHelper != null) {
            return databaseHelper.recordExists(mAppContext, id);
        }
        Log.e(TAG, "Unable to load content because database is null");
        return false;
    }

    /**
     * The action for when the watchlist button is clicked.
     *
     * @param contentId     The content id.
     * @param addContent    True if the content should be added to the watchlist, false if it
     *                      shouldn't.
     * @param actionAdapter The action adapter.
     */
    private void watchlistButtonClicked(String contentId, boolean addContent,
                                        SparseArrayObjectAdapter actionAdapter) {
    
        WatchlistDatabaseHelper databaseHelper = WatchlistDatabaseHelper.getInstance();
    
        if (databaseHelper != null) {
            if (addContent) {
                databaseHelper.addRecord(mAppContext, contentId);
            }
            else {
                databaseHelper.deleteRecord(mAppContext, contentId);
            }
        }
        else {
            Log.e(TAG, "Unable to perform watchlist button action because database is null");
        }
        toggleWatchlistButton(addContent, actionAdapter);
    }

    /**
     * Get content time remaining
     *
     * @param content Content.
     * @return Time remaining in ms.
     */
    public long getContentTimeRemaining(Content content) {

        RecentRecord record = getRecentRecord(content);
        if (record != null && !record.isPlaybackComplete()) {

            // Calculate time remaining as duration minus playback location
            long duration = record.getDuration();
            long currentPlaybackPosition = record.getPlaybackLocation();

            if (currentPlaybackPosition > 0 && duration > currentPlaybackPosition) {
                return (duration - currentPlaybackPosition);
            }
        }

        return 0;
    }

    /**
     * Get content playback position percentage for progress bar.
     *
     * @param content Content.
     * @return Percentage playback complete.
     */
    public double getContentPlaybackPositionPercentage(Content content) {

        RecentRecord record = getRecentRecord(content);
        // Calculate the playback position percentage as the current playback position
        // over the entire video duration
        if (record != null && !record.isPlaybackComplete()) {

            // Calculate time remaining as duration minus playback location
            long duration = record.getDuration();
            long currentPlaybackPosition = record.getPlaybackLocation();

            if (currentPlaybackPosition > 0 && duration > currentPlaybackPosition) {
                return (((double) currentPlaybackPosition) / duration);
            }
        }

        return 0;
    }

    /**
     * Get Recent Record from database based on content id
     *
     * @param content Content.
     * @return Recent Record.
     */
    private RecentRecord getRecentRecord(Content content) {

        RecentRecord record = null;
        RecentDatabaseHelper databaseHelper = RecentDatabaseHelper.getInstance();
        if (databaseHelper != null) {
            if (databaseHelper.recordExists(mAppContext, content.getId())) {
                record = databaseHelper.getRecord(mAppContext, content.getId());
            }
        }
        else {
            Log.e(TAG, "Unable to load content because database is null");
        }

        return record;
    }

    /**
     * Get a list of contents to display in the "Continue Watching" row that have been watched for
     * more than the grace period value located in the custom.xml as recent_grace_period.
     *
     * @return A list of contents.
     */
    public List<Content> getRecentContent() {

        List<Content> contentList = new ArrayList<>();
        RecentDatabaseHelper databaseHelper = RecentDatabaseHelper.getInstance();
        if (databaseHelper != null) {
            List<RecentRecord> records = databaseHelper.getUnfinishedRecords(mAppContext,
                    mAppContext.getResources().getInteger(R.integer.recent_grace_period));

            for (RecentRecord record : records) {
                Content content = mContentLoader.getRootContentContainer()
                                                .findContentById(record.getContentId());
                if (content != null) {
                    contentList.add(content);
                }
            }
        }

        return contentList;
    }

    /**
     * Set subscribed flag.
     *
     * @param flag Subscribed flag.
     */
    public void setSubscribed(boolean flag) {

        mSubscribed = flag;
    }

    /**
     * Update content actions.
     */
    public void updateContentActions() {

        mEventBus.post(new ActionUpdateEvent(true));
    }

    /**
     * Handle on activity result.
     *
     * @param requestCode Request code.
     * @param resultCode  Result code.
     * @param data        Intent.
     */
    public void handleOnActivityResult(int requestCode, int resultCode,
                                       Intent data) {

        Log.d(TAG, "handleOnActivityResult " + requestCode);

        if (requestCode == AuthHelper.AUTH_ON_ACTIVITY_RESULT_REQUEST_CODE) {
            mAuthHelper.handleOnActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * show authentication Error Dialog
     *
     * @param iScreenSwitchListener Screen switch listener.
     */
    public void showAuthenticationErrorDialog(IScreenSwitchListener iScreenSwitchListener) {

        AlertDialogFragment.createAndShowAlertDialogFragment(
                mNavigator.getActiveActivity(),
                mAppContext.getString(R.string.optional_login_dialog_title),
                mAppContext.getString(R.string.optional_login_dialog_message),
                mAppContext.getString(R.string.now),
                mAppContext.getString(R.string.later),
                new AlertDialogFragment.IAlertDialogListener() {

                    @Override
                    public void onDialogPositiveButton(
                            AlertDialogFragment alertDialogFragment) {

                        mAuthHelper.handleAuthChain(
                                iScreenSwitchListener::onScreenSwitch);
                    }

                    @Override
                    public void onDialogNegativeButton
                            (AlertDialogFragment alertDialogFragment) {

                        Preferences.setBoolean(
                                AuthHelper.LOGIN_LATER_PREFERENCES_KEY, true);
                        iScreenSwitchListener.onScreenSwitch(null);
                    }
                });
    }

    /**
     * Verify screen switch.
     *
     * @param screenName                Screen name
     * @param iScreenSwitchListener     Screen switch listener.
     * @param iScreenSwitchErrorHandler Screen switch error handler
     */
    private void verifyScreenSwitch(String screenName,
                                   IScreenSwitchListener iScreenSwitchListener,
                                   IScreenSwitchErrorHandler iScreenSwitchErrorHandler) {

        verifyScreenSwitch(screenName, null, iScreenSwitchListener,
                           iScreenSwitchErrorHandler);
    }

    /**
     * Verify screen switch with given content.
     *
     * @param screenName                Screen name
     * @param content                   Content
     * @param iScreenSwitchListener     Screen switch listener.
     * @param iScreenSwitchErrorHandler Screen switch error handler
     */
    public void verifyScreenSwitch(String screenName, Content content,
                                   IScreenSwitchListener iScreenSwitchListener,
                                   IScreenSwitchErrorHandler iScreenSwitchErrorHandler) {

        UINode uiNode = (UINode) mNavigator.getNodeObjectByScreenName(screenName);
        // Check if the content is meant for free watching. Free content doesn't need the
        // authentication.
        boolean freeContent = content != null && content.getExtraValue(Recipe.CONTENT_TYPE_TAG)
                != null && (content.getExtraValue(Recipe.CONTENT_TYPE_TAG).toString().equals
                (FREE_CONTENT));

        Log.d(TAG, "verifyScreenSwitch called in:" + screenName);
        Log.d(TAG, "isVerifyScreenAccess needed:" + uiNode.isVerifyScreenAccess() + " and is free" +
                " content:" + freeContent);

        if (uiNode.isVerifyScreenAccess() && !freeContent) {

            if (!mAuthHelper.getIAuthentication().isAuthenticationCanBeDoneLater()) {
                mAuthHelper.handleAuthChain(iScreenSwitchListener::onScreenSwitch);
            }
            else {
                boolean loginLater = Preferences.getBoolean(AuthHelper.LOGIN_LATER_PREFERENCES_KEY);
                if (!loginLater && mAuthHelper.getIAuthentication()
                                              .isAuthenticationCanBeDoneLater()) {

                    mAuthHelper.isAuthenticated().subscribe(extras -> {
                        if (extras.getBoolean(AuthHelper.RESULT)) {
                            mAuthHelper.handleAuthChain(
                                    iScreenSwitchListener::onScreenSwitch, extras);
                        }
                        else {
                            iScreenSwitchErrorHandler.onErrorHandler(iScreenSwitchListener);
                        }
                    });
                }
                else {
                    iScreenSwitchListener.onScreenSwitch(null);
                }
            }
        }
        else {
            iScreenSwitchListener.onScreenSwitch(null);
        }
    }

    /**
     * Switch to screen by name.
     *
     * @param screenName Screen name.
     */
    public void switchToScreen(String screenName) {
        switchToScreen(screenName, (Navigator.ActivitySwitchListener) null);
    }

    /**
     * Switch to screen by name with listener.
     *
     * @param screenName             Screen name.
     * @param activitySwitchListener Activity switch listener.
     */
    private void switchToScreen(String screenName, Navigator.ActivitySwitchListener
            activitySwitchListener) {

        verifyScreenSwitch(screenName, extra -> mNavigator.startActivity(screenName, activitySwitchListener), this::showAuthenticationErrorDialog
        );
    }

    /**
     * Switch to screen by name with bundle for given content.
     *
     * @param screenName Screen name.
     * @param content    Content.
     * @param bundle     Bundle.
     */
    public void switchToScreen(String screenName, Content content, Bundle bundle) {

        verifyScreenSwitch(screenName, content, extra ->
                                   mNavigator.startActivity(screenName, bundle), this::showAuthenticationErrorDialog
        );
    }

    /**
     * Switch to screen by name for given content.
     *
     * @param screenName Screen name.
     * @param content    Content
     */
    public void switchToScreen(String screenName, Content content) {
        switchToScreen(screenName, content, (Navigator.ActivitySwitchListener) null);
    }

    /**
     * Switch to screen by name with listener for given content.
     *
     * @param screenName             Screen name.
     * @param content                Content
     * @param activitySwitchListener Activity switch listener.
     */
    private void switchToScreen(String screenName, Content content, Navigator.ActivitySwitchListener activitySwitchListener) {
        verifyScreenSwitch(screenName, content, extra ->
                                   mNavigator.startActivity(screenName, activitySwitchListener), this::showAuthenticationErrorDialog
        );
    }

    /**
     * Switch to renderer screen.
     *
     * @param content  Content.
     * @param actionId Action id.
     */
    private void switchToRendererScreen(Content content, int actionId) {

        switchToScreen(ContentBrowser.CONTENT_RENDERER_SCREEN, content, intent -> {
            intent.putExtra(Content.class.getSimpleName(), content);

            // Reset saved seek position if watching content from beginning.
            if (actionId == CONTENT_ACTION_WATCH_FROM_BEGINNING) {
                RecentDatabaseHelper databaseHelper = RecentDatabaseHelper.getInstance();
                if (databaseHelper == null) {
                    Log.e(TAG, "Error retrieving database. Recent not saved.");
                    return;
                }
                databaseHelper.addRecord(mAppContext, content.getId(), 0, false,
                                         DateAndTimeHelper.getCurrentDate().getTime(),
                                         content.getDuration());
            }
        });
    }

    /**
     * Switch to renderer screen.
     *
     * @param inputIntent Input intent for launching renderer screen.
     */
    private void switchToRendererScreen(Intent inputIntent) {

        switchToScreen(ContentBrowser.CONTENT_RENDERER_SCREEN, (Content) inputIntent
                .getSerializableExtra(Content.class.getSimpleName()), intent -> { intent.putExtras(inputIntent.getExtras()); });
    }

    /**
     * Handle renderer screen switch.
     *
     * @param activity Activity.
     * @param content  Content.
     * @param actionId Action id.
     */
    private void handleRendererScreenSwitch(Activity activity, Content content, int actionId, boolean showErrorDialog) {

        if (mIAPDisabled) {
            switchToRendererScreen(content, actionId);
        }
        else {
            Log.d(TAG, "validating purchase while handleRendererScreenSwitch");
            mPurchaseHelper
                    .isSubscriptionValidObservable()
                    .subscribe(resultBundle -> {
                        if (resultBundle.getBoolean(PurchaseHelper.RESULT) &&
                                resultBundle.getBoolean(PurchaseHelper.RESULT_VALIDITY)) {
                            // Switch to renderer screen.
                            switchToRendererScreen(content, actionId);
                        }
                        else if (resultBundle.getBoolean(PurchaseHelper.RESULT) &&
                                !resultBundle.getBoolean(PurchaseHelper.RESULT_VALIDITY)) {

                            if (showErrorDialog) {
                                AlertDialogFragment.createAndShowAlertDialogFragment(
                                        mNavigator.getActiveActivity(),
                                        mAppContext.getString(R.string.iap_error_dialog_title),
                                        mAppContext.getString(R.string.subscription_expired),
                                        null,
                                        mAppContext.getString(R.string.ok),
                                        new AlertDialogFragment.IAlertDialogListener() {

                                            @Override
                                            public void onDialogPositiveButton
                                                    (AlertDialogFragment alertDialogFragment) {

                                            }

                                            @Override
                                            public void onDialogNegativeButton
                                                    (AlertDialogFragment alertDialogFragment) {

                                                alertDialogFragment.dismiss();
                                            }
                                        });
                            }
                            else {
                                Log.e(TAG, "Purchase expired while handleRendererScreenSwitch");
                                ContentBrowser.getInstance(activity).setLastSelectedContent(content)
                                              .switchToScreen(ContentBrowser
                                                                      .CONTENT_DETAILS_SCREEN,
                                                              content);
                            }
                            updateContentActions();
                        }
                        else {
                            // IAP errors are handled by IAP sdk.
                            Log.e(TAG, "IAP error!!!");
                        }
                    }, throwable -> {
                        // IAP errors are handled by IAP sdk.
                        Log.e(TAG, "IAP error!!!", throwable);
                    });
        }
    }

    /**
     * Action triggered.
     *
     * @param activity                Activity.
     * @param content                 Content.
     * @param actionId                Action id.
     * @param actionAdapter           The adapter that holds the actions.
     * @param actionCompletedListener Optional parameter that will be called
     *                                after the action is completed.
     */
    public void actionTriggered(Activity activity, Content content, int actionId,
                                SparseArrayObjectAdapter actionAdapter, IContentActionListener
                                        actionCompletedListener) {

        List<IContentActionListener> iContentActionListenersList =
                mContentActionListeners.get(actionId);

        if (iContentActionListenersList != null && iContentActionListenersList.size() > 0) {
            for (IContentActionListener listener : iContentActionListenersList) {
                listener.onContentAction(activity, content, actionId);
            }
        }

        AnalyticsHelper.trackContentDetailsAction(content, actionId);
        switch (actionId) {
            case CONTENT_ACTION_WATCH_NOW:
            case CONTENT_ACTION_WATCH_FROM_BEGINNING:
            case CONTENT_ACTION_RESUME:
                handleRendererScreenSwitch(activity, content, actionId, true);
                break;
            case CONTENT_ACTION_SUBSCRIPTION:
            case CONTENT_ACTION_DAILY_PASS:
                mPurchaseHelper.handleAction(activity, content, actionId);
                break;
            case CONTENT_ACTION_ADD_WATCHLIST:
                watchlistButtonClicked(content.getId(), true, actionAdapter);
                break;
            case CONTENT_ACTION_REMOVE_WATCHLIST:
                watchlistButtonClicked(content.getId(), false, actionAdapter);
                break;
            case CONTENT_ACTION_ADD_TO_FAVORITES:
            case CONTENT_ACTION_REMOVE_FROM_FAVORITES:
                // TODO Leo - Implement add/remove actions
                break;
        }
        if (actionCompletedListener != null) {
            actionCompletedListener.onContentActionCompleted(activity, content, actionId);
        }
    }

    /**
     * Toggles the watch list action button text.
     *
     * @param addToList     True if the text should read "Add to Watchlist"; false if the
     *                      text should read "Remove from Watchlist".
     * @param actionAdapter The array adapter that contains the actions.
     */
    private void toggleWatchlistButton(boolean addToList, SparseArrayObjectAdapter actionAdapter) {

        for (int i = 0; i < actionAdapter.size(); i++) {
            Action action = LeanbackHelpers.translateActionAdapterObjectToAction(actionAdapter
                                                                                         .get(i));
            if (action.getId() == CONTENT_ACTION_ADD_WATCHLIST ||
                    action.getId() == CONTENT_ACTION_REMOVE_WATCHLIST) {

                // Update the button text.
                if (addToList) {
                    action.setLabel1(mAppContext.getResources().getString(R.string.watchlist_2));
                    action.setId(CONTENT_ACTION_REMOVE_WATCHLIST);
                }
                else {
                    action.setLabel1(mAppContext.getResources().getString(R.string.watchlist_1));
                    action.setId(CONTENT_ACTION_ADD_WATCHLIST);
                }
                // Reset the action in the adapter and notify change.
                actionAdapter.set(i, LeanbackHelpers.translateActionToLeanBackAction(action));
                actionAdapter.notifyArrayItemRangeChanged(i, 1);
                break;
            }
        }
    }


    /**
     * Run global recipes.
     */
    public void runGlobalRecipes(Activity activity, ICancellableLoad cancellable) {

        final ContentContainer root = new ContentContainer("Root");
        Subscription subscription =
                Observable.range(0, mNavigator.getNavigatorModel().getGlobalRecipes().size())
                          // Do this first to make sure were running in new thread right a way.
                          .subscribeOn(Schedulers.newThread())
                          .concatMap(index -> mContentLoader.runGlobalRecipeAtIndex(index, root))
                          .onBackpressureBuffer() // This must be right after concatMap.
                          .doOnNext(o -> {
                              if (DEBUG_RECIPE_CHAIN) {
                                  Log.d(TAG, "doOnNext");
                              }
                          })
                          // This should be last so the rest is running on a separate thread.
                          .observeOn(AndroidSchedulers.mainThread())
                          .subscribe(objectPair -> {
                              if (DEBUG_RECIPE_CHAIN) {
                                  Log.d(TAG, "subscriber onNext called");
                              }
                          }, throwable -> {
                              Log.e(TAG, "Recipe chain failed:", throwable);
                              ErrorHelper.injectErrorFragment(
                                      mNavigator.getActiveActivity(),
                                      ErrorUtils.ERROR_CATEGORY.FEED_ERROR,
                                      (errorDialogFragment, errorButtonType,
                                       errorCategory) -> {
                                          if (errorButtonType ==
                                                  ErrorUtils.ERROR_BUTTON_TYPE.EXIT_APP) {
                                              mNavigator.getActiveActivity().finishAffinity();
                                          }
                                      });

                          }, () -> {

                              Log.v(TAG, "Recipe chain completed");
                              // Remove empty sub containers.
                              root.removeEmptySubContainers();

                              mContentLoader.setRootContentContainer(root);
                              if (mIRootContentContainerListener != null) {
                                  mIRootContentContainerListener.onRootContentContainerPopulated
                                          (mContentLoader.getRootContentContainer());
                              }
                              mContentLoader.setContentReloadRequired(false);
                              mContentLoader.setContentLoaded(true);
                              if (cancellable != null && cancellable.isLoadingCancelled()) {
                                  Log.d(TAG, "Content load complete but app has been cancelled, " +
                                          "returning from here");
                                  return;
                              }
                              if (mLauncherIntegrationManager != null && activity != null &&
                                      LauncherIntegrationManager
                                              .isCallFromLauncher(activity.getIntent())) {

                                  Log.d(TAG, "Call from launcher with intent " +
                                          activity.getIntent());
                                  String contentId = null;
                                  try {

                                      contentId = LauncherIntegrationManager
                                              .getContentIdToPlay(mAppContext,
                                                                  activity.getIntent());

                                      Content content =
                                              getRootContentContainer().findContentById(contentId);
                                      if (content == null) {
                                          mRecommendationManager.dismissRecommendation(contentId);
                                          throw new IllegalArgumentException("No content exist " +
                                                                                     "for " +
                                                                                     "contentId "
                                                                                     + contentId);
                                      }
                                      AnalyticsHelper.trackLauncherRequest(contentId, content,
                                                                           getSourceOfContentPlayRequest(activity.getIntent()));
                                      Intent intent = new Intent();
                                      intent.putExtra(Content.class.getSimpleName(), content);
                                      intent.putExtra(REQUEST_FROM_LAUNCHER, true);
                                      intent.putExtra(PreferencesConstants.CONTENT_ID,
                                                      content.getId());
                                      switchToHomeScreen(intent);

                                  }
                                  catch (Exception e) {
                                      Log.e(TAG, e.getLocalizedMessage(), e);
                                      AnalyticsHelper.trackLauncherRequest(contentId, null,
                                                                           getSourceOfContentPlayRequest(activity.getIntent()));
                                      AlertDialogFragment.createAndShowAlertDialogFragment
                                              (mNavigator.getActiveActivity(),
                                               "Error",
                                               "The selected content is no longer available",
                                               null,
                                               mAppContext.getString(R.string.ok),
                                               new AlertDialogFragment.IAlertDialogListener() {

                                                   @Override
                                                   public void onDialogPositiveButton
                                                           (AlertDialogFragment
                                                                    alertDialogFragment) {

                                                   }

                                                   @Override
                                                   public void onDialogNegativeButton
                                                           (AlertDialogFragment
                                                                    alertDialogFragment) {

                                                       alertDialogFragment.dismiss();
                                                       if (cancellable != null &&
                                                               cancellable.isLoadingCancelled()) {
                                                           Log.d(TAG, "switchToHomeScreen after " +
                                                                   "launcher integration " +
                                                                   "exception cancelled");
                                                           return;
                                                       }
                                                       switchToHomeScreen();
                                                   }
                                               });
                                  }
                              }
                              else {
                                  if (cancellable != null &&
                                          cancellable.isLoadingCancelled()) {
                                      Log.d(TAG, "switchToHomeScreen after Splash cancelled");
                                      return;
                                  }

                                  // Send recommendations if authentication is not required, or if
                                  // the user is logged in.
                                  if (!Navigator.isScreenAccessVerificationRequired(
                                          mNavigator.getNavigatorModel()) ||
                                          Preferences.getBoolean(
                                                  LauncherIntegrationManager
                                                          .PREFERENCE_KEY_USER_AUTHENTICATED)) {
                                      mRecommendationManager.cleanDatabase();
                                      mRecommendationManager
                                              .updateGlobalRecommendations(mAppContext);
                                  }
                                  if (shouldRestoreLastActivity(activity)) {
                                      Log.d(TAG, "Ran global recipes from app launch. Will " +
                                              "add intent extra to resume previous activity");
                                      switchToHomeScreen(activity.getIntent());
                                  }
                                  else {
                                      switchToHomeScreen();
                                  }
                              }
                          });

        mCompositeSubscription.add(subscription);
    }

    /**
     * Figures out if we should restore the last activity or not. If the app was opened in the last
     * refresh period (found in resources), it will start from the fresh state instead of restoring.
     * Also, looks at the activity's intent and returns the value for the {@link #RESTORE_ACTIVITY}
     * extra which indicates if we should restore or not.
     *
     * @param activity The activity containing the intent.
     * @return True if we should restore the previous activity; false otherwise.
     */
    private boolean shouldRestoreLastActivity(Activity activity) {

        if (activity != null && activity.getIntent() != null &&
                activity.getIntent().getBooleanExtra(ContentBrowser.RESTORE_ACTIVITY, false)) {

            boolean lessThan24Hours = true;

            long lastTimeMs = Preferences.getLong(com.amazon.android.ui.constants
                                                          .PreferencesConstants.TIME_LAST_SAVED);
            // Check if the app was last opened within the refresh period.
            if (lastTimeMs > 0) {
                long currentTimeMs = DateAndTimeHelper.getCurrentDate().getTime();
                long elapsedTimeMs = (currentTimeMs - lastTimeMs);

                long refreshTimeSec =
                        activity.getResources().getInteger(R.integer.state_refresh_period);
                lessThan24Hours = (elapsedTimeMs > 0 && (elapsedTimeMs / 1000) < refreshTimeSec);
            }
            // If the app was opened within the refresh period and the intent says to restore
            // return true.
            return lessThan24Hours;
        }
        return false;
    }


    /**
     * Switches to home screen.
     *
     * @param inputIntent input intent to be passed to home screen.
     */
    private void switchToHomeScreen(Intent inputIntent) {

        switchToScreen(CONTENT_HOME_SCREEN, intent -> {
            // Make sure we clear activity stack.
            updateIntentToClearActivityStack(intent);
            intent.putExtras(inputIntent.getExtras());
        });
    }

    /**
     * Switches to home screen.
     */
    private void switchToHomeScreen() {

        // Make sure we clear activity stack.
        switchToScreen(CONTENT_HOME_SCREEN, this::updateIntentToClearActivityStack);
    }

    /**
     * Add required flags to the intent to clear the activity stack.
     *
     * @param intent Intent to be updated.
     */
    private void updateIntentToClearActivityStack(Intent intent) {

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    /**
     * Method which returns true if IAP is enabled, false otherwise.
     *
     * @return true if IAP is enabled, false otherwise.
     */
    public boolean isIapDisabled() {

        return mIAPDisabled;
    }

    /**
     * Method which returns true if user authentication is mandatory, false otherwise.
     *
     * @return true if user authentication is mandatory, false otherwise.
     */
    public boolean isUserAuthenticationMandatory() {

        if (mAuthHelper == null || mAuthHelper.getIAuthentication() == null) {
            return false;
        }
        else {
            return !mAuthHelper.getIAuthentication().isAuthenticationCanBeDoneLater();
        }
    }

    /**
     * Get the recommendation manager instance.
     *
     * @return The recommendation manager.
     */
    public RecommendationManager getRecommendationManager() {

        return mRecommendationManager;
    }

    /**
     * Launches the splash activity to properly initialize the app.
     *
     * @param activity The calling activity.
     */
    private void initFromImmatureApp(Activity activity) {

        Log.d(TAG, "init from immature app");
        // Make sure we clear activity stack.
        mNavigator.startActivity(CONTENT_SPLASH_SCREEN, this::updateIntentToClearActivityStack);
        if (activity != null) {
            activity.finish();
        }
        mContentLoader.setContentReloadRequired(false);
        mContentLoader.setContentLoaded(false);

    }

    /**
     * Reloads the feed. Launches splash activity to simply display loading text.
     *
     * @param activity The calling activity.
     */
    private void reloadFeed(Activity activity) {

        Log.d(TAG, "Content reload required, switching to splash");
        mNavigator.startActivity(CONTENT_SPLASH_SCREEN, intent -> {
            intent.putExtra(CONTENT_WILL_UPDATE, true);
            // Make sure we clear activity stack.
            updateIntentToClearActivityStack(intent);
        });
        if (activity != null) {
            activity.finish();
        }
        mContentLoader.setContentReloadRequired(false);
        mContentLoader.setContentLoaded(false);

        runGlobalRecipes(activity, ContentBrowser.this);
    }
}