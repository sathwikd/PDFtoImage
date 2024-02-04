package com.sathwikd.pdftoimage;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeUtilsBasic {

    private static final String SHARED_PREFS = "shared_prefs";
    private static final String THEME_STATUS = "theme_status";

    public static void loadTheme(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        int themeValue = sharedPreferences.getInt(THEME_STATUS, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(themeValue);
    }

    public static void saveTheme(Context context, int themeValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(THEME_STATUS, themeValue).apply();
    }
}
