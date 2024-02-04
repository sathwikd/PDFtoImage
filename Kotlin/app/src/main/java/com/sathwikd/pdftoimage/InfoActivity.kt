package com.sathwikd.pdftoimage

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.button.MaterialButtonToggleGroup

class InfoActivity : AppCompatActivity() {

    private val lightThemeValue = AppCompatDelegate.MODE_NIGHT_NO
    private val darkThemeValue = AppCompatDelegate.MODE_NIGHT_YES
    private val defaultThemeValue = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtilsBasic.loadTheme(this)
        setContentView(R.layout.activity_info)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val callback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@InfoActivity,MainActivity::class.java))
                finish()
                isEnabled = false
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

        val materialButtonTHEME: MaterialButtonToggleGroup = findViewById(R.id.MaterialButtonToggleTheme)
        materialButtonTHEME.check(loadToggleData())

        materialButtonTHEME.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.buttonLight -> {
                        delegate.localNightMode = lightThemeValue
                        ThemeUtilsBasic.saveTheme(this, lightThemeValue)
                    }
                    R.id.buttonDark -> {
                        delegate.localNightMode = darkThemeValue
                        ThemeUtilsBasic.saveTheme(this, darkThemeValue)
                    }
                    else -> {
                        delegate.localNightMode = defaultThemeValue
                        ThemeUtilsBasic.saveTheme(this, defaultThemeValue)
                    }
                }
            }
        }

    }

    private fun loadToggleData(): Int {
        return when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_NO -> R.id.buttonLight
            AppCompatDelegate.MODE_NIGHT_YES -> R.id.buttonDark
            else -> R.id.buttonDefault
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        return true
    }
}