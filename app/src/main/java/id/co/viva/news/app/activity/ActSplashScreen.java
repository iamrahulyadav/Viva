package id.co.viva.news.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.services.GCM;

/**
 * Created by reza on 31/10/14.
 */
public class ActSplashScreen extends Activity {

    private ImageView imageSplash;
    private boolean isInternet = false;
    private GCM gcm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splash_screen);

        isInternet = Global.getInstance(this).getConnectionStatus().isConnectingToInternet();
        gcm = GCM.getInstance(this);

        imageSplash = (ImageView)findViewById(R.id.image_splash);
        imageSplash.setImageResource(R.drawable.icon_launcher);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade);
        imageSplash.startAnimation(fadeInAnimation);

        if(isInternet) {
            new GetRegistrationID().execute();
        } else {
            new Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            goToApplication(Constant.MOVE_APPLICATION);
                        }
                    }, 2000);
        }

    }

    private void goToApplication(String intentType) {
        if (intentType.equals(Constant.MOVE_APPLICATION)) {
            Intent intents = new Intent(getApplicationContext(), ActLanding.class);
            startActivity(intents);
            overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
            finish();
        } else if (intentType.equals(Constant.MOVE_TUTORIAL)) {
            Intent intent = new Intent(getApplicationContext(), ActTutorial.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
            finish();
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
            new Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            if (Global.getInstance(ActSplashScreen.this).getSharedPreferences(ActSplashScreen.this)
                                    .getBoolean(Constant.FIRST_INSTALL_TUTORIAL, true)) {
                                goToApplication(Constant.MOVE_TUTORIAL);
                                Global.getInstance(ActSplashScreen.this).getSharedPreferences(ActSplashScreen.this).
                                        edit().putBoolean(Constant.FIRST_INSTALL_TUTORIAL, false).commit();
                            } else {
                                goToApplication(Constant.MOVE_APPLICATION);
                            }
                        }
                    }, 2000);
        }
    }

}
