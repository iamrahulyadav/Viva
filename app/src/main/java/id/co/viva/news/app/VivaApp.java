package id.co.viva.news.app;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import id.co.viva.news.app.connection.ConnectionDetector;

/**
 * Created by rezarachman on 02/10/14.
 */
public class VivaApp extends Application {

    private RequestQueue mRequestQueue;
    private ConnectionDetector mConnectionDetector;
    private static VivaApp mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized VivaApp getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public ConnectionDetector getConnectionStatus() {
        if(mConnectionDetector == null) {
            mConnectionDetector = new ConnectionDetector(getInstance());
        }
        return mConnectionDetector;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? Constant.TAG : tag);
        getRequestQueue().add(req);
    }

}
