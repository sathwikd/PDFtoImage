package com.sathwikd.pdftoimage;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.button.MaterialButtonToggleGroup;

public class InfoActivity extends AppCompatActivity {

    private static final int lightThemeValue = AppCompatDelegate.MODE_NIGHT_NO;
    private static final int darkThemeValue = AppCompatDelegate.MODE_NIGHT_YES;
    private static final int defaultThemeValue = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtilsBasic.loadTheme(this);
        setContentView(R.layout.activity_info);

        MaterialButtonToggleGroup materialButtonTHEME = findViewById(R.id.MaterialButtonToggleTheme);
        materialButtonTHEME.check(loadToggleData());

        materialButtonTHEME.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    if (checkedId == R.id.buttonLight) {
                        getDelegate().setLocalNightMode(lightThemeValue);
                        ThemeUtilsBasic.saveTheme(InfoActivity.this,lightThemeValue);
                    }
                    else if (checkedId == R.id.buttonDark) {
                        getDelegate().setLocalNightMode(darkThemeValue);
                        ThemeUtilsBasic.saveTheme(InfoActivity.this,darkThemeValue);
                    }
                    else {
                        getDelegate().setLocalNightMode(defaultThemeValue);
                        ThemeUtilsBasic.saveTheme(InfoActivity.this,defaultThemeValue);
                    }
                }
            }
        });

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private int loadToggleData() {
        switch (AppCompatDelegate.getDefaultNightMode()) {
            case (int) AppCompatDelegate.MODE_NIGHT_NO:
                return R.id.buttonLight;

            case (int) AppCompatDelegate.MODE_NIGHT_YES:
                return R.id.buttonDark;

            default:
                return R.id.buttonDefault;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(InfoActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(InfoActivity.this,MainActivity.class));
        finish();
        return true;
    }
}