package org.grameen.fdp.kasapin.ui.setup;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.landing.LandingActivity;
import org.grameen.fdp.kasapin.utilities.AppLogger;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements LoginContract.View {

    @Inject
    LoginPresenter  mPresenter;

    @BindView(R.id.email)
    EditText mEmailEditText;

    @BindView(R.id.password)
    EditText mPasswordEditText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                int flags = getWindow().getDecorView().getSystemUiVisibility();
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                getWindow().getDecorView().setSystemUiVisibility(flags);
                getWindow().setStatusBarColor(Color.WHITE);
            }

        setContentView(R.layout.activity_login);

        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));

        mPresenter.takeView(this);






    }

    @OnClick(R.id.sign_in_button)
    void onServerLoginClick(View v) {

        //Todo Perform login
       // mPresenter.makeLoginApiCall(null, null);


        mPresenter.openNextActivity();


     }




    @Override
    protected void onDestroy() {
         mPresenter.dropView();
        super.onDestroy();
    }


    @Override
    public void onLoginSuccessful() {

    }

    @Override
    public void openNextActivity() {

        AppLogger.i(TAG, "Opening Landing Page activity");

        startActivity(new Intent(this, LandingActivity.class));
        finish();


      }

    @Override
    public void openLoginActivityOnTokenExpire() {

    }

    @Override
    public void onToggleFullScreenClicked(Boolean hideNavBar) {

    }
}