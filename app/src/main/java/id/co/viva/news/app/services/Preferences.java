package id.co.viva.news.app.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Set;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.VivaApp;

/**
 * Created by reza on 03/11/14.
 */
public class Preferences {

    private final static String TAG = "VIVA-PREFS";

    public static final int LOGIN_STATUS_OUT = 0;
    public static final int LOGIN_STATUS_IN = 1;
    public static final int LOGIN_TYPE_GUEST = 0;
    public static final int LOGIN_TYPE_VIVA = 1;

    private static final String PROPERTY_GCM_CLIENT_ID = "gcm_client_id";
    private static final String PROPERTY_GCM_REG_ID = "gcm_reg_id";
    private static final String PROPERTY_GCM_REG_APP_VERSION = "gcm_reg_app_version";
    private static final String PROPERTY_GCM_IS_REG_GCM_SERVER = "gcm_is_reg_gcm_server";
    private static final String PROPERTY_GCM_IS_REG_BACKEND_SERVER = "gcm_is_reg_backend_server";
    private static final String PROPERTY_LOGIN_STATUS = "login_status";
    private static final String PROPERTY_LOGIN_TYPE = "login_type";
    private static final String PROPERTY_LOGIN_SOCIAL_ID = "login_social_id";
    private static final String PROPERTY_LOGIN_MEMBER_ID = "login_member_id";
    private static final String PROPERTY_LOGIN_GUEST_ID = "login_guest_id";
    private static final String PROPERTY_LOGIN_EMAIL = "login_email";
    private static final String PROPERTY_LOGIN_FULL_NAME = "login_full_name";
    private static final String PROPERTY_PATH_ACCESS_TOKEN = "path_access_token";
    private static final String PROPERTY_PATH_USER_ID = "path_user_id";
    private static final String PROPERTY_PATH_USER_NAME = "path_user_name";
    private static final String PROPERTY_PATH_USER_EMAIL = "path_user_email";
    private static final String PROPERTY_PATH_USER_PHOTO = "path_user_photo";
    private static final String PROPERTY_FACEBOOK_USER_ID = "facebook_user_id";
    private static final String PROPERTY_FACEBOOK_USER_NAME = "facebook_user_name";
    private static final String PROPERTY_FACEBOOK_USER_EMAIL = "facebook_user_email";
    private static final String PROPERTY_GPLUS_USER_NAME = "gplus_user_name";
    private static final String PROPERTY_GPLUS_USER_EMAIL = "gplus_user_email";
    private static final String PROPERTY_GPLUS_USER_PHOTO = "gplus_user_photo";

    private static String gcm_client_id = "", gcm_reg_id = "";
    private static int gcm_reg_app_version = 0;
    private static boolean gcm_reg_gcm_server = false, gcm_reg_backend_server = false;
    private static int login_status = LOGIN_STATUS_OUT, login_type = LOGIN_TYPE_GUEST, login_social_id = 0, login_guest_id = 0;
    private static String login_member_id = "", login_email = "", login_full_name = "";
    private static String path_access_token = "", path_user_id = "", path_user_name = "", path_user_email = "", path_user_photo = "";
    private static String facebook_user_id = "", facebook_user_name = "", facebook_user_email = "";
    private static String gplus_user_name = "", gplus_user_email = "", gplus_user_photo = "";

    public Preferences() {}

    public static String getGCMClientId() {
        return gcm_client_id;
    }

    public static void setGCMClientId(String gcm_client_id) {
        gcm_client_id = gcm_client_id;
    }

    public static String getGCMRegId() {
        return gcm_reg_id;
    }

    public static void setGCMRegId(String gcm_reg_id) {
        Preferences.gcm_reg_id = gcm_reg_id;
    }

    public static int getGCMRegAppVersion() {
        return gcm_reg_app_version;
    }

    public static void setGCMRegAppVersion(int gcm_reg_app_version) {
        Preferences.gcm_reg_app_version = gcm_reg_app_version;
    }

    public static boolean isGCMRegGCMServer() {
        return gcm_reg_gcm_server;
    }

    public static void setGCMRegGCMServer(boolean gcm_reg_gcm_server) {
        Preferences.gcm_reg_gcm_server = gcm_reg_gcm_server;
    }

    public static boolean isGCMRegBackendServer() {
        return gcm_reg_backend_server;
    }

    public static void setGCMRegBackendServer(boolean gcm_reg_backend_server) {
        Preferences.gcm_reg_backend_server = gcm_reg_backend_server;
    }

    public static int getLoginStatus() {
        return login_status;
    }

    public static void setLoginStatus(int login_status) {
        Preferences.login_status = login_status;
    }

    public static int getLoginType() {
        return login_type;
    }

    public static void setLoginType(int login_type) {
        Preferences.login_type = login_type;
    }

    public static int getLoginSocialId() {
        return login_social_id;
    }

    public static void setLoginSocialId(int login_social_id) {
        Preferences.login_social_id = login_social_id;
    }

    public static String getLoginMemberId() {
        return login_member_id;
    }

    public static void setLoginMemberId(String login_member_id) {
        Preferences.login_member_id = login_member_id;
    }

    public static int getLoginGuestId() {
        return login_guest_id;
    }

    public static void setLoginGuestId(int login_guest_id) {
        Preferences.login_guest_id = login_guest_id;
    }

    public static String getLoginEmail() {
        return login_email;
    }

    public static void setLoginEmail(String login_email) {
        Preferences.login_email = login_email;
    }

    public static String getLoginFullName() {
        return login_full_name;
    }

    public static void setLoginFullName(String login_full_name) {
        Preferences.login_full_name = login_full_name;
    }

    public static String getPathAccessToken() {
        return path_access_token;
    }

    public static void setPathAccessToken(String path_access_token) {
        Preferences.path_access_token = path_access_token;
    }

    public static String getPathUserId() {
        return path_user_id;
    }

    public static void setPathUserId(String path_user_id) {
        Preferences.path_user_id = path_user_id;
    }

    public static String getPathUserName() {
        return path_user_name;
    }

    public static void setPathUserName(String path_user_name) {
        Preferences.path_user_name = path_user_name;
    }

    public static String getPathUserEmail() {
        return path_user_email;
    }

    public static void setPathUserEmail(String path_user_email) {
        Preferences.path_user_email = path_user_email;
    }

    public static String getPathUserPhoto() {
        return path_user_photo;
    }

    public static void setPathUserPhoto(String path_user_photo) {
        Preferences.path_user_photo = path_user_photo;
    }

    public static String getFacebookUserId() {
        return facebook_user_id;
    }

    public static void setFacebookUserId(String facebook_user_id) {
        Preferences.facebook_user_id = facebook_user_id;
    }

    public static String getFacebookUserName() {
        return facebook_user_name;
    }

    public static void setFacebookUserName(String facebook_user_name) {
        Preferences.facebook_user_name = facebook_user_name;
    }

    public static String getFacebookUserEmail() {
        return facebook_user_email;
    }

    public static void setFacebookUserEmail(String facebook_user_email) {
        Preferences.facebook_user_email = facebook_user_email;
    }

    public static String getGPlusUserName() {
        return gplus_user_name;
    }

    public static void setGPlusUserName(String gplus_user_name) {
        Preferences.gplus_user_name = gplus_user_name;
    }

    public static String getGPlusUserEmail() {
        return gplus_user_email;
    }

    public static void setGPlusUserEmail(String gplus_user_email) {
        Preferences.gplus_user_email = gplus_user_email;
    }

    public static String getGPlusUserPhoto() {
        return gplus_user_photo;
    }

    public static void setGPlusUserPhoto(String gplus_user_photo) {
        Preferences.gplus_user_photo = gplus_user_photo;
    }

    public static void loadSharedPreferences(Context context) {
        Log.e(TAG, "loadSharedPreferences()");

        SharedPreferences Preferences = VivaApp.getInstance().getSharedPreferences(context);
        gcm_client_id = Preferences.getString(PROPERTY_GCM_CLIENT_ID, "");
        gcm_reg_id = Preferences.getString(PROPERTY_GCM_REG_ID, "");
        gcm_reg_app_version = Preferences.getInt(PROPERTY_GCM_REG_APP_VERSION, 0);
        gcm_reg_gcm_server = Preferences.getBoolean(PROPERTY_GCM_IS_REG_GCM_SERVER, false);
        gcm_reg_backend_server = Preferences.getBoolean(PROPERTY_GCM_IS_REG_BACKEND_SERVER, false);
        login_status = Preferences.getInt(PROPERTY_LOGIN_STATUS, LOGIN_STATUS_OUT);
        login_type = Preferences.getInt(PROPERTY_LOGIN_TYPE, LOGIN_TYPE_GUEST);
        login_social_id = Preferences.getInt(PROPERTY_LOGIN_SOCIAL_ID, 0);
        login_member_id = Preferences.getString(PROPERTY_LOGIN_MEMBER_ID, "");
        login_guest_id = Preferences.getInt(PROPERTY_LOGIN_GUEST_ID, 0);
        login_email = Preferences.getString(PROPERTY_LOGIN_EMAIL, "");
        login_full_name = Preferences.getString(PROPERTY_LOGIN_FULL_NAME, "");
        path_access_token = Preferences.getString(PROPERTY_PATH_ACCESS_TOKEN, "");
        path_user_id = Preferences.getString(PROPERTY_PATH_USER_ID, "");
        path_user_name = Preferences.getString(PROPERTY_PATH_USER_NAME, "");
        path_user_email = Preferences.getString(PROPERTY_PATH_USER_EMAIL, "");
        path_user_photo = Preferences.getString(PROPERTY_PATH_USER_PHOTO, "");
        facebook_user_id = Preferences.getString(PROPERTY_FACEBOOK_USER_ID, "");
        facebook_user_name = Preferences.getString(PROPERTY_FACEBOOK_USER_NAME, "");
        facebook_user_email = Preferences.getString(PROPERTY_FACEBOOK_USER_EMAIL, "");
        gplus_user_name = Preferences.getString(PROPERTY_GPLUS_USER_NAME, "");
        gplus_user_email = Preferences.getString(PROPERTY_GPLUS_USER_EMAIL, "");
        gplus_user_photo = Preferences.getString(PROPERTY_GPLUS_USER_PHOTO, "");

        Set<String> keys = Preferences.getAll().keySet();
        for(String key : keys) {
            Log.d(TAG, key);
        }
    }

    public static void storeSharedPreferencesGCM(Context context) {
        Log.e(TAG, "storeSharedPreferencesGCM()");

        SharedPreferences Preferences = VivaApp.getInstance().getSharedPreferences(context);
        SharedPreferences.Editor editor = Preferences.edit();
        editor.putString(PROPERTY_GCM_CLIENT_ID, gcm_client_id);
        editor.putString(PROPERTY_GCM_REG_ID, gcm_reg_id);
        editor.putInt(PROPERTY_GCM_REG_APP_VERSION, gcm_reg_app_version);
        editor.putBoolean(PROPERTY_GCM_IS_REG_GCM_SERVER, gcm_reg_gcm_server);
        editor.putBoolean(PROPERTY_GCM_IS_REG_BACKEND_SERVER, gcm_reg_backend_server);
        editor.commit();

        Set<String> keys = Preferences.getAll().keySet();
        for(String key : keys) {
            Log.d(TAG, key);
        }
    }

    public static void storeSharedPreferencesLogin(Context context) {
        Log.e(TAG, "storeSharedPreferencesLogin()");

        SharedPreferences Preferences = VivaApp.getInstance().getSharedPreferences(context);
        SharedPreferences.Editor editor = Preferences.edit();
        editor.putInt(PROPERTY_LOGIN_STATUS, login_status);
        editor.putInt(PROPERTY_LOGIN_TYPE, login_type);
        editor.putInt(PROPERTY_LOGIN_SOCIAL_ID, login_social_id);
        editor.putString(PROPERTY_LOGIN_MEMBER_ID, login_member_id);
        editor.putInt(PROPERTY_LOGIN_GUEST_ID, login_guest_id);
        editor.putString(PROPERTY_LOGIN_EMAIL, login_email);
        editor.putString(PROPERTY_LOGIN_FULL_NAME, login_full_name);
        editor.commit();

        Set<String> keys = Preferences.getAll().keySet();
        for(String key : keys) {
            Log.d(TAG, key);
        }
    }

    public static void removeSharedPreferencesLogin(Context context) {
        Log.e(TAG, "removeSharedPreferencesLogin()");

        SharedPreferences Preferences = VivaApp.getInstance().getSharedPreferences(context);
        SharedPreferences.Editor editor = Preferences.edit();
        editor.remove(PROPERTY_LOGIN_STATUS);
        editor.remove(PROPERTY_LOGIN_TYPE);
        editor.remove(PROPERTY_LOGIN_SOCIAL_ID);
        editor.remove(PROPERTY_LOGIN_MEMBER_ID);
        editor.remove(PROPERTY_LOGIN_GUEST_ID);
        editor.remove(PROPERTY_LOGIN_EMAIL);
        editor.remove(PROPERTY_LOGIN_FULL_NAME);
        editor.commit();

        Set<String> keys = Preferences.getAll().keySet();
        for(String key : keys) {
            Log.d(TAG, key);
        }
    }

    public static void storeSharedPreferencesPath(Context context) {
        Log.e(TAG, "storeSharedPreferencesPath()");

        SharedPreferences Preferences = VivaApp.getInstance().getSharedPreferences(context);
        SharedPreferences.Editor editor = Preferences.edit();
        editor.putString(PROPERTY_PATH_ACCESS_TOKEN, path_access_token);
        editor.putString(PROPERTY_PATH_USER_ID, path_user_id);
        editor.putString(PROPERTY_PATH_USER_NAME, path_user_name);
        editor.putString(PROPERTY_PATH_USER_EMAIL, path_user_email);
        editor.putString(PROPERTY_PATH_USER_PHOTO, path_user_photo);
        editor.commit();

        Set<String> keys = Preferences.getAll().keySet();
        for(String key : keys) {
            Log.d(TAG, key);
        }
    }

    public static void removeSharedPreferencesPath(Context context) {
        Log.e(TAG, "removeSharedPreferencesLogin()");

        SharedPreferences Preferences = VivaApp.getInstance().getSharedPreferences(context);
        SharedPreferences.Editor editor = Preferences.edit();
        editor.remove(PROPERTY_PATH_ACCESS_TOKEN);
        editor.remove(PROPERTY_PATH_USER_ID);
        editor.remove(PROPERTY_PATH_USER_NAME);
        editor.remove(PROPERTY_PATH_USER_EMAIL);
        editor.remove(PROPERTY_PATH_USER_PHOTO);
        editor.commit();

        Set<String> keys = Preferences.getAll().keySet();
        for(String key : keys) {
            Log.d(TAG, key);
        }
    }

    public static void storeSharedPreferencesFacebook(Context context) {
        Log.e(TAG, "storeSharedPreferencesFacebook()");

        SharedPreferences Preferences = VivaApp.getInstance().getSharedPreferences(context);
        SharedPreferences.Editor editor = Preferences.edit();
        editor.putString(PROPERTY_FACEBOOK_USER_ID, facebook_user_id);
        editor.putString(PROPERTY_FACEBOOK_USER_NAME, facebook_user_name);
        editor.putString(PROPERTY_FACEBOOK_USER_EMAIL, facebook_user_email);
        editor.commit();

        Set<String> keys = Preferences.getAll().keySet();
        for(String key : keys) {
            Log.d(TAG, key);
        }
    }

    public static void removeSharedPreferencesFacebook(Context context) {
        Log.e(TAG, "removeSharedPreferencesLogin()");

        SharedPreferences Preferences = VivaApp.getInstance().getSharedPreferences(context);
        SharedPreferences.Editor editor = Preferences.edit();
        editor.remove(PROPERTY_FACEBOOK_USER_ID);
        editor.remove(PROPERTY_FACEBOOK_USER_NAME);
        editor.remove(PROPERTY_FACEBOOK_USER_EMAIL);
        editor.commit();

        Set<String> keys = Preferences.getAll().keySet();
        for(String key : keys) {
            Log.d(TAG, key);
        }
    }

    public static void storeSharedPreferencesGPlus(Context context) {
        Log.e(TAG, "storeSharedPreferencesGPlus()");

        SharedPreferences Preferences = VivaApp.getInstance().getSharedPreferences(context);
        SharedPreferences.Editor editor = Preferences.edit();
        editor.putString(PROPERTY_GPLUS_USER_NAME, gplus_user_name);
        editor.putString(PROPERTY_GPLUS_USER_EMAIL, gplus_user_email);
        editor.putString(PROPERTY_GPLUS_USER_PHOTO, gplus_user_photo);
        editor.commit();

        Set<String> keys = Preferences.getAll().keySet();
        for(String key : keys) {
            Log.d(TAG, key);
        }
    }

    public static void removeSharedPreferencesGPlus(Context context) {
        Log.e(TAG, "removeSharedPreferencesLogin()");

        SharedPreferences Preferences = VivaApp.getInstance().getSharedPreferences(context);
        SharedPreferences.Editor editor = Preferences.edit();
        editor.remove(PROPERTY_GPLUS_USER_NAME);
        editor.remove(PROPERTY_GPLUS_USER_EMAIL);
        editor.remove(PROPERTY_GPLUS_USER_PHOTO);
        editor.commit();

        Set<String> keys = Preferences.getAll().keySet();
        for(String key : keys) {
            Log.d(TAG, key);
        }
    }

}
