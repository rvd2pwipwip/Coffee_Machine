package com.stingray.qello.firetv.android.tv.tenfoot.ui.fragments.MyQello;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Handler;
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

import static android.view.View.VISIBLE;

public abstract class BrowsePageFragment extends VerticalGridFragment {

    private static final int ACTIVITY_ENTER_TRANSITION_FADE_DURATION = 1500;

    protected ObservableFactory observableFactory = new ObservableFactory();

    private View progressView;
    private View emptyView;
    protected Button actionButton1;
    private ArrayObjectAdapter mAdapter;
    private Handler loadingHandler = new Handler();

    private boolean isEmpty = true;

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

        actionButton1.setVisibility(View.INVISIBLE);

        loadingHandler.postDelayed(() -> fadeIn(progressView, 200), 500);

        observableFactory.create(new BrowsePageCallable("MY_SERVICE", getBrowsePage()))
                .subscribe(this::loadContent,
                        throwable -> {
                            loadingHandler.removeCallbacksAndMessages(null);
                            crossFade(progressView, emptyView);
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
            imageView1.setImageDrawable(getResources().getDrawable(getEmptyImageDrawable()));
            ImageView imageView2 = emptyView.findViewById(R.id.imageView2);
            imageView2.setImageDrawable(getResources().getDrawable(getEmptyMsgDrawable()));

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
        isEmpty = true;
        toggleEmpty();
    }

    private void updateActionButtonVisibility() {
        boolean hasSubscription = Preferences.getBoolean(PreferencesConstants.HAS_SUBSCRIPTION);
        int visibility = (hasSubscription && !isEmpty) ? VISIBLE : View.INVISIBLE;
        actionButton1.setVisibility(visibility);
    }

    private void toggleEmpty() {
        if (isEmpty) {
            crossFade(progressView, emptyView);
        } else {
            fadeOut(progressView,300);
        }
        updateActionButtonVisibility();
    }

    private void loadContent(ContentContainerExt favoritesContent) {
        loadingHandler.removeCallbacksAndMessages(null);
        ContentContainer contentContainer = favoritesContent.getContentContainer();
        isEmpty = contentContainer.getContentCount() < 1;
        toggleEmpty();

        for (Content entry : contentContainer) {
            mAdapter.add(entry);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onAuthenticationStatusUpdateEvent(AuthenticationStatusUpdateEvent authenticationStatusUpdateEvent) {
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

    private void crossFade(View view1, View view2) {
        fadeIn(view2, 600);
        fadeOut(view1, 200);
    }

    private void fadeIn(View view, int duration) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(1f)
                .setDuration(duration)
                .setListener(null);
    }

    private void fadeOut(View view, int duration) {
        view.animate()
                .alpha(0f)
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.INVISIBLE);
                        view.setAlpha(1f);
                    }
                });
    }
}