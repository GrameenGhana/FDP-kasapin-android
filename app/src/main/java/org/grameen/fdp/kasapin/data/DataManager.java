package org.grameen.fdp.kasapin.data;

import org.grameen.fdp.kasapin.data.db.model.User;
import org.grameen.fdp.kasapin.data.prefs.PreferencesHelper;

public interface DataManager extends PreferencesHelper {
    void updateApiHeader(Long userId, String accessToken);
    void setUserAsLoggedOut();
    void clearAllTablesFromDb();
    int backupRestoreDatabase(boolean shouldBackup);
    void updateUserInfo(
            String accessToken,
            int userId,
            String uuid,
            String firstName,
            String lastName,
            String email,
            Boolean active,
            String confirmationCode,
            Boolean confirmed,
            String image);

    void updateUserInfo(User user);

    enum LoggedInMode {
        LOGGED_OUT(0),
        LOGGED_IN(1),
        SERVER(3);

        private final int mType;
        LoggedInMode(int type) {
            mType = type;
        }
        public int getType() {
            return mType;
        }
    }
}
