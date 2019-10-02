package org.grameen.fdp.kasapin.ui.base;


import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.grameen.fdp.kasapin.FDPKasapin;
import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.data.DataManager;
import org.grameen.fdp.kasapin.data.db.entity.FormAndQuestions;
import org.grameen.fdp.kasapin.data.db.entity.FormAnswerData;
import org.grameen.fdp.kasapin.data.db.entity.Question;
import org.grameen.fdp.kasapin.data.db.entity.RealFarmer;
import org.grameen.fdp.kasapin.data.prefs.AppPreferencesHelper;
import org.grameen.fdp.kasapin.di.component.ActivityComponent;
import org.grameen.fdp.kasapin.di.component.DaggerActivityComponent;
import org.grameen.fdp.kasapin.di.module.ViewModule;
import org.grameen.fdp.kasapin.ui.addFarmer.AddEditFarmerActivity;
import org.grameen.fdp.kasapin.ui.familyMembers.FamilyMembersActivity;
import org.grameen.fdp.kasapin.ui.farmerProfile.FarmerProfileActivity;
import org.grameen.fdp.kasapin.ui.login.LoginActivity;
import org.grameen.fdp.kasapin.utilities.AppConstants;
import org.grameen.fdp.kasapin.utilities.AppLogger;
import org.grameen.fdp.kasapin.utilities.CommonUtils;
import org.grameen.fdp.kasapin.utilities.CustomToast;
import org.grameen.fdp.kasapin.utilities.KeyboardUtils;
import org.grameen.fdp.kasapin.utilities.NetworkUtils;
import org.grameen.fdp.kasapin.utilities.ScreenUtils;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import javax.inject.Inject;
import javax.script.ScriptEngine;

import butterknife.Unbinder;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static org.grameen.fdp.kasapin.ui.farmerProfile.FarmerProfileActivity.familyMembersFormPosition;
import static org.grameen.fdp.kasapin.utilities.AppConstants.ROOT_DIR;

/**
 * Created by AangJnr on 18, September, 2018 @ 8:21 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */


public abstract class BaseActivity extends AppCompatActivity
        implements BaseContract.View, BaseFragment.Callback {

    public static String DEVICE_ID;
    public static Gson gson = new Gson();
    public static List<FormAndQuestions> FORM_AND_QUESTIONS;
    public static List<FormAndQuestions> PLOT_FORM_AND_QUESTIONS;
    public static List<FormAndQuestions> FILTERED_FORMS;
    public static int CURRENT_FORM_POSITION;
    public static int CURRENT_PAGE;
    public static boolean IS_TABLET;
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
    private Unbinder mUnBinder;

    public static Gson getGson() {
        return gson;
    }

    public static void runLayoutAnimation(final GridView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_fall_down);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.scheduleLayoutAnimation();
    }

    public static void runLayoutAnimation(final ListView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_fall_down);
        recyclerView.setLayoutAnimation(controller);
        //recyclerView.getAdapter().notifyAll();
        recyclerView.scheduleLayoutAnimation();
    }


    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getClass().getSimpleName();

        //Sets theme for if Diagnostic or Monitoring mode
        if (getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE).getBoolean(AppPreferencesHelper.PREF_KEY_IS_MONITORING_MODE, true))
            setTheme(R.style.AppTheme_Monitoring);


        overridePendingTransition(R.anim.right_slide, R.anim.slide_out_left);


        IS_TABLET = ScreenUtils.isTablet(this);
        DEVICE_ID = CommonUtils.getDeviceId(this);

    }

    @Override
    public void toggleFullScreen(Boolean hideNavBar, Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int flags = getWindow().getDecorView().getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            window.getDecorView().setSystemUiVisibility(flags);
            window.setStatusBarColor(Color.WHITE);
        }
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
        runOnUiThread(() -> {


            hideLoading();
            mProgressDialog = CommonUtils.showLoadingDialog(mProgressDialog);
        });
    }

    @Override
    public void setLoadingMessage(String message) {
        runOnUiThread(() -> {
            if (mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.setMessage(message);

        });
    }

    @Override
    public void showLoading(String title, String message, boolean indeterminate, int icon, boolean cancelableOnTouchOutside) {
        hideLoading();
        mProgressDialog = CommonUtils.showLoadingDialog(mProgressDialog, title, message, indeterminate, icon, cancelableOnTouchOutside);

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
                .findViewById(R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(this, R.color.white));
        snackbar.show();
    }

    @Override
    public void onError(String message) {
        runOnUiThread(() -> {
            if (message != null) {
                showSnackBar(message);
            } else {
                showSnackBar(getString(R.string.some_error));
            }
        });

    }

    @Override
    public void onError(@StringRes int resId) {
        onError(getString(resId));
    }

    @Override
    public void showMessage(String message) {

        runOnUiThread(() -> {

            if (message != null) {
                CustomToast.makeText(BaseActivity.this, message, Toast.LENGTH_LONG).show();
            } else {
                CustomToast.makeText(BaseActivity.this, getString(R.string.some_error), Toast.LENGTH_LONG).show();
            }

        });
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

        showDialog(true, "Re-authenticate", "Please login again to download updated data", (dialog, which) -> {
            dialog.dismiss();
            mAppDataManager.setUserLoggedInMode(DataManager.LoggedInMode.LOGGED_OUT);

            Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                startActivity(intent);
                finish();
            }, 1000);

        }, "OK", (dialog, which) -> dialog.cancel(), "CANCEL", 0);

    }

    public String getStringResources(int resource) {
        return getString(resource);
    }

    protected void logOut() {
        //Todo show dialog to confirm logout
        CommonUtils.showAlertDialog(mAlertDialogBuilder, true, getString(R.string.log_out), getString(R.string.log_out_rational),
                (dialogInterface, i) -> {


                    getAppDataManager().getCompositeDisposable().add(Completable.fromAction(() -> mAppDataManager.setUserAsLoggedOut())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> {
                                dialogInterface.dismiss();

                                Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                            }, throwable -> {
                                throwable.printStackTrace();
                                showMessage(R.string.error_has_occurred);
                            }));






/*

                    Completable.fromAction(() -> mAppDataManager.setUserAsLoggedOut()).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                dialogInterface.dismiss();

                                Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                showMessage(R.string.error_has_occurred);
                            }
                        });*/


                }, getString(R.string.yes), (dialogInterface, i) -> dialogInterface.dismiss(), getString(R.string.no), 0);
    }

    @Override
    public void showDialog(Boolean cancelable, String title, String message, DialogInterface.OnClickListener onPositiveButtonClickListener, String positiveText, DialogInterface.OnClickListener onNegativeButtonClickListener, String negativeText, int icon_drawable) {
        CommonUtils.showAlertDialog(mAlertDialogBuilder, cancelable, title, message, onPositiveButtonClickListener, positiveText, onNegativeButtonClickListener,
                negativeText, icon_drawable);

    }







    protected Toolbar setToolbar(String title) {
        Toolbar toolbar = null;
        try {
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_white_24dp);

            toolbar.setNavigationOnClickListener((v) -> {
                onBackPressed();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toolbar;
    }

    public AppDataManager getAppDataManager() {
        return mAppDataManager;
    }

    public ScriptEngine getScriptEngine() {
        return scriptEngine;
    }

    public void showNoDataView() {
        findViewById(R.id.place_holder).setVisibility(View.VISIBLE);
    }

    public void hideNoDataView() {
        findViewById(R.id.place_holder).setVisibility(View.GONE);
    }

    public void setBackListener(@Nullable View view) {
        onBackPressed();
    }

    protected boolean hasPermissions(Context context, String permission) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {

            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {


                }
                return false;
            }

        }
        return true;
    }

    protected File createTemporaryFile(String part, String ext) throws Exception {

        File dir = new File(ROOT_DIR + File.separator + "temp/");
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            AppLogger.i(TAG, "Is DIR created?  " + created);

        }


        AppLogger.i(TAG, "Destination path is " + dir);


        return File.createTempFile(part, ext, dir);
    }

    public void openNextActivity() {
    }

    public void onBackClicked() {
        try {
            findViewById(R.id.back).setOnClickListener(view -> onBackPressed());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onBackClicked(View v) {
        try {
            onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void finishActivity() {
        finish();
    }

    protected String captureScreenshot(View v, String activityName) {
        String fileLocation = null;

        String dir = ROOT_DIR + "/screenCaptures/";

        File file = new File(dir);
        if (!file.exists()) file.mkdirs();


        try {
            // image naming and path  to include sd card  appending name you choose for file
            fileLocation = dir + activityName + ".jpg";


            // create bitmap screen capture
            v.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache());
            v.setDrawingCacheEnabled(false);

            File imageFile = new File(fileLocation);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
        return fileLocation;
    }

    public void goToFamilyMembersTable(RealFarmer FARMER){
        //Family members button clicked. Check to see if user provided the value for "farmer_familycount_COUNTRY" under Farmer Profile form

        Question numberFamilyMembersQuestion = getAppDataManager().getDatabaseManager().questionDao().get("farmer_familycount_");
        int farmerProfileFormId = getAppDataManager().getDatabaseManager().formsDao().getId(AppConstants.FARMER_PROFILE).blockingGet(0);

        FormAnswerData answerData = getAppDataManager().getDatabaseManager().formAnswerDao().getFormAnswerData(FARMER.getCode(), farmerProfileFormId);

        if(numberFamilyMembersQuestion != null) {
            if(answerData != null){
                int numberFamilyMembers;
                try {
                    numberFamilyMembers = Integer.valueOf(answerData.getJsonData().getString(numberFamilyMembersQuestion.getLabelC()));
                } catch (JSONException ignored) {
                    numberFamilyMembers = 1;
                }




                Intent intent = new Intent(this, FamilyMembersActivity.class);
                intent.putExtra("noFamilyMembers", numberFamilyMembers);
                intent.putExtra("farmer", getGson().toJson(FARMER));

                startActivity(intent);
                finish();

            }else
                showDialog(false, getStringResources(R.string.fill_data),
                        getStringResources(R.string.enter_data_rationale) + FARMER.getFarmerName() + getStringResources(R.string.before_proceed_suffux),
                        (dialog, which) -> dialog.dismiss(), getStringResources(R.string.ok), null, "", 0);
        }else
            showMessage(getStringResources(R.string.error_has_occurred));
    }

    public void moveToNextForm(RealFarmer FARMER) {
        CURRENT_FORM_POSITION++;

        if(CURRENT_FORM_POSITION == familyMembersFormPosition) {
            goToFamilyMembersTable(FARMER);
            return;
        }

        if (CURRENT_FORM_POSITION < FILTERED_FORMS.size()) {
            //CURRENT_FORM_QUESTION = FORM_AND_QUESTIONS.get(CURRENT_FORM_POSITION);

            Intent intent = new Intent(this, AddEditFarmerActivity.class);
            intent.putExtra("farmer", getGson().toJson(FARMER));
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        } else
            showFarmerDetailsActivity(FARMER);

    }

    public void showFarmerDetailsActivity(RealFarmer farmer) {
        Intent intent = new Intent(this, FarmerProfileActivity.class);
        intent.putExtra("farmer", getGson().toJson(farmer));
        startActivity(intent);
        finish();

    }

    public void setStatusBarColor(Window window, int statusBarColor, boolean isLightStatus) {
        if(isLightStatus)
                window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int flags = window.getDecorView().getSystemUiVisibility();
                    flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                    window.getDecorView().setSystemUiVisibility(flags);
                }
        //setStatusBarColor(window, statusBarColor);
    }

    public void setStatusBarColor(Window window, int statusBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.setStatusBarColor(ContextCompat.getColor(this, statusBarColor));


        }
    }

}