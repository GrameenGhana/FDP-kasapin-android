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
import org.grameen.fdp.kasapin.data.db.model.User;
import org.grameen.fdp.kasapin.data.network.FdpApiService;
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

import io.reactivex.disposables.CompositeDisposable;


@Singleton
public class AppDataManager implements DataManager {

    private static final String TAG = "AppDataManager";

    private final Context mContext;
    private final AppDatabase mAppDatabase;
    private final PreferencesHelper mPreferencesHelper;


    @Inject
    CompositeDisposable compositeDisposable;

    @Inject
    FdpApiService fdpApiService;


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
        mPreferencesHelper.setAccessToken(accessToken);

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
    public int getUserLoggedInMode() {
        return mPreferencesHelper.getUserLoggedInMode();
    }

    @Override
    public void setUserLoggedInMode(LoggedInMode mode) {
        mPreferencesHelper.setUserLoggedInMode(mode);
    }

    @Override
    public int getUserId() {
        return mPreferencesHelper.getUserId();
    }

    @Override
    public void setUserId(int userId) {
        mPreferencesHelper.setUserId(userId);
    }

    @Override
    public String getUserFirstName() {
        return mPreferencesHelper.getUserFirstName();
    }

    @Override
    public void setUserFirstName(String firstName) {
        mPreferencesHelper.setUserFirstName(firstName);
    }

    @Override
    public String getUserLastName() {
        return mPreferencesHelper.getUserLastName();
    }

    @Override
    public void setUserLastName(String lastName) {
        mPreferencesHelper.setUserLastName(lastName);
    }


    @Override
    public String getUserEmail() {
        return mPreferencesHelper.getUserEmail();
    }

    @Override
    public void setUserEmail(String email) {
        mPreferencesHelper.setUserEmail(email);
    }

    @Override
    public String getUserUuid() {
        return (mPreferencesHelper.getUserUuid() != null) ? mPreferencesHelper.getUserUuid(): getUserEmail();
    }

    @Override
    public void setUserUuid(String uuid) {
        mPreferencesHelper.setUserUuid(uuid);
    }

    @Override
    public Boolean getUserIsActive() {
        return mPreferencesHelper.getUserIsActive();
    }

    @Override
    public void setUserIsActive(boolean isActive) {
        mPreferencesHelper.setUserIsActive(isActive);
    }

    @Override
    public String getUserProfilePicUrl() {
        return mPreferencesHelper.getUserProfilePicUrl();
    }

    @Override
    public void setUserProfilePicUrl(String profilePicUrl) {
        mPreferencesHelper.setUserProfilePicUrl(profilePicUrl);
    }


    public String getUserFullName(){
        return getUserFirstName() + " " + getUserLastName();
    }

    @Override
    public String getUserConfirmationCode() {
        return mPreferencesHelper.getUserConfirmationCode();
    }

    @Override
    public void setUserConfirmationCode(String code) {
        mPreferencesHelper.setUserConfirmationCode(code);
    }

    @Override
    public Boolean getUserIsConfirmed() {
        return mPreferencesHelper.getUserIsConfirmed();
    }

    @Override
    public void setUserIsConfirmed(boolean isActive) {
        mPreferencesHelper.setUserIsConfirmed(isActive);
    }


    @Override
    public void clearSecurePreferences() {
        mPreferencesHelper.clearSecurePreferences();
    }

    @Override
    public void updateUserInfo(String accessToken,
                               int userId,
                               String uuid,
                               String firstName,
                               String lastName,
                               String email,
                               Boolean active,
                               String confirmationCode,
                               Boolean confirmed,
                               String image) {

        mPreferencesHelper.setAccessToken(accessToken);
        mPreferencesHelper.setUserId(userId);
        mPreferencesHelper.setUserUuid(uuid);
        mPreferencesHelper.setUserFirstName(firstName);
        mPreferencesHelper.setUserLastName(lastName);
        mPreferencesHelper.setUserEmail(email);
        mPreferencesHelper.setUserIsActive(active);
        mPreferencesHelper.setUserConfirmationCode(confirmationCode);
        mPreferencesHelper.setUserIsConfirmed(confirmed);
        mPreferencesHelper.setUserProfilePicUrl(image);

    }

    @Override
    public void updateUserInfo(User user) {
        mPreferencesHelper.setUserId(user.getId());
        mPreferencesHelper.setUserUuid(user.getUuid());
        mPreferencesHelper.setUserFirstName(user.getFirstName());
        mPreferencesHelper.setUserLastName(user.getLastName());
        mPreferencesHelper.setUserEmail(user.getEmail());
        mPreferencesHelper.setUserIsActive(user.isActive());
        mPreferencesHelper.setUserConfirmationCode(user.getConfirmationCode());
        mPreferencesHelper.setUserIsConfirmed(user.isConfirmed());
        mPreferencesHelper.setUserProfilePicUrl(user.getAvatarLocation());
    }

    @Override
    public void updateApiHeader(Long userId, String accessToken) {

    }

    @Override
    public void setUserAsLoggedOut() {

       updateUserInfo(new User(-1,
               "",
               "",
               "",
               "",
               "",
               false,
               "",
               false));

        clearPreferences();
        clearSecurePreferences();
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


    public CompositeDisposable getCompositeDisposable() {
        return compositeDisposable;
    }

    public FdpApiService getFdpApiService() {
        return fdpApiService;
    }
}
