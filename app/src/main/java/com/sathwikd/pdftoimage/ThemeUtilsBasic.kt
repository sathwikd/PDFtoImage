package com.sathwikd.pdftoimage

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object ThemeUtilsBasic {

    private const val SHARED_PREFS = "shared_prefs_theme"
    private const val THEME_STATUS = "theme_status"

    fun loadTheme(context: Context) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val themeValue = sharedPreferences.getInt(THEME_STATUS, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(themeValue)
    }

    fun saveTheme(context: Context, themeValue: Int) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt(THEME_STATUS, themeValue).apply()
    }
}