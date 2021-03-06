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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.stingray.qello.firetv.android.ui.constants.PreferencesConstants;


/**
 * Preferences helper class.
 */
public class Preferences {

    /**
     * Context.
     */
    private static Context sContext;

    /**
     * Set context.
     *
     * @param context Context.
     */
    public static void setContext(Context context) {

        if (sContext == null) {
            sContext = context;
        }
    }

    /**
     * Set string value to preferences.
     *
     * @param key   Key value.
     * @param value String value.
     */
    public static void setString(String key, String value) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(sContext);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Set boolean value to preferences.
     *
     * @param key   Key value.
     * @param value Boolean value.
     */
    public static void setBoolean(String key, boolean value) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(sContext);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Set long value to preferences.
     *
     * @param key   Key value.
     * @param value Long value.
     */
    public static void setLong(String key, long value) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(sContext);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong(key, value);
        editor.apply();
    }

    /**
     * Set string value to preferences.
     *
     * @param key Key value.
     * @return String value.
     */
    public static String getString(String key) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(sContext);
        return prefs.getString(key, "");
    }

    /**
     * Set boolean value to preferences.
     *
     * @param key Key value.
     * @return Boolean value.
     */
    public static boolean getBoolean(String key) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(sContext);
        return prefs.getBoolean(key, false);
    }

    /**
     * Set long value to preferences.
     *
     * @param key Key value.
     * @return Long value.
     */
    public static long getLong(String key) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(sContext);
        return prefs.getLong(key, 0);
    }

    /**
     * Checks if there is a preference stored for the given key.
     *
     * @param key The key to check.
     * @return True if there's a value stored for the key; false otherwise.
     */
    public static boolean containsPreference(String key) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(sContext);
        return prefs.contains(key);
    }

    public static void setLoggedOutState() {
        Preferences.setBoolean(PreferencesConstants.IS_LOGGED_IN, false);
        Preferences.setString(PreferencesConstants.ACCESS_TOKEN, null);
        Preferences.setLong(PreferencesConstants.ACCESS_TOKEN_EXPIRED_TIME_IN_MS, 0);
        Preferences.setString(PreferencesConstants.REFRESH_TOKEN, null);
        Preferences.setString(PreferencesConstants.SUBSCRIPTION_END_DATE, null);
        Preferences.setString(PreferencesConstants.STINGRAY_EMAIL, null);
        Preferences.setBoolean(PreferencesConstants.HAS_SUBSCRIPTION, false);
        Preferences.setString(PreferencesConstants.USER_TRACKING_ID, null);
    }

    public static void setLoggedInState(String sessionId, String accessToken, long accessTokenExpiryTime,
                                        String refreshToken, String subscriptionPlan, String userTrackingId,
                                        String subscriptionEnd, String email) {
        Preferences.setBoolean(PreferencesConstants.IS_LOGGED_IN, true);
        Preferences.setString(PreferencesConstants.SESSION_ID, sessionId);
        Preferences.setString(PreferencesConstants.REFRESH_TOKEN, refreshToken);
        Preferences.setString(PreferencesConstants.ACCESS_TOKEN, accessToken);
        Preferences.setLong(PreferencesConstants.ACCESS_TOKEN_EXPIRED_TIME_IN_MS, accessTokenExpiryTime);
        updateUserInfo(subscriptionPlan, subscriptionEnd, email, userTrackingId);
    }

    public static void updateUserInfo(String subscriptionPlan, String subscriptionEnd, String email, String userTrackingId) {
        boolean hasSubscription = subscriptionPlan != null && !subscriptionPlan.equalsIgnoreCase("NONE");
        Preferences.setBoolean(PreferencesConstants.IS_LOGGED_IN, true);
        Preferences.setString(PreferencesConstants.SUBSCRIPTION_END_DATE, subscriptionEnd);
        Preferences.setString(PreferencesConstants.STINGRAY_EMAIL, email);
        Preferences.setBoolean(PreferencesConstants.HAS_SUBSCRIPTION, hasSubscription);
        Preferences.setString(PreferencesConstants.USER_TRACKING_ID, userTrackingId);
    }
}
