package id.co.viva.news.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

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
                            Intent intent = new Intent(getApplicationContext(), ActLanding.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
                            finish();
                        }
                    }, 3000);
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
                            Intent intent = new Intent(getApplicationContext(), ActLanding.class);
                            startActivity(intent);
                            finish();
                        }
                    }, 3000);
        }
    }

}
