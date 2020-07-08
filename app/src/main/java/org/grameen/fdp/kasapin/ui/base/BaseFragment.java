package org.grameen.fdp.kasapin.ui.base;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.utilities.CommonUtils;

import javax.script.ScriptEngine;

import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment implements BaseContract.View {
    public String TAG;
    private BaseActivity mActivity;
    private Unbinder mUnBinder;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        TAG = getClass().getSimpleName();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUp(view);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) context;
            this.mActivity = activity;
            activity.onFragmentAttached();
        }
    }

    @Override
    public void showLoading(){}

    @Override
    public void hideLoading() {
        if (mActivity.mProgressDialog != null && mActivity.mProgressDialog.isShowing()) {
            mActivity.mProgressDialog.cancel();
        }
    }

    @Override
    public void onError(String message) {
        if (mActivity != null) {
            mActivity.onError(message);
        }
    }

    @Override
    public void onError(@StringRes int resId) {
        if (mActivity != null) {
            mActivity.onError(resId);
        }
    }

    @Override
    public void onSuccess(String message) {
        if (mActivity != null) {
            mActivity.onSuccess(message);
        }
    }

    @Override
    public void showMessage(String message) {
        if (mActivity != null) {
            mActivity.showMessage(message);
        }
    }

    @Override
    public void showMessage(@StringRes int resId) {
        if (mActivity != null) {
            mActivity.showMessage(resId);
        }
    }

    @Override
    public boolean isNetworkConnected() {
        if (mActivity != null) {
            return mActivity.isNetworkConnected();
        }
        return false;
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    @Override
    public void hideKeyboard() {
        if (mActivity != null) {
            mActivity.hideKeyboard();
        }
    }


    public BaseActivity getBaseActivity() {
        return mActivity;
    }

    public AppDataManager getAppDataManager() {
        return mActivity.mAppDataManager;
    }

    public ScriptEngine getScriptEngine() {
        return mActivity.getScriptEngine();
    }

    public void setUnBinder(Unbinder unBinder) {
        mUnBinder = unBinder;
    }

    protected abstract void setUp(View view);

    @Override
    public void onDestroy() {
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
        super.onDestroy();
    }

    @Override
    public void showDialog(Boolean cancelable, String title, String message, DialogInterface.OnClickListener onPositiveButtonClickListener, String positiveText, DialogInterface.OnClickListener onNegativeButtonClickListener, String negativeText, int icon_drawable) {
        CommonUtils.showAlertDialog(mActivity.mAlertDialogBuilder, cancelable, title, message, onPositiveButtonClickListener, positiveText, onNegativeButtonClickListener,
                negativeText, icon_drawable);
    }

    @Override
    public void setLoadingMessage(String message) {
        if (mActivity.mProgressDialog != null && mActivity.mProgressDialog.isShowing())
            mActivity.mProgressDialog.setMessage(message);
    }

    public abstract void openNextActivity();

    public interface Callback {
        void onFragmentAttached();
        void onFragmentDetached(String tag);
    }
}