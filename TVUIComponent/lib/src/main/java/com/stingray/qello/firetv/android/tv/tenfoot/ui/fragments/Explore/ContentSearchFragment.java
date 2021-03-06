/**
 * This file was modified by Amazon:
 * Copyright 2015-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * A copy of the License is located at
 * <p>
 * http://aws.amazon.com/apache2.0/
 * <p>
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
package com.stingray.qello.firetv.android.tv.tenfoot.ui.fragments.Explore;


import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.RowsFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.BaseCardView;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowHeaderPresenter;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.SearchBar;
import android.support.v17.leanback.widget.SearchEditText;
import android.support.v17.leanback.widget.SpeechOrbView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.stingray.qello.firetv.android.async.ObservableFactory;
import com.stingray.qello.firetv.android.contentbrowser.ContentBrowser;
import com.stingray.qello.firetv.android.contentbrowser.callable.ExplorePageCallable;
import com.stingray.qello.firetv.android.contentbrowser.callable.GenreFilterCallable;
import com.stingray.qello.firetv.android.model.SvodMetadata;
import com.stingray.qello.firetv.android.model.content.Content;
import com.stingray.qello.firetv.android.model.content.Genre;
import com.stingray.qello.firetv.android.search.SearchManager;
import com.stingray.qello.firetv.android.tv.tenfoot.BuildConfig;
import com.stingray.qello.firetv.android.tv.tenfoot.R;
import com.stingray.qello.firetv.android.tv.tenfoot.presenter.CardPresenter;
import com.stingray.qello.firetv.android.tv.tenfoot.presenter.CustomListRowPresenter;
import com.stingray.qello.firetv.android.tv.tenfoot.ui.activities.ContentDetailsActivity;
import com.stingray.qello.firetv.android.utils.Helpers;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides search capabilities by extending the android ContentSearchFragment.
 * The primary function of this class is to take user input and pass that data into {@link
 * SearchManager}.
 * This class will populate the search results row when the search is complete.
 */
public class ContentSearchFragment extends android.support.v17.leanback.app.SearchFragment
        implements android.support.v17.leanback.app.SearchFragment.SearchResultProvider {

    private static final String TAG = ContentSearchFragment.class.getSimpleName();
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final long RESULTS_LOAD_DELAY = 300L;

    private final Handler resultLoadHandler = new Handler();
    private ArrayObjectAdapter mRowsAdapter;
    private String loadedQuery;
    private SearchBar searchBar;
    private RelativeLayout searchBarItems;
    private SearchEditText mSearchEditText = null;
    private ObservableFactory observableFactory = new ObservableFactory();
    private View focusedGenreButton = null;
    private List<View> genreButtons = new ArrayList<>();
    private boolean returnToSearch = false;
    private boolean returningToSearch = false;
    private View searchResultsLayout;
    private View noResultsView;
    private boolean hasResults = false;
    private List<Object> resultsHolder;
    private boolean itemWasClicked = false;

    // A local list row Adapter
    private ArrayObjectAdapter mListRowAdapter;

    /**
     * This handler will be used to give the focus to search textview.
     * Needs to be attached to this fragment
     */
    private Handler mAutoTextViewFocusHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        CustomListRowPresenter presenter = new CustomListRowPresenter();
        presenter.setShadowEnabled(false);
        presenter.setHeaderPresenter(new RowHeaderPresenter());
        presenter.getHeaderPresenter().setNullItemVisibilityGone(true);

        mRowsAdapter = new ArrayObjectAdapter(presenter);
        resultsHolder = new ArrayList<>();

        setSearchResultProvider(this);
        setOnItemViewClickedListener(new ItemViewClickedListener());

        // TODO: Set up speech recognizer for AndroidTV only.
        if (!hasPermission(Manifest.permission.RECORD_AUDIO)) {
            // SpeechRecognitionCallback is not required and if not provided recognition will be
            // handled using internal speech recognizer, in which case you must have RECORD_AUDIO
            // permission.
            setSpeechRecognitionCallback(() -> {
                if (DEBUG) Log.v(TAG, "recognizeSpeech");
                try {
                    // Disabling speech recognizer so the fragment works on Fire TV.
                    //startActivityForResult(getRecognizerIntent(), REQUEST_SPEECH);
                } catch (ActivityNotFoundException e) {
                    Log.e(TAG, "Cannot find activity for speech recognizer", e);
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);

        // Required because we need to hijack the private mRowsFragment data member
        // from the parent class SearchFragment to override a behaviour
        getChildFragmentManager().executePendingTransactions();

        RowsFragment rowsFragment = (RowsFragment) getChildFragmentManager().findFragmentById(R.id.lb_results_frame);
        if (rowsFragment != null) {
            rowsFragment.setOnItemViewSelectedListener((itemViewHolder, item, rowViewHolder, row) -> {
                if (DEBUG) {
                    int position = rowsFragment.getSelectedPosition();
                    Log.v(TAG, String.format("onItemSelected %d", position));
                }
            });
        }

        if (view != null) {
            noResultsView = view.findViewById(R.id.no_results_container);

            observableFactory.create(new ExplorePageCallable())
                    .subscribe(genres -> createGenreButtons(view, inflater, genres));

            searchResultsLayout = view.findViewById(R.id.lb_results_frame);

            // Set background color and drawable.
            view.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.transparent));
            view.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.background_explore));

            searchBar = view.findViewById(R.id.lb_search_bar);

            searchBar.getViewTreeObserver().addOnGlobalFocusChangeListener(((oldFocus, newFocus) -> {
                if (oldFocus == null) {
                    return;
                }

                if (oldFocus.getId() == R.id.lb_search_text_editor && newFocus instanceof ImageCardView) {
                    returnToSearch = true;
                }
            }));

            searchBarItems = searchBar.findViewById(R.id.lb_search_bar_items);
            SpeechOrbView speechOrbView = searchBar.findViewById(R.id.lb_search_bar_speech_orb);

            final SearchEditText searchEditText = searchBar.findViewById(R.id.lb_search_text_editor);

            if (searchBarItems != null && speechOrbView != null && searchEditText != null) {
                searchBarItems.setBackground(getResources().getDrawable(R.drawable.search_edit_text_bg_unfocused));

                // There's too much logic binded to this, don't use it
                speechOrbView.setOnOrbClickedListener(null);
                speechOrbView.setOnFocusChangeListener(null);
                speechOrbView.setVisibility(View.GONE);
                speechOrbView.setOrbIcon(ContextCompat.getDrawable(getActivity(), R.drawable.search_icon));

                mSearchEditText = searchEditText;
                mSearchEditText.setOnFocusChangeListener((view1, hasFocus) -> {
                    if (hasFocus) {
                        searchBarItems.setBackground(getResources().getDrawable(R.drawable.search_edit_text_bg));
                        loadQuery(mSearchEditText.getText().toString());
                    } else {
                        searchBarItems.setBackground(getResources().getDrawable(R.drawable.search_edit_text_bg_unfocused));
                    }
                });

                // Handle keyboard being dismissed to prevent focus going to SearchOrb
                // If user presses back from keyboard, you don't get KeyboardDismissListener
                // so handle that here.

                mSearchEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
                    loadQuery(mSearchEditText.getText().toString());

                    // Track search if keyboard is closed with IME_ACTION_PREVIOUS or
                    // if IME_ACTION_SEARCH occurs.
                    if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_PREVIOUS) {
                        //TODO seg track search?
                    }

                    if (hasResults && loadedQuery != null) {
                        searchResultsLayout.requestFocus();
                    }

                    // Hide keyboard since we are handling the action
                    if (isAdded()) {
                        // Ensure we are added before calling getActivity
                        InputMethodManager inputManager =
                                (InputMethodManager) getActivity().getSystemService(
                                        Context.INPUT_METHOD_SERVICE);
                        if (inputManager != null) {
                            inputManager.hideSoftInputFromWindow(
                                    getActivity().getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    } else {
                        Log.e(TAG, "Cannot find activity, can't dismiss keyboard");
                        // Couldn't handle action.
                        // Will expose other focus issues potentially.
                        return false;
                    }
                    // No more processing of this action.
                    return true;
                });

                // Override the dismiss listener to get around keyboard issue where dismissing
                // keyboard takes user into first search result's
                // content_details_activity_layout page.
                searchEditText.setOnKeyboardDismissListener(() -> {
                    loadQuery(mSearchEditText.getText().toString());
                    // We don't need to clearFocus on SearchEditText here, the first
                    // result will be selected already.
                    if (hasResults && loadedQuery != null) {
                        searchResultsLayout.requestFocus();
                    }
                });

            }
        }
        return view;
    }

    private boolean isValidQuery(String query) {
        return !TextUtils.isEmpty(query) && !query.equals("nil") && query.length() > 1;
    }

    @Override
    public void onResume() {

        super.onResume();
        if (mSearchEditText != null) {
            mSearchEditText.setFocusable(false);
            mSearchEditText.clearFocus();
            if (isValidQuery(mSearchEditText.getText().toString()) && !itemWasClicked) {
                resultsHolder = new ArrayList<>();
            }
        }

        mAutoTextViewFocusHandler.postDelayed(() -> {
            mSearchEditText.setFocusable(true);
            mSearchEditText.setHint(getResources().getString(R.string.Explore_SearchPlaceholder));

            if (!itemWasClicked) {
                mSearchEditText.requestFocus();

                for (Object row : resultsHolder) {
                    mRowsAdapter.add(row);
                }

                if (!hasResults && mRowsAdapter.size() > 0) {
                    noResultsView.setVisibility(View.VISIBLE);
                }
            }
            itemWasClicked = false;
        }, 500);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        mAutoTextViewFocusHandler.removeCallbacksAndMessages(null);
        resultLoadHandler.removeCallbacksAndMessages(null);

        if (!itemWasClicked) {
            loadedQuery = null;
            resultsHolder = new ArrayList<>();
            for (int i = 0; i < mRowsAdapter.size(); i++) {
                resultsHolder.add(mRowsAdapter.get(i));
            }
            mRowsAdapter.clear();
        }

        super.onPause();
    }

    @Override
    public ObjectAdapter getResultsAdapter() {
        return mRowsAdapter;
    }

    @Override
    public boolean onQueryTextChange(String newQuery) {
        Log.i(TAG, String.format("Search Query Text Change %s", newQuery));
        loadQuery(newQuery);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.i(TAG, String.format("Search Query Text Submit %s", query));
        loadQuery(query);
        return true;
    }

    private boolean hasPermission(final String permission) {

        final Context context = getActivity();
        return PackageManager.PERMISSION_GRANTED == context.getPackageManager().checkPermission(
                permission, context.getPackageName());
    }

    private void loadQuery(String query) {
        if (isValidQuery(query) && !query.equalsIgnoreCase(loadedQuery)) {
            resultLoadHandler.removeCallbacksAndMessages(null);

            Runnable loadQuery = () -> {
                mListRowAdapter = new ArrayObjectAdapter(new CardPresenter(BaseCardView.CARD_TYPE_INFO_UNDER, 120, 160));
                ContentBrowser.getInstance(getActivity()).search(query, this::updateResults);
                loadedQuery = query;
            };

            resultLoadHandler.postDelayed(loadQuery, RESULTS_LOAD_DELAY);
        }
    }

    public void loadGenreAssets(String genreId) {
        resultLoadHandler.removeCallbacksAndMessages(null);
        Runnable loadGenreAssets = () -> observableFactory.create(new GenreFilterCallable(genreId)).subscribe(genreAssets -> {
            SvodMetadata metadata = genreAssets.getMetadata();
            mListRowAdapter = new ArrayObjectAdapter(new CardPresenter(BaseCardView.CARD_TYPE_INFO_UNDER, 120, 160));

            for (Content entry : genreAssets.getContentContainer()) {
                updateResults(entry, metadata, false);
            }

            updateResults(null, metadata, true);
            loadedQuery = null;
        });

        resultLoadHandler.postDelayed(loadGenreAssets, RESULTS_LOAD_DELAY);
    }

    /**
     * This method is the entry point for new content to be added to the view.
     * If there is no more new content coming in the current in flight query the boolean is set to
     * true and we send a new {@link ListRow} to the {@link #mRowsAdapter}
     *
     * @param inputContent the new content to be added
     * @param done         a boolean that tells us that there are no new items coming in the
     *                     current
     *                     in flight query
     */
    private void updateResults(Object inputContent, @Nullable SvodMetadata metadata, boolean done) {

        // If done then add the content to the mRowsAdapter.
        if (done) {
            mRowsAdapter.clear();

            String displayName = "";

            if (metadata != null && metadata.getDisplayName() != null) {
                displayName = metadata.getDisplayName();
            }

            HeaderItem header = new HeaderItem(displayName);

            hasResults = mListRowAdapter.size() > 0;

            if (hasResults) {
                noResultsView.setVisibility(View.GONE);

                int elementsInRow = getResources().getInteger(R.integer.num_of_search_elements_in_row);

                int rows = mListRowAdapter.size() / elementsInRow;

                if (mListRowAdapter.size() % elementsInRow > 0) {
                    rows++;
                }

                int index = 0;

                for (int i = 0; i < rows; i++) {
                    ArrayObjectAdapter row = new ArrayObjectAdapter(new CardPresenter(BaseCardView.CARD_TYPE_INFO_UNDER, 120, 160));

                    for (int j = index; j < (index + elementsInRow) && (j < mListRowAdapter.size()); j++) {
                        row.add(mListRowAdapter.get(j));
                    }

                    if (i > 0) {
                        mRowsAdapter.add(new ListRow(row));
                    } else {
                        mRowsAdapter.add(new ListRow(header, row));
                    }

                    index += elementsInRow;
                }
            } else {
                ArrayObjectAdapter row = new ArrayObjectAdapter(new CardPresenter(BaseCardView.CARD_TYPE_INFO_UNDER, 120, 160));
                mRowsAdapter.add(new ListRow(header, row));
                noResultsView.setAlpha(0f);
                noResultsView.setVisibility(View.VISIBLE);
                noResultsView.animate()
                        .alpha(1f)
                        .setDuration(500)
                        .setListener(null);
            }
            RowsFragment rowsFragment = (RowsFragment) getChildFragmentManager()
                    .findFragmentById(R.id.lb_results_frame);
            ;

            if (rowsFragment != null) {
                rowsFragment.setSelectedPosition(0);
            }

        } else {
            // Only add the content if the adapter does not already contain it.
            if (mListRowAdapter.indexOf(inputContent) == -1) {
                mListRowAdapter.add(inputContent);
            }
        }
    }

    private void focusTextView(int delay) {
        mAutoTextViewFocusHandler.postDelayed(() -> {
            if (mSearchEditText != null) {
                mSearchEditText.setFocusable(true);
                mSearchEditText.requestFocus();
            }
        }, delay);
    }


    private void createGenreButtons(View view, LayoutInflater inflater, List<Genre> genres) {
        // TODO Refactor this, the logic is really convoluted (but it works so maybe later)
        LinearLayout explorePageGenres = view.findViewById(R.id.explore_page_genres);
        explorePageGenres.getViewTreeObserver().addOnGlobalFocusChangeListener((oldFocus, newFocus) -> {
            boolean oldFocusInGenresMenu = genreButtons.contains(oldFocus);
            boolean newFocusInGenresMenu = genreButtons.contains(newFocus);
            if (oldFocusInGenresMenu || newFocusInGenresMenu) {
                boolean enteringGenresMenu = !oldFocusInGenresMenu;
                boolean leavingGenreMenu = oldFocusInGenresMenu && !newFocusInGenresMenu;

                boolean enteringGrid = newFocus instanceof ImageCardView;
                boolean leavingGrid = oldFocus instanceof ImageCardView;

                boolean oldIsNavMenu = oldFocus != null && !leavingGrid && !(oldFocus instanceof SearchEditText) && !oldFocusInGenresMenu;

                if ((enteringGenresMenu && oldIsNavMenu) || (returnToSearch && leavingGrid)) {
                    focusTextView(0);
                    returnToSearch = false;
                    returningToSearch = true;
                } else if (focusedGenreButton != null) {
                    focusedGenreButton.setBackground(getResources().getDrawable(R.drawable.button_bg_stroke));

                    if (!hasResults && newFocus.getId() == R.id.row_content) {
                        focusedGenreButton.requestFocus();
                        return;
                    }

                    if (enteringGenresMenu && leavingGrid) {
                        focusedGenreButton.requestFocus();
                    } else if (leavingGenreMenu && enteringGrid) {
                        focusedGenreButton.setBackground(getResources().getDrawable(R.drawable.button_bg_stroke_focused));
                    }

                    if (leavingGenreMenu && !enteringGrid) {
                        focusedGenreButton = null;
                    }
                }
            }
        });

        ViewGroup.LayoutParams buttonLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        for (Genre genre : genres) {
            View cView = inflater.inflate(R.layout.explore_page_category_button, explorePageGenres, false);
            Button genreButton = cView.findViewById(R.id.explore_page_genre_btn);
            genreButton.setText(genre.getTitle());
            genreButton.setOnFocusChangeListener((view1, motionEvent) -> {
                if (view1.isFocused() && !view1.equals(focusedGenreButton)) {
                    if (returningToSearch) {
                        focusedGenreButton = view1;
                        focusedGenreButton.setBackground(getResources().getDrawable(R.drawable.button_bg_stroke_no_focus));
                        returningToSearch = false;
                    } else {
                        focusedGenreButton = view1;
                        loadGenreAssets(genre.getId());
                    }
                }
            });
            genreButtons.add(genreButton);
            explorePageGenres.addView(genreButton, buttonLayoutParams);
        }
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {

        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof Content) {
                itemWasClicked = true;

                Content content = (Content) item;
                if (Helpers.DEBUG) {
                    Log.d(TAG, "Content: " + content.toString());
                }

                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
                        ContentDetailsActivity.SHARED_ELEMENT_NAME).toBundle();

                ContentBrowser.getInstance(getActivity())
                        .setLastSelectedContent(content)
                        .switchToScreen(ContentBrowser.CONTENT_DETAILS_SCREEN, content,
                                bundle);
            } else {
                Toast.makeText(getActivity(), ((String) item), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}