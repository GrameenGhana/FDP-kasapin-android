package org.grameen.fdp.kasapin.ui.base;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.db.AppDatabase;
import org.grameen.fdp.kasapin.syncManager.LogRecorder;
import org.grameen.fdp.kasapin.utilities.CommonUtils;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;

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
    public void showLoading() {
    }

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

    @Override
    public void showLoading(String title, String message, boolean indeterminate, int icon, boolean cancelableOnTouchOutside) {
    }

    @Override
    public void openLoginActivityOnTokenExpire() {
    }

    @Override
    public void toggleFullScreen(Boolean hideNavBar, Window W) {
    }

    public BaseActivity getBaseActivity() {
        return mActivity;
    }

    protected AppDataManager getAppDataManager() {
        return mActivity.mAppDataManager;
    }

    public LogRecorder getLogRecorder(){
        return mActivity.logRecorder;
    }

    public void addDisposable(Disposable d){
        mActivity.getAppDataManager().getCompositeDisposable().add(d);
    }

    public AppDatabase getDatabaseManager(){
        return getAppDataManager().getDatabaseManager();
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

    public interface Callback {
        void onFragmentAttached();
        void onFragmentDetached(String tag);
    }
}