package com.stingray.qello.firetv.android.tv.tenfoot.ui.fragments.MyQello;

import android.os.Bundle;
import android.support.v17.leanback.app.VerticalGridFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.BaseCardView;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.stingray.qello.firetv.android.async.ObservableFactory;
import com.stingray.qello.firetv.android.contentbrowser.ContentBrowser;
import com.stingray.qello.firetv.android.contentbrowser.callable.BrowsePageCallable;
import com.stingray.qello.firetv.android.contentbrowser.callable.ClearBrowsePageCallable;
import com.stingray.qello.firetv.android.event.AuthenticationStatusUpdateEvent;
import com.stingray.qello.firetv.android.model.content.Content;
import com.stingray.qello.firetv.android.model.content.ContentContainer;
import com.stingray.qello.firetv.android.model.content.ContentContainerExt;
import com.stingray.qello.firetv.android.tv.tenfoot.R;
import com.stingray.qello.firetv.android.tv.tenfoot.presenter.CardPresenter;
import com.stingray.qello.firetv.android.tv.tenfoot.presenter.BrowsePageVerticalGridPresenter;
import com.stingray.qello.firetv.android.ui.constants.PreferencesConstants;
import com.stingray.qello.firetv.android.utils.Helpers;
import com.stingray.qello.firetv.android.utils.Preferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public abstract class BrowsePageFragment extends VerticalGridFragment {

    private static final int ACTIVITY_ENTER_TRANSITION_FADE_DURATION = 1500;

    protected ObservableFactory observableFactory = new ObservableFactory();

    private View progressView;
    private View emptyView;
    protected Button actionButton1;
    private ArrayObjectAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        BrowsePageVerticalGridPresenter gridPresenter = new BrowsePageVerticalGridPresenter();
        gridPresenter.setNumberOfColumns(4);
        gridPresenter.setShadowEnabled(false);
        setGridPresenter(gridPresenter);
        setOnItemViewClickedListener(new ContentClickedListener());

        Helpers.handleActivityEnterFadeTransition(getActivity(), ACTIVITY_ENTER_TRANSITION_FADE_DURATION);
    }

    @Override
    public void onStart() {
        Log.v(getTag(), "onStart called.");
        super.onStart();
        CardPresenter cardPresenter = new CardPresenter(BaseCardView.CARD_TYPE_INFO_UNDER, 120, 160);
        mAdapter = new ArrayObjectAdapter(cardPresenter);
        setAdapter(mAdapter);

        progressView.setVisibility(View.VISIBLE);

        observableFactory.create(new BrowsePageCallable("MY_SERVICE", getBrowsePage()))
                .subscribe(response -> {
                            loadContent(response);
                            progressView.setVisibility(View.GONE);
                        },
                        throwable -> {
                            emptyView.setVisibility(View.VISIBLE);
                            progressView.setVisibility(View.GONE);
                            Log.e(getTag(), "Failed to load content.", throwable);
                        });
    }

    protected abstract String getBrowsePage();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        if (view != null) {
            progressView = view.findViewById(R.id.progress_container);
            emptyView = view.findViewById(R.id.empty_container);

            ImageView imageView1 = emptyView.findViewById(R.id.imageView1);
            imageView1.setImageDrawable(getResources().getDrawable(getEmptyMsgDrawable()));
            ImageView imageView2 = emptyView.findViewById(R.id.imageView2);
            imageView2.setImageDrawable(getResources().getDrawable(getEmptyImageDrawable()));

            actionButton1 = view.findViewById(R.id.actionButton1);
            actionButton1.setText(getClearButtonText());
            setActionButtonListener();
        }

        return view;
    }

    protected abstract int getEmptyMsgDrawable();
    protected abstract int getEmptyImageDrawable();
    protected abstract String getClearButtonText();
    protected abstract void setActionButtonListener();

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    protected void clearContents() {
        mAdapter.clear();
        emptyView.setVisibility(View.VISIBLE);
    }

    private void updateActionButtonVisibility() {
        actionButton1.setVisibility(mapToVisibility(Preferences.getBoolean(PreferencesConstants.HAS_SUBSCRIPTION)));
    }

    private void toggleEmpty(boolean isEmpty) {
        emptyView.setVisibility(mapToVisibility(isEmpty));
        updateActionButtonVisibility();
    }

    private void loadContent(ContentContainerExt favoritesContent) {
        ContentContainer contentContainer = favoritesContent.getContentContainer();
        toggleEmpty(contentContainer.getContentCount() < 1);

        for (Content entry : contentContainer) {
            mAdapter.add(entry);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onAuthenticationStatusUpdateEvent(AuthenticationStatusUpdateEvent
                                                          authenticationStatusUpdateEvent) {
        getActivity().runOnUiThread(this::updateActionButtonVisibility);
    }


    private final class ContentClickedListener implements OnItemViewClickedListener {

        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof Content) {
                Content content = (Content) item;
                Log.d(getTag(), "Content with title " + content.getTitle() + " was clicked");

                ContentBrowser.getInstance(getActivity())
                        .setLastSelectedContent(content)
                        .switchToScreen(ContentBrowser.CONTENT_DETAILS_SCREEN, content);
            }
        }
    }

    private int mapToVisibility(boolean isVisible) {
        if (isVisible) {
            return View.VISIBLE;
        } else {
            return View.GONE;
        }
    }
}