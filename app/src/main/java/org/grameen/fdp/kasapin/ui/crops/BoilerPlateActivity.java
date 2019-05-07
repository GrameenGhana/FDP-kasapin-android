package org.grameen.fdp.kasapin.ui.crops;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;

import butterknife.ButterKnife;


/**
 * A login screen that offers login via email/password.
 */
public class BoilerPlateActivity extends BaseActivity implements BoilerPlateContract.View {

    //@Inject
    BoilerPlatePresenter mPresenter;


    public static Intent getStartIntent(Context context) {
        return new Intent(context, BoilerPlateActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_farmer_details);
        setUnBinder(ButterKnife.bind(this));


        //getActivityComponent().inject(this);
        //mPresenter.takeView(this);
        //mAppDataManager = mPresenter.getAppDataManager();


    }


    @Override
    protected void onDestroy() {
        if (mPresenter != null)
            mPresenter.dropView();
        super.onDestroy();
    }


    @Override
    public void openNextActivity() {


    }

    @Override
    public void openLoginActivityOnTokenExpire() {

    }


}