package org.grameen.fdp.kasapin.ui.fdpStatus;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * A login screen that offers login via email/password.
 */
public class FDPStatusActivity extends BaseActivity implements FDPStatusContract.View {

    @Inject
    FDPStatusPresenter mPresenter;


    @BindView(R.id.monitoring)
    View monitoring;

    @BindView(R.id.diagnostic)
    View diagnostics;


    public static Intent getStartIntent(Context context) {
        return new Intent(context, FDPStatusActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    protected void onDestroy() {
        mPresenter.dropView();
        super.onDestroy();
    }


    @Override
    public void openLoginActivityOnTokenExpire() {


    }


}