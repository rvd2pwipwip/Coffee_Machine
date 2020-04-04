package com.stingray.qello.firetv.android.tv.tenfoot.ui.fragments.MyQello;

import android.os.Bundle;
import android.support.v17.leanback.app.VerticalGridFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.BaseCardView;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.stingray.qello.firetv.android.async.ObservableFactory;
import com.stingray.qello.firetv.android.contentbrowser.ContentBrowser;
import com.stingray.qello.firetv.android.contentbrowser.callable.BrowsePageCallable;
import com.stingray.qello.firetv.android.model.content.Content;
import com.stingray.qello.firetv.android.model.content.ContentContainer;
import com.stingray.qello.firetv.android.model.content.ContentContainerExt;
import com.stingray.qello.firetv.android.tv.tenfoot.R;
import com.stingray.qello.firetv.android.tv.tenfoot.presenter.CardPresenter;
import com.stingray.qello.firetv.android.tv.tenfoot.presenter.CustomVerticalGridPresenter;
import com.stingray.qello.firetv.android.utils.Helpers;

public class FavoritesFragment extends VerticalGridFragment {

    private final String TAG = FavoritesFragment.class.getSimpleName();

    private static final int ACTIVITY_ENTER_TRANSITION_FADE_DURATION = 1500;

    private ObservableFactory observableFactory = new ObservableFactory();

    private View progressView;
    private View emptyView;
    private ArrayObjectAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CustomVerticalGridPresenter gridPresenter = new CustomVerticalGridPresenter();
        gridPresenter.setNumberOfColumns(3);
        setGridPresenter(gridPresenter);
        setOnItemViewClickedListener(new ContentClickedListener());

        Helpers.handleActivityEnterFadeTransition(getActivity(), ACTIVITY_ENTER_TRANSITION_FADE_DURATION);
    }

    @Override
    public void onStart() {
        Log.v(TAG, "onStart called.");
        super.onStart();
        CardPresenter cardPresenter = new CardPresenter(BaseCardView.CARD_TYPE_INFO_UNDER, 120, 160);
        mAdapter = new ArrayObjectAdapter(cardPresenter);
        setAdapter(mAdapter);

        progressView.setVisibility(View.VISIBLE);

        observableFactory.create(new BrowsePageCallable("MY_SERVICE", "FAVORITE"))
                .subscribe(response -> {
                            loadContent(response);
                            progressView.setVisibility(View.GONE);
                        },
                        throwable -> {
                            Log.e(TAG, "Failed to load favorites.", throwable);
                        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        if (view != null) {
            progressView = view.findViewById(R.id.progress_container);
            emptyView = view.findViewById(R.id.empty_container);

            ImageView imageView1 = emptyView.findViewById(R.id.imageView1);
            imageView1.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.empty_favorites_msg));
            ImageView imageView2 = emptyView.findViewById(R.id.imageView2);
            imageView2.setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.like));
        }

        return view;
    }

    private void loadContent(ContentContainerExt favoritesContent) {
        ContentContainer contentContainer = favoritesContent.getContentContainer();

        if (contentContainer != null && contentContainer.getContentCount() > 0) {
            setTitle(favoritesContent.getMetadata().getDisplayName());
            for (Content entry : contentContainer) {
                mAdapter.add(entry);
            }
        } else {
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    private final class ContentClickedListener implements OnItemViewClickedListener {

        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof Content) {
                Content content = (Content) item;
                Log.d(TAG, "Content with title " + content.getTitle() + " was clicked");

                ContentBrowser.getInstance(getActivity())
                        .setLastSelectedContent(content)
                        .switchToScreen(ContentBrowser.CONTENT_DETAILS_SCREEN, content);
            }
        }
    }
}