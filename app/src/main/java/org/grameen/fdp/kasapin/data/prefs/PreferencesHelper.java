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


import org.grameen.fdp.kasapin.data.DataManager;

/**
 * Created by janisharali on 27/01/17.
 */

public interface PreferencesHelper {

    int getUserLoggedInMode();

    void setUserLoggedInMode(DataManager.LoggedInMode mode);

    int getUserId();

    void setUserId(int userId);

    String getUserFirstName();

    void setUserFirstName(String firstName);

    String getUserLastName();

    void setUserLastName(String lastName);

    String getUserEmail();

    void setUserEmail(String email);

    String getUserUuid();

    void setUserUuid(String uuid);


    Boolean getUserIsActive();

    void setUserIsActive(boolean isActive);


    String getUserProfilePicUrl();

    void setUserProfilePicUrl(String profilePicUrl);

    String getUserConfirmationCode();

    void setUserConfirmationCode(String code);

    Boolean getUserIsConfirmed();

    void setUserIsConfirmed(boolean isActive);


    String getAccessToken();

    void setAccessToken(String accessToken);


    void setIsMonitoringMode(boolean isMonitoringMode);


    boolean isMonitoring();


    void clearPreferences();

    void setIsTranslationToggled(boolean isTranslationToggled);

    boolean isTranslation();


    void clearSecurePreferences();


    String getStringValue(String key);

    void setStringValue(String key, String value);


    boolean getBooleanValue(String key);

    void setBooleanValue(String key, boolean value);


}
