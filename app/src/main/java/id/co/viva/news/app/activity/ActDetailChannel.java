package id.co.viva.news.app.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.melnykov.fab.FloatingActionButton;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.adapter.ChannelBigAdapter;
import id.co.viva.news.app.adapter.ChannelListAdapter;
import id.co.viva.news.app.ads.AdsConfig;
import id.co.viva.news.app.component.LoadMoreListView;
import id.co.viva.news.app.component.ProgressWheel;
import id.co.viva.news.app.interfaces.OnLoadMoreListener;
import id.co.viva.news.app.model.Ads;
import id.co.viva.news.app.model.ChannelList;
import id.co.viva.news.app.services.Analytics;

/**
 * Created by reza on 27/10/14.
 */
public class ActDetailChannel extends ActionBarActivity implements
        OnLoadMoreListener, AdapterView.OnItemClickListener, View.OnClickListener {

    //Collections
    public static ArrayList<ChannelList> channelListArrayList;
    private ArrayList<Ads> adsArrayList;

    //Parameters
    private String color;
    private String id;
    private String channel_title;
    private String timeStamp;
    private String name;
    private String level;
    private String channel;

    private int paging = 1;
    private boolean isInternetPresent = false;
    private ProgressWheel progressWheel;
    private TextView tvNoResult;
    private TextView tvChannel;
    private LoadMoreListView listView;
    private LoadMoreListView listViewBigCard;
    private AnimationAdapter mAnimAdapter;
    private SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;
    private Analytics analytics;
    private int dataSize = 0;
    private RippleView rippleView;
    private FloatingActionButton floatingActionButton;
    private LinearLayout mParentLayout;

    //Adapter
    private ChannelListAdapter adapter;
    private ChannelBigAdapter bigAdapter;

    //Ads
    private PublisherAdView publisherAdViewBottom;
    private PublisherAdView publisherAdViewTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detail_channel_list);

        isInternetPresent = Global.getInstance(this)
                .getConnectionStatus().isConnectingToInternet();

        //Get Intent Data
        getIntentData();

        //Send Analytics
        setAnalytics(String.valueOf(paging), name);

        //Define All Views
        defineViews();

        //Set Theme
        setActionBarTheme(color, channel);

        if (isInternetPresent) {
            retrieveData(channel, channel_title, level);
        } else {
            checkCache(channel_title);
        }
    }

    private void retrieveData(String channel, String channelTitle, String level) {
        StringRequest request = new StringRequest(Request.Method.GET,
                getUrl(channelTitle, level) + "/screen/" + channel.replace(" ", "_") + "_" + channelTitle.replace(" ", "_") + "_screen",
                new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                parseData(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressWheel.setVisibility(View.GONE);
                rippleView.setVisibility(View.VISIBLE);
            }
        });
        request.setShouldCache(true);
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Global.getInstance(this).getRequestQueue().getCache().invalidate(
                getUrl(channelTitle, level) + "/screen/" + channel.replace(" ", "_") + "_" + channelTitle.replace(" ", "_") + "_screen", true);
        Global.getInstance(this).getRequestQueue().getCache()
                .get(getUrl(channelTitle, level) + "/screen/" + channel.replace(" ", "_") + "_" + channelTitle.replace(" ", "_") + "_screen");
        Global.getInstance(this).addToRequestQueue(request, Constant.JSON_REQUEST);
    }

    private void loadMoreData(String dataPage) {
        StringRequest request = new StringRequest(Request.Method.GET,
                getPagingUrl(channel_title, dataPage, timeStamp, level),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        parseData(s);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressWheel.setVisibility(View.GONE);
                rippleView.setVisibility(View.VISIBLE);
            }
        });
        request.setShouldCache(true);
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Global.getInstance(this).getRequestQueue().getCache().invalidate(
                getUrl(channelTitle, level) + "/screen/" + channel.replace(" ", "_") + "_" + channelTitle.replace(" ", "_") + "_screen", true);
        Global.getInstance(this).getRequestQueue().getCache()
                .get(getUrl(channelTitle, level) + "/screen/" + channel.replace(" ", "_") + "_" + channelTitle.replace(" ", "_") + "_screen");
        Global.getInstance(this).addToRequestQueue(request, Constant.JSON_REQUEST);
    }

    private void checkCache(String channelTitle) {
        if (Global.getInstance(this).getRequestQueue().getCache()
                .get(getUrl(channelTitle, level) + "/screen/" +
                        channel.replace(" ", "_") + "_" + channelTitle.replace(" ", "_") + "_screen") != null) {
            String cachedResponse = new String(Global.getInstance(this)
                    .getRequestQueue().getCache()
                    .get(getUrl(channelTitle, level) + "/screen/" +
                            channel.replace(" ", "_") + "_" + channelTitle.replace(" ", "_") + "_screen").data);
            parseData(cachedResponse);
        } else {
            progressWheel.setVisibility(View.GONE);
            tvNoResult.setVisibility(View.VISIBLE);
        }
    }

    private void parseData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArrayResponses = jsonObject.getJSONArray(Constant.response);
            if (jsonArrayResponses.length() > 0) {
                //Get content
                JSONObject objNews = jsonArrayResponses.getJSONObject(0);
                JSONArray jsonArraySegment = objNews.getJSONArray(Constant.news);
                if (jsonArraySegment.length() > 0) {
                    for (int i = 0; i < jsonArraySegment.length(); i++) {
                        JSONObject jsonHeadline = jsonArraySegment.getJSONObject(i);
                        String id = jsonHeadline.getString(Constant.id);
                        String title = jsonHeadline.getString(Constant.title);
                        String kanal = jsonHeadline.getString(Constant.kanal);
                        String image_url = jsonHeadline.getString(Constant.image_url);
                        String date_publish = jsonHeadline.getString(Constant.date_publish);
                        String url = jsonHeadline.getString(Constant.url);
                        String timestamp = jsonHeadline.getString(Constant.timestamp);
                        channelListArrayList.add(new ChannelList(id, title, kanal,
                                image_url, date_publish, url, timestamp));
                        Log.i(Constant.TAG, "CHANNEL LIST : " + channelListArrayList.get(i).getTitle());
                    }
                }
                //Check Ads if exists
                if (isInternetPresent) {
                    JSONObject objAds = jsonArrayResponses.getJSONObject(jsonArrayResponses.length() - 1);
                    JSONArray jsonArraySegmentAds = objAds.getJSONArray(Constant.adses);
                    if (jsonArraySegmentAds.length() > 0) {
                        for (int j=0; j<jsonArraySegmentAds.length(); j++) {
                            JSONObject jsonAds = jsonArraySegmentAds.getJSONObject(j);
                            String name = jsonAds.getString(Constant.name);
                            int position = jsonAds.getInt(Constant.position);
                            int type = jsonAds.getInt(Constant.type);
                            String unit_id = jsonAds.getString(Constant.unit_id);
                            adsArrayList.add(new Ads(name, type, position, unit_id));
                            Log.i(Constant.TAG, "ADS : " + adsArrayList.get(j).getmUnitId());
                        }
                    }
                }
            }
            //Get last published
            timeStamp = channelListArrayList.get(channelListArrayList.size() - 1).getTimestamp();
            //Populate content
            if (channelListArrayList.size() > 0 || !channelListArrayList.isEmpty()) {
                //Small Card List Style
                mAnimAdapter = new ScaleInAnimationAdapter(adapter);
                mAnimAdapter.setAbsListView(listView);
                listView.setAdapter(mAnimAdapter);
                mAnimAdapter.notifyDataSetChanged();
                //Big Card List Style
                swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(bigAdapter);
                swingBottomInAnimationAdapter.setAbsListView(listViewBigCard);
                assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(1000);
                listViewBigCard.setAdapter(swingBottomInAnimationAdapter);
                bigAdapter.notifyDataSetChanged();
                //Hide progress
                progressWheel.setVisibility(View.GONE);
            }
            //Show Ads
            if (isInternetPresent) {
                showAds();
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void showAds() {
        if (ActDetailChannel.this != null) {
            if (adsArrayList != null) {
                if (adsArrayList.size() > 0) {
                    AdsConfig adsConfig = new AdsConfig();
                    for (int i=0; i<adsArrayList.size(); i++) {
                        if (adsArrayList.get(i).getmPosition() == Constant.POSITION_BANNER_TOP) {
                            if (publisherAdViewTop == null) {
                                publisherAdViewTop = new PublisherAdView(this);
                                adsConfig.setAdsBanner(publisherAdViewTop,
                                        adsArrayList.get(i).getmUnitId(), Constant.POSITION_BANNER_TOP, mParentLayout);
                            }
                        } else if (adsArrayList.get(i).getmPosition() == Constant.POSITION_BANNER_BOTTOM) {
                            if (publisherAdViewBottom == null) {
                                publisherAdViewBottom = new PublisherAdView(this);
                                adsConfig.setAdsBanner(publisherAdViewBottom,
                                        adsArrayList.get(i).getmUnitId(), Constant.POSITION_BANNER_BOTTOM, mParentLayout);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        if (item.getItemId() == R.id.action_change_layout) {
            invalidateOptionsMenu();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //Switch View
        if (listView.getVisibility() == View.VISIBLE) {
            listView.setVisibility(View.GONE);
            listViewBigCard.setVisibility(View.VISIBLE);
            if (menu != null) {
                if (menu.hasVisibleItems()) {
                    if (menu.findItem(R.id.action_change_layout) != null) {
                        menu.removeItem(R.id.action_change_layout);
                    }
                }
            }
            MenuItem mi = menu.add(Menu.NONE, R.id.action_change_layout, 2, "");
            mi.setIcon(R.drawable.ic_preview_small);
            mi.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        } else {
            listViewBigCard.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            if (menu != null) {
                if (menu.hasVisibleItems()) {
                    if (menu.findItem(R.id.action_change_layout) != null) {
                        menu.removeItem(R.id.action_change_layout);
                    }
                }
            }
            MenuItem mi = menu.add(Menu.NONE, R.id.action_change_layout, 2, "");
            mi.setIcon(R.drawable.ic_preview_big);
            mi.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_channel_detail, menu);
        //SearchView OnClick
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        android.support.v7.widget.SearchView searchView =
                (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(searchItem);
        if (searchView != null) {
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));
        }
        return true;
    }

    @Override
    public void onLoadMore() {
        String data = String.valueOf(dataSize += 10);
        paging += 1;
        if(isInternetPresent) {
            setAnalytics(String.valueOf(paging), name);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, getPagingUrl(channel_title, data, timeStamp, level),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String volleyResponse) {
                            Log.i(Constant.TAG, "CHANNEL BOLA RESPONSE : " + volleyResponse);
                            try {
                                JSONObject jsonObject = new JSONObject(volleyResponse);
                                jsonArrayResponses = jsonObject.getJSONArray(Constant.response);
                                if (jsonArrayResponses != null) {
                                    JSONObject objHeadline = jsonArrayResponses.getJSONObject(0);
                                    if (objHeadline != null) {
                                        jsonArraySegmentHeadline = objHeadline.getJSONArray(Constant.headlines);
                                        for (int i = 0; i < jsonArraySegmentHeadline.length(); i++) {
                                            JSONObject jsonHeadline = jsonArraySegmentHeadline.getJSONObject(i);
                                            String id = jsonHeadline.getString(Constant.id);
                                            String title = jsonHeadline.getString(Constant.title);
                                            String kanal = jsonHeadline.getString(Constant.kanal);
                                            String image_url = jsonHeadline.getString(Constant.image_url);
                                            String date_publish = jsonHeadline.getString(Constant.date_publish);
                                            String url = jsonHeadline.getString(Constant.url);
                                            String timestamp = jsonHeadline.getString(Constant.timestamp);
                                            channelListArrayList.add(new ChannelList(id, title, kanal,
                                                    image_url, date_publish, url, timestamp));
                                        }
                                    }
                                }
                                timeStamp = channelListArrayList.get(channelListArrayList.size()-1).getTimestamp();
                                if (channelListArrayList.size() > 0 || !channelListArrayList.isEmpty()) {
                                    //Small Card List Style
                                    mAnimAdapter = new ScaleInAnimationAdapter(adapter);
                                    mAnimAdapter.setAbsListView(listView);
                                    mAnimAdapter.notifyDataSetChanged();
                                    listView.onLoadMoreComplete();
                                    //Big Card List Style
                                    swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(bigAdapter);
                                    swingBottomInAnimationAdapter.setAbsListView(listViewBigCard);
                                    assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                                    swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(1000);
                                    bigAdapter.notifyDataSetChanged();
                                    listViewBigCard.onLoadMoreComplete();
                                }
                            } catch (Exception e) {
                                e.getMessage();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            if (listView.getVisibility() == View.VISIBLE) {
                                listView.onLoadMoreComplete();
                                listView.setSelection(0);
                            } else if (listViewBigCard.getVisibility() == View.VISIBLE) {
                                listViewBigCard.onLoadMoreComplete();
                                listViewBigCard.setSelection(0);
                            }
                            Toast.makeText(ActDetailChannel.this, R.string.label_error, Toast.LENGTH_SHORT).show();
                        }
                    });
            stringRequest.setShouldCache(true);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    Constant.TIME_OUT,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Global.getInstance(this).getRequestQueue().getCache().invalidate(getPagingUrl(channel_title, data, timeStamp, level), true);
            Global.getInstance(this).getRequestQueue().getCache().get(getPagingUrl(channel_title, data, timeStamp, level));
            Global.getInstance(this).addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (channelListArrayList.size() > 0) {
            ChannelList channelList = channelListArrayList.get(position);
            Bundle bundle = new Bundle();
            bundle.putString("id", channelList.getId());
            bundle.putString("channel_title", channel_title);
            Intent intent = new Intent(this, ActDetailContent.class);
            intent.putExtras(bundle);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.layout_ripple_view) {
            if (isInternetPresent) {
                rippleView.setVisibility(View.GONE);
                progressWheel.setVisibility(View.VISIBLE);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, getUrl(channel_title, level),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String volleyResponse) {
                                Log.i(Constant.TAG, "CHANNEL BOLA RESPONSE : " + volleyResponse);
                                try {
                                    JSONObject jsonObject = new JSONObject(volleyResponse);
                                    jsonArrayResponses = jsonObject.getJSONArray(Constant.response);
                                    if (jsonArrayResponses.length() > 0) {
                                        //Get content
                                        JSONObject objHeadline = jsonArrayResponses.getJSONObject(0);
                                        jsonArraySegmentHeadline = objHeadline.getJSONArray(Constant.headlines);
                                        if (jsonArraySegmentHeadline.length() > 0) {
                                            for (int i = 0; i < jsonArraySegmentHeadline.length(); i++) {
                                                JSONObject jsonHeadline = jsonArraySegmentHeadline.getJSONObject(i);
                                                String id = jsonHeadline.getString(Constant.id);
                                                String title = jsonHeadline.getString(Constant.title);
                                                String kanal = jsonHeadline.getString(Constant.kanal);
                                                String image_url = jsonHeadline.getString(Constant.image_url);
                                                String date_publish = jsonHeadline.getString(Constant.date_publish);
                                                String url = jsonHeadline.getString(Constant.url);
                                                String timestamp = jsonHeadline.getString(Constant.timestamp);
                                                channelListArrayList.add(new ChannelList(id, title, kanal,
                                                        image_url, date_publish, url, timestamp));
                                                Log.i(Constant.TAG, "CHANNEL BOLA : " + channelListArrayList.get(i).getTitle());
                                            }
                                        }
                                        //Check Ads if exists
                                        JSONObject objAds = jsonArrayResponses.getJSONObject(jsonArrayResponses.length() - 1);
                                        JSONArray jsonArraySegmentAds = objAds.getJSONArray(Constant.adses);
                                        if (jsonArraySegmentAds.length() > 0) {
                                            for (int j=0; j<jsonArraySegmentAds.length(); j++) {
                                                JSONObject jsonAds = jsonArraySegmentAds.getJSONObject(j);
                                                String name = jsonAds.getString(Constant.name);
                                                int position = jsonAds.getInt(Constant.position);
                                                int type = jsonAds.getInt(Constant.type);
                                                String unit_id = jsonAds.getString(Constant.unit_id);
                                                adsArrayList.add(new Ads(name, type, position, unit_id));
                                                Log.i(Constant.TAG, "ADS : " + adsArrayList.get(j).getmUnitId());
                                            }
                                        }
                                    }
                                    //Get last published
                                    timeStamp = channelListArrayList.get(channelListArrayList.size()-1).getTimestamp();
                                    //Populate content
                                    if (channelListArrayList.size() > 0 || !channelListArrayList.isEmpty()) {
                                        //Small Card List Style
                                        mAnimAdapter = new ScaleInAnimationAdapter(adapter);
                                        mAnimAdapter.setAbsListView(listView);
                                        listView.setAdapter(mAnimAdapter);
                                        mAnimAdapter.notifyDataSetChanged();
                                        //Big Card List Style
                                        swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(bigAdapter);
                                        swingBottomInAnimationAdapter.setAbsListView(listViewBigCard);
                                        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                                        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(1000);
                                        listViewBigCard.setAdapter(swingBottomInAnimationAdapter);
                                        bigAdapter.notifyDataSetChanged();
                                        //Hide progress
                                        progressWheel.setVisibility(View.GONE);
                                        if (rippleView.getVisibility() == View.VISIBLE) {
                                            rippleView.setVisibility(View.GONE);
                                        }
                                    }
                                    //Show Ads
                                    showAds();
                                } catch (Exception e) {
                                    e.getMessage();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                progressWheel.setVisibility(View.GONE);
                                rippleView.setVisibility(View.VISIBLE);
                            }
                        });
                stringRequest.setShouldCache(true);
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        Constant.TIME_OUT,
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Global.getInstance(this).getRequestQueue().getCache().invalidate(getUrl(channel_title, level), true);
                Global.getInstance(this).getRequestQueue().getCache().get(getUrl(channel_title, level));
                Global.getInstance(this).addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
            }
        } else if (view.getId() == R.id.fab) {
            if (listView.getVisibility() == View.VISIBLE) {
                listView.setSelection(0);
            } else if (listViewBigCard.getVisibility() == View.VISIBLE) {
                listViewBigCard.setSelection(0);
            }
        }
    }

    private String getUrl(String channelTitle, String level) {
        String url_channel;
        if (channelTitle.equalsIgnoreCase(Constant.AllNews)) {
            url_channel = Constant.NEW_KANAL + "ch/" + id + Constant.ALL_NEWS_URL;
        } else {
            if (level.equals("1")) {
                url_channel = Constant.NEW_KANAL + "ch/" + id + Constant.SUB_CHANNEL_LV_1_URL;
            } else {
                url_channel = Constant.NEW_KANAL + "ch/" + id + Constant.SUB_CHANNEL_LV_2_URL;
            }
        }
        return url_channel;
    }

    private String getPagingUrl(String channelTitle, String page, String timeStamp, String level) {
        String url_channel_paging;
        if (channelTitle.equalsIgnoreCase(Constant.AllNews)) {
            url_channel_paging = Constant.NEW_KANAL + "ch/" + id + Constant.ALL_NEWS_URL_PAGING + timeStamp + "/type/terbaru";
        } else {
            if (level.equals("1")) {
                url_channel_paging = Constant.NEW_KANAL + "ch/" + id + Constant.SUB_CHANNEL_LV_1_URL_PAGING + page;
            } else {
                url_channel_paging = Constant.NEW_KANAL + "ch/" + id + Constant.SUB_CHANNEL_LV_2_URL_PAGING + page;
            }
        }
        return url_channel_paging;
    }

    private void setAnalytics(String page, String channelName) {
        if (analytics == null) {
            analytics = new Analytics(this);
        }
        analytics.getAnalyticByATInternet(channelName.toUpperCase().replace(" ", "_")
                + "_"
                + channel_title.toUpperCase()
                + "_"
                + "HAL_"
                + page);
        analytics.getAnalyticByGoogleAnalytic(channelName.toUpperCase().replace(" ", "_")
                + "_"
                + channel_title.toUpperCase()
                + "_"
                + "HAL_"
                + page);
    }

    private void getIntentData() {
        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");
        channel_title = bundle.getString("channel_title");
        color = bundle.getString("color");
        name = bundle.getString("name");
        level = bundle.getString("level");
        channel = bundle.getString("channel");
    }

    private void setActionBarTheme(String channelColor, String text) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(channelColor)));
        getSupportActionBar().setTitle(text);
    }

    private void defineViews() {
        progressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        mParentLayout = (LinearLayout) findViewById(R.id.parent_layout);

        rippleView = (RippleView) findViewById(R.id.layout_ripple_view);
        rippleView.setVisibility(View.GONE);
        rippleView.setOnClickListener(this);

        tvChannel = (TextView) findViewById(R.id.text_channel);
        tvChannel.setText(channel_title.toUpperCase());

        tvNoResult = (TextView) findViewById(R.id.text_no_result_detail_channel_list);
        tvNoResult.setVisibility(View.GONE);

        //Collections
        channelListArrayList = new ArrayList<>();
        adsArrayList = new ArrayList<>();

        //Adapter
        adapter = new ChannelListAdapter(this, channelListArrayList);
        bigAdapter = new ChannelBigAdapter(this, Constant.BIG_CARD_CHANNEL_LIST, channelListArrayList, null);

        //Small Card List
        listView = (LoadMoreListView) findViewById(R.id.list_channel);
        listView.setVisibility(View.GONE);
        listView.setOnItemClickListener(this);
        listView.setOnLoadMoreListener(this);

        //Big Card List
        listViewBigCard = (LoadMoreListView) findViewById(R.id.list_detail_channel_big_card);
        listViewBigCard.setOnItemClickListener(this);
        listViewBigCard.setOnLoadMoreListener(this);

        //Set floating button into small card list
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setVisibility(View.GONE);
        floatingActionButton.attachToListView(listView, new FloatingActionButton.FabOnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                int firstIndex = listView.getFirstVisiblePosition();
                if (firstIndex > Constant.NUMBER_OF_TOP_LIST_ITEMS_BIG_CARD) {
                    floatingActionButton.setVisibility(View.VISIBLE);
                } else {
                    floatingActionButton.setVisibility(View.GONE);
                }
            }
        });
        //Set floating button into big card list
        floatingActionButton.attachToListView(listViewBigCard, new FloatingActionButton.FabOnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                int firstIndex = listViewBigCard.getFirstVisiblePosition();
                if (firstIndex > Constant.NUMBER_OF_TOP_LIST_ITEMS_BIG_CARD) {
                    floatingActionButton.setVisibility(View.VISIBLE);
                } else {
                    floatingActionButton.setVisibility(View.GONE);
                }
            }
        });
        //Handle floating onClick
        floatingActionButton.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (publisherAdViewBottom != null) {
            publisherAdViewBottom.resume();
        }
        if (publisherAdViewTop != null) {
            publisherAdViewTop.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (publisherAdViewBottom != null) {
            publisherAdViewBottom.pause();
        }
        if (publisherAdViewTop != null) {
            publisherAdViewTop.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (publisherAdViewBottom != null) {
            publisherAdViewBottom.destroy();
        }
        if (publisherAdViewTop != null) {
            publisherAdViewTop.destroy();
        }
    }

}
