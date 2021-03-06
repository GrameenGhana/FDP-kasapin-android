package org.grameen.fdp.kasapin.ui.base;


import android.content.DialogInterface;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.Window;

/**
 * Created by AangJnr on 19, September, 2018 @ 4:25 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */

public class BaseContract {

    public interface Presenter<T extends View> {

        /**
         * Binds presenter with a view when resumed. The Presenter will perform initialization here.
         *
         * @param view the view associated with this presenter
         */
        void takeView(T view);

        /**
         * Drops the reference to the view when destroyed
         */
        void dropView();

        void setUserAsLoggedOut();

        boolean isViewAttached();

        void openNextActivity();

        void onTokenExpire();

    }


    public interface View<T> {

        void showLoading();

        void setLoadingMessage(String message);

        void hideLoading();

        void showLoading(String title, String message, boolean indeterminate, @DrawableRes int icon, boolean cancelableOnTouchOutside);

        void openLoginActivityOnTokenExpire();

        void onError(@StringRes int resId);

        void onError(String message);

        void showMessage(String message);

        void showMessage(@StringRes int resId);

        boolean isNetworkConnected();

        void hideKeyboard();

        void toggleFullScreen(Boolean hideNavBar, Window W);

        void showDialog(Boolean cancelable, String title, String message, DialogInterface.OnClickListener onPositiveButtonClickListener, String positiveText, DialogInterface.OnClickListener onNegativeButtonClickListener, String negativeText, int icon_drawable);
    }
}
