package com.stingray.qello.firetv.android.tv.tenfoot.ui.fragments.MyQello;

import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.stingray.qello.firetv.android.async.ObservableFactory;
import com.stingray.qello.firetv.android.contentbrowser.ContentBrowser;
import com.stingray.qello.firetv.android.contentbrowser.callable.HistoryCallable;
import com.stingray.qello.firetv.android.model.content.Content;
import com.stingray.qello.firetv.android.tv.tenfoot.presenter.CardPresenter;
import com.stingray.qello.firetv.android.tv.tenfoot.presenter.CustomVerticalGridPresenter;

public class HistoryFragment extends VerticalGridFragment {

    private static String TAG = HistoryFragment.class.getSimpleName();

    private ArrayObjectAdapter mAdapter;

    private ObservableFactory observableFactory = new ObservableFactory();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        CustomVerticalGridPresenter gridPresenter = new CustomVerticalGridPresenter();
        gridPresenter.setNumberOfColumns(4);
        setGridPresenter(gridPresenter);

        setOnItemViewClickedListener(new ContentClickedListener());

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.v(TAG, "onStart called.");
        super.onStart();
        CardPresenter cardPresenter = new CardPresenter(BaseCardView.CARD_TYPE_INFO_UNDER, 150, 200);
        mAdapter = new ArrayObjectAdapter(cardPresenter);
        setAdapter(mAdapter);

        loadContent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View  view = super.onCreateView(inflater, container, savedInstanceState);
        setTitle(null);
        return view;
    }

    private void loadContent() {
        observableFactory.create(new HistoryCallable())
                .subscribe(contentContainerExt -> {
                    for (Content entry : contentContainerExt.getContentContainer()) {
                        mAdapter.add(entry);
                    }
                });

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
