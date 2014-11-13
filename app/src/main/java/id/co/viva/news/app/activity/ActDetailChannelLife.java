package id.co.viva.news.app.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import id.co.viva.news.app.component.LoadMoreListView;
import id.co.viva.news.app.interfaces.OnLoadMoreListener;
import id.co.viva.news.app.services.Analytics;
import id.co.viva.news.app.Constant;
import id.co.viva.news.app.R;
import id.co.viva.news.app.VivaApp;
import id.co.viva.news.app.adapter.ChannelLifeAdapter;
import id.co.viva.news.app.model.ChannelLife;

/**
 * Created by reza on 27/10/14.
 */
public class ActDetailChannelLife extends ActBase implements
        AdapterView.OnItemClickListener, OnLoadMoreListener, View.OnClickListener {

    public static ArrayList<ChannelLife> channelLifeArrayList;
    private String id;
    private String channel_title;
    private boolean isInternetPresent = false;
    private RelativeLayout loading_layout;
    private TextView tvNoResult;
    private TextView tvChannel;
    private LoadMoreListView listView;
    private String cachedResponse;
    private AnimationAdapter mAnimAdapter;
    private JSONArray jsonArrayResponses, jsonArraySegmentHeadline;
    private Analytics analytics;
    private ChannelLifeAdapter adapter;
    private int dataSize = 0;
    private String data;
    private RippleView rippleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.item_detail_channel_life, null, false);
        mDrawerLayout.addView(contentView, 0);

        isInternetPresent = VivaApp.getInstance().getConnectionStatus().isConnectingToInternet();

        analytics = new Analytics();
        analytics.getAnalyticByATInternet(Constant.SUBKANAL_LIFE_PAGE);
        analytics.getAnalyticByGoogleAnalytic(Constant.SUBKANAL_LIFE_PAGE);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(getResources().getColor(R.color.color_life));
        getActionBar().setBackgroundDrawable(colorDrawable);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setIcon(R.drawable.logo_viva_coid_second);

        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");
        channel_title = bundle.getString("channel_title");

        loading_layout = (RelativeLayout) findViewById(R.id.loading_progress_layout);

        rippleView = (RippleView) findViewById(R.id.layout_ripple_view);
        rippleView.setVisibility(View.GONE);
        rippleView.setOnClickListener(this);

        tvChannel = (TextView) findViewById(R.id.text_channel);
        tvChannel.setText(channel_title.toUpperCase());

        tvNoResult = (TextView) findViewById(R.id.text_no_result_detail_channel_life);
        tvNoResult.setVisibility(View.GONE);

        channelLifeArrayList = new ArrayList<ChannelLife>();
        adapter = new ChannelLifeAdapter(VivaApp.getInstance(), channelLifeArrayList);

        listView = (LoadMoreListView) findViewById(R.id.list_channel_life);
        listView.setOnItemClickListener(this);
        listView.setOnLoadMoreListener(this);

        if(isInternetPresent) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_KANAL_DETAIL + id + "/2/0/10",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String volleyResponse) {
                            Log.i(Constant.TAG, "CHANNEL LIFE RESPONSE : " + volleyResponse);
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
                                            channelLifeArrayList.add(new ChannelLife(id, title, kanal,
                                                    image_url, date_publish, url));
                                            Log.i(Constant.TAG, "CHANNEL LIFE : " + channelLifeArrayList.get(i).getTitle());
                                        }
                                    }
                                }
                                if (channelLifeArrayList.size() > 0 || !channelLifeArrayList.isEmpty()) {
                                    mAnimAdapter = new ScaleInAnimationAdapter(adapter);
                                    mAnimAdapter.setAbsListView(listView);
                                    listView.setAdapter(mAnimAdapter);
                                    mAnimAdapter.notifyDataSetChanged();
                                    loading_layout.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                e.getMessage();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            volleyError.getMessage();
                            loading_layout.setVisibility(View.GONE);
                            rippleView.setVisibility(View.VISIBLE);
                        }
                    });
            stringRequest.setShouldCache(true);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    Constant.TIME_OUT,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VivaApp.getInstance().getRequestQueue().getCache().invalidate(Constant.URL_KANAL_DETAIL + id + "/2/0/10", true);
            VivaApp.getInstance().getRequestQueue().getCache().get(Constant.URL_KANAL_DETAIL + id + "/2/0/10");
            VivaApp.getInstance().addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
        } else {
            Toast.makeText(VivaApp.getInstance(), R.string.title_no_connection, Toast.LENGTH_SHORT).show();
            if(VivaApp.getInstance().getRequestQueue().getCache().get(Constant.URL_KANAL_DETAIL + id + "/2/0/10") != null) {
                cachedResponse = new String(VivaApp.getInstance().
                        getRequestQueue().getCache().get(Constant.URL_KANAL_DETAIL + id + "/2/0/10").data);
                Log.i(Constant.TAG, "CHANNEL LIFE CACHED RESPONSE : " + cachedResponse);
                try{
                    JSONObject jsonObject = new JSONObject(cachedResponse);
                    jsonArrayResponses = jsonObject.getJSONArray(Constant.response);
                    if(jsonArrayResponses != null) {
                        JSONObject objHeadline = jsonArrayResponses.getJSONObject(0);
                        if (objHeadline != null) {
                            jsonArraySegmentHeadline = objHeadline.getJSONArray(Constant.headlines);
                            for(int i=0; i<jsonArraySegmentHeadline.length(); i++) {
                                JSONObject jsonHeadline = jsonArraySegmentHeadline.getJSONObject(i);
                                String id = jsonHeadline.getString(Constant.id);
                                String title = jsonHeadline.getString(Constant.title);
                                String kanal = jsonHeadline.getString(Constant.kanal);
                                String image_url = jsonHeadline.getString(Constant.image_url);
                                String date_publish = jsonHeadline.getString(Constant.date_publish);
                                String url = jsonHeadline.getString(Constant.url);
                                channelLifeArrayList.add(new ChannelLife(id, title, kanal,
                                        image_url, date_publish, url));
                                Log.i(Constant.TAG, "CHANNEL LIFE CACHED : " + channelLifeArrayList.get(i).getTitle());
                            }
                        }
                    }
                    if(channelLifeArrayList.size() > 0 || !channelLifeArrayList.isEmpty()) {
                        mAnimAdapter = new ScaleInAnimationAdapter(adapter);
                        mAnimAdapter.setAbsListView(listView);
                        listView.setAdapter(mAnimAdapter);
                        mAnimAdapter.notifyDataSetChanged();
                        loading_layout.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            } else {
                loading_layout.setVisibility(View.GONE);
                tvNoResult.setVisibility(View.VISIBLE);
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_frag_default, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (channelLifeArrayList.size() > 0) {
            ChannelLife news = channelLifeArrayList.get(position);
            Log.i(Constant.TAG, "ID : " + news.getId());
            Bundle bundle = new Bundle();
            bundle.putString("id", news.getId());
            Intent intent = new Intent(VivaApp.getInstance(), ActDetailContentLife.class);
            intent.putExtras(bundle);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
        }
    }

    @Override
    public void onLoadMore() {
        data = String.valueOf(dataSize += 10);
        Log.i(Constant.TAG, "DATA PAGE : " + data);
        if(isInternetPresent) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_KANAL_DETAIL + id + "/2/" + data + "/10",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String volleyResponse) {
                            Log.i(Constant.TAG, "CHANNEL LIFE RESPONSE : " + volleyResponse);
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
                                            channelLifeArrayList.add(new ChannelLife(id, title, kanal,
                                                    image_url, date_publish, url));
                                        }
                                    }
                                }
                                if (channelLifeArrayList.size() > 0 || !channelLifeArrayList.isEmpty()) {
                                    mAnimAdapter = new ScaleInAnimationAdapter(adapter);
                                    mAnimAdapter.setAbsListView(listView);
                                    mAnimAdapter.notifyDataSetChanged();
                                    listView.onLoadMoreComplete();
                                }
                            } catch (Exception e) {
                                e.getMessage();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            volleyError.getMessage();
                        }
                    });
            stringRequest.setShouldCache(true);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    Constant.TIME_OUT,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VivaApp.getInstance().getRequestQueue().getCache().invalidate(Constant.URL_KANAL_DETAIL + id + "/2/" + data + "/10", true);
            VivaApp.getInstance().getRequestQueue().getCache().get(Constant.URL_KANAL_DETAIL + id + "/2/" + data + "/10");
            VivaApp.getInstance().addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.layout_ripple_view) {
            if(isInternetPresent) {
                rippleView.setVisibility(View.GONE);
                loading_layout.setVisibility(View.VISIBLE);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_KANAL_DETAIL + id + "/2/0/10",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String volleyResponse) {
                                Log.i(Constant.TAG, "CHANNEL LIFE RESPONSE : " + volleyResponse);
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
                                                channelLifeArrayList.add(new ChannelLife(id, title, kanal,
                                                        image_url, date_publish, url));
                                                Log.i(Constant.TAG, "CHANNEL LIFE : " + channelLifeArrayList.get(i).getTitle());
                                            }
                                        }
                                    }
                                    if (channelLifeArrayList.size() > 0 || !channelLifeArrayList.isEmpty()) {
                                        mAnimAdapter = new ScaleInAnimationAdapter(adapter);
                                        mAnimAdapter.setAbsListView(listView);
                                        listView.setAdapter(mAnimAdapter);
                                        mAnimAdapter.notifyDataSetChanged();
                                        loading_layout.setVisibility(View.GONE);
                                        if(rippleView.getVisibility() == View.VISIBLE) {
                                            rippleView.setVisibility(View.GONE);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.getMessage();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                volleyError.getMessage();
                                loading_layout.setVisibility(View.GONE);
                                rippleView.setVisibility(View.VISIBLE);
                            }
                        });
                stringRequest.setShouldCache(true);
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        Constant.TIME_OUT,
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                VivaApp.getInstance().getRequestQueue().getCache().invalidate(Constant.URL_KANAL_DETAIL + id + "/2/0/10", true);
                VivaApp.getInstance().getRequestQueue().getCache().get(Constant.URL_KANAL_DETAIL + id + "/2/0/10");
                VivaApp.getInstance().addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
            }
        }
    }

}
