package org.grameen.fdp.kasapin.data;

import android.content.Context;
import android.content.SharedPreferences;

import org.grameen.fdp.kasapin.MockData;
import org.grameen.fdp.kasapin.data.db.AppDatabase;
import org.grameen.fdp.kasapin.data.db.model.User;
import org.grameen.fdp.kasapin.data.prefs.PreferencesHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Single;

import static org.junit.Assert.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AppDataManagerTest {
    @Mock
    Context context;
    @Mock AppDatabase appDatabase;
    @Mock PreferencesHelper preferencesHelper;

    AppDataManager appDataManager;

    @Before
    public void setUp() {
        appDataManager = new AppDataManager(context, appDatabase, preferencesHelper);
    }

    @After
    public void tearDown() {
        appDataManager.compositeDisposable.dispose();
    }

    @Test
    public void test_getAccessToken() {
        when(appDataManager.getAccessToken()).thenAnswer(i->
                MockData.fakeAccessToken);
        String token = appDataManager.getAccessToken();
        assertEquals(token, MockData.fakeAccessToken);
    }

    @Test
    public void test_setAccessToken() {
        String fakeAccessToken = MockData.fakeAccessToken;
        appDataManager.setAccessToken(MockData.fakeAccessToken);

        appDataManager.setAccessToken(MockData.fakeAccessToken);
        verify(preferencesHelper, times(2)).setAccessToken(ArgumentMatchers.eq(MockData.fakeAccessToken));
    }

    @Test
    public void test_setIsMonitoringMode_stores_the_value() {
        appDataManager.setIsMonitoringMode(true);
        verify(preferencesHelper).setIsMonitoringMode(ArgumentMatchers.eq(true));

        appDataManager.setIsMonitoringMode(false);
        verify(preferencesHelper).setIsMonitoringMode(ArgumentMatchers.eq(false));
    }

    @Test
    public void test_isMonitoring() {
        when(appDataManager.isMonitoring()).thenAnswer( i->
                true);
assertTrue(appDataManager.isMonitoring());
    }

    @Test
    public void test_clearPreferences() {
        appDataManager.clearPreferences();
        verify(preferencesHelper, atLeastOnce()).clearPreferences();
    }

    @Test
    public void test_getPreferences() {
        when(appDataManager.getPreferences()).thenReturn(null);
        appDataManager.getPreferences();
        verify(preferencesHelper, atLeastOnce()).getPreferences();
    }

    @Test
    public void test_setIsTranslation() {
        appDataManager.setIsTranslation(true);
        verify(preferencesHelper).setIsTranslation(ArgumentMatchers.eq(true));

        appDataManager.setIsTranslation(false);
        verify(preferencesHelper).setIsTranslation(ArgumentMatchers.eq(false));
    }

    @Test
    public void test_isTranslation_returns_a_value() {
        when(appDataManager.isTranslation()).thenAnswer( i->
                true);
        assertTrue(appDataManager.isTranslation());
    }

    @Test
    public void test_getUserLoggedInMode_returns_a_LoginMode_value() {
        when(appDataManager.getUserLoggedInMode()).thenAnswer( i->
                DataManager.LoggedInMode.LOGGED_IN.getType());

        assert(appDataManager.getUserLoggedInMode() == DataManager.LoggedInMode.LOGGED_IN.getType());
        verify(preferencesHelper, times(1)).getUserLoggedInMode();
    }

    @Test
    public void test_setUserLoggedInMode() {
        appDataManager.setUserLoggedInMode(DataManager.LoggedInMode.LOGGED_IN);
        verify(preferencesHelper).setUserLoggedInMode(ArgumentMatchers.eq(DataManager.LoggedInMode.LOGGED_IN));

        appDataManager.setUserLoggedInMode(DataManager.LoggedInMode.LOGGED_OUT);
        verify(preferencesHelper).setUserLoggedInMode(ArgumentMatchers.eq(DataManager.LoggedInMode.LOGGED_OUT));

        verify(preferencesHelper, times(1)).setUserLoggedInMode(DataManager.LoggedInMode.LOGGED_IN);
    }

    @Test
    public void test_getUserId() {
        when(appDataManager.getUserId()).thenAnswer( i->
                MockData.getFakeUser().getId());
        appDataManager.getUserId();
        assert(appDataManager.getUserId() == 1);
        verify(preferencesHelper, times(2)).getUserId();
    }

    @Test
    public void test_setUserId_stores_value_for_userId_in_preferences() {
        appDataManager.setUserId(1);
        verify(preferencesHelper).setUserId(ArgumentMatchers.eq(1));

        appDataManager.setUserId(0);
        verify(preferencesHelper).setUserId(ArgumentMatchers.eq(0));
    }

    @Test
    public void test_getUserFirstName() {
        when(appDataManager.getUserFirstName()).thenAnswer(i->
                MockData.getFakeUser().getFirstName());
        String name = appDataManager.getUserFirstName();
        verify(preferencesHelper, times(1)).getUserFirstName();
        assertEquals(name, "Fake");
    }

    @Test
    public void test_setUserFirstName_stores_value_for_UserFirstName_in_preferences() {
        appDataManager.setUserFirstName(MockData.getFakeUser().getFirstName());
        verify(preferencesHelper).setUserFirstName(MockData.getFakeUser().getFirstName());
    }

    @Test
    public void test_getUserLastName() {
        when(appDataManager.getUserLastName()).thenAnswer(i->
                MockData.getFakeUser().getLastName());
        String lastName = appDataManager.getUserLastName();
        verify(preferencesHelper, times(1)).getUserLastName();
        assertEquals(lastName, "User");
    }

    @Test
    public void test_setUserLastName_stores_value_in_preferences() {
        appDataManager.setUserLastName(MockData.getFakeUser().getLastName());
        verify(preferencesHelper).setUserLastName(MockData.getFakeUser().getLastName());
    }


    @Test
    public void test_getUserEmail() {
        when(appDataManager.getUserEmail()).thenAnswer(i->
                MockData.getFakeUser().getEmail());
        appDataManager.getUserEmail();
        verify(preferencesHelper, times(1)).getUserEmail();
    }

    @Test
    public void test_setUserEmail_stores_value_in_preferences() {
        appDataManager.setUserEmail(MockData.getFakeUser().getEmail());
        verify(preferencesHelper).setUserEmail(MockData.getFakeUser().getEmail());
    }

    @Test
    public void test_getUserUuid() {
        when(appDataManager.getUserUuid()).thenAnswer(i->
                MockData.getFakeUser().getUuid());
        verify(preferencesHelper, times(1)).getUserUuid();
    }

    @Test
    public void test_setUserUuid_stores_value_in_preferences() {
        appDataManager.setUserUuid(MockData.getFakeUser().getUuid());
        verify(preferencesHelper).setUserUuid(MockData.getFakeUser().getUuid());
    }

    @Test
    public void test_getUserIsActive() {
        when(appDataManager.getUserIsActive()).thenAnswer(i->
                true);
        assertTrue(appDataManager.getUserIsActive());
        verify(preferencesHelper, times(1)).getUserIsActive();
    }

    @Test
    public void test_setUserIsActive_stores_value_in_preferences() {
        appDataManager.setUserIsActive(true);
        verify(preferencesHelper).setUserIsActive(true);

        appDataManager.setUserIsActive(false);
        verify(preferencesHelper).setUserIsActive(false);
    }

    @Test
    public void test_getUserProfilePicUrl() {
        when(appDataManager.getUserProfilePicUrl()).thenAnswer(i->
                MockData.getFakeUser().getAvatarLocation());
        appDataManager.getUserProfilePicUrl();

        verify(preferencesHelper, times(1)).getUserProfilePicUrl();
    }


    @Test
    public void test_setUserProfilePicUrl_stores_value_in_preferences() {
        appDataManager.setUserProfilePicUrl(MockData.getFakeUser().getAvatarLocation());
        verify(preferencesHelper).setUserProfilePicUrl(MockData.getFakeUser().getAvatarLocation());
    }

    @Test
    public void test_getUserConfirmationCode() {
        when(appDataManager.getUserConfirmationCode()).thenAnswer(i->
                MockData.getFakeUser().getConfirmationCode());
        appDataManager.getUserConfirmationCode();
        verify(preferencesHelper, times(1)).getUserConfirmationCode();
    }

    @Test
    public void test_setUserConfirmationCode_stores_value_in_preferences() {
        appDataManager.setUserConfirmationCode(MockData.getFakeUser().getConfirmationCode());
        verify(preferencesHelper).setUserConfirmationCode(MockData.getFakeUser().getConfirmationCode());
    }

    @Test
    public void test_getUserIsConfirmed() {
        when(appDataManager.getUserIsConfirmed()).thenAnswer(i->
                true);
        assertTrue(appDataManager.getUserIsConfirmed());
        verify(preferencesHelper, times(1)).getUserIsConfirmed();
    }

    @Test
    public void test_setUserIsConfirmed_stores_value_in_preferences() {
        appDataManager.setUserIsConfirmed(true);
        verify(preferencesHelper).setUserIsConfirmed(true);

        appDataManager.setUserIsConfirmed(false);
        verify(preferencesHelper).setUserIsConfirmed(false);
    }

    @Test
    public void test_clearSecurePreferences_clears_secure_preferences_data() {
        appDataManager.clearPreferences();
        verify(preferencesHelper, atLeastOnce()).clearPreferences();
    }

    @Test
    public void test_UpdateUserInfo() {
        appDataManager.updateUserInfo(new User());
        verify(preferencesHelper, atLeastOnce()).setUserUuid(null);
        verify(preferencesHelper, atLeastOnce()).setUserId(0);
    }

    @Test
    public void setUserAsLoggedOut() {
        appDataManager.setUserAsLoggedOut();

        verify(preferencesHelper, times(1)).clearPreferences();
        verify(preferencesHelper, times(1)).clearSecurePreferences();

        verify(preferencesHelper, atLeastOnce()).setUserUuid("");
        verify(preferencesHelper, atLeastOnce()).setUserId(-1);

        verify(appDatabase, times(1)).clearAllTables();
    }

    @Test
    public void clearAllTablesFromDb() {
        appDataManager.clearAllTablesFromDb();
        verify(appDatabase, times(1)).clearAllTables();
    }

    @Test
    public void test_that_getDatabaseManager_returns_appDatabase() {
        assert(appDataManager.getDatabaseManager() == appDatabase);
    }

    @Test
    public void test_that_setStringValue_updates_the_value_with_required_key() {
        appDataManager.setStringValue("someKey", "Some value");

        verify(preferencesHelper, times(1))
                .setStringValue(ArgumentMatchers.eq("someKey"), ArgumentMatchers.eq("Some value"));
    }

    @Test
    public void test_getBooleanValue_returns_a_boolean_value() {
        when(appDataManager.getBooleanValue("someKey")).thenAnswer( i->
                true);
        assertTrue(appDataManager.getBooleanValue("someKey"));
        verify(preferencesHelper, times(1)).getBooleanValue("someKey");
    }

    @Test
    public void test_that_setBooleanValue_updates_the_value_with_required_key() {
        appDataManager.setBooleanValue("someKey", true);
        verify(preferencesHelper, times(1))
                .setBooleanValue(ArgumentMatchers.eq("someKey"), ArgumentMatchers.eq(true));
    }

    @Test
    public void getStringValue() {
        when(appDataManager.getStringValue("someKey")).thenAnswer( i->
                "Some value");
        assertTrue(appDataManager.getStringValue("someKey").contains("Some value"));
        verify(preferencesHelper, times(1)).getStringValue("someKey");
    }
}