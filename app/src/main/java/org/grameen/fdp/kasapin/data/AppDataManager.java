/*
 * Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://mindorks.com/license/apache-v2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package org.grameen.fdp.kasapin.data;


import android.content.Context;
import android.os.Environment;

import org.grameen.fdp.kasapin.data.db.AppDatabase;
import org.grameen.fdp.kasapin.data.prefs.PreferencesHelper;
import org.grameen.fdp.kasapin.di.Scope.ApplicationContext;
import org.grameen.fdp.kasapin.utilities.AppConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import javax.inject.Inject;
import javax.inject.Singleton;



@Singleton
public class AppDataManager implements DataManager {

    private static final String TAG = "AppDataManager";

    private final Context mContext;
    private final AppDatabase mAppDatabase;
    private final PreferencesHelper mPreferencesHelper;

    @Inject
    public AppDataManager(@ApplicationContext Context context, AppDatabase appDatabase, PreferencesHelper preferencesHelper) {
        mContext = context;
        mAppDatabase = appDatabase;
        mPreferencesHelper = preferencesHelper;


     }




    @Override
    public String getAccessToken() {
        return mPreferencesHelper.getAccessToken();
    }

    @Override
    public void setAccessToken(String accessToken) {

    }

    @Override
    public void setIsMonitoringMode(boolean isMonitoringMode) {
        mPreferencesHelper.setIsMonitoringMode(isMonitoringMode);

    }

    @Override
    public boolean isMonitoring() {
        return mPreferencesHelper.isMonitoring();
    }

    @Override
    public void clearPreferences() {
        mPreferencesHelper.clearPreferences();
    }

    @Override
    public void setIsTranslationToggled(boolean isTranslationToggled) {
        mPreferencesHelper.setIsTranslationToggled(isTranslationToggled);
    }

    @Override
    public boolean isTranslation() {
        return mPreferencesHelper.isTranslation();
    }


    @Override
    public int getCurrentUserLoggedInMode() {
        return mPreferencesHelper.getCurrentUserLoggedInMode();
    }

    @Override
    public void setCurrentUserLoggedInMode(LoggedInMode mode) {
        mPreferencesHelper.setCurrentUserLoggedInMode(mode);
    }

    @Override
    public Long getCurrentUserId() {
        return mPreferencesHelper.getCurrentUserId();
    }

    @Override
    public void setCurrentUserId(Long userId) {
        mPreferencesHelper.setCurrentUserId(userId);
    }

    @Override
    public String getCurrentUserName() {
        return mPreferencesHelper.getCurrentUserName();
    }

    @Override
    public void setCurrentUserName(String userName) {
        mPreferencesHelper.setCurrentUserName(userName);
    }

    @Override
    public String getCurrentUserEmail() {
        return mPreferencesHelper.getCurrentUserEmail();
    }

    @Override
    public void setCurrentUserEmail(String email) {
        mPreferencesHelper.setCurrentUserEmail(email);
    }

    @Override
    public String getCurrentUserProfilePicUrl() {
        return mPreferencesHelper.getCurrentUserProfilePicUrl();
    }

    @Override
    public void setCurrentUserProfilePicUrl(String profilePicUrl) {
        mPreferencesHelper.setCurrentUserProfilePicUrl(profilePicUrl);
    }



    @Override
    public void updateUserInfo(
            String accessToken,
            Long userId,
            LoggedInMode loggedInMode,
            String userName,
            String email,
            String profilePicPath) {

        setAccessToken(accessToken);
        setCurrentUserId(userId);
        setCurrentUserLoggedInMode(loggedInMode);
        setCurrentUserName(userName);
        setCurrentUserEmail(email);
        setCurrentUserProfilePicUrl(profilePicPath);

        updateApiHeader(userId, accessToken);
    }

    @Override
    public void updateApiHeader(Long userId, String accessToken) {

    }

    @Override
    public void setUserAsLoggedOut() {
        updateUserInfo(
                null,
                null,
                DataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT,
                null,
                null,
                null);

        clearPreferences();

        clearAllTablesFromDb();
    }

    @Override
    public void clearAllTablesFromDb() {
        getDatabaseManager().clearAllTables();
    }


    public AppDatabase getDatabaseManager() {
        return mAppDatabase;
    }


    @Override
    public int backupRestoreDatabase(boolean shouldbackup) {

        String oldPath;
        String newPath;

        if (shouldbackup) {
            oldPath = Environment.getDataDirectory().getPath() + "/data/" + mContext.getPackageName() + "/databases/" + AppConstants.DATABASE_NAME;
            newPath = AppConstants.DATABASE_BACKUP_DIR + "/" + AppConstants.DATABASE_NAME;

        } else {

            oldPath = AppConstants.DATABASE_BACKUP_DIR + "/" + AppConstants.DATABASE_NAME;
            newPath = Environment.getDataDirectory().getPath() + "/data/" + mContext.getPackageName() + "/databases/" + AppConstants.DATABASE_NAME;
        }


        int value;

        File currentDB = new File(oldPath);

        File backupDB = new File(newPath);

        if (!shouldbackup && !currentDB.exists())
            return 0;

        FileChannel source;
        FileChannel destination;

        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();

            value = 1;
        } catch (IOException e) {
            e.printStackTrace();
            value = -1;
        }

        return value;

    }
}
