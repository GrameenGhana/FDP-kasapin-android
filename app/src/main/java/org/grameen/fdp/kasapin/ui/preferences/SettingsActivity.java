package org.grameen.fdp.kasapin.ui.preferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.EditTextPreference;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import org.grameen.fdp.kasapin.BuildConfig;
import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.landing.LandingActivity;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.CommonUtils;
import org.grameen.fdp.kasapin.utilities.CustomToast;

public class SettingsActivity extends BaseActivity {

    static AlertDialog.Builder mAlertDialogBuilder;
    static String oldUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.settings);

        oldUrl = PreferenceManager.getDefaultSharedPreferences(this).getString(AppConstants.SERVER_URL, BuildConfig.END_POINT);

        if(getSupportActionBar() != null)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();


        mAlertDialogBuilder = new AlertDialog.Builder(this, R.style.AppDialog);


        onBackClicked();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

        }




        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            bindPreferenceSummaryToValue(findPreference(getString(R.string.server_url_key)));

        }



    }





    private static void bindPreferenceSummaryToValue(Preference preference) {

        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, oldUrl);

    }


    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = (preference, newValue) -> {
        String stringValue = newValue.toString().trim().replace(" ", "");

        AppLogger.e("Settings Activity", "VALUE IS >>>>>> " + stringValue);


        if (preference instanceof EditTextPreference) {
            if (preference.getKey().equals(AppConstants.SERVER_URL)) {
                // update the changed url to summary filed

                if(!URLUtil.isValidUrl(stringValue)) {

                    PreferenceManager.getDefaultSharedPreferences(preference.getContext()).edit().putString(preference.getKey(), BuildConfig.END_POINT).apply();

                    preference.setSummary( PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), BuildConfig.END_POINT));
                    CustomToast.makeText(preference.getContext(), R.string.enter_valid_url, Toast.LENGTH_LONG).show();

                }
                else {

                    //PreferenceManager.getDefaultSharedPreferences(preference.getContext()).edit().putString(preference.getKey(), stringValue).apply();
                    preference.setSummary(stringValue);

                    if(!oldUrl.equals(stringValue))
                    CommonUtils.showAlertDialog(mAlertDialogBuilder, false, "Restart App", "Server url has changed. Please restart the app to take effect.",
                            (d, w) -> {

                        d.dismiss();

                                new Handler().post(() -> {
                                    System.exit(0);
                                    android.os.Process.killProcess(android.os.Process.myPid());

                                    new Handler().postDelayed(() -> {
                                        Intent i = new Intent( preference.getContext(), LandingActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        preference.getContext().startActivity(i);

                                    }, 2000);
                                });
                            }, preference.getContext().getString(R.string.ok), null, "", 0);

                }
            }
        }
        return true;
    };



}