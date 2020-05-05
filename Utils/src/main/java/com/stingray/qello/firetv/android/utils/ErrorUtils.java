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
package com.stingray.qello.firetv.android.utils;

import com.stingray.qello.firetv.utils.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for errors/exception handling related to UI
 */
public class ErrorUtils {

    private static final String TAG = ErrorUtils.class.getSimpleName();

    /**
     * Possible error categories.
     */
    public enum ERROR_CATEGORY {
        NETWORK_ERROR,
        FEED_ERROR,
        AUTHENTICATION_ERROR,
        AUTHORIZATION_ERROR,
        PLAYER_ERROR,
        AUTHENTICATION_SYSTEM_ERROR
    }

    /**
     * Possible error button types.
     */
    public enum ERROR_BUTTON_TYPE {
        NETWORK_SETTINGS,
        DISMISS,
        LOGOUT,
        EXIT_APP
    }

    /**
     * Find the error message to display for the given error category.
     *
     * @param context        The context.
     * @param error_category The error category.
     * @return The error message string to display.
     */
    public static String getErrorMessage(Context context, ERROR_CATEGORY error_category) {

        String errorMessage = null;
        switch (error_category) {
            case NETWORK_ERROR:
                errorMessage = context.getResources().getString(R.string.Global_NetworkUnavailable);
                break;
            case FEED_ERROR:
                errorMessage = context.getResources().getString(R.string.Global_ServiceUnavailable);
                break;
            case AUTHENTICATION_ERROR:
                errorMessage =
                        context.getResources().getString(R.string.Login_InvalidCredential);
                break;
            case AUTHENTICATION_SYSTEM_ERROR:
                errorMessage = context.getString(R.string.authentication_system_error_message);
                break;
            case AUTHORIZATION_ERROR:
                errorMessage =
                        context.getResources().getString(R.string.Account_SubscribeText);
                break;
            case PLAYER_ERROR:
                errorMessage = context.getResources().getString(R.string.BaseViewController_ErrorMessage);
                break;
            default:
                break;
        }
        return errorMessage;
    }

    /**
     * Find the button labels to display for the given error category.
     *
     * @param context        The context.
     * @param error_category The error category.
     * @return Button labels list.
     */
    public static List<String> getButtonLabelsList(Context context, ERROR_CATEGORY error_category) {

        List<String> buttonLabelsList = new ArrayList<>();
        switch (error_category) {
            case NETWORK_ERROR:
                buttonLabelsList.add(
                        context.getResources().getString(R.string.Global_NetworkUnavailable));
                break;
            case FEED_ERROR:
                buttonLabelsList.add(context.getResources().getString(R.string.Global_ServiceUnavailable));
                break;
            case AUTHENTICATION_ERROR:
                buttonLabelsList.add(context.getResources().getString(R.string.Global_ButtonDismiss));
                break;
            case AUTHENTICATION_SYSTEM_ERROR:
                buttonLabelsList.add(context.getResources().getString(R.string.Global_ServiceUnavailable));
                break;
            case AUTHORIZATION_ERROR:
                buttonLabelsList.add(context.getResources().getString(R.string.Global_ButtonDismiss));
                buttonLabelsList.add(context.getResources().getString(R.string.Global_LogOut));
                break;
            case PLAYER_ERROR:
                buttonLabelsList.add(context.getResources().getString(R.string.Global_ButtonDismiss));
                break;
            default:
                break;
        }
        return buttonLabelsList;
    }

    /**
     * Returns the type of error button to display.
     *
     * @param activity   The activity for which button type needs to be set.
     * @param buttonText The button text for which button type needs to be set.
     * @return {@link com.stingray.qello.firetv.android.utils.ErrorUtils.ERROR_BUTTON_TYPE}
     */
    public static ERROR_BUTTON_TYPE getErrorButtonType(Activity activity, String buttonText) {

        if (buttonText.equalsIgnoreCase(activity.getResources()
                                                .getString(R.string.Global_NetworkUnavailable)
        )) {
            return ERROR_BUTTON_TYPE.NETWORK_SETTINGS;
        }
        else if (buttonText.equalsIgnoreCase(activity.getResources()
                                                     .getString(R.string.Global_ButtonDismiss))) {
            return ERROR_BUTTON_TYPE.DISMISS;
        }
        else if (buttonText.equalsIgnoreCase(activity.getResources()
                                                     .getString(R.string.Global_LogOut))) {
            return ERROR_BUTTON_TYPE.LOGOUT;
        }
        else if (buttonText.equalsIgnoreCase(activity.getResources()
                                                     .getString(R.string.Global_ServiceUnavailable))) {
            return ERROR_BUTTON_TYPE.EXIT_APP;
        }

        return ERROR_BUTTON_TYPE.DISMISS;
    }

    /**
     * Show the network settings activity in case of network failure
     */
    public static void showNetworkSettings(Activity activity) {

        Intent wifiSettingsDialog = new Intent(Settings.ACTION_WIFI_SETTINGS);
        wifiSettingsDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(wifiSettingsDialog);
    }
}
