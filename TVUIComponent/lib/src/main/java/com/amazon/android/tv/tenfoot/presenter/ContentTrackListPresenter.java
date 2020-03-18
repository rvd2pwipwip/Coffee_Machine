/**
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
package com.amazon.android.tv.tenfoot.presenter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v17.leanback.widget.Presenter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.amazon.android.configuration.ConfigurationManager;
import com.amazon.android.model.content.Content;
import com.amazon.android.model.content.ContentWithTracks;
import com.amazon.android.model.content.Track;
import com.amazon.android.tv.tenfoot.R;
import com.amazon.android.tv.tenfoot.base.TenFootApp;
import com.amazon.android.ui.constants.ConfigurationConstants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

/**
 * An {@link Presenter} for rendering the detailed description of an item.
 * The description needs to have a title and subtitle.
 */
public class ContentTrackListPresenter extends Presenter {

    private final static String TAG = ContentTrackListPresenter.class.getSimpleName();

    private Context mContext;

    /**
     * View holder for the details description. It contains title, subtitle, and body text views.
     */
    public static class ViewHolder extends Presenter.ViewHolder {

        private final TextView mTitle;
        private final TextView mBody;
        private final TableLayout mContentTrackListTable;

        public ViewHolder(final View view) {

            super(view);
            mTitle = (TextView) view.findViewById(R.id.details_description_title);
            mBody = (TextView) view.findViewById(R.id.details_description_body);
            mContentTrackListTable = (TableLayout) view.findViewById(R.id.content_track_list_table);
        }

        public TextView getTitle() {

            return mTitle;
        }

        public TextView getBody() {
            return mBody;
        }

        public TableLayout getContentTrackListTable() {
            return mContentTrackListTable;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent) {

        Log.v(TAG, "onCreateViewHolder called.");

        mContext = parent.getContext();

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.details_content_track_list_layout, parent,
                        false);

        return new ViewHolder(view);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {

        Log.v(TAG, "onBindViewHolder called.");
        ViewHolder customViewHolder = (ViewHolder) viewHolder;
        onBindDescription(customViewHolder, item);
    }

    private void onBindDescription(ViewHolder viewHolder, Object item) {

        Log.v(TAG, "onBindDescription called.");
        ContentWithTracks contentWithTracks = (ContentWithTracks) item;

        if (contentWithTracks != null) {
            populateViewHolder(viewHolder, contentWithTracks);
        } else {
            Log.e(TAG, "Content is null in onBindDescription");
        }
    }

    private void populateViewHolder(ViewHolder viewHolder, ContentWithTracks contentWithTracks) {
        Content content = contentWithTracks.getContent();

        ConfigurationManager config = ConfigurationManager.getInstance(TenFootApp.getInstance());

        viewHolder.getTitle().setEllipsize(TextUtils.TruncateAt.END);
        viewHolder.getTitle().setSingleLine();

        String title = content.getSubtitle() + " - " + content.getTitle();
        String body = contentWithTracks.getTracks().size() + " tracks";

        viewHolder.getTitle().setText(title);
        CalligraphyUtils.applyFontToTextView(TenFootApp.getInstance(), viewHolder.getTitle(),
                config.getTypefacePath(ConfigurationConstants.LIGHT_FONT));

        viewHolder.getBody().setText(body);

        viewHolder.getContentTrackListTable().removeAllViews();

        for (Track track: contentWithTracks.getTracks()) {
            viewHolder.getContentTrackListTable()
                    .addView(createTrackListTableRow(track));
        }
    }

    public TableRow createTrackListTableRow(Track track) {
        TableLayout.LayoutParams tableLayoutParams =
                new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT, 1);

        TableRow tableRow = new TableRow(mContext);
        tableRow.setId(View.generateViewId());
        tableRow.setLayoutParams(tableLayoutParams);

        RelativeLayout trackInfoLayout = createTrackInfoLayout(track);

        Button addToPlaylistButton = createBaseTrackListTableButton(0.07F);
        addToPlaylistButton.setTextSize( mContext.getResources().getDimension(R.dimen.action_text));
        addToPlaylistButton.setText("+");

        Button addToFavoritesButton = createBaseTrackListTableButton(0.07F);
        addToFavoritesButton.setTextSize( mContext.getResources().getDimension(R.dimen.content_track_info_size));
        addToFavoritesButton.setText("Like");

        tableRow.setMinimumHeight(0);

        tableRow.addView(trackInfoLayout);
        tableRow.addView(addToPlaylistButton);
        tableRow.addView(addToFavoritesButton);

        return tableRow;
    }

    public RelativeLayout createTrackInfoLayout(Track track) {
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.US);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String duration = formatter.format(new Date(track.getDuration()));

        TableRow.LayoutParams trackInfoLayoutParams =
                new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 0.86F);

        RelativeLayout relativeLayout = new RelativeLayout(mContext);
        int padding = mContext.getResources().getDimensionPixelSize(R.dimen.content_track_list_btn_padding);
        relativeLayout.setPadding(padding, padding, padding, padding);
        relativeLayout.setBackground(mContext.getResources().getDrawable(R.drawable.button_bg_stroke));
        relativeLayout.setLayoutParams(trackInfoLayoutParams);
        relativeLayout.setClickable(true);
        relativeLayout.setFocusable(true);

        RelativeLayout.LayoutParams titleViewlayoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        titleViewlayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        titleViewlayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);

        TextView titleView = new TextView(mContext);
        titleView.setId(View.generateViewId());
        titleView.setText(track.getTitle());
        titleView.setLayoutParams(titleViewlayoutParams);

        RelativeLayout.LayoutParams subtitleViewlayoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        subtitleViewlayoutParams.addRule(RelativeLayout.BELOW, titleView.getId());

        TextView subtitleView = new TextView(mContext);
        subtitleView.setText(track.getSubtitle());
        subtitleView.setLayoutParams(subtitleViewlayoutParams);

        RelativeLayout.LayoutParams durationViewlayoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        durationViewlayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        TextView durationView = new TextView(mContext);
        durationView.setText(duration);
        durationView.setLayoutParams(durationViewlayoutParams);

        relativeLayout.addView(titleView);
        relativeLayout.addView(subtitleView);
        relativeLayout.addView(durationView);

        return relativeLayout;
    }


    public Button createBaseTrackListTableButton(float weight) {
        Resources resources = mContext.getResources();

        Button tableButton = new Button(mContext);
        int padding = resources.getDimensionPixelSize(R.dimen.content_track_list_btn_padding);
        tableButton.setPadding(padding, padding, padding, padding);
        tableButton.setBackground(mContext.getResources().getDrawable(R.drawable.button_bg_stroke));
        tableButton.setTextColor(resources.getColor(R.color.button_text));
        tableButton.setAllCaps(false);

        TableRow.LayoutParams trackInfoButtonLayoutParams =
                new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, weight);
        tableButton.setLayoutParams(trackInfoButtonLayoutParams);

        return tableButton;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewAttachedToWindow(Presenter.ViewHolder holder) {

        Log.v(TAG, "onViewAttachedToWindow called.");
        ViewHolder customViewHolder = (ViewHolder) holder;
        super.onViewAttachedToWindow(customViewHolder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewDetachedFromWindow(Presenter.ViewHolder holder) {

        Log.v(TAG, "onViewDetachedFromWindow called.");
        super.onViewDetachedFromWindow(holder);
    }

}
