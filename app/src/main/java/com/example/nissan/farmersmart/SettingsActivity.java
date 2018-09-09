package com.example.nissan.farmersmart;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import static android.content.pm.PackageManager.GET_META_DATA;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setLocale();
        resetTitles();
        setContentView(R.layout.activity_settings);
    }
    public static class FarmersPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);
        }

    }



    private void setLocale(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String locale = sharedPrefs.getString(getString(R.string.settings_language_key),
                getString(R.string.settings_language_default));
//        Locale current = getResources().getConfiguration().locale;
//        if (!current.toString().equals(locale)){
            LocaleManager.setLocale(this,locale);
//        }
    }
    private void resetTitles() {
        try {
            ActivityInfo info = getPackageManager().getActivityInfo(getComponentName(), GET_META_DATA);
            if (info.labelRes != 0) {
                setTitle(info.labelRes);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ChangeLangContextWrapper.wrap(newBase));
    }
}
