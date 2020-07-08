package org.grameen.fdp.kasapin.data.prefs;

import android.content.SharedPreferences;

import org.grameen.fdp.kasapin.data.DataManager;

public interface PreferencesHelper {
    int getUserLoggedInMode();
    int getUserId();

    void setUserLoggedInMode(DataManager.LoggedInMode mode);
    void setUserId(int userId);
    void setUserFirstName(String firstName);
    void setUserLastName(String lastName);
    void setUserUuid(String uuid);
    void setUserEmail(String email);
    void setUserIsActive(boolean isActive);
    void setUserProfilePicUrl(String profilePicUrl);
    void setUserConfirmationCode(String code);
    void setUserIsConfirmed(boolean isActive);
    void setAccessToken(String accessToken);
    void setIsMonitoringMode(boolean isMonitoringMode);
    void clearPreferences();
    void setIsTranslationToggled(boolean isTranslationToggled);
    void clearSecurePreferences();
    void setStringValue(String key, String value);
    void setBooleanValue(String key, boolean value);

    String getUserFirstName();
    String getUserLastName();
    String getUserEmail();
    String getUserUuid();
    String getUserProfilePicUrl();
    String getUserConfirmationCode();
    String getAccessToken();
    String getStringValue(String key);

    Boolean getUserIsActive();
    Boolean getUserIsConfirmed();
    boolean isMonitoring();
    boolean isTranslation();
    boolean getBooleanValue(String key);

    SharedPreferences getPreferences();
}
