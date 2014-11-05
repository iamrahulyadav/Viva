package id.co.viva.news.app.services;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.VivaApp;
import id.co.viva.news.app.model.DeviceInfo;

/**
 * Created by reza on 03/11/14.
 */
public class GCM {

    private static GCM instance;
    private Activity activity;
    private GoogleCloudMessaging gcm;
    private final String package_name;
    private final String sender_id;
    private final String backend_server_url;
    private DeviceInfo deviceInfo;

    public static GCM getInstance(Activity activity) {
        if(instance == null) instance = new GCM(activity);
        return instance;
    }

    public GCM(Activity activity) {
        this.activity = activity;
        this.package_name = ((Context) activity).getPackageName();
        this.sender_id = Constant.GCM_SENDER_ID;
        this.backend_server_url = Constant.GCM_URL_BACKEND_SERVER;
        deviceInfo = new DeviceInfo(activity);
    }

    public void doRegistration() {
        checkRegistrationId();
        if(!Preferences.isGCMRegGCMServer() || !Preferences.isGCMRegBackendServer()) {
            if(checkPlayServices()) {
                if(!Preferences.isGCMRegGCMServer())
                    registerToGCM();
                if(Preferences.isGCMRegGCMServer() && !Preferences.isGCMRegBackendServer())
                    registerToBackend();
                Preferences.setGCMRegAppVersion(deviceInfo.getAppVersion());
                Preferences.storeSharedPreferencesGCM(activity);
            } else {
                Log.i(Constant.TAG_GCM, "No Play Services");
            }
        } else {
            Log.i(Constant.TAG_GCM, "Registered");
        }
    }

    private boolean checkPlayServices() {
        Log.i(Constant.TAG_GCM, "CheckPlayServices()");
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity, Constant.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(Constant.TAG_GCM, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    private void checkRegistrationId() {
        Log.i(Constant.TAG_GCM, "checkRegistrationId()");
        if (Preferences.getGCMRegId().equals("") || Preferences.getGCMRegAppVersion() != deviceInfo.getAppVersion()) {
            Log.i(Constant.TAG_GCM, "Registration Id not found or app version changed.");
            Preferences.setGCMClientId("");
            Preferences.setGCMRegId("");
            Preferences.setGCMRegGCMServer(false);
            Preferences.setGCMRegBackendServer(false);
        }
    }

    private void registerToGCM() {
        Log.i(Constant.TAG_GCM, "registerToGCM() = " + sender_id);
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(activity);
            }
            Preferences.setGCMRegId(gcm.register(sender_id));
            Log.i(Constant.TAG_GCM, "Device registered, registration ID = " + Preferences.getGCMRegId());
            Preferences.setGCMRegGCMServer(true);
        } catch(IOException e) {
            Log.e(Constant.TAG_GCM, e.getMessage());
        }
    }

    private void registerToBackend() {
        Log.i(Constant.TAG_GCM, "RegisterToBackend()");
        String url = backend_server_url;
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonObject) {
                        try {
                            JSONObject json = new JSONObject(jsonObject);
                            String status = json.getString("status");
                            String client_id = json.getString("client_id");
                            if(status.equals("1")){
                                Preferences.setGCMClientId(client_id);
                                Preferences.setGCMRegBackendServer(true);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.getMessage();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reg_id", Preferences.getGCMRegId());
                params.put("package_name", package_name);
                params.put("app_version", String.valueOf(deviceInfo.getAppVersion()));
                params.put("os_version", deviceInfo.getOSVersion());
                params.put("model", deviceInfo.getModel());
                params.put("country", deviceInfo.getSIMCountryISO());
                params.put("operator", deviceInfo.getSIMOperatorName());
                params.put("msisdn", deviceInfo.getPhoneFromTelephony());
                return params;
            }
        };
        jsonObjectRequest.setShouldCache(true);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VivaApp.getInstance().addToRequestQueue(jsonObjectRequest, Constant.JSON_REQUEST);
    }

}
