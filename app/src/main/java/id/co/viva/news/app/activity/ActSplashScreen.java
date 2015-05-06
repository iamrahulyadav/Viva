package id.co.viva.news.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.ads.AdsConfig;
import id.co.viva.news.app.ads.AdsConfigList;
import id.co.viva.news.app.model.Ads;
import id.co.viva.news.app.services.GCM;

/**
 * Created by reza on 31/10/14.
 */
public class ActSplashScreen extends Activity {

    private ImageView imageSplash;
    private boolean isInternet = false;
    private ArrayList<Ads> adsList;
    private AdsConfigList mConfigList;
    private GCM gcm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splash_screen);

        //Check connection
        isInternet = Global.getInstance(this)
                .getConnectionStatus().isConnectingToInternet();

        //Initiate GCM Service
        gcm = GCM.getInstance(this);

        //Initiate Ad list
        mConfigList = new AdsConfigList();

        //Set Animation Image
        imageSplash = (ImageView)findViewById(R.id.image_splash);
        imageSplash.setImageResource(R.drawable.icon_launcher);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade);
        imageSplash.startAnimation(fadeInAnimation);

        //Check existing connection
        if (isInternet) {
            new GetRegistrationID().execute();
        } else {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    checkFirstTime(Constant.MOVE_APPLICATION);
                }
            }, 1000);
        }
    }

    private void checkFirstTime(String intentType) {
        if (intentType.equals(Constant.MOVE_APPLICATION)) {
            showAds();
        } else if (intentType.equals(Constant.MOVE_TUTORIAL)) {
            moveTo(ActTutorial.class);
        }
    }

    private void moveTo(Class<?> aClass) {
        Intent intent = new Intent(getApplicationContext(), aClass);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
        finish();
    }

    //TODO Getting ads
    private void getAds() {
        adsList = mConfigList.getAdList(this);
    }

    private void showAds() {
        if (isInternet) {
            InterstitialAd interstitialAd = new InterstitialAd(this);
            AdsConfig adsConfig = new AdsConfig();
            adsConfig.setAdsInterstitial(this, interstitialAd, Constant.unitIdInterstitialOpen,
                    ActLanding.class, Constant.ADS_TYPE_OPENING, null, null);
        } else {
            moveTo(ActLanding.class);
        }
    }

    private class GetRegistrationID extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            gcm.doRegistration();
            return null;
        }
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    loadMainConfig();
                }
            }, 1000);
        }
    }

    private void loadMainConfig() {
        StringRequest request = new StringRequest(Request.Method.GET, Constant.MAIN_CONFIG,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        JSONArray listAds = jsonObject.getJSONArray(Constant.adses);
                        if (listAds.length() > 0) {
                            for (int i=0; i<listAds.length(); i++) {
                                JSONObject data = listAds.getJSONObject(i);
                                String screen_name = data.getString(Constant.screen_name);
                                String unit_id = data.getString(Constant.unit_id);
                                int type = data.getInt(Constant.type);
                                int position = data.getInt(Constant.position);
                                mConfigList.addAds(ActSplashScreen.this, new Ads(screen_name, type, position, unit_id));
                            }
                        }
                        checkPreferences();
                    } catch (JSONException je) {
                        je.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    checkPreferences();
                }
        });
        request.setShouldCache(true);
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT_LONG,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Global.getInstance(this).getRequestQueue().getCache().invalidate(Constant.MAIN_CONFIG, true);
        Global.getInstance(this).getRequestQueue().getCache().get(Constant.MAIN_CONFIG);
        Global.getInstance(this).addToRequestQueue(request, Constant.JSON_REQUEST);
    }

    private void checkPreferences() {
        if (Global.getInstance(ActSplashScreen.this).getSharedPreferences(ActSplashScreen.this)
                .getBoolean(Constant.FIRST_INSTALL_TUTORIAL, true)) {
            checkFirstTime(Constant.MOVE_TUTORIAL);
            Global.getInstance(ActSplashScreen.this).getSharedPreferences(ActSplashScreen.this).
                    edit().putBoolean(Constant.FIRST_INSTALL_TUTORIAL, false).commit();
        } else {
            checkFirstTime(Constant.MOVE_APPLICATION);
        }
    }

}
