package id.co.viva.news.app;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.at.ATParams;
import com.at.ATTag;
import com.google.analytics.tracking.android.EasyTracker;

import id.co.viva.news.app.connection.ConnectionDetector;

/**
 * Created by rezarachman on 02/10/14.
 */
public class VivaApp extends Application {

    private RequestQueue mRequestQueue;
    private ConnectionDetector mConnectionDetector;
    private static VivaApp mInstance;
    private EasyTracker easyTracker;
    private ATTag atTag;
    private ATParams atParams; 

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized VivaApp getInstance() {
        return mInstance;
    }

    public EasyTracker getEasyTracker() {
        if(easyTracker == null) {
            easyTracker = EasyTracker.getInstance(getInstance());
        }
        return easyTracker;
    }

    public ATParams getAtParams() {
        if(atTag == null) {
            atTag = ATTag.init(getInstance(), Constant.AT_SUB_DOMAIN,
                    Constant.AT_SITE_ID, Constant.AT_SUB_SITE);
            atTag.setLogDomain(Constant.AT_LOG_DOMAIN);
        }
        if(atParams == null) {
            atParams = new ATParams();
        }
        return atParams;
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
