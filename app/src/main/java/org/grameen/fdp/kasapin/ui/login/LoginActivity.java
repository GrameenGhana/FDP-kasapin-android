package org.grameen.fdp.kasapin.ui.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Selection;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.karan.churi.PermissionManager.PermissionManager;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.ui.base.BaseActivity;
import org.grameen.fdp.kasapin.ui.landing.LandingActivity;
import org.grameen.fdp.kasapin.ui.serverUrl.AddEditServerUrlActivity;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.CommonUtils;
import org.grameen.fdp.kasapin.utilities.FdpCallbacks;
import org.grameen.fdp.kasapin.utilities.FileUtils;
import org.grameen.fdp.kasapin.utilities.KeyboardUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements LoginContract.View, FdpCallbacks.UrlSelectedListener {
    @Inject
    LoginPresenter mPresenter;
    @BindView(R.id.email)
    EditText mEmailView;
    @BindView(R.id.password)
    EditText mPasswordView;
    @BindView(R.id.url_text)
    TextView serverUrlTextView;
    PermissionManager permissionManager;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(getWindow(), R.color.colorPrimary, true);
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_login);
        getActivityComponent().inject(this);
        setUnBinder(ButterKnife.bind(this));
        mPresenter.takeView(this);

        serverUrlTextView.setText((!TextUtils.isEmpty(getAppDataManager().getStringValue(AppConstants.SERVER_URL))
                ? getAppDataManager().getStringValue(AppConstants.SERVER_URL)
                : "N/A"));

        mPasswordView.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                onServerLoginClick();
                return true;
            }
            return false;
        });

        mPasswordView.setOnTouchListener((v, motionEvent) -> {
            KeyboardUtils.showSoftInput(mPasswordView, LoginActivity.this);

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
                showDialog(false, getString(R.string.hello),
                        getString(R.string.permissions_needed_rationale), (dialogInterface, i) ->
                                permissionManager.checkAndRequestPermissions(LoginActivity.this),
                        getString(R.string.grant_permissions), (dialogInterface, i) -> {
                            //supportFinishAfterTransition();
                            dialogInterface.dismiss();
                        },
                        getString(R.string.ok), 0);
            }

            @Override
            public void ifCancelledAndCannotRequest(Activity activity) {
                showDialog(false, getString(R.string.permissions_not_provided),
                        "Please provide the permission in Settings.", null,
                        "", (dialogInterface, i) -> {
                            //supportFinishAfterTransition();
                            dialogInterface.dismiss();
                        }, getString(R.string.ok), 0);
            }
        };

        permissionManager.checkAndRequestPermissions(this);

    }

    @OnClick(R.id.sign_in_button)
    void onServerLoginClick() {
        if (isNetworkConnected())
            validateData();
        else
            onError(R.string.no_internet_connection_available);
    }

    @Override
    protected void onDestroy() {
        mPresenter.dropView();
        super.onDestroy();
    }

    @Override
    public void openNextActivity() {
        startActivity(new Intent(this, LandingActivity.class));
        finish();
    }

    @Override
    public void openLoginActivityOnTokenExpire() {
    }

    @OnClick(R.id.url_text)
    void goToAddServerActivity() {
        startActivity(new Intent(this, AddEditServerUrlActivity.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //To get Granted Permission and Denied Permission
        ArrayList<String> granted = permissionManager.getStatus().get(0).granted;
        ArrayList<String> denied = permissionManager.getStatus().get(0).denied;
        if (granted.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            FileUtils.createNoMediaFile();
        }
        permissionManager.checkResult(requestCode, permissions, grantResults);
    }

    private void validateData() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a server url, if the user entered one.
        if (TextUtils.isEmpty(mAppDataManager.getStringValue(AppConstants.SERVER_URL))) {
            onError(getString(R.string.select_url_rational));
            return;
        }

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
        } else //Todo Perform login
            mPresenter.makeLoginApiCall(mEmailView.getText().toString(), mPasswordView.getText().toString());
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return CommonUtils.isEmailValid(email);
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 6;
    }

    @Override
    public void onUrlSelected(String url) {
    }
}