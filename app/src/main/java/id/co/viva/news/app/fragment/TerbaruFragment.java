package id.co.viva.news.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.activity.ActDetailTerbaru;
import id.co.viva.news.app.adapter.TerbaruAdapter;
import id.co.viva.news.app.ads.AdsConfig;
import id.co.viva.news.app.component.GoogleMusicDicesDrawable;
import id.co.viva.news.app.component.LoadMoreListView;
import id.co.viva.news.app.interfaces.OnLoadMoreListener;
import id.co.viva.news.app.model.Ads;
import id.co.viva.news.app.model.News;
import id.co.viva.news.app.services.Analytics;

/**
 * Created by root on 09/10/14.
 */
public class TerbaruFragment extends Fragment implements AdapterView.OnItemClickListener,
        OnLoadMoreListener, View.OnClickListener {

    public static ArrayList<News> newsArrayList;
    private ArrayList<Ads> adsArrayList;
    private SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;
    private TerbaruAdapter terbaruAdapter;
    private LoadMoreListView listView;
    private LinearLayout mParentLayout;
    private PublisherAdView publisherAdViewBottom;
    private PublisherAdView publisherAdViewTop;
    private TextView lastUpdate;
    private ProgressBar loading_layout;
    private TextView labelLoadData;
    private Boolean isInternetPresent = false;
    private JSONArray jsonArrayResponses,
            jsonArraySegmentNews, jsonArraySegmentAds;
    private TextView labelText;
    private SimpleDateFormat date, time;
    private Analytics analytics;
    private RippleView rippleView;
    private FloatingActionButton floatingActionButton;
    private String lastPublished;
    private int page = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isInternetPresent = Global.getInstance(getActivity())
                .getConnectionStatus().isConnectingToInternet();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(getResources().getColor(R.color.new_base_color));
        ActionBarActivity mActivity = (ActionBarActivity) activity;
        if (mActivity != null) {
            mActivity.getSupportActionBar().setBackgroundDrawable(colorDrawable);
            mActivity.getSupportActionBar().setIcon(R.drawable.logo_viva_coid_second);
        }
    }

    private Drawable getProgressDrawable() {
        Drawable progressDrawable;
        progressDrawable = new GoogleMusicDicesDrawable.Builder().build();
        return progressDrawable;
    }

    private void defineViews(View rootView) {
        mParentLayout = (LinearLayout) rootView.findViewById(R.id.parent_layout);

        //Loading Progress
        loading_layout = (ProgressBar) rootView.findViewById(R.id.loading_progress_layout_headline_terbaru);
        labelLoadData = (TextView) rootView.findViewById(R.id.text_loading_data);
        Rect bounds = loading_layout.getIndeterminateDrawable().getBounds();
        loading_layout.setIndeterminateDrawable(getProgressDrawable());
        loading_layout.getIndeterminateDrawable().setBounds(bounds);
        labelLoadData.setVisibility(View.VISIBLE);
        loading_layout.setVisibility(View.VISIBLE);

        //Retry Button
        rippleView = (RippleView) rootView.findViewById(R.id.layout_ripple_view);
        rippleView.setVisibility(View.GONE);
        rippleView.setOnClickListener(this);

        //All about analytic
        analytics = new Analytics(getActivity());
        analytics.getAnalyticByATInternet(Constant.TERBARU_PAGE + String.valueOf(page));
        analytics.getAnalyticByGoogleAnalytic(Constant.TERBARU_PAGE + String.valueOf(page));

        //Set some text on top of view
        labelText = (TextView) rootView.findViewById(R.id.text_terbaru_headline);
        labelText.setText(getString(R.string.label_terbaru));
        lastUpdate = (TextView) rootView.findViewById(R.id.date_terbaru_headline);

        //Populate content
        newsArrayList = new ArrayList<>();
        adsArrayList = new ArrayList<>();
        terbaruAdapter = new TerbaruAdapter(getActivity(), newsArrayList);

        //Content list
        listView = (LoadMoreListView) rootView.findViewById(R.id.list_terbaru_headline);
        listView.setOnItemClickListener(this);
        listView.setOnLoadMoreListener(this);

        //Set 'go to the top' button
        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        floatingActionButton.setVisibility(View.GONE);
        floatingActionButton.attachToListView(listView, new FloatingActionButton.FabOnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                int firstIndex = listView.getFirstVisiblePosition();
                if (firstIndex > Constant.NUMBER_OF_TOP_LIST_ITEMS) {
                    floatingActionButton.setVisibility(View.VISIBLE);
                } else {
                    floatingActionButton.setVisibility(View.GONE);
                }
            }
        });
        floatingActionButton.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_terbaru_headline, container, false);
        defineViews(rootView);
        parseJson(newsArrayList);
        return rootView;
    }

    private void parseJson(final ArrayList<News> news) {
        if (isInternetPresent) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.NEW_TERBARU,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String volleyResponse) {
                            Log.i(Constant.TAG, volleyResponse);
                            try {
                                JSONObject jsonObject = new JSONObject(volleyResponse);
                                jsonArrayResponses = jsonObject.getJSONArray(Constant.response);
                                if (jsonArrayResponses.length() > 0) {
                                    //Get content list
                                    JSONObject objTerbaru = jsonArrayResponses.getJSONObject(0);
                                    jsonArraySegmentNews = objTerbaru.getJSONArray(Constant.NEWS);
                                    if (jsonArraySegmentNews.length() > 0) {
                                        for (int z=0; z<jsonArraySegmentNews.length(); z++) {
                                            JSONObject jsonTerbaru = jsonArraySegmentNews.getJSONObject(z);
                                            String id = jsonTerbaru.getString(Constant.id);
                                            String title = jsonTerbaru.getString(Constant.title);
                                            String slug = jsonTerbaru.getString(Constant.slug);
                                            String kanal = jsonTerbaru.getString(Constant.kanal);
                                            String url = jsonTerbaru.getString(Constant.url);
                                            String image_url = jsonTerbaru.getString(Constant.image_url);
                                            String date_publish = jsonTerbaru.getString(Constant.date_publish);
                                            String timestamp = jsonTerbaru.getString(Constant.timestamp);
                                            newsArrayList.add(new News(id, title, slug, kanal, url,
                                                    image_url, date_publish, timestamp));
                                            Log.i(Constant.TAG, "NEWS : " + newsArrayList.get(z).getTitle());
                                        }
                                    }
                                    //Check Ads if exists
                                    JSONObject objAds = jsonArrayResponses.getJSONObject(jsonArrayResponses.length() - 1);
                                    jsonArraySegmentAds = objAds.getJSONArray(Constant.adses);
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
                                lastPublished = news.get(news.size()-1).getTimeStamp();
                                //Fill data from API
                                if(news.size() > 0 || !news.isEmpty()) {
                                    swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(terbaruAdapter);
                                    swingBottomInAnimationAdapter.setAbsListView(listView);
                                    assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                                    swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(1000);
                                    listView.setAdapter(swingBottomInAnimationAdapter);
                                    terbaruAdapter.notifyDataSetChanged();
                                    loading_layout.setVisibility(View.GONE);
                                    labelLoadData.setVisibility(View.GONE);
                                }
                                //Set local time
                                Calendar cal=Calendar.getInstance();
                                date = new SimpleDateFormat("dd MMM yyyy");
                                time = new SimpleDateFormat("HH:mm");
                                String date_name = date.format(cal.getTime());
                                String time_name = time.format(cal.getTime());
                                lastUpdate.setText(date_name + " | " + time_name);
                                //Show Ads
                                showAds();
                            } catch (Exception e) {
                                e.getMessage();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    try {
                        if(Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_TERBARU) != null) {
                            String cachedResponse = new String(Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_TERBARU).data);
                            JSONObject jsonObject = new JSONObject(cachedResponse);
                            jsonArrayResponses = jsonObject.getJSONArray(Constant.response);
                            if(jsonArrayResponses != null) {
                                JSONObject objTerbaru = jsonArrayResponses.getJSONObject(0);
                                if(objTerbaru !=  null) {
                                    jsonArraySegmentNews = objTerbaru.getJSONArray(Constant.NEWS);
                                    for(int i=0; i<jsonArraySegmentNews.length(); i++) {
                                        JSONObject jsonTerbaru = jsonArraySegmentNews.getJSONObject(i);
                                        String id = jsonTerbaru.getString(Constant.id);
                                        String title = jsonTerbaru.getString(Constant.title);
                                        String slug = jsonTerbaru.getString(Constant.slug);
                                        String kanal = jsonTerbaru.getString(Constant.kanal);
                                        String url = jsonTerbaru.getString(Constant.url);
                                        String image_url = jsonTerbaru.getString(Constant.image_url);
                                        String date_publish = jsonTerbaru.getString(Constant.date_publish);
                                        String timestamp = jsonTerbaru.getString(Constant.timestamp);
                                        news.add(new News(id, title, slug, kanal, url,
                                                image_url, date_publish, timestamp));
                                        Log.i(Constant.TAG, "NEWS CACHED : " + news.get(i).getTitle());
                                    }
                                }
                            }

                            lastPublished = news.get(news.size()-1).getTimeStamp();

                            if(news.size() > 0 || !news.isEmpty()) {
                                swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(terbaruAdapter);
                                swingBottomInAnimationAdapter.setAbsListView(listView);
                                assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                                swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(1000);
                                listView.setAdapter(swingBottomInAnimationAdapter);
                                terbaruAdapter.notifyDataSetChanged();
                                loading_layout.setVisibility(View.GONE);
                                labelLoadData.setVisibility(View.GONE);
                            }

                            lastUpdate.setText(R.string.label_content_not_update);
                        } else {
                            loading_layout.setVisibility(View.GONE);
                            labelLoadData.setVisibility(View.GONE);
                            rippleView.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
            });
            stringRequest.setShouldCache(true);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    Constant.TIME_OUT,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Global.getInstance(getActivity()).getRequestQueue().getCache().invalidate(Constant.NEW_TERBARU, true);
            Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_TERBARU);
            Global.getInstance(getActivity()).addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
        } else {
            try {
                if(Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_TERBARU) != null) {
                    String cachedResponse = new String(Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_TERBARU).data);
                    Log.i(Constant.TAG, "From Cached : " + cachedResponse);
                    JSONObject jsonObject = new JSONObject(cachedResponse);
                    jsonArrayResponses = jsonObject.getJSONArray(Constant.response);
                    if(jsonArrayResponses != null) {
                        JSONObject objTerbaru = jsonArrayResponses.getJSONObject(0);
                        if(objTerbaru !=  null) {
                            jsonArraySegmentNews = objTerbaru.getJSONArray(Constant.NEWS);
                            for(int i=0; i<jsonArraySegmentNews.length(); i++) {
                                JSONObject jsonTerbaru = jsonArraySegmentNews.getJSONObject(i);
                                String id = jsonTerbaru.getString(Constant.id);
                                String title = jsonTerbaru.getString(Constant.title);
                                String slug = jsonTerbaru.getString(Constant.slug);
                                String kanal = jsonTerbaru.getString(Constant.kanal);
                                String url = jsonTerbaru.getString(Constant.url);
                                String image_url = jsonTerbaru.getString(Constant.image_url);
                                String date_publish = jsonTerbaru.getString(Constant.date_publish);
                                String timestamp = jsonTerbaru.getString(Constant.timestamp);
                                news.add(new News(id, title, slug, kanal, url,
                                        image_url, date_publish, timestamp));
                                Log.i(Constant.TAG, "NEWS CACHED : " + news.get(i).getTitle());
                            }
                        }
                    }

                    lastPublished = news.get(news.size()-1).getTimeStamp();

                    if(news.size() > 0 || !news.isEmpty()) {
                        swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(terbaruAdapter);
                        swingBottomInAnimationAdapter.setAbsListView(listView);
                        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(1000);
                        listView.setAdapter(swingBottomInAnimationAdapter);
                        terbaruAdapter.notifyDataSetChanged();
                        loading_layout.setVisibility(View.GONE);
                        labelLoadData.setVisibility(View.GONE);
                    }

                    lastUpdate.setText(R.string.label_content_not_update);
                } else {
                    loading_layout.setVisibility(View.GONE);
                    labelLoadData.setVisibility(View.GONE);
                    rippleView.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.getMessage();
            }
            Toast.makeText(getActivity(), R.string.title_no_connection, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if(newsArrayList.size() > 0) {
            News news = newsArrayList.get(position);
            Bundle bundle = new Bundle();
            bundle.putString("id", news.getId());
            Intent intent = new Intent(getActivity(), ActDetailTerbaru.class);
            intent.putExtras(bundle);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
        }
    }

    @Override
    public void onLoadMore() {
        Log.i(Constant.TAG, "Last Published : " + lastPublished);
        page += 1;
        if(isInternetPresent) {
            analytics.getAnalyticByATInternet(Constant.TERBARU_PAGE + String.valueOf(page));
            analytics.getAnalyticByGoogleAnalytic(Constant.TERBARU_PAGE + String.valueOf(page));
                StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.NEW_TERBARU +
                        "published/" + lastPublished,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String volleyResponse) {
                                Log.i(Constant.TAG, volleyResponse);
                                try {
                                    JSONObject jsonObject = new JSONObject(volleyResponse);
                                    jsonArrayResponses = jsonObject.getJSONArray(Constant.response);
                                    if(jsonArrayResponses != null) {
                                        JSONObject objTerbaru = jsonArrayResponses.getJSONObject(0);
                                        if(objTerbaru !=  null) {
                                            jsonArraySegmentNews = objTerbaru.getJSONArray(Constant.NEWS);
                                            for(int i=0; i<jsonArraySegmentNews.length(); i++) {
                                                JSONObject jsonTerbaru = jsonArraySegmentNews.getJSONObject(i);
                                                String id = jsonTerbaru.getString(Constant.id);
                                                String title = jsonTerbaru.getString(Constant.title);
                                                String slug = jsonTerbaru.getString(Constant.slug);
                                                String kanal = jsonTerbaru.getString(Constant.kanal);
                                                String url = jsonTerbaru.getString(Constant.url);
                                                String image_url = jsonTerbaru.getString(Constant.image_url);
                                                String date_publish = jsonTerbaru.getString(Constant.date_publish);
                                                String timestamp = jsonTerbaru.getString(Constant.timestamp);
                                                newsArrayList.add(new News(id, title, slug, kanal, url,
                                                        image_url, date_publish, timestamp));
                                            }
                                        }
                                    }

                                    lastPublished = newsArrayList.get(newsArrayList.size()-1).getTimeStamp();

                                    if(newsArrayList.size() > 0 || !newsArrayList.isEmpty()) {
                                        swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(terbaruAdapter);
                                        swingBottomInAnimationAdapter.setAbsListView(listView);
                                        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                                        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(1000);
                                        terbaruAdapter.notifyDataSetChanged();
                                        listView.onLoadMoreComplete();
                                    }
                                } catch (Exception e) {
                                    e.getMessage();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        listView.onLoadMoreComplete();
                        listView.setSelection(0);
                        if(getActivity() != null) {
                            Toast.makeText(getActivity(), R.string.label_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                stringRequest.setShouldCache(true);
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        Constant.TIME_OUT,
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Global.getInstance(getActivity()).getRequestQueue().getCache().invalidate(Constant.NEW_TERBARU +
                        "published/" + lastPublished, true);
                Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_TERBARU +
                        "published/" + lastPublished);
                Global.getInstance(getActivity()).addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
        } else {
            listView.onLoadMoreComplete();
            listView.setSelection(0);
            if (getActivity() != null) {
                Toast.makeText(getActivity(), R.string.title_no_connection, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.layout_ripple_view) {
            if(isInternetPresent) {
                loading_layout.setVisibility(View.VISIBLE);
                labelLoadData.setVisibility(View.VISIBLE);
                rippleView.setVisibility(View.GONE);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.NEW_TERBARU,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String volleyResponse) {
                                Log.i(Constant.TAG, volleyResponse);
                                try {
                                    JSONObject jsonObject = new JSONObject(volleyResponse);
                                    jsonArrayResponses = jsonObject.getJSONArray(Constant.response);
                                    if (jsonArrayResponses.length() > 0) {
                                        //Get content list
                                        JSONObject objTerbaru = jsonArrayResponses.getJSONObject(0);
                                        jsonArraySegmentNews = objTerbaru.getJSONArray(Constant.NEWS);
                                        if (jsonArraySegmentNews.length() > 0) {
                                            for (int z=0; z<jsonArraySegmentNews.length(); z++) {
                                                JSONObject jsonTerbaru = jsonArraySegmentNews.getJSONObject(z);
                                                String id = jsonTerbaru.getString(Constant.id);
                                                String title = jsonTerbaru.getString(Constant.title);
                                                String slug = jsonTerbaru.getString(Constant.slug);
                                                String kanal = jsonTerbaru.getString(Constant.kanal);
                                                String url = jsonTerbaru.getString(Constant.url);
                                                String image_url = jsonTerbaru.getString(Constant.image_url);
                                                String date_publish = jsonTerbaru.getString(Constant.date_publish);
                                                String timestamp = jsonTerbaru.getString(Constant.timestamp);
                                                newsArrayList.add(new News(id, title, slug, kanal, url,
                                                        image_url, date_publish, timestamp));
                                                Log.i(Constant.TAG, "NEWS : " + newsArrayList.get(z).getTitle());
                                            }
                                        }
                                        //Check Ads if exists
                                        JSONObject objAds = jsonArrayResponses.getJSONObject(jsonArrayResponses.length() - 1);
                                        jsonArraySegmentAds = objAds.getJSONArray(Constant.adses);
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
                                    lastPublished = newsArrayList.get(newsArrayList.size()-1).getTimeStamp();
                                    //Fill content
                                    if (newsArrayList.size() > 0 || !newsArrayList.isEmpty()) {
                                        swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(terbaruAdapter);
                                        swingBottomInAnimationAdapter.setAbsListView(listView);
                                        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                                        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(1000);
                                        listView.setAdapter(swingBottomInAnimationAdapter);
                                        terbaruAdapter.notifyDataSetChanged();
                                        loading_layout.setVisibility(View.GONE);
                                        labelLoadData.setVisibility(View.GONE);
                                        rippleView.setVisibility(View.GONE);
                                    }
                                    //Set time
                                    Calendar cal=Calendar.getInstance();
                                    date = new SimpleDateFormat("dd MMM yyyy");
                                    time = new SimpleDateFormat("HH:mm");
                                    String date_name = date.format(cal.getTime());
                                    String time_name = time.format(cal.getTime());
                                    lastUpdate.setText(date_name + " | " + time_name);
                                    //Show Ads
                                    showAds();
                                } catch (Exception e) {
                                    e.getMessage();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        try {
                            if(Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_TERBARU) != null) {
                                String cachedResponse = new String(Global.getInstance(getActivity()).getRequestQueue()
                                        .getCache().get(Constant.NEW_TERBARU).data);
                                Log.i(Constant.TAG, "From Cached : " + cachedResponse);
                                JSONObject jsonObject = new JSONObject(cachedResponse);
                                jsonArrayResponses = jsonObject.getJSONArray(Constant.response);
                                if(jsonArrayResponses != null) {
                                    JSONObject objTerbaru = jsonArrayResponses.getJSONObject(0);
                                    if(objTerbaru !=  null) {
                                        jsonArraySegmentNews = objTerbaru.getJSONArray(Constant.NEWS);
                                        for(int i=0; i<jsonArraySegmentNews.length(); i++) {
                                            JSONObject jsonTerbaru = jsonArraySegmentNews.getJSONObject(i);
                                            String id = jsonTerbaru.getString(Constant.id);
                                            String title = jsonTerbaru.getString(Constant.title);
                                            String slug = jsonTerbaru.getString(Constant.slug);
                                            String kanal = jsonTerbaru.getString(Constant.kanal);
                                            String url = jsonTerbaru.getString(Constant.url);
                                            String image_url = jsonTerbaru.getString(Constant.image_url);
                                            String date_publish = jsonTerbaru.getString(Constant.date_publish);
                                            String timestamp = jsonTerbaru.getString(Constant.timestamp);
                                            newsArrayList.add(new News(id, title, slug, kanal, url,
                                                    image_url, date_publish,timestamp));
                                            Log.i(Constant.TAG, "NEWS CACHED : " + newsArrayList.get(i).getTitle());
                                        }
                                    }
                                }

                                lastPublished = newsArrayList.get(newsArrayList.size()-1).getTimeStamp();

                                if(newsArrayList.size() > 0 || !newsArrayList.isEmpty()) {
                                    swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(terbaruAdapter);
                                    swingBottomInAnimationAdapter.setAbsListView(listView);
                                    assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                                    swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(1000);
                                    listView.setAdapter(swingBottomInAnimationAdapter);
                                    terbaruAdapter.notifyDataSetChanged();
                                    loading_layout.setVisibility(View.GONE);
                                    labelLoadData.setVisibility(View.GONE);
                                }

                                lastUpdate.setText(R.string.label_content_not_update);
                            } else {
                                loading_layout.setVisibility(View.GONE);
                                labelLoadData.setVisibility(View.GONE);
                                rippleView.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                });
                stringRequest.setShouldCache(true);
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        Constant.TIME_OUT,
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Global.getInstance(getActivity()).getRequestQueue().getCache().invalidate(Constant.NEW_TERBARU, true);
                Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_TERBARU);
                Global.getInstance(getActivity()).addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
            } else {
                Toast.makeText(getActivity(), R.string.title_no_connection, Toast.LENGTH_SHORT).show();
            }
        } else if(view.getId() == R.id.fab) {
            listView.setSelection(0);
        }
    }

    private void showAds() {
        if (getActivity() != null) {
            if (adsArrayList != null) {
                if (adsArrayList.size() > 0) {
                    AdsConfig adsConfig = new AdsConfig();
                    for (int i=0; i<adsArrayList.size(); i++) {
                        if (adsArrayList.get(i).getmPosition() == Constant.POSITION_BANNER_TOP) {
                            if (publisherAdViewTop == null) {
                                publisherAdViewTop = new PublisherAdView(getActivity());
                                adsConfig.setAdsBanner(publisherAdViewTop,
                                        adsArrayList.get(i).getmUnitId(), Constant.POSITION_BANNER_TOP, mParentLayout);
                            }
                        } else if (adsArrayList.get(i).getmPosition() == Constant.POSITION_BANNER_BOTTOM) {
                            if (publisherAdViewBottom == null) {
                                publisherAdViewBottom = new PublisherAdView(getActivity());
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
