/**
 * This file was modified by Amazon:
 * Copyright 2015-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *     http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.stingray.qello.firetv.android.tv.tenfoot.ui.fragments.Home;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.DetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowHeaderPresenter;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.SparseArrayObjectAdapter;
import android.support.v17.leanback.widget.TenFootActionPresenterSelector;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.stingray.qello.firetv.android.async.ObservableFactory;
import com.stingray.qello.firetv.android.contentbrowser.ContentBrowser;
import com.stingray.qello.firetv.android.contentbrowser.callable.ContentInfoCallable;
import com.stingray.qello.firetv.android.contentbrowser.callable.ContentTrackListCallable;
import com.stingray.qello.firetv.android.contentbrowser.callable.RelatedContentCallable;
import com.stingray.qello.firetv.android.contentbrowser.callable.model.Item;
import com.stingray.qello.firetv.android.contentbrowser.callable.model.SvodConcert;
import com.stingray.qello.firetv.android.contentbrowser.showscreen.ContentTrackListRow;
import com.stingray.qello.firetv.android.model.Action;
import com.stingray.qello.firetv.android.model.content.Content;
import com.stingray.qello.firetv.android.model.content.ContentContainer;
import com.stingray.qello.firetv.android.model.content.ContentContainerExt;
import com.stingray.qello.firetv.android.model.content.ContentWithTracks;
import com.stingray.qello.firetv.android.model.content.Track;
import com.stingray.qello.firetv.android.tv.tenfoot.R;
import com.stingray.qello.firetv.android.tv.tenfoot.presenter.CardPresenter;
import com.stingray.qello.firetv.android.tv.tenfoot.presenter.ContentTrackListPresenter;
import com.stingray.qello.firetv.android.tv.tenfoot.presenter.DetailsDescriptionPresenter;
import com.stingray.qello.firetv.android.tv.tenfoot.ui.activities.ContentDetailsActivity;
import com.stingray.qello.firetv.android.utils.GlideHelper;
import com.stingray.qello.firetv.android.utils.Helpers;
import com.stingray.qello.firetv.android.utils.LeanbackHelpers;

import java.util.List;

import rx.Observable;


public class ContentDetailsFragment extends android.support.v17.leanback.app.DetailsFragment {

    private static final String TAG = ContentDetailsFragment.class.getSimpleName();

    private static final int DETAIL_THUMB_WIDTH = 280;
    private static final int DETAIL_THUMB_HEIGHT = 367;

    private static final int MILLISECONDS_IN_SECOND = 1000;
    private static final int SECONDS_IN_MINUTE = 60;
    private static final int SECONDS_IN_HOUR = 60 * 60;

    private Content mSelectedContent;

    private ArrayObjectAdapter mAdapter;
    private ClassPresenterSelector mPresenterSelector;

    private BackgroundManager mBackgroundManager;
    private DisplayMetrics mMetrics;
    private boolean mShowRelatedContent;
    private ObservableFactory observableFactory = new ObservableFactory();

    SparseArrayObjectAdapter mActionAdapter = new SparseArrayObjectAdapter();

    // Decides whether the action button should be enabled or not.
    private boolean mActionInProgress = false;

    private ContentBrowser.IContentActionListener mActionCompletedListener =
            new ContentBrowser.IContentActionListener() {
                @Override
                public void onContentAction(Activity activity, Content content, int actionId) {

                }

                @Override
                public void onContentActionCompleted(Activity activity, Content content,
                                                     int actionId) {

                    mActionInProgress = false;
                }

            };

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "onCreate DetailsFragment");
        super.onCreate(savedInstanceState);

        prepareBackgroundManager();

        mSelectedContent = ContentBrowser.getInstance(getActivity()).getLastSelectedContent();
        mShowRelatedContent = ContentBrowser.getInstance(getActivity()).isShowRelatedContent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = super.onCreateView(inflater, container, savedInstanceState);


//        BrowseFrameLayout browseFrameLayout = (BrowseFrameLayout) root.findViewById(R.id.details_fragment_root);
//        LinearLayout.LayoutParams browseFrameLayoutParams = (LinearLayout.LayoutParams) browseFrameLayout.getLayoutParams();
//        browseFrameLayoutParams.setMargins(0,0,0,0);
//        browseFrameLayout.setLayoutParams(browseFrameLayoutParams);
//
//        FrameLayout detailsRowsDock = (FrameLayout) root.findViewById(R.id.details_rows_dock);
//        FrameLayout.LayoutParams detailsRowsDockLayoutParams = (FrameLayout.LayoutParams) detailsRowsDock.getLayoutParams();
//        detailsRowsDockLayoutParams.setMargins(0,0,0,0);
//        detailsRowsDock.setLayoutParams(detailsRowsDockLayoutParams);

        return root;
    }

    @Override
    public void onStart() {

        Log.v(TAG, "onStart called.");
        super.onStart();
        if (mSelectedContent != null || checkGlobalSearchIntent()) {
            // TODO Refactor this to be more optimal

            setupAdapter();
            setupDetailsOverviewRowPresenter();
            setupContentListRowPresenter();
            updateBackground(mSelectedContent.getBackgroundImageUrl());
            setOnItemViewClickedListener(new ItemViewClickedListener());

            Observable.zip(
                    observableFactory.createDetached(new ContentInfoCallable(mSelectedContent.getId()))
                            .doOnError(t ->  Log.e(TAG, "Failed to get concert info.", t))
                            .onErrorReturn(t -> null),
                    observableFactory.createDetached(new ContentTrackListCallable(mSelectedContent.getId()))
                            .doOnError(t ->  Log.e(TAG, "Failed to get track list.", t))
                            .onErrorReturn(t -> null),
                    observableFactory.createDetached(new RelatedContentCallable(mSelectedContent.getId()))
                            .doOnError(t ->  Log.e(TAG, "Failed to get related content.", t))
                            .onErrorReturn(t -> null),
                    ContentPageWrapper::new
            ).subscribe(contentPageWrapper ->  {
                getActivity().runOnUiThread(() -> {
                    if (contentPageWrapper.getContentInfoItem() != null && contentPageWrapper.getContentInfoItem().getData() != null) {
                        SvodConcert concert = contentPageWrapper.getContentInfoItem().getData().getData();
                        setupDetailsOverviewRow(concert.isLiked());
                    } else {
                        setupDetailsOverviewRow(false);
                    }

                    if (contentPageWrapper.getTrackList() != null && !contentPageWrapper.getTrackList().isEmpty()) {
                        ContentWithTracks contentWithTracks = new ContentWithTracks(mSelectedContent, contentPageWrapper.getTrackList());
                        setupTrackListPresenter(contentWithTracks.getTracks().size());
                        mAdapter.add(new ContentTrackListRow(contentWithTracks));
                    }

                    if (contentPageWrapper.getRelatedContentContainer() != null) {
                        setupRelatedContentRow(contentPageWrapper.getRelatedContentContainer());
                    }
                });
            });
        } else {
            Log.v(TAG, "Start CONTENT_HOME_SCREEN.");
            ContentBrowser.getInstance(getActivity())
                          .switchToScreen(ContentBrowser.CONTENT_HOME_SCREEN);
        }
    }

    /**
     * Overriding this method to return null since we do not want the title view to be available
     * in ContentDetails page.
     * {@inheritDoc}
     */
    protected View inflateTitle(LayoutInflater inflater, ViewGroup parent,
                                Bundle savedInstanceState) {

        return null;
    }

    /**
     * Check if there is a global search intent.
     */
    private boolean checkGlobalSearchIntent() {

        Log.v(TAG, "checkGlobalSearchIntent called.");
        Intent intent = getActivity().getIntent();
        String intentAction = intent.getAction();
        String globalSearch = getString(R.string.global_search);
        if (globalSearch.equalsIgnoreCase(intentAction)) {
            Uri intentData = intent.getData();
            Log.d(TAG, "action: " + intentAction + " intentData:" + intentData);
            int selectedIndex = Integer.parseInt(intentData.getLastPathSegment());

            ContentContainer contentContainer = ContentBrowser.getInstance(getActivity())
                                                              .getRootContentContainer();

            int contentTally = 0;
            if (contentContainer == null) {
                return false;
            }

            for (Content content : contentContainer) {
                ++contentTally;
                if (selectedIndex == contentTally) {
                    mSelectedContent = content;
                    return true;
                }
            }
        }
        return false;
    }

    private void prepareBackgroundManager() {

        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    private void updateBackground(String uri) {

        Log.v(TAG, "updateBackground called");
        if (Helpers.DEBUG) {
            Log.v(TAG, "updateBackground called: " + uri);
        }

        SimpleTarget<Bitmap> bitmapTarget = new SimpleTarget<Bitmap>(mMetrics.widthPixels,
                                                                     mMetrics.heightPixels) {
            @Override
            public void onResourceReady(Bitmap resource,
                                        GlideAnimation<? super Bitmap> glideAnimation) {

                Bitmap bitmap = Helpers.adjustOpacity(resource, getResources().getInteger(
                        R.integer.content_details_fragment_bg_opacity));

                mBackgroundManager.setBitmap(bitmap);
            }
        };

        GlideHelper.loadImageIntoSimpleTargetBitmap(getActivity(), uri,
                                                    new GlideHelper.LoggingListener(),
                                                    android.R.color.transparent, bitmapTarget);
    }

    private void setupAdapter() {

        Log.v(TAG, "setupAdapter called.");
        mPresenterSelector = new ClassPresenterSelector();
        mAdapter = new ArrayObjectAdapter(mPresenterSelector);
        setAdapter(mAdapter);
    }

    public void updateActions() {
        observableFactory.create(new ContentInfoCallable(mSelectedContent.getId()))
                .subscribe(contentInfoItem -> {
                    if (contentInfoItem.getData() != null) {
                        SvodConcert concert = contentInfoItem.getData().getData();
                        updateActions(concert.isLiked());
                    }
                });
    }

    public void updateActions(boolean isFavorited) {

        List<Action> contentActionList = ContentBrowser.getInstance(getActivity())
                                                       .getContentActionList(mSelectedContent, isFavorited);

        int i = 0;
        mActionAdapter.clear();
        for (Action action : contentActionList) {
            mActionAdapter.set(i++, LeanbackHelpers.translateActionToLeanBackAction(action));
        }

        mActionInProgress = false;
    }

    private void setupDetailsOverviewRow(boolean isLiked) {

        Log.d(TAG, "doInBackground");
        if (Helpers.DEBUG) {
            Log.d(TAG, "Selected content is: " + mSelectedContent.toString());
        }
        final DetailsOverviewRow row = new DetailsOverviewRow(mSelectedContent);
        row.setActionsAdapter(new ArrayObjectAdapter(new TenFootActionPresenterSelector()));
        row.setImageDrawable(ContextCompat.getDrawable(getActivity(),
                                                       android.R.color.transparent));
        int width = Helpers.convertDpToPixel(getActivity().getApplicationContext(),
                                             DETAIL_THUMB_WIDTH);
        int height = Helpers.convertDpToPixel(getActivity().getApplicationContext(),
                                              DETAIL_THUMB_HEIGHT);

        long timeRemaining = ContentBrowser.getInstance(getActivity())
                                           .getContentTimeRemaining(mSelectedContent);
        double playbackPercentage = ContentBrowser.getInstance(getActivity())
                                                  .getContentPlaybackPositionPercentage
                                                          (mSelectedContent);

        Log.d(TAG, "Time Remaining: " + timeRemaining);
        Log.d(TAG, "Playback Percentage: " + playbackPercentage);

        SimpleTarget<Bitmap> bitmapTarget = new SimpleTarget<Bitmap>(width, height) {
            @Override
            public void onResourceReady(Bitmap resource,
                                        GlideAnimation<? super Bitmap> glideAnimation) {

                Log.d(TAG,
                      "content_details_activity_layout overview card image url ready: " + resource);

                int cornerRadius =
                        getResources().getInteger(R.integer.details_overview_image_corner_radius);

                Bitmap bitmap = Helpers.roundCornerImage(getActivity(), resource, cornerRadius);

                if (playbackPercentage > 0) {
                    bitmap = Helpers.addProgress(getActivity(), bitmap, playbackPercentage);
                }

                long secondsRemaining = timeRemaining / MILLISECONDS_IN_SECOND;

                if (secondsRemaining > 0) {

                    long hours = 0;
                    long minutes = 0;

                    if (secondsRemaining >= SECONDS_IN_HOUR) {
                        hours = secondsRemaining / SECONDS_IN_HOUR;
                        secondsRemaining -= hours * SECONDS_IN_HOUR;
                    }

                    if (secondsRemaining >= SECONDS_IN_MINUTE) {
                        minutes = secondsRemaining / SECONDS_IN_MINUTE;
                        secondsRemaining -= minutes * SECONDS_IN_MINUTE;
                    }

                    long seconds = secondsRemaining;

                    Resources res = getResources();

                    String durationText = res.getString(R.string.duration, hours, minutes, seconds);
                    String timeRemainingText = res.getString(R.string.time_remaining, durationText);

                    bitmap = Helpers.addTimeRemaining(getActivity(), bitmap, timeRemainingText);

                }

                row.setImageBitmap(getActivity(), bitmap);

                mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
            }
        };

        GlideHelper.loadImageIntoSimpleTargetBitmap(getActivity(),
                                                    mSelectedContent.getCardImageUrl(),
                                                    new GlideHelper.LoggingListener<>(),
                                                    android.R.color.transparent,
                                                    bitmapTarget);

        updateActions(isLiked);
        row.setActionsAdapter(mActionAdapter);

        mAdapter.add(row);
    }

    private void setupDetailsOverviewRowPresenter() {

        DetailsDescriptionPresenter detailsDescPresenter = new DetailsDescriptionPresenter();

        // Set detail background and style.
        DetailsOverviewRowPresenter detailsPresenter =
                new DetailsOverviewRowPresenter(detailsDescPresenter) {
                    @Override
                    protected void initializeRowViewHolder(RowPresenter.ViewHolder vh) {

                        super.initializeRowViewHolder(vh);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            vh.view.findViewById(R.id.details_overview_image)
                                   .setTransitionName(ContentDetailsActivity.SHARED_ELEMENT_NAME);
                        }
                    }
                };
        detailsPresenter.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        detailsPresenter.setStyleLarge(true);

        // Hook up transition element.
        detailsPresenter.setSharedElementEnterTransition(getActivity(),
                                                         ContentDetailsActivity
                                                                 .SHARED_ELEMENT_NAME);

        detailsPresenter.setOnActionClickedListener(action -> {
            try {
                if (mActionInProgress) {
                    return;
                }
                mActionInProgress = true;

                int actionId = (int) action.getId();
                Log.v(TAG, "detailsPresenter.setOnActionClicked:" + actionId);

                ContentBrowser.getInstance(getActivity()).actionTriggered(getActivity(),
                                                                          mSelectedContent,
                                                                          actionId,
                                                                          mActionAdapter,
                                                                          mActionCompletedListener);
            }
            catch (Exception e) {
                Log.e(TAG, "caught exception while clicking action", e);
                mActionInProgress = false;
            }
        });
        mPresenterSelector.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);
    }

    private void setupTrackListPresenter(int nbOfTracks) {
        ContentTrackListPresenter presenter = new ContentTrackListPresenter();

        DetailsOverviewRowPresenter rowPresenter =
                new DetailsOverviewRowPresenter(presenter) {
                    @Override
                    protected void initializeRowViewHolder(RowPresenter.ViewHolder vh) {

                        super.initializeRowViewHolder(vh);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            vh.view.findViewById(R.id.details_overview_image)
                                    .setTransitionName(ContentDetailsActivity.SHARED_ELEMENT_NAME);
                        }
                    }
                    @Override
                    protected RowPresenter.ViewHolder createRowViewHolder(ViewGroup parent) {
                        RowPresenter.ViewHolder vh = super.createRowViewHolder(parent);
                        View view = vh.view.findViewById(R.id.details_frame);
                        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                        if(nbOfTracks > 0) {
                            layoutParams.height = getResources().getDimensionPixelSize(R.dimen.content_tracklist_row_height);
                        } else {
                            layoutParams.height = getResources().getDimensionPixelSize(R.dimen.content_tracklist_row_height_empty);
                        }

                        view.setLayoutParams(layoutParams);
                        return vh;
                    }
                };
        rowPresenter.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        rowPresenter.setStyleLarge(true);
        // Hook up transition element.
        rowPresenter.setSharedElementEnterTransition(getActivity(),
                ContentDetailsActivity
                        .SHARED_ELEMENT_NAME);

        rowPresenter.setOnActionClickedListener(action -> {
            try {
                if (mActionInProgress) {
                    return;
                }
                mActionInProgress = true;

                int actionId = (int) action.getId();
                Log.v(TAG, "detailsPresenter.setOnActionClicked:" + actionId);

                ContentBrowser.getInstance(getActivity()).actionTriggered(getActivity(),
                        mSelectedContent,
                        actionId,
                        mActionAdapter,
                        mActionCompletedListener);
            }
            catch (Exception e) {
                Log.e(TAG, "caught exception while clicking action", e);
                mActionInProgress = false;
            }
        });

        mPresenterSelector.addClassPresenter(ContentTrackListRow.class, rowPresenter);
    }

    /**
     * Builds the related content row. Uses contents from the selected content's category.
     */
    private void setupRelatedContentRow(ContentContainerExt contentContainerExt) {
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());

        for (Content c : contentContainerExt.getContentContainer()) {
            listRowAdapter.add(c);
        }
        // Only add the header and row for recommendations if there are any recommended content.
        if (listRowAdapter.size() > 0) {
            HeaderItem header = new HeaderItem(0, contentContainerExt.getMetadata().getDisplayName());
            mAdapter.add(new ListRow(header, listRowAdapter));
        }
    }

    private void setupContentListRowPresenter() {

        ListRowPresenter presenter = new ListRowPresenter();
        presenter.setHeaderPresenter(new RowHeaderPresenter());
        mPresenterSelector.addClassPresenter(ListRow.class, presenter);
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {

        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof Content) {
                Content content = (Content) item;
                if (Helpers.DEBUG) {
                    Log.d(TAG, "Item: " + content.getId());
                }
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
                        ContentDetailsActivity.SHARED_ELEMENT_NAME).toBundle();

                ContentBrowser.getInstance(getActivity())
                              .setLastSelectedContent(content)
                              .switchToScreen(ContentBrowser.CONTENT_DETAILS_SCREEN, content,
                                              bundle);
            }
        }
    }

    @Override
    public void onResume() {

        Log.v(TAG, "onResume called.");
        super.onResume();
        updateActionsProperties();
        mActionInProgress = false;
    }

    /**
     * Since we do not have direct access to the details overview actions row, we are adding a
     * delayed handler that waits for some time, searches for the row and then updates the
     * properties. This is not a fool-proof method,
     * > In slow devices its possible that this does not succeed in achieving the desired result.
     * > In fast devices its possible that the update is clearly visible to the user.
     * TODO: Find a better approach to update action properties
     */
    private void updateActionsProperties() {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            View view = getView();
            if (view != null) {
                HorizontalGridView horizontalGridView =
                        (HorizontalGridView) view.findViewById(R.id.details_overview_actions);

                if (horizontalGridView != null) {
                    // This is required to make sure this button gets the focus whenever
                    // detailsFragment is resumed.
                    horizontalGridView.requestFocus();
                    for (int i = 0; i < horizontalGridView.getChildCount(); i++) {
                        final Button button = (Button) horizontalGridView.getChildAt(i);
                        if (button != null) {
                            // Button objects are recreated every time MovieDetailsFragment is
                            // created or restored, so we have to bind OnKeyListener to them on
                            // resuming the Fragment.
                            button.setOnKeyListener((v, keyCode, keyEvent) -> {
                                if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE &&
                                        keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                                    button.performClick();
                                }
                                return false;
                            });
                        }
                    }
                }
            }
        }, 400);
    }

    private class ContentPageWrapper {
        private Item<Item<SvodConcert>> contentInfoItem;
        private List<Track> trackList;
        private ContentContainerExt relatedContentContainer;

        public ContentPageWrapper(Item<Item<SvodConcert>> contentInfoItem, List<Track> trackList, ContentContainerExt relatedContentContainer) {
            this.contentInfoItem = contentInfoItem;
            this.trackList = trackList;
            this.relatedContentContainer = relatedContentContainer;
        }

        public Item<Item<SvodConcert>> getContentInfoItem() {
            return contentInfoItem;
        }

        public List<Track> getTrackList() {
            return trackList;
        }

        public ContentContainerExt getRelatedContentContainer() {
            return relatedContentContainer;
        }
    }
}
