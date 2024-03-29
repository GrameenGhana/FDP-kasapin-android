package org.grameen.fdp.kasapin.utilities;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.Settings;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CommonUtils {
    private static final String TAG = "CommonUtils";

    private CommonUtils() {
        // This utility class is not publicly instantiable
    }

    public static void showLoadingDialog(ProgressDialog progressDialog) {
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public static void showLoadingDialog(ProgressDialog progressDialog, String title, String message, boolean indeterminate, @DrawableRes int icon, boolean cancelableOnTouchOutside) {
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        if (icon != 0)
            progressDialog.setIcon(icon);
        progressDialog.setIndeterminate(indeterminate);
        progressDialog.setCancelable(cancelableOnTouchOutside);
        progressDialog.setCanceledOnTouchOutside(cancelableOnTouchOutside);
        progressDialog.show();
    }

    public static String toCamelCase(String value) {
        return (value == null || value.equals("null")) ? "" :
                (value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase());
    }

    public static void showAlertDialog(AlertDialog.Builder builder, Boolean cancelable, @Nullable String title, @Nullable String message,
                                       @Nullable DialogInterface.OnClickListener onPositiveButtonClickListener,
                                       @NonNull String positiveText,
                                       @Nullable DialogInterface.OnClickListener onNegativeButtonClickListener,
                                       @NonNull String negativeText, int icon_drawable) {


        builder.setPositiveButton("", null);
        builder.setNegativeButton("", null);
        builder.setIcon(0);
        builder.setTitle(title);
        builder.setCancelable(cancelable);
        if (icon_drawable != 0) builder.setIcon(icon_drawable);
        builder.setMessage(message);

        if (onPositiveButtonClickListener != null)
            builder.setPositiveButton(positiveText, onPositiveButtonClickListener);
        if (onNegativeButtonClickListener != null)
            builder.setNegativeButton(negativeText, onNegativeButtonClickListener);
        builder.show();
    }

    @SuppressLint("all")
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static boolean isEmailValid(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN =
                "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String getTimeStamp() {
        return new SimpleDateFormat(AppConstants.TIMESTAMP_FORMAT, Locale.US).format(new Date());
    }


    public static String getDateStamp() {
        return new SimpleDateFormat(AppConstants.DATE_FORMAT, Locale.US).format(new Date());
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}