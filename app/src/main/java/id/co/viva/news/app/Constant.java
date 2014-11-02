package id.co.viva.news.app;

import android.app.ActionBar;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by rezarachman on 02/10/14.
 */
public class Constant {

    public static final int TIME_OUT = 3000;

    public static final String TAG = VivaApp.class.getSimpleName();
    private static String BASE_URL = "http://www.viva.co.id/rss/api/mobile/";
    public static String URL_HOMEPAGE = BASE_URL + "homepage";
    public static String URL_KANAL_NEWS = BASE_URL + "kanal_news";
    public static String URL_KANAL_BOLA = BASE_URL + "kanal_bola";
    public static String URL_KANAL_LIFE = BASE_URL + "kanal_life";
    public static String URL_KANAL_DETAIL = BASE_URL + "kanal/";
    public static String URL_DETAIL = BASE_URL + "detail/";
    public static String URL_SEARCH = BASE_URL + "search/";
    public static final String JSON_REQUEST = "json_obj_req";

    public static final String response = "response";
    public static final String headlines = "headlines";
    public static final String related_article = "related_article";
    public static final String detail = "detail";
    public static final String url = "url";
    public static final String search = "search";
    public static final String id = "id";
    public static final String title = "title";
    public static final String slug = "slug";
    public static final String kanal = "kanal";
    public static final String image_url = "image_url";
    public static final String date_publish = "date_publish";
    public static final String source = "source";
    public static final String reporter_name = "reporter_name";
    public static final String content = "content";

    public static final String article_id = "article_id";
    public static final String related_article_id = "related_article_id";
    public static final String related_title = "related_title";
    public static final String related_channel_level_1_id = "related_channel_level_1_id";
    public static final String channel_id = "channel_id";
    public static final String related_date_publish = "related_date_publish";
    public static final String image = "image";

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new ActionBar.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public static String getTimeAgo(long time, Context ctx) {
        if (time < 1000000000000L) {
            time *= 1000;
        }
        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "baru saja";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "satu menit yang lalu";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " menit yang lalu";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "satu jam yang lalu";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " jam yang lalu";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "kemarin";
        } else {
            return diff / DAY_MILLIS + " hari yang lalu";
        }
    }

}
