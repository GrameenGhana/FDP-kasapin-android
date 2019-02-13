package org.grameen.fdp.kasapin.ui.landing;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.balsikandar.crashreporter.CrashReporter;
import com.balsikandar.crashreporter.ui.CrashReporterActivity;
import com.balsikandar.crashreporter.utils.CrashUtil;
import com.balsikandar.crashreporter.utils.FileUtils;
import com.crashlytics.android.Crashlytics;

import org.grameen.fdp.kasapin.BuildConfig;
import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.exceptions.ParserException;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.main.MainActivity;
import org.grameen.fdp.kasapin.ui.test.CrashTestingActivity;
import org.grameen.fdp.kasapin.utilities.ComplexCalculationParser;
import org.grameen.fdp.kasapin.utilities.CustomToast;
import org.grameen.fdp.kasapin.utilities.NetworkUtils;
import org.grameen.fdp.kasapin.utilities.Tokenizer;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
/**
 * A login screen that offers login via email/password.
 */
public class LandingActivity extends BaseActivity implements LandingContract.View {

    @Inject
    LandingPresenter mPresenter;


    @BindView(R.id.monitoring)
    View monitoring;

    @BindView(R.id.diagnostic)
    View diagnostics;


    public static Intent getStartIntent(Context context) {
        return new Intent(context, LandingActivity.class);
     }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int flags = getWindow().getDecorView().getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            getWindow().getDecorView().setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }*/


        toggleFullScreen(false, getWindow());

        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_landing_page);

        getActivityComponent().inject(this);
        mPresenter.takeView(this);
        setUnBinder(ButterKnife.bind(this));


        findViewById(R.id.diagnostic).setOnClickListener(v -> {

                mPresenter.getAppDataManager().setIsMonitoringMode(false);
                mPresenter.openNextActivity();

        });


        findViewById(R.id.monitoring).setOnClickListener(v -> {

            mPresenter.getAppDataManager().setIsMonitoringMode(true);
            mPresenter.openNextActivity();

        });







        org.grameen.fdp.kasapin.utilities.FileUtils.createNoMediaFile();


     }



    @Override
    protected void onDestroy() {
        mPresenter.dropView();
        super.onDestroy();
    }



    @Override
    public void openMainActivity() {

        startActivity(new Intent(this, MainActivity.class));

    }

    @Override
    public void openLoginActivityOnTokenExpire() {


    }



    public void showPopUp(@Nullable final View v) {

        PopupMenu menu = new PopupMenu(this, v);
        menu.getMenuInflater().inflate(R.menu.activity_landing_page_menu, menu.getMenu());

        if (BuildConfig.DEBUG) {
            menu.getMenu().findItem(R.id.crash_reports).setVisible(true);
        }

        menu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {

                case R.id.upload_logs:

                    //Todo upload logs to server
                    if (NetworkUtils.isNetworkConnected(LandingActivity.this))
                        mPresenter.uploadLogsToServer();
                    else showMessage("Please make sure you have an active internet connection");

                    return true;

                case R.id.crash_reports:

                    startActivity(new Intent(LandingActivity.this, CrashTestingActivity.class));
                    return true;

                case R.id.export_database:

                    int value = mPresenter.getAppDataManager().backupRestoreDatabase(true);

                    if (value == 1) showMessage(R.string.database_exported);
                    else if (value == 0) showMessage(R.string.no_database_data_found);
                    else showMessage(R.string.database_export_error);

                    return true;

                case R.id.import_database:

                    showDialog(true, getString(R.string.import_data_question), getString(R.string.import_data_rationale),
                            (dialog, which) -> {
                                int value1;
                                value1 = mPresenter.getAppDataManager().backupRestoreDatabase(false);

                                if (value1 == 1) showMessage(R.string.database_imported);

                                else if (value1 == 0) showMessage(R.string.no_database_data_found);

                                else showMessage(R.string.database_import_error);

                            }, getString(R.string.yes), (dialog, which) -> dialog.dismiss(), getString(R.string.no), 0);
                    return true;


                default:
                    return false;
            }
        });

        menu.show();

    }








}