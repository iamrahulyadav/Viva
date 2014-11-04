package id.co.viva.news.app.services;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;

import id.co.viva.news.app.VivaApp;

/**
 * Created by reza on 03/11/14.
 */
public class Analytics {

    public Analytics() {}

    public void getAnalyticByATInternet(String page) {
        VivaApp.getInstance().getAtParams().setPage(page);
        VivaApp.getInstance().getAtParams().xt_sendTag();
    }

    public void getAnalyticByGoogleAnalytic(String page) {
        VivaApp.getInstance().getEasyTracker().set(Fields.SCREEN_NAME, page);
        VivaApp.getInstance().getEasyTracker().set(Fields.APP_VERSION,
                VivaApp.getInstance().getEasyTracker().get(Fields.APP_VERSION));
        VivaApp.getInstance().getEasyTracker().send(MapBuilder.createAppView().build());
    }

}
