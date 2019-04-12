package org.grameen.fdp.kasapin.ui.detailedYearMonthlyView;


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
public class DetailedMonthActivity extends BaseActivity implements DetailedMonthContract.View {

    @Inject
    DetailedMonthPresenter mPresenter;


    @BindView(R.id.monitoring)
    View monitoring;

    @BindView(R.id.diagnostic)
    View diagnostics;


    public static Intent getStartIntent(Context context) {
        return new Intent(context, DetailedMonthActivity.class);
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