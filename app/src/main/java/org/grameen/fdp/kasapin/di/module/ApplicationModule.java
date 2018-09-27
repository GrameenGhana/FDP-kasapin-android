package org.grameen.fdp.kasapin.di.module;


import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import org.grameen.fdp.kasapin.data.db.AppDatabase;
import org.grameen.fdp.kasapin.data.prefs.AppPreferencesHelper;
import org.grameen.fdp.kasapin.data.prefs.PreferencesHelper;
 import org.grameen.fdp.kasapin.di.Scope.ApplicationContext;
 import org.grameen.fdp.kasapin.utilities.AppConstants;

import javax.inject.Singleton;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import dagger.Module;
import dagger.Provides;

/**
 * Created by AangJnr on 19, September, 2018 @ 11:04 PM
 * Work Mail cibrahim@grameenfoundation.org
 * Personal mail aang.jnr@gmail.com
 */


@Module
public class ApplicationModule {

    private final Application application;

    public ApplicationModule(Application app) {
        application = app;
    }


    @Provides
    @ApplicationContext
    Context provideContext() {
        return application;
    }


    @Provides
    Application providesApplication() {
        return application;
    }

    @Singleton
    @Provides
    public AppDatabase providesDatabase() {
        return Room.databaseBuilder(application, AppDatabase.class, AppConstants.DATABASE_NAME)
                .build();

    }


    @Provides
    @Singleton
    PreferencesHelper providePreferencesHelper(AppPreferencesHelper appPreferencesHelper) {
        return appPreferencesHelper;
    }

    @Singleton
    @Provides
    SharedPreferences providesSharedPrefs() {
        return application.getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
    }




}