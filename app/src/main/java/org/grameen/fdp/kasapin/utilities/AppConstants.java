package org.grameen.fdp.kasapin.utilities;

import android.os.Environment;

import java.io.File;

public class AppConstants {
    public static final String SERVER_URL = "server_url";
    public static final String API_VERSION = "api/v1/";
    public static final String API = "api/";
    public static final int PERMISSION_FINE_LOCATION = 1001;
    public static final int PLACE_PICKER_REQUEST = 1002;
    public static final int PERMISSION_CALL = 1003;
    public static final String DATABASE_NAME = "fdp_database";
    public static final String DISPLAY_TYPE_FORM = "Form";
    public static final String DISPLAY_TYPE_TABLE = "Table";
    public static final String DISPLAY_TYPE_PLOT_FORM = "Plot form";
    public static final String DISPLAY_TYPE_HISTORICAL = "Historical";
    public static final String DISPLAY_TYPE_P_AND_L = "P&L";
    public static final String FARMER_TABLE = "farmer_c";
    public static final String FARMER_TABLE_EXTERNAL_ID_FIELD = "external_id_c";
    public static final String FARMER_TABLE_AGE_FIELD = "age_c";
    public static final String FARMER_TABLE_CODE_FIELD = "farmer_code_c";
    public static final String FARMER_TABLE_EDUCATION_LEVEL_FIELD = "educational_level_c";
    public static final String FARMER_TABLE_PHOTO_FIELD = "farmer_photo_c";
    public static final String FARMER_TABLE_NAME_FIELD = "full_name_c";
    public static final String FARMER_TABLE_COUNTRY_ADMIN_LEVEL_FIELD = "country_admin_level_id";
    public static final String FARMER_TABLE_GENDER_FIELD = "gender_c";
    public static final String FARMER_TABLE_BIRTHDAY_FIELD = "birthday_c";
    public static final String FAMILY_MEMBERS_TABLE = "family_member_c";
    public static final String PLOT_TABLE = "plot_c";
    public static final String PLOT_GPS_POINT = "gps_point_c";
    public static final String PLOT_GPS_POINT_LAT_FIELD = "latitude_c";
    public static final String PLOT_GPS_POINT_LNG_FIELD = "longitude_c";
    public static final String PLOT_GPS_POINT_ALTITUDE_FIELD = "altitude_c";
    public static final String PLOT_GPS_POINT_PRECISION_FIELD = "precision_c";

    public static final String TYPE_FIELD_NAME = "type_c";

    public static final String PLOT_EXTERNAL_ID_FIELD = "plot_external_id_c";

    public static final String EXTERNAL_ID_FIELD = "external_id_c";
    public static final String PLOT_NAME_FIELD = "name_c";
    public static final String PLOT_AGE_FIELD = "age_c";
    public static final String PLOT_AREA_FIELD = "area_c";
    public static final String PLOT_EST_PROD_FIELD = "estimated_production_kg_c";
    public static final String OBSERVATION_TABLE = "observation_c";
    public static final String DIAGONOSTIC_MONITORING_TABLE = "diagnostic_monitoring_c";
    public static final String DIAGONOSTIC_MONITORING_EXTERNAL_ID_C = "diagnostic_monitoring_external_id_c";

    public static final String LABOR_DAYS = "laborDays";
    public static final String LABOR_COSTS = "laborCosts";
    public static final String SUPPLIES_COSTS = "suppliesCosts";
    public static final String STATUS_CODE_SUCCESS = "success";
    public static final String STATUS_CODE_FAILED = "failed";
    public static final String DB_NAME = "fdp.db";
    public static final String TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss";
    public static final String DATE_FORMAT = "yyyyMMdd";
    public static final String PREF_NAME = "fdp_pref";
    public static final String FARMER_SUBMITTED_YES = "YES";
    public static final long NULL_INDEXL = -1L;
    public static final int NULL_INDEX = -1;
    public static final int API_STATUS_CODE_LOCAL_ERROR = 0;
    public static final String TYPE_TEXT = "text";
    public static final String TYPE_NUMBER = "number";
    public static final String TYPE_NUMBER_DECIMAL = "decimal";
    public static final String TYPE_COMPLEX_CALCULATION = "complex calculation";
    public static final String FORMULA_TYPE_COMPLEX_FORMULA = "complex formula";
    public static final String ANSWERS = "fpd_Answer__c";
    public static final String EMPTY_STRING = "";
    public static final String RESPONSE_SUCCESS = "0";
    public static final String RESOPNSE_ERROR = "1";
    public static final String SUBMISSION = "submission";
    public static final String RESPONSE_CODE = "responseCode";
    public static final String FARMER_ID = "farmerId";
    public static final int SYNC_STATUS_COMPLETE = 0;
    public static final int SYNC_STATUS_PARTIAL_SYNC = 1;
    public static final int SYNC_STATUS_NO_SYNC = -1;
    public static final String ID = "Id";
    public static final String NULL_STRING = "null";
    public static final String TYPE_LOCATION = "geolocation";
    public static final String TYPE_PHOTO = "photo";
    public static final String TYPE_SELECTABLE = "single select";
    public static final String TYPE_MULTI_SELECTABLE = "multi select";
    public static final String TYPE_CHECKBOX = "checkbox";
    public static final String TYPE_TIMEPICKER = "timePicker";
    public static final String TYPE_DATEPICKER = "date";
    public static final String TYPE_FORMULA = "formula";
    public static final String TYPE_MATH_FORMULA = "math formula";
    public static final int BATCH_NO = 30;
    public static final String TYPE_LOGIC_FORMULA = "logic formula";
    public static final String SHOW = "false";
    public static final String HIDE = "true";
    public static final String FARMER_PROFILE = "farmer profile";
    public static final String ADOPTION_OBSERVATION_RESULTS = "adoption observation results";
    public static final String AGGREGATE_ECONOMIC_RESULTS = "agregate economic results";
    public static final String OTHER = "other";
    public static final String ADDITIONAL_INTERVENTION = "additional intervention";
    public static final String ADOPTION_OBSERVATIONS = "adoption observations";
    public static final String PRODUCTIVE_PROFILE = "productive profile";
    public static final String FARMING_ECONOMIC_PROFILE = "farming economic profile";
    public static final String LABOUR_FORM = "Labor Form";

    public static final String SOCIO_ECONOMIC_PROFILE = "socioeconomic profile";
    public static final String FAMILY_MEMBERS = "family members";
    public static final String PLOT_RESULTS = "plot results";
    public static final String PLOT_INFORMATION = "plot information";
    public static final String AO_MONITORING = "ao monitoring";
    public static final String AO_MONITORING_RESULT = "ao monitoring result";
    public static final String MONITORING_PLOT_INFORMATION = "monitoring plot information";
    public static final String COMPETENCE_MONITORING = "competence monitoring";
    public static final String FAILURE_MONITORING = "failure monitoring";
    public static final String FDP_STATUS = "fdp status";
    public static final String DIAGNOSTIC = "diagnostic";
    public static final String MONITORING = "monitoring";
    public static final String DIAGNOSTIC_MONITORING = "diagnostic monitoring";
    public static final String TAG_TITLE_TEXT_VIEW = "titleTag";
    public static final String BUTTON_VIEW = "buttonTag";
    public static final String TAG_OTHER_TEXT_VIEW = "textTag";
    public static final String TAG_CALCULATION = "calculationTag";
    public static final String TAG_VIEW = "viewTag";
    public static final String TAG_RESULTS = "resultsTag";
    public static final String NO_MONITORING_PLACE_HOLDER = "N/A - Please complete monitoring A0";
    public static final String YES = "yes";
    public static final String NO = "no";
    public static int TABLET_COLUMN_COUNT = 6;
    public static int PHONE_COLUMN_COUNT = 4;
    public static int CAMERA_INTENT = 1004;
    public static int PERMISSION_CAMERA = 1005;
    public static String ROOT_DIR = Environment.getExternalStorageDirectory() + File.separator + ".fdpkasapin";
    public static String CRASH_REPORTS_DIR = AppConstants.ROOT_DIR + File.separator + "crashReports";
    public static String DATABASE_BACKUP_DIR = AppConstants.ROOT_DIR + File.separator + "databaseBackups";
    public static String IS_USER_SIGNED_IN = "isUserSignedIn";
    public static String IS_RETAILER_SIGNED_IN = "isUserSignedIn";
    public static String IS_GAME_CENTER_SIGNED_IN = "isUserSignedIn";
    public static String USERS_PHONE = "phoneNo";
    public static String USER_NAME = "userName";
    public static String USER_AGE = "userAge";
    public static String USER_EMAIL = "userEmail";
    public static String USER_UID = "userUid";
    public static String USER_TOKEN = "userToken";
    public static String USER_PHOTO_LOCAL_URL = "userProfilePhotoLocal";
    public static String USER_PHOTO_CLOUD_URL = "userProfilePhotoCloud";
    public static String IS_NIGHT_MODE = "isNightMode";
    public static int LAST_SELECTED_FRAGMENT = 1;
    public static int SYNC_OK = 1;
    public static int SYNC_NOT_OK = 0;

    public static String RECOMMENDATION_CALCULATION_TYPE_COST = "Cost";
    public static String RECOMMENDATION_NO_FDP = "incomplete ao";
    public static String MODULE_TYPE_DIAGNOSTIC = "Diagnostic";
    public static String MODULE_TYPE_MONITORING = "Monitoring";



    //Token Constants
    public static int TOKEN_IF = 1;
    public static int TOKEN_VARIABLE = 2;
    public static int TOKEN_BRAKET_CLOSED = 4;
    public static int TOKEN_BRACKET_OPEN = 5;
    public static int TOKEN_ARITHMETIC_PLUS_MINUS = 6;
    public static int TOKEN_ARITHMETIC_MULTI_DIV = 6;
    public static int TOKEN_INT = 7;
    public static int TOKEN_MUL_DIV = 8;
    public static int TOKEN_EXP = 9;
    public static int TOKEN_MULTIPLIER = 10;
    public static int TOKEN_CHAR = 11;
    public static int TOKEN_OPEN_BRACES = 16;
    public static int TOKEN_CLOSE_BRACES = 17;
    public static int TOKEN_ELSE = 18;
    public static int TOKEN_QUOTATION = 19;
    public static int TOKEN_OPERATOR = 3;
    public static int TOKEN_OPERATOR_OR = 3;
    public static int TOKEN_OPERATOR_AND = 3;
    public static int TOKEN_OPERATOR_EQUAL_TO = 3;
    public static int TOKEN_OPERATOR_GREATER_THAN = 3;
    public static int TOKEN_OPERATOR_LESS_THAN = 3;
    public static int TOKEN_OPERATOR_GREATER_THAN_EQUALS = 3;
    public static int TOKEN_OPERATOR_LESS_THAN_EQUALS = 3;


    private AppConstants() {
    }

}
