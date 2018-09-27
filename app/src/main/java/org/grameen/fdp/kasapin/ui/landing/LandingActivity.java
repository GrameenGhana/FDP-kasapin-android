package org.grameen.fdp.kasapin.ui.landing;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.main.MainActivity;

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
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_landing_page);

        getActivityComponent().inject(this);
        mPresenter.takeView(this);
        setUnBinder(ButterKnife.bind(this));



        findViewById(R.id.diagnostic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mPresenter.getAppDataManager().setIsMonitoringMode(false);
                mPresenter.openNextActivity();


            }
        });



        findViewById(R.id.monitoring).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.getAppDataManager().setIsMonitoringMode(true);
                mPresenter.openNextActivity();

            }
        });





     }



    @Override
    protected void onDestroy() {
        mPresenter.dropView();
        super.onDestroy();
    }



    @Override
    public void openNextActivity() {

        startActivity(new Intent(this, MainActivity.class));

    }

    @Override
    public void openLoginActivityOnTokenExpire() {


    }



    @Override
    public void onToggleFullScreenClicked(Boolean hideNavBar) {

    }




}