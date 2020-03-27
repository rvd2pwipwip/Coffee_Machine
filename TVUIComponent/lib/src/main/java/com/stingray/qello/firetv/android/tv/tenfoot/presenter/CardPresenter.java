/**
 * Copyright 2015-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *     http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stingray.qello.firetv.android.tv.tenfoot.presenter;

import com.stingray.qello.firetv.android.model.content.Content;
import com.stingray.qello.firetv.android.model.content.ContentContainer;
import com.stingray.qello.firetv.android.model.content.ViewMore;
import com.stingray.qello.firetv.android.utils.GlideHelper;
import com.stingray.qello.firetv.android.utils.Helpers;
import com.stingray.qello.firetv.android.tv.tenfoot.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.BaseCardView;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A CardPresenter is used to generate Views and bind Objects to them on demand.
 * It contains an Image CardView
 */
public class CardPresenter extends Presenter {

    private static final String TAG = CardPresenter.class.getSimpleName();

    private int mCardWidthPx = 120;
    private int mCardHeightPx = 160;

    private Drawable mDefaultCardImage;
    private Context mContext;
    private int cardViewType = BaseCardView.CARD_TYPE_MAIN_ONLY;

    public CardPresenter() {
    }

    public CardPresenter(int cardViewType, int cardWidthPx, int cardHeightPx) {
        this.cardViewType = cardViewType;
        mCardWidthPx = cardWidthPx;
        mCardHeightPx = cardHeightPx;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {

        mContext = parent.getContext();
        try {
            mDefaultCardImage = ContextCompat.getDrawable(mContext, R.drawable.default_poster);
        }
        catch (Resources.NotFoundException e) {
            Log.e(TAG, "Could not find resource ", e);
            throw e;
        }

        ImageCardView cardView = new ImageCardView(mContext) {
            @Override
            public void setSelected(boolean selected) {
                super.setSelected(selected);
            }
        };
        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);

        // Set the type and visibility of the info area.
        cardView.setCardType(cardViewType);
        cardView.setInfoVisibility(BaseCardView.CARD_REGION_VISIBLE_ALWAYS);
        cardView.setBackgroundColor(Color.TRANSPARENT);
        cardView.setInfoAreaBackgroundColor(Color.TRANSPARENT);

        TextView subtitle = (TextView) cardView.findViewById(R.id.content_text);
        if (subtitle != null) {
            subtitle.setEllipsize(TextUtils.TruncateAt.END);
        }

        return new ViewHolder(cardView);
    }

    public void test(){
    }


    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {

        ImageCardView cardView = (ImageCardView) viewHolder.view;

        int cardWidthDp = Helpers.convertPixelToDp(mContext, mCardWidthPx);
        int cardHeightDp = Helpers.convertPixelToDp(mContext, mCardHeightPx);

        if (item instanceof Content) {
            Content content = (Content) item;

            if (content.getCardImageUrl() != null) {

                // The word 'Title' is not logically correct in setTitleText,
                // the 'TitleText' is actually smaller text compared to 'ContentText',
                // so we are using TitleText to show subtitle and ContentText to show the
                // actual Title.
                cardView.setTitleText(content.getSubtitle());
                cardView.setContentText(content.getTitle());
                cardView.setMainImageDimensions(cardWidthDp, cardHeightDp);
                GlideHelper.loadImageIntoView(cardView.getMainImageView(),
                                              viewHolder.view.getContext(),
                                              content.getCardImageUrl(),
                                              new GlideHelper.LoggingListener<>(),
                                              R.drawable.default_poster);
            }
        }
        else if (item instanceof ContentContainer) {
            ContentContainer contentContainer = (ContentContainer) item;
            cardView.setContentText(contentContainer.getName());
            cardView.setMainImageDimensions(cardWidthDp, cardHeightDp);
            cardView.getMainImageView().setImageDrawable(mDefaultCardImage);
        }  else if (item instanceof ViewMore) {
            cardView.setMainImageDimensions(cardWidthDp, cardHeightDp);
            //TODO update poster
            cardView.getMainImageView().setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.view_more_poster));
            cardView.setBackgroundColor(Color.DKGRAY);
        }
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {

        ImageCardView cardView = (ImageCardView) viewHolder.view;
        // Remove references to images so that the garbage collector can free up memory.
        cardView.setBadgeImage(null);
        cardView.setMainImage(null);
    }
}

