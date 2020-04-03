/**
 * Copyright 2015-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *     http://aws.amazon.com/apache2.0/
 *
 * or in the "license"file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.stingray.qello.firetv.user_tracking;

import android.content.Context;
import android.os.Bundle;
import android.widget.FrameLayout;

/**
 * Ads interface.
 */
public interface ITracking {


    void init(Context context);

    public void trackAccountCreation(Context context);

    public void trackAccountLogin(Context context);

    public void screen(Context context, String pageId);

    public void trackPlaybackStarted(Context context, String sessionId, String assetId, long totalLength, long position);
    public void trackPlaybackPaused(Context context, String sessionId, String assetId, long totalLength, long position);
    public void trackPlaybackInterrupted(Context context, String sessionId, String assetId, long totalLength, long position, String error);
    public void trackPlaybackResumed(Context context, String sessionId, String assetId, long totalLength, long position);
    public void trackPlaybackCompleted(Context context, String sessionId, String assetId, long totalLength, long position);

    public void trackContentStarted(Context context, String sessionId, String assetId, long totalLength, long position);
    public void trackContentPlaying(Context context, String sessionId, String assetId, long totalLength, long position);
    public void trackContentCompleted(Context context, String sessionId, String assetId, long totalLength, long position);

}