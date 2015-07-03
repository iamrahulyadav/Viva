package id.co.viva.news.app.model;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.R;
import id.co.viva.news.app.activity.ActBrowser;
import id.co.viva.news.app.activity.ActDetailChannel;
import id.co.viva.news.app.activity.ActDetailChannelLife;
import id.co.viva.news.app.activity.ActDetailChannelNews;
import id.co.viva.news.app.activity.ActDetailContentDefault;
import id.co.viva.news.app.activity.ActSearchResult;

/**
 * Created by reza on 15/04/15.
 */
public class ChannelMapper {

    private Activity mActivity;
    private Intent mIntent;

    public ChannelMapper(Activity activity) {
        this.mActivity = activity;
    }

    public void checkDetailArticle(String article_id, String content) {
        mIntent = new Intent(mActivity, ActDetailContentDefault.class);
        mIntent.putExtra("id", article_id);
        mIntent.putExtra("type", mActivity.getResources().getString(R.string.label_item_navigation_scan_berita));
        mIntent.putExtra("shared_url", content);
    }

    public void checkSearchURL(String keyword) {
        mIntent = new Intent(mActivity, ActSearchResult.class);
        mIntent.putExtra("keyword", keyword);
    }

    public void checkNonVivaURL(String content) {
        mIntent = new Intent(mActivity, ActBrowser.class);
        mIntent.putExtra("url", content);
    }

    public void checkChannelUrl(String url) {
        if (url.equalsIgnoreCase(Constant.CHANNEL_INDEX_NEWS)) {
            mIntent = new Intent(mActivity, ActDetailChannelNews.class);
            mIntent.putExtra("id", "1");
            mIntent.putExtra("channel_title", Constant.AllNews);
        } else if (url.equalsIgnoreCase(Constant.CHANNEL_POLITIK)) {
            mIntent = new Intent(mActivity, ActDetailChannelNews.class);
            mIntent.putExtra("id", "1");
            mIntent.putExtra("channel_title", Constant.LABEL_CHANNEL_POLITIK);
        } else if (url.equalsIgnoreCase(Constant.CHANNEL_BISNIS)) {
            mIntent = new Intent(mActivity, ActDetailChannelNews.class);
            mIntent.putExtra("id", "2");
            mIntent.putExtra("channel_title", Constant.LABEL_CHANNEL_BISNIS);
        } else if (url.equalsIgnoreCase(Constant.CHANNEL_NASIONAL)) {
            mIntent = new Intent(mActivity, ActDetailChannelNews.class);
            mIntent.putExtra("id", "5");
            mIntent.putExtra("channel_title", Constant.LABEL_CHANNEL_NASIONAL);
        } else if (url.equalsIgnoreCase(Constant.CHANNEL_METRO)) {
            mIntent = new Intent(mActivity, ActDetailChannelNews.class);
            mIntent.putExtra("id", "4");
            mIntent.putExtra("channel_title", Constant.LABEL_CHANNEL_METRO);
        } else if (url.equalsIgnoreCase(Constant.CHANNEL_DUNIA)) {
            mIntent = new Intent(mActivity, ActDetailChannelNews.class);
            mIntent.putExtra("id", "12");
            mIntent.putExtra("channel_title", Constant.LABEL_CHANNEL_DUNIA);
        } else if (url.equalsIgnoreCase(Constant.CHANNEL_TEKNOLOGI)) {
            mIntent = new Intent(mActivity, ActDetailChannelNews.class);
            mIntent.putExtra("id", "3");
            mIntent.putExtra("channel_title", Constant.LABEL_CHANNEL_TEKNOLOGI);
        } else if (url.equalsIgnoreCase(Constant.CHANNEL_OTOMOTIF)) {
            mIntent = new Intent(mActivity, ActDetailChannelNews.class);
            mIntent.putExtra("id", "11");
            mIntent.putExtra("channel_title", Constant.LABEL_CHANNEL_OTOMOTIF);
        } else if (url.equalsIgnoreCase(Constant.CHANNEL_SOROT)) {
            mIntent = new Intent(mActivity, ActDetailChannelNews.class);
            mIntent.putExtra("id", "22");
            mIntent.putExtra("channel_title", Constant.LABEL_CHANNEL_SOROT);
        } else if (url.equalsIgnoreCase(Constant.CHANNEL_WAWANCARA)) {
            mIntent = new Intent(mActivity, ActDetailChannelNews.class);
            mIntent.putExtra("id", "24");
            mIntent.putExtra("channel_title", Constant.LABEL_CHANNEL_WAWANCARA);
        } else if (url.equalsIgnoreCase(Constant.CHANNEL_FOKUS)) {
            mIntent = new Intent(mActivity, ActDetailChannelNews.class);
            mIntent.putExtra("id", "30");
            mIntent.putExtra("channel_title", Constant.LABEL_CHANNEL_FOKUS);
        } else if (url.equalsIgnoreCase(Constant.CHANNEL_INDEX_BOLA)) {
            mIntent = new Intent(mActivity, ActDetailChannel.class);
            mIntent.putExtra("id", "9");
            mIntent.putExtra("channel_title", Constant.AllNews);
        } else if (url.equalsIgnoreCase(Constant.CHANNEL_LIGA_INDONESIA)) {
            mIntent = new Intent(mActivity, ActDetailChannel.class);
            mIntent.putExtra("id", "901");
            mIntent.putExtra("channel_title", Constant.LABEL_CHANNEL_LIGA_INDONESIA);
        } else if (url.equalsIgnoreCase(Constant.CHANNEL_LIGA_INGGRIS)) {
            mIntent = new Intent(mActivity, ActDetailChannel.class);
            mIntent.putExtra("id", "902");
            mIntent.putExtra("channel_title", Constant.LABEL_CHANNEL_LIGA_INGGRIS);
        } else if (url.equalsIgnoreCase(Constant.CHANNEL_LIGA_ITALIA)) {
            mIntent = new Intent(mActivity, ActDetailChannel.class);
            mIntent.putExtra("id", "903");
            mIntent.putExtra("channel_title", Constant.LABEL_CHANNEL_LIGA_ITALIA);
            Log.i(Constant.TAG, "Liga Italia");
        } else if (url.equalsIgnoreCase(Constant.CHANNEL_LIGA_SPANYOL)) {
            mIntent = new Intent(mActivity, ActDetailChannel.class);
            mIntent.putExtra("id", "904");
            mIntent.putExtra("channel_title", Constant.LABEL_CHANNEL_LIGA_SPANYOL);
        } else if (url.equalsIgnoreCase(Constant.CHANNEL_BOLA_NASIONAL)) {
            mIntent = new Intent(mActivity, ActDetailChannel.class);
            mIntent.putExtra("id", "908");
            mIntent.putExtra("channel_title", Constant.LABEL_CHANNEL_BOLA_NASIONAL);
        } else if (url.equalsIgnoreCase(Constant.CHANNEL_BOLA_SEJAGAT)) {
            mIntent = new Intent(mActivity, ActDetailChannel.class);
            mIntent.putExtra("id", "906");
            mIntent.putExtra("channel_title", Constant.LABEL_CHANNEL_BOLA_SEJAGAT);
        } else if (url.equalsIgnoreCase(Constant.CHANNEL_INDEX_LIFE)) {
            mIntent = new Intent(mActivity, ActDetailChannelLife.class);
            mIntent.putExtra("id", "15");
            mIntent.putExtra("channel_title", Constant.AllNews);
        } else if (url.equalsIgnoreCase(Constant.CHANNEL_STYLE)) {
            mIntent = new Intent(mActivity, ActDetailChannelLife.class);
            mIntent.putExtra("id", "1501");
            mIntent.putExtra("channel_title", Constant.LABEL_CHANNEL_STYLE);
        } else if (url.equalsIgnoreCase(Constant.CHANNEL_SHOWBIZ)) {
            mIntent = new Intent(mActivity, ActDetailChannelLife.class);
            mIntent.putExtra("id", "1503");
            mIntent.putExtra("channel_title", Constant.LABEL_CHANNEL_SHOWBIZ);
        } else if (url.equalsIgnoreCase(Constant.CHANNEL_FOODLIVING)) {
            mIntent = new Intent(mActivity, ActDetailChannelLife.class);
            mIntent.putExtra("id", "1502");
            mIntent.putExtra("channel_title", Constant.LABEL_CHANNEL_FOODLIVING);
        } else if (url.equalsIgnoreCase(Constant.CHANNEL_HEALTHSEX)) {
            mIntent = new Intent(mActivity, ActDetailChannelLife.class);
            mIntent.putExtra("id", "1505");
            mIntent.putExtra("channel_title", Constant.LABEL_CHANNEL_HEALTHSEX);
        } else if (url.equalsIgnoreCase(Constant.CHANNEL_TRAVEL)) {
            mIntent = new Intent(mActivity, ActDetailChannelLife.class);
            mIntent.putExtra("id", "1504");
            mIntent.putExtra("channel_title", Constant.LABEL_CHANNEL_TRAVEL);
        }
    }

    public void executeResult() {
        if (mIntent != null) {
            mActivity.startActivity(mIntent);
        }
    }

}
