package org.grameen.fdp.kasapin.ui.login;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.Selection;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.karan.churi.PermissionManager.PermissionManager;

import org.grameen.fdp.kasapin.FDPKasapin;
import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.landing.LandingActivity;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.CommonUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements LoginContract.View {

    @Inject
    LoginPresenter mPresenter;

    @BindView(R.id.email)
    EditText mEmailView;

    @BindView(R.id.password)
    EditText mPasswordView;

    PermissionManager permissionManager;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        toggleFullScreen(false, getWindow());

        setContentView(R.layout.activity_login);

        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));

        mPresenter.takeView(this);

        mPasswordView.setOnEditorActionListener((textView, i, keyEvent) -> {

            if (i == EditorInfo.IME_ACTION_DONE) {
                onServerLoginClick();
                return true;
            }
            return false;


        });


        mPasswordView.setOnTouchListener((v, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mPasswordView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mPasswordView.requestFocus();
                    Selection.setSelection(mPasswordView.getText(), (mPasswordView.getText().toString().isEmpty()) ? 0 : mPasswordView.getText().length());

                    break;
                case MotionEvent.ACTION_UP:
                    mPasswordView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mPasswordView.requestFocus();
                    Selection.setSelection(mPasswordView.getText(), (mPasswordView.getText().toString().isEmpty()) ? 0 : mPasswordView.getText().length());

                    break;
            }

            return true;
        });


        permissionManager = new PermissionManager() {
            @Override
            public void ifCancelledAndCanRequest(Activity activity) {

                AppLogger.i("****** Can request");

                showDialog(false, getString(R.string.permissions_needed),
                        getString(R.string.permissions_needed_rationale), (dialogInterface, i) ->
                                permissionManager.checkAndRequestPermissions(LoginActivity.this),
                        getString(R.string.grant_permissions), (dialogInterface, i) -> supportFinishAfterTransition(),
                        getString(R.string.quit), 0);

            }

            @Override
            public void ifCancelledAndCannotRequest(Activity activity) {
                AppLogger.i("****** Cannot request");


                showDialog(false, getString(R.string.permissions_not_provided),
                        "Please provide the permission in Settings.", null,
                        "", (dialogInterface, i) -> supportFinishAfterTransition(), getString(R.string.quit), 0);

            }

        };


        new Handler().postDelayed(() -> showDialog(false, getString(R.string.hello),
                getString(R.string.provide_all_permissions_rationale), (dialogInterface, i) -> {
                    permissionManager.checkAndRequestPermissions(LoginActivity.this);
                }, getString(R.string.grant_permissions), (dialogInterface, i) -> finishAfterTransition(),
                getString(R.string.quit), 0), 500);


    }

    @OnClick(R.id.sign_in_button)
    void onServerLoginClick() {
        if (isNetworkConnected())
            validateData();
        else
            showMessage(R.string.no_internet_connection_available);
    }


    @Override
    protected void onDestroy() {
        mPresenter.dropView();
        super.onDestroy();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        //To get Granted Permission and Denied Permission
        ArrayList<String> granted = permissionManager.getStatus().get(0).granted;
        ArrayList<String> denied = permissionManager.getStatus().get(0).denied;


        if (granted.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            FDPKasapin.createNoMediaFile();
        }

        permissionManager.checkResult(requestCode, permissions, grantResults);

    }


    private boolean validateData() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {  //Todo Perform login
            mPresenter.makeLoginApiCall(mEmailView.getText().toString(), mPasswordView.getText().toString());
        }
        return cancel;
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return CommonUtils.isEmailValid(email);
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 6;
    }

}