package org.grameen.fdp.kasapin.data.prefs;

import android.content.SharedPreferences;

import org.grameen.fdp.kasapin.data.DataManager;

public interface PreferencesHelper {
    int getUserLoggedInMode();

    void setUserLoggedInMode(DataManager.LoggedInMode mode);

    int getUserId();

    void setUserId(int userId);

    void setIsMonitoringMode(boolean isMonitoringMode);

    void clearPreferences();

    void setIsTranslation(boolean isTranslationToggled);

    void clearSecurePreferences();

    void setStringValue(String key, String value);

    void setBooleanValue(String key, boolean value);

    String getUserFirstName();

    void setUserFirstName(String firstName);

    String getUserLastName();

    void setUserLastName(String lastName);

    String getUserEmail();

    void setUserEmail(String email);

    String getUserUuid();

    void setUserUuid(String uuid);

    String getUserProfilePicUrl();

    void setUserProfilePicUrl(String profilePicUrl);

    String getUserConfirmationCode();

    void setUserConfirmationCode(String code);

    String getAccessToken();

    void setAccessToken(String accessToken);

    String getStringValue(String key);

    Boolean getUserIsActive();

    void setUserIsActive(boolean isActive);

    Boolean getUserIsConfirmed();

    void setUserIsConfirmed(boolean isActive);

    boolean isMonitoring();

    boolean isTranslation();

    boolean getBooleanValue(String key);

    SharedPreferences getPreferences();
}
