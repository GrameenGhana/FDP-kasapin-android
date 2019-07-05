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

package org.grameen.fdp.kasapin.data.prefs;

import android.content.SharedPreferences;

import org.grameen.fdp.kasapin.data.DataManager;
import org.grameen.fdp.kasapin.utilities.AppConstants;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by janisharali on 27/01/17.
 */

@Singleton
public class AppPreferencesHelper implements PreferencesHelper {

    public static final String PREF_KEY_IS_MONITORING_MODE = "PREF_KEY_MONITORING_MODE";
    private static final String PREF_KEY_USER_LOGGED_IN_MODE = "PREF_KEY_USER_LOGGED_IN_MODE";
    private static final String PREF_KEY_USER_ID = "PREF_KEY_USER_ID";
    private static final String PREF_KEY_USER_UUID = "PREF_KEY_USER_UUID";
    private static final String PREF_KEY_USER_FNAME = "PREF_KEY_USER_FNAME";
    private static final String PREF_KEY_USER_LNAME = "PREF_KEY_USER_LNAME";
    private static final String PREF_KEY_USER_EMAIL = "PREF_KEY_USER_EMAIL";
    private static final String PREF_KEY_USER_ACTIVE = "PREF_KEY_USER_ACTIVE";
    private static final String PREF_KEY_USER_CONFIRMATION_CODE = "PREF_KEY_USER_CONFIRMATION_CODE";
    private static final String PREF_KEY_USER_CONFIRMED = "PREF_KEY_USER_CONFIRMED";
    private static final String PREF_KEY_USER_PROFILE_PIC_URL = "PREF_KEY_USER_PROFILE_PIC_URL";
    private static final String PREF_KEY_IS_TRANSLATION = "IS_TRANSLATION_TOGGLED";
    private static final String PREF_KEY_ACCESS_TOKEN = "PREF_KEY_ACCESS_TOKEN";
    private final SharedPreferences mPrefs;

    // private SecurePreferences securedPreferences;

    @Inject
    public AppPreferencesHelper(SharedPreferences preferences) {
        mPrefs = preferences;
        //securedPreferences = new SecurePreferences(mPrefs, BaseActivity.DEVICE_ID, false);
    }

    @Override
    public int getUserId() {
        return mPrefs.getInt(PREF_KEY_USER_ID, AppConstants.NULL_INDEX);
    }

    @Override
    public void setUserId(int userId) {
        mPrefs.edit().putInt(PREF_KEY_USER_ID, userId).apply();
    }

    @Override
    public String getUserFirstName() {
        return mPrefs.getString(PREF_KEY_USER_FNAME, null);
    }

    @Override
    public void setUserFirstName(String firstName) {
        mPrefs.edit().putString(PREF_KEY_USER_FNAME, firstName).apply();

    }

    @Override
    public String getUserLastName() {
        return mPrefs.getString(PREF_KEY_USER_LNAME, null);
    }

    @Override
    public void setUserLastName(String lastName) {
        mPrefs.edit().putString(PREF_KEY_USER_LNAME, lastName).apply();
    }


    @Override
    public String getUserEmail() {
        //return securedPreferences.getString(PREF_KEY_USER_EMAIL);
        return mPrefs.getString(PREF_KEY_USER_EMAIL, null);

    }

    @Override
    public void setUserEmail(String email) {

        //securedPreferences.put(PREF_KEY_USER_EMAIL, email);
        mPrefs.edit().putString(PREF_KEY_USER_EMAIL, email).apply();


    }

    @Override
    public String getUserUuid() {

        //return securedPreferences.getString(PREF_KEY_USER_UUID);
        return mPrefs.getString(PREF_KEY_USER_UUID, null);


    }

    @Override
    public void setUserUuid(String uuid) {
        //securedPreferences.put(PREF_KEY_USER_UUID, uuid);
        mPrefs.edit().putString(PREF_KEY_USER_UUID, uuid).apply();

    }

    @Override
    public Boolean getUserIsActive() {
        return mPrefs.getBoolean(PREF_KEY_USER_ACTIVE, false);
    }

    @Override
    public void setUserIsActive(boolean isActive) {
        mPrefs.edit().putBoolean(PREF_KEY_USER_ACTIVE, isActive).apply();

    }

    @Override
    public String getUserProfilePicUrl() {
        return mPrefs.getString(PREF_KEY_USER_PROFILE_PIC_URL, null);
    }

    @Override
    public void setUserProfilePicUrl(String profilePicUrl) {
        mPrefs.edit().putString(PREF_KEY_USER_PROFILE_PIC_URL, profilePicUrl).apply();
    }

    @Override
    public String getUserConfirmationCode() {
        return mPrefs.getString(PREF_KEY_USER_CONFIRMATION_CODE, null);
    }

    @Override
    public void setUserConfirmationCode(String code) {

        mPrefs.edit().putString(PREF_KEY_USER_CONFIRMATION_CODE, code).apply();

    }

    @Override
    public Boolean getUserIsConfirmed() {
        return mPrefs.getBoolean(PREF_KEY_USER_CONFIRMED, false);
    }

    @Override
    public void setUserIsConfirmed(boolean isConfirmed) {
        mPrefs.edit().putBoolean(PREF_KEY_USER_CONFIRMED, isConfirmed).apply();

    }

    @Override
    public int getUserLoggedInMode() {
        return mPrefs.getInt(PREF_KEY_USER_LOGGED_IN_MODE, DataManager.LoggedInMode.LOGGED_OUT.getType());
    }

    @Override
    public void setUserLoggedInMode(DataManager.LoggedInMode mode) {
        mPrefs.edit().putInt(PREF_KEY_USER_LOGGED_IN_MODE, mode.getType()).apply();
    }

    @Override
    public String getAccessToken() {
        //return securedPreferences.getString(PREF_KEY_ACCESS_TOKEN);
        return mPrefs.getString(PREF_KEY_ACCESS_TOKEN, null);

    }

    @Override
    public void setAccessToken(String accessToken) {

        //securedPreferences.put(PREF_KEY_ACCESS_TOKEN, accessToken);
        mPrefs.edit().putString(PREF_KEY_ACCESS_TOKEN, accessToken).apply();


    }

    @Override
    public void setIsMonitoringMode(boolean isMonitoringMode) {
        mPrefs.edit().putBoolean(PREF_KEY_IS_MONITORING_MODE, isMonitoringMode).apply();


    }

    @Override
    public boolean isMonitoring() {
        return mPrefs.getBoolean(PREF_KEY_IS_MONITORING_MODE, false);
    }

    @Override
    public void clearPreferences() {
        mPrefs.edit().clear().apply();
    }

    @Override
    public boolean isTranslation() {
        return mPrefs.getBoolean(PREF_KEY_IS_TRANSLATION, false);
    }

    @Override
    public void clearSecurePreferences() {
        //securedPreferences.clear();
    }

    @Override
    public void setIsTranslationToggled(boolean isTranslationToggled) {

        mPrefs.edit().putBoolean(PREF_KEY_IS_TRANSLATION, isTranslationToggled).apply();

    }


    @Override
    public String getStringValue(String key) {
        return mPrefs.getString(key, "");
    }

    @Override
    public void setStringValue(String key, String value) {
        mPrefs.edit().putString(key, value).apply();
    }


    @Override
    public void setBooleanValue(String key, boolean value) {

        mPrefs.edit().putBoolean(key, value).apply();
    }

    @Override
    public boolean getBooleanValue(String key) {
        return mPrefs.getBoolean(key, false);
    }
}
