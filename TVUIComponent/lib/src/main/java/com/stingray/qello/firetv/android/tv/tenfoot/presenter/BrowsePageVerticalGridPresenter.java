package com.stingray.qello.firetv.android.tv.tenfoot.presenter;

import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.support.v17.leanback.widget.VerticalGridView;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;

public class BrowsePageVerticalGridPresenter extends VerticalGridPresenter {

    public BrowsePageVerticalGridPresenter() {

        super();
    }

    @Override
    protected ViewHolder createGridViewHolder(ViewGroup parent) {
        ViewHolder gridViewHolder = super.createGridViewHolder(parent);
        VerticalGridView gridView = gridViewHolder.getGridView();
        gridView.setVerticalMargin(80);
        gridView.setHorizontalMargin(30);
        gridView.setGravity(Gravity.CENTER_HORIZONTAL);

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        gridView.setLayoutParams(params);

        int top = 30;//this is the new value for top padding
        int bottom = gridView.getPaddingBottom();
        int right = gridView.getPaddingRight();
        int left = gridView.getPaddingLeft();
        gridView.setPadding(left,top,right,bottom);

        return gridViewHolder;
    }
}
