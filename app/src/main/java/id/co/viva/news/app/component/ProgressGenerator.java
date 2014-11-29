package id.co.viva.news.app.component;

import android.os.Handler;

import com.dd.processbutton.ProcessButton;

import java.util.Random;

import id.co.viva.news.app.interfaces.OnCompleteListener;

/**
 * Created by reza on 28/11/14.
 */
public class ProgressGenerator {

    private OnCompleteListener mListener;
    private int mProgress;
    private Random random = new Random();

    public ProgressGenerator(OnCompleteListener listener) {
        mListener = listener;
    }

    private int generateDelay() {
        return random.nextInt(1000);
    }

    public void start(final ProcessButton button) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mProgress += 10;
                button.setProgress(mProgress);
                if (mProgress < 100) {
                    handler.postDelayed(this, generateDelay());
                } else {
                    mListener.onComplete();
                }
            }
        }, generateDelay());
    }

}
