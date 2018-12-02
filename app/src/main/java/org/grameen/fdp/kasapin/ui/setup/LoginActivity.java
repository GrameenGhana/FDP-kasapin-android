package org.grameen.fdp.kasapin.ui.setup;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.karan.churi.PermissionManager.PermissionManager;

import org.grameen.fdp.kasapin.FDPKasapin;
import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.exceptions.ParserException;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.landing.LandingActivity;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.ComplexCalculationParser;
import org.grameen.fdp.kasapin.utilities.Tokenizer;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.script.ScriptException;

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

    PermissionManager permissionManager;


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


        permissionManager = new PermissionManager() {
            @Override
            public void ifCancelledAndCanRequest(Activity activity) {
                // Do Customized operation if permission is cancelled without checking "Don't ask again"
                // Use super.ifCancelledAndCanRequest(activity); or Don't override this method if not in use
                showDialog(false, getString(R.string.permissions_needed),
                        getString(R.string.permissions_needed_rationale), (dialogInterface, i) ->
                                permissionManager.checkAndRequestPermissions(LoginActivity.this),
                        getString(R.string.grant_permissions), (dialogInterface, i) -> supportFinishAfterTransition(),
                        getString(R.string.quit), 0);

            }

            @Override
            public void ifCancelledAndCannotRequest(Activity activity) {
                // Do Customized operation if permission is cancelled with checking "Don't ask again"
                // Use super.ifCancelledAndCannotRequest(activity); or Don't override this method if not in use


                showDialog(false, getString(R.string.permissions_not_provided),
                        getString(R.string.permissions_not_provided_rationale), null,
                        "", (dialogInterface, i) ->
                                supportFinishAfterTransition(), getString(R.string.quit), 0);

            }

        };

        //Request permissions before login into the app

        new Handler().postDelayed(() -> showDialog(false, getString(R.string.hello),
                getString(R.string.provide_all_permissions_rationale), (dialogInterface, i) -> {

                    permissionManager.checkAndRequestPermissions(this);


                }, getString(R.string.grant_permissions), (dialogInterface, i) -> finishAfterTransition(),
                getString(R.string.quit), 0), 1000);

        



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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        permissionManager.checkResult(requestCode, permissions, grantResults);
        //To get Granted Permission and Denied Permission
        ArrayList<String> granted = permissionManager.getStatus().get(0).granted;
        ArrayList<String> denied = permissionManager.getStatus().get(0).denied;


        if (granted.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            FDPKasapin.createNoMediaFile();
        }
    }
}