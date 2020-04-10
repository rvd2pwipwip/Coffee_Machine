package com.stingray.qello.firetv.android.tv.tenfoot.presenter;

import android.support.v17.leanback.widget.FocusHighlight;
import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.support.v17.leanback.widget.VerticalGridView;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;

public class ViewMoreVerticalGridPresenter extends VerticalGridPresenter {

    public ViewMoreVerticalGridPresenter() {
        super(FocusHighlight.ZOOM_FACTOR_MEDIUM, false);
    }

    @Override
    protected ViewHolder createGridViewHolder(ViewGroup parent) {
        ViewHolder gridViewHolder = super.createGridViewHolder(parent);
        VerticalGridView gridView = gridViewHolder.getGridView();
        gridView.setVerticalMargin(80);
        gridView.setHorizontalMargin(45);
        gridView.setGravity(Gravity.CENTER_HORIZONTAL);

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        gridView.setLayoutParams(params);
        return gridViewHolder;
    }
}
