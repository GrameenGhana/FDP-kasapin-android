package org.grameen.fdp.kasapin.ui.base;


import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
 import android.widget.TextView;
import android.widget.Toast;

import org.grameen.fdp.kasapin.FDPKasapin;
import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.prefs.AppPreferencesHelper;
import org.grameen.fdp.kasapin.di.component.ActivityComponent;
import org.grameen.fdp.kasapin.di.component.DaggerActivityComponent;
import org.grameen.fdp.kasapin.di.module.ViewModule;
import org.grameen.fdp.kasapin.ui.setup.LoginActivity;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.CommonUtils;
import org.grameen.fdp.kasapin.utilities.CustomToast;
import org.grameen.fdp.kasapin.utilities.KeyboardUtils;
import org.grameen.fdp.kasapin.utilities.NetworkUtils;

import javax.inject.Inject;
import javax.script.ScriptEngine;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by AangJnr on 18, September, 2018 @ 8:21 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */


public abstract class BaseActivity extends AppCompatActivity
        implements BaseContract.View, BaseFragment.Callback {

    private Unbinder mUnBinder;
    public String TAG;

    @Inject
    public AppDataManager mAppDataManager;

    @Inject
    public AlertDialog.Builder mAlertDialogBuilder;

    @Inject
    ProgressDialog mProgressDialog;

    @Inject
    ScriptEngine scriptEngine;

    ActivityComponent activityComponent;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getClass().getSimpleName();

        //Sets theme for if Diagnostic or Monitoring mode
         if (getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE).getBoolean(AppPreferencesHelper.PREF_KEY_IS_MONITORING_MODE, true))
            setTheme(R.style.AppTheme_Monitoring);
        overridePendingTransition(R.anim.right_slide, R.anim.slide_out_left);















    }


    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissionsSafely(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasPermission(String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void showLoading() {
        hideLoading();
        mProgressDialog = CommonUtils.showLoadingDialog(mProgressDialog);
    }


    @Override
    public void showLoading(String title, String message, boolean indeterminate, int icon, boolean cancelableOnTouchOutside) {
        hideLoading();
        mProgressDialog = CommonUtils.showLoadingDialog(mProgressDialog, title, message, indeterminate, 0, cancelableOnTouchOutside);

    }

    @Override
    public void hideLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                message, Snackbar.LENGTH_SHORT);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView
                .findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(this, R.color.white));
        snackbar.show();
    }

    @Override
    public void onError(String message) {
        if (message != null) {
            showSnackBar(message);
        } else {
            showSnackBar(getString(R.string.some_error));
        }
    }

    @Override
    public void onError(@StringRes int resId) {
        onError(getString(resId));
    }

    @Override
    public void showMessage(String message) {
        if (message != null) {
            CustomToast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            CustomToast.makeText(this, getString(R.string.some_error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showMessage(@StringRes int resId) {
        showMessage(getString(resId));
    }

    @Override
    public boolean isNetworkConnected() {
        return NetworkUtils.isNetworkConnected(getApplicationContext());
    }

    @Override
    public void onFragmentAttached() {

    }

    @Override
    public void onFragmentDetached(String tag) {

    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            KeyboardUtils.hideSoftInput(this);
        }
    }


    public void setUnBinder(Unbinder unBinder) {
        mUnBinder = unBinder;
    }

    @Override
    protected void onDestroy() {

        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
        super.onDestroy();
    }



    public ActivityComponent getActivityComponent() {
       if (activityComponent == null) {
            activityComponent = DaggerActivityComponent.builder()
                    .viewModule(new ViewModule(this))
                    .applicationComponent(FDPKasapin.getAppContext(this).getComponent())
                    .build();
        }
        return activityComponent;
    }


    @Override
    public void openLoginActivityOnTokenExpire() {

        //Todo Clear user credentials from shared prefs

        mAppDataManager.setUserAsLoggedOut();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);



    }

    public String getStringResources(int resource) {
        return getString(resource);
    }



    protected void logOut(){

        //Todo show dialog to confirm logout
        CommonUtils.showAlertDialog(mAlertDialogBuilder, true, getString(R.string.log_out), getString(R.string.log_out_rational),
                (dialogInterface, i) ->   {
                        mAppDataManager.setUserAsLoggedOut();
                         dialogInterface.dismiss();
                }, getString(R.string.yes), (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                }, getString(R.string.no), 0);
    }

    @Override
    public void showDialog(Boolean cancelable, String title, String message, DialogInterface.OnClickListener onPositiveButtonClickListener, String positiveText, DialogInterface.OnClickListener onNegativeButtonClickListener, String negativeText, int icon_drawable) {
        CommonUtils.showAlertDialog(mAlertDialogBuilder, cancelable, title, message, onPositiveButtonClickListener, positiveText, onNegativeButtonClickListener,
                negativeText, icon_drawable);

    }


    public AppDataManager getAppDataManager() {
        return mAppDataManager;
    }

    public ScriptEngine getScriptEngine() {
        return scriptEngine;
    }
}