package com.stingray.qello.firetv.android.tv.tenfoot.presenter;

import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.support.v17.leanback.widget.VerticalGridView;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;

public class ViewMoreVerticalGridPresenter extends VerticalGridPresenter {

    public ViewMoreVerticalGridPresenter() {

        super();
    }

    @Override
    protected ViewHolder createGridViewHolder(ViewGroup parent) {
        ViewHolder gridViewHolder = super.createGridViewHolder(parent);
        VerticalGridView gridView = gridViewHolder.getGridView();
        gridView.setVerticalMargin(100);
        gridView.setHorizontalMargin(45);
        gridView.setGravity(Gravity.CENTER_HORIZONTAL);

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        gridView.setLayoutParams(params);
        return gridViewHolder;
    }
}
