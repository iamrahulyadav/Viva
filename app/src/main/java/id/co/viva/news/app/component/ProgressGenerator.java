package id.co.viva.news.app.component;

import android.os.Handler;

import com.dd.CircularProgressButton;

import java.util.Random;

import id.co.viva.news.app.interfaces.OnProgressDoneListener;

/**
 * Created by reza on 03/12/14.
 */
public class ProgressGenerator {

    private OnProgressDoneListener onProgressDoneListener;
    private Random random = new Random();
    private int mProgress;
    private String mType;

    public ProgressGenerator(OnProgressDoneListener onProgressDoneListener, String mType) {
        this.onProgressDoneListener = onProgressDoneListener;
        this.mType = mType;
    }

    public void start(final CircularProgressButton button) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mProgress += 10;
                button.setProgress(mProgress);
                if (mProgress < 100) {
                    handler.postDelayed(this, generateDelay());
                    onProgressDoneListener.onProgressProcess();
                } else {
                    onProgressDoneListener.onProgressDone(mType);
                }
            }
        }, generateDelay());
    }

    private int generateDelay() {
        return random.nextInt(1000);
    }

}
