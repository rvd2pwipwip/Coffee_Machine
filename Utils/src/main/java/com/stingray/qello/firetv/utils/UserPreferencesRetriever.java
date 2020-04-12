package com.stingray.qello.firetv.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;

import java.util.Locale;

public class UserPreferencesRetriever {
    public static String getLanguageCode() {
        return Locale.getDefault().toLanguageTag();
    }

    @SuppressLint("HardwareIds")
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
