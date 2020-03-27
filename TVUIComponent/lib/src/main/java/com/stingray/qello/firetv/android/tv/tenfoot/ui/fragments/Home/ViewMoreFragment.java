package com.stingray.qello.firetv.android.tv.tenfoot.ui.fragments.Home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.VerticalGridFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.BaseCardView;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stingray.qello.firetv.android.async.ObservableFactory;
import com.stingray.qello.firetv.android.contentbrowser.ContentBrowser;
import com.stingray.qello.firetv.android.contentbrowser.explorepage.ViewMoreCallable;
import com.stingray.qello.firetv.android.model.content.Content;
import com.stingray.qello.firetv.android.model.content.ViewMore;
import com.stingray.qello.firetv.android.tv.tenfoot.R;
import com.stingray.qello.firetv.android.tv.tenfoot.presenter.CardPresenter;

public class ViewMoreFragment extends VerticalGridFragment {

    private static String TAG = ViewMoreFragment.class.getSimpleName();

    private ArrayObjectAdapter mAdapter;
    private ViewMore viewMoreItem;

    private ObservableFactory observableFactory = new ObservableFactory();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter();
        gridPresenter.setNumberOfColumns(4);
        setGridPresenter(gridPresenter);
        setOnItemViewClickedListener(new ItemViewClickedListener());

        super.onCreate(savedInstanceState);
        viewMoreItem = ContentBrowser.getInstance(getActivity()).getLastSelectedViewMore();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.accent));
        setTitle(viewMoreItem.getItemName());

        View titleText =  (TextView) view.findViewById(R.id.title_text);

        return view;
    }

    @Override
    public void onStart() {
        Log.v(TAG, "onStart called.");
        super.onStart();

        VerticalGridPresenter gridPresenter = new VerticalGridPresenter();
        gridPresenter.setNumberOfColumns(5);
        super.setGridPresenter(gridPresenter);

        CardPresenter cardPresenter = new CardPresenter(BaseCardView.CARD_TYPE_INFO_UNDER);
        mAdapter = new ArrayObjectAdapter(cardPresenter);
        loadContent();
        setAdapter(mAdapter);
    }

    private void loadContent() {
        observableFactory.create(new ViewMoreCallable(viewMoreItem.getItemId()))
                .subscribe(contentContainerExt -> {
                    for (Content entry : contentContainerExt.getContentContainer()) {
                        mAdapter.add(entry);
                    }

                });

    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {

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
