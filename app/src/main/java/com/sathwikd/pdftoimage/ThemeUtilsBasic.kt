package com.sathwikd.pdftoimage

import android.app.Activity
import android.content.Context
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ComponentActivity
import androidx.core.view.WindowCompat

object ThemeUtilsBasic {

    private const val SHARED_PREFS = "shared_prefs_theme"
    private const val THEME_STATUS = "theme_status"

    fun applyThemeAndEdgeToEdge(activity: androidx.activity.ComponentActivity) {
        activity.enableEdgeToEdge()
        loadTheme(activity)
    }

    private fun loadTheme(context: Context) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val themeValue = sharedPreferences.getInt(THEME_STATUS, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(themeValue)
    }

    fun saveTheme(context: Context, themeValue: Int) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt(THEME_STATUS, themeValue).apply()
    }
}
