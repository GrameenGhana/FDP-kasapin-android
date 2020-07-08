package org.grameen.fdp.kasapin.di.module;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.grameen.fdp.kasapin.R;
import org.grameen.fdp.kasapin.data.AppDataManager;
import org.grameen.fdp.kasapin.di.Scope.ActivityContext;
import org.grameen.fdp.kasapin.ui.base.BaseContract;
import org.grameen.fdp.kasapin.ui.base.BasePresenter;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import dagger.Module;
import dagger.Provides;

@Module
public class ViewModule {
    private AppCompatActivity mActivity;

    public ViewModule(AppCompatActivity activity) {
        mActivity = activity;
    }

    @Provides
    @ActivityContext
    Context provideContext() {
        return mActivity;
    }

    @Provides
    Activity provideActivity() {
        return mActivity;
    }

    @Provides
    public BasePresenter<BaseContract.View> providesPresenter(AppDataManager appDataManager) {
        return new BasePresenter<BaseContract.View>(appDataManager);
    }

    @Provides
    AlertDialog.Builder provideAppDialogBuilder() {
        return new AlertDialog.Builder(mActivity, R.style.AppAlertDialog);
    }

    @Provides
    ProgressDialog provideProgressDialog() {
        return new ProgressDialog(mActivity);
    }

    @Provides
    ScriptEngine providesScriptEngine() {
        return new ScriptEngineManager().getEngineByName("rhino");
    }
}