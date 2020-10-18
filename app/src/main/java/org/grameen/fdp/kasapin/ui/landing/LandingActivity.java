package org.grameen.fdp.kasapin.ui.landing;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import org.grameen.fdp.kasapin.BuildConfig;
import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.db.entity.Farmer;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.main.MainActivity;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.FileUtils;
import org.grameen.fdp.kasapin.utilities.ImageUtil;
import org.grameen.fdp.kasapin.utilities.NetworkUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        setStatusBarColor(getWindow(), R.color.white, true);
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_landing_page);
        getActivityComponent().inject(this);
        mPresenter.takeView(this);
        setUnBinder(ButterKnife.bind(this));

        findViewById(R.id.diagnostic).setOnClickListener(v -> {
          getAppDataManager().setIsMonitoringMode(false);
            openMainActivity();
        });
        findViewById(R.id.monitoring).setOnClickListener(v -> {
            getAppDataManager().setIsMonitoringMode(true);
            openMainActivity();
        });
        FileUtils.createNoMediaFile();

        if(!getAppDataManager().getBooleanValue(AppConstants.IS_FARMER_IMAGES_CACHED))
            new Handler().postDelayed(() -> mPresenter.getFarmers(), 500);
    }

    @Override
    protected void onDestroy() {
        mPresenter.dropView();
        super.onDestroy();
    }



    @Override
    public void cacheFarmerImages(List<Farmer> farmers) {
        List<Farmer> updatedData = new ArrayList<>();
        for (Farmer farmer : farmers) {
            if (farmer.getImageBase64() != null) {
                String imageUri  = convertBase64ToUrl(farmer.getImageBase64(),farmer.getCode() + ".jpg");
                farmer.setImageLocalUrl(imageUri);
                 updatedData.add(farmer);
            }
        }

        mPresenter.updateFarmerData(updatedData);
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