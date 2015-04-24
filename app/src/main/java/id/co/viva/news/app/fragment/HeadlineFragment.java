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
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
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
import id.co.viva.news.app.activity.ActDetailHeadline;
import id.co.viva.news.app.adapter.HeadlineAdapter;
import id.co.viva.news.app.component.GoogleMusicDicesDrawable;
import id.co.viva.news.app.component.LoadMoreListView;
import id.co.viva.news.app.interfaces.OnLoadMoreListener;
import id.co.viva.news.app.model.Headline;
import id.co.viva.news.app.services.Analytics;

/**
 * Created by reza on 28/10/14.
 */
public class HeadlineFragment extends Fragment implements
        AdapterView.OnItemClickListener, View.OnClickListener, OnLoadMoreListener {

    private static String HEADLINES = "headlines";
    public static ArrayList<Headline> headlineArrayList;
    private ActionBarActivity mActivity;
    private String lastPublished;
    private SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;
    private HeadlineAdapter headlineAdapter;
    private LoadMoreListView listView;
    private LinearLayout mParentLayout;
    private PublisherAdView publisherAdViewBottom;
    private PublisherAdView publisherAdViewTop;
    private TextView lastUpdate;
    private TextView labelLoadData;
    private Boolean isInternetPresent = false;
    private JSONArray jsonArrayResponses, jsonArraySegmentHeadline;
    private TextView labelText;
    private SimpleDateFormat date, time;
    private Analytics analytics;
    private RippleView rippleView;
    private ProgressBar loading_layout;
    private FloatingActionButton floatingActionButton;
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
        mActivity = (ActionBarActivity) activity;
        if (mActivity != null) {
            mActivity.getSupportActionBar().setBackgroundDrawable(colorDrawable);
            mActivity.getSupportActionBar().setIcon(R.drawable.logo_viva_coid_second);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isInternetPresent) {
            if (getActivity() != null) {
                //Load ad request
                PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
                //Ad Top
                if (Constant.unitIdTop != null) {
                    if (Constant.unitIdTop.length() > 0) {
                        publisherAdViewTop = new PublisherAdView(getActivity());
                        publisherAdViewTop.setAdUnitId(Constant.unitIdTop);
                        publisherAdViewTop.setAdSizes(AdSize.SMART_BANNER);
                        mParentLayout.addView(publisherAdViewTop, 0);
                        publisherAdViewTop.loadAd(adRequest);
                    }
                }
                //Ad Bottom
                if (Constant.unitIdBottom != null) {
                    if (Constant.unitIdBottom.length() > 0) {
                        publisherAdViewBottom = new PublisherAdView(getActivity());
                        publisherAdViewBottom.setAdUnitId(Constant.unitIdBottom);
                        publisherAdViewBottom.setAdSizes(AdSize.SMART_BANNER);
                        mParentLayout.addView(publisherAdViewBottom);
                        publisherAdViewBottom.loadAd(adRequest);
                    }
                }
            }
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
        analytics.getAnalyticByATInternet(Constant.HEADLINE_PAGE + String.valueOf(page));
        analytics.getAnalyticByGoogleAnalytic(Constant.HEADLINE_PAGE + String.valueOf(page));

        //Set some text on top of view
        lastUpdate = (TextView) rootView.findViewById(R.id.date_terbaru_headline);
        labelText = (TextView) rootView.findViewById(R.id.text_terbaru_headline);
        labelText.setText(getString(R.string.label_headline));

        //List Content
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

        //Assign array
        headlineArrayList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_terbaru_headline, container, false);
        defineViews(rootView);
        parseJson(headlineArrayList);
        return rootView;
    }

    private void parseJson(final ArrayList<Headline> headlines) {
        if(isInternetPresent) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.NEW_HEADLINE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String volleyResponse) {
                            Log.i(Constant.TAG, volleyResponse);
                            try {
                                JSONObject jsonObject = new JSONObject(volleyResponse);
                                jsonArrayResponses = jsonObject.getJSONArray(Constant.response);
                                if(jsonArrayResponses != null) {
                                    JSONObject objHeadline = jsonArrayResponses.getJSONObject(0);
                                    if(objHeadline !=  null) {
                                        jsonArraySegmentHeadline = objHeadline.getJSONArray(HEADLINES);
                                        for(int i=0; i<jsonArraySegmentHeadline.length(); i++) {
                                            JSONObject jsonHeadline = jsonArraySegmentHeadline.getJSONObject(i);
                                            String id = jsonHeadline.getString(Constant.id);
                                            String title = jsonHeadline.getString(Constant.title);
                                            String slug = jsonHeadline.getString(Constant.slug);
                                            String kanal = jsonHeadline.getString(Constant.kanal);
                                            String url = jsonHeadline.getString(Constant.url);
                                            String image_url = jsonHeadline.getString(Constant.image_url);
                                            String date_publish = jsonHeadline.getString(Constant.date_publish);
                                            String source = jsonHeadline.getString(Constant.source);
                                            String timestamp = jsonHeadline.getString(Constant.timestamp);
                                            headlines.add(new Headline(id, title, slug, kanal,
                                                    image_url, date_publish, source, url, timestamp));
                                            Log.i(Constant.TAG, "HEADLINES : " + headlines.get(i).getTitle());
                                        }
                                    }
                                }

                                //Get last published
                                lastPublished = headlines.get(headlines.size()-1).getTimestamp();

                                //Get dynamic ad
                                /**
                                 *
                                 */

                                if(headlines.size() > 0 || !headlines.isEmpty()) {
                                    headlineAdapter = new HeadlineAdapter(getActivity(), headlines);
                                    swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(headlineAdapter);
                                    swingBottomInAnimationAdapter.setAbsListView(listView);
                                    assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                                    swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(1000);
                                    listView.setAdapter(swingBottomInAnimationAdapter);
                                    headlineAdapter.notifyDataSetChanged();
                                    loading_layout.setVisibility(View.GONE);
                                    labelLoadData.setVisibility(View.GONE);
                                }

                                Calendar cal=Calendar.getInstance();
                                date = new SimpleDateFormat("dd MMM yyyy");
                                time = new SimpleDateFormat("HH:mm");
                                String date_name = date.format(cal.getTime());
                                String time_name = time.format(cal.getTime());
                                lastUpdate.setText(date_name + " | " + time_name);
                            } catch (Exception e) {
                                e.getMessage();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    try {
                        if(Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_HEADLINE) != null) {
                            String cachedResponse = new String(Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_HEADLINE).data);
                            Log.i(Constant.TAG, "From Cached : " + cachedResponse);
                            JSONObject jsonObject = new JSONObject(cachedResponse);
                            jsonArrayResponses = jsonObject.getJSONArray(Constant.response);
                            if(jsonArrayResponses != null) {
                                JSONObject objHeadline = jsonArrayResponses.getJSONObject(0);
                                if(objHeadline !=  null) {
                                    jsonArraySegmentHeadline = objHeadline.getJSONArray(HEADLINES);
                                    for(int i=0; i<jsonArraySegmentHeadline.length(); i++) {
                                        JSONObject jsonHeadline = jsonArraySegmentHeadline.getJSONObject(i);
                                        String id = jsonHeadline.getString(Constant.id);
                                        String title = jsonHeadline.getString(Constant.title);
                                        String slug = jsonHeadline.getString(Constant.slug);
                                        String kanal = jsonHeadline.getString(Constant.kanal);
                                        String url = jsonHeadline.getString(Constant.url);
                                        String image_url = jsonHeadline.getString(Constant.image_url);
                                        String date_publish = jsonHeadline.getString(Constant.date_publish);
                                        String source = jsonHeadline.getString(Constant.source);
                                        String timestamp = jsonHeadline.getString(Constant.timestamp);
                                        headlines.add(new Headline(id, title, slug, kanal,
                                                image_url, date_publish, source, url, timestamp));
                                        Log.i(Constant.TAG, "HEADLINES CACHED : " + headlines.get(i).getTitle());
                                    }
                                }
                            }

                            lastPublished = headlines.get(headlines.size()-1).getTimestamp();

                            if(headlines.size() > 0 || !headlines.isEmpty()) {
                                headlineAdapter = new HeadlineAdapter(getActivity(), headlines);
                                swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(headlineAdapter);
                                swingBottomInAnimationAdapter.setAbsListView(listView);
                                assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                                swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(1000);
                                listView.setAdapter(swingBottomInAnimationAdapter);
                                headlineAdapter.notifyDataSetChanged();
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
            Global.getInstance(getActivity()).getRequestQueue().getCache().invalidate(Constant.NEW_HEADLINE, true);
            Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_HEADLINE);
            Global.getInstance(getActivity()).addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
        } else {
            try {
                if(Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_HEADLINE) != null) {
                    String cachedResponse = new String(Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_HEADLINE).data);
                    Log.i(Constant.TAG, "From Cached : " + cachedResponse);
                    JSONObject jsonObject = new JSONObject(cachedResponse);
                    jsonArrayResponses = jsonObject.getJSONArray(Constant.response);
                    if(jsonArrayResponses != null) {
                        JSONObject objHeadline = jsonArrayResponses.getJSONObject(0);
                        if(objHeadline !=  null) {
                            jsonArraySegmentHeadline = objHeadline.getJSONArray(HEADLINES);
                            for(int i=0; i<jsonArraySegmentHeadline.length(); i++) {
                                JSONObject jsonHeadline = jsonArraySegmentHeadline.getJSONObject(i);
                                String id = jsonHeadline.getString(Constant.id);
                                String title = jsonHeadline.getString(Constant.title);
                                String slug = jsonHeadline.getString(Constant.slug);
                                String kanal = jsonHeadline.getString(Constant.kanal);
                                String url = jsonHeadline.getString(Constant.url);
                                String image_url = jsonHeadline.getString(Constant.image_url);
                                String date_publish = jsonHeadline.getString(Constant.date_publish);
                                String source = jsonHeadline.getString(Constant.source);
                                String timestamp = jsonHeadline.getString(Constant.timestamp);
                                headlines.add(new Headline(id, title, slug, kanal,
                                        image_url, date_publish, source, url, timestamp));
                                Log.i(Constant.TAG, "HEADLINES CACHED : " + headlines.get(i).getTitle());
                            }
                        }
                    }

                    lastPublished = headlines.get(headlines.size()-1).getTimestamp();

                    if(headlines.size() > 0 || !headlines.isEmpty()) {
                        headlineAdapter = new HeadlineAdapter(getActivity(), headlines);
                        swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(headlineAdapter);
                        swingBottomInAnimationAdapter.setAbsListView(listView);
                        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(1000);
                        listView.setAdapter(swingBottomInAnimationAdapter);
                        headlineAdapter.notifyDataSetChanged();
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
            Toast.makeText(mActivity, R.string.title_no_connection, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (headlineArrayList.size() > 0) {
            Headline headline = headlineArrayList.get(position);
            Log.i(Constant.TAG, "ID : " + headline.getId());
            Bundle bundle = new Bundle();
            bundle.putString("id", headline.getId());
            Intent intent = new Intent(getActivity(), ActDetailHeadline.class);
            intent.putExtras(bundle);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.layout_ripple_view) {
            if(isInternetPresent) {
                loading_layout.setVisibility(View.VISIBLE);
                labelLoadData.setVisibility(View.VISIBLE);
                rippleView.setVisibility(View.GONE);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.NEW_HEADLINE,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String volleyResponse) {
                                Log.i(Constant.TAG, volleyResponse);
                                if(volleyResponse.contains("null")) {
                                    loading_layout.setVisibility(View.GONE);
                                    labelLoadData.setVisibility(View.GONE);
                                    rippleView.setVisibility(View.VISIBLE);
                                }
                                try {
                                    JSONObject jsonObject = new JSONObject(volleyResponse);
                                    jsonArrayResponses = jsonObject.getJSONArray(Constant.response);
                                    if(jsonArrayResponses != null) {
                                        JSONObject objHeadline = jsonArrayResponses.getJSONObject(0);
                                        if(objHeadline !=  null) {
                                            jsonArraySegmentHeadline = objHeadline.getJSONArray(HEADLINES);
                                            for(int i=0; i<jsonArraySegmentHeadline.length(); i++) {
                                                JSONObject jsonHeadline = jsonArraySegmentHeadline.getJSONObject(i);
                                                String id = jsonHeadline.getString(Constant.id);
                                                String title = jsonHeadline.getString(Constant.title);
                                                String slug = jsonHeadline.getString(Constant.slug);
                                                String kanal = jsonHeadline.getString(Constant.kanal);
                                                String url = jsonHeadline.getString(Constant.url);
                                                String image_url = jsonHeadline.getString(Constant.image_url);
                                                String date_publish = jsonHeadline.getString(Constant.date_publish);
                                                String source = jsonHeadline.getString(Constant.source);
                                                String timestamp = jsonHeadline.getString(Constant.timestamp);
                                                headlineArrayList.add(new Headline(id, title, slug, kanal,
                                                        image_url, date_publish, source, url, timestamp));
                                                Log.i(Constant.TAG, "HEADLINES : " + headlineArrayList.get(i).getTitle());
                                            }
                                        }
                                    }

                                    //Get last published
                                    lastPublished = headlineArrayList.get(headlineArrayList.size()-1).getTimestamp();

                                    //Get dynamic ad
                                    /**
                                     *
                                     */

                                    if(headlineArrayList.size() > 0 || !headlineArrayList.isEmpty()) {
                                        headlineAdapter = new HeadlineAdapter(getActivity(), headlineArrayList);
                                        swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(headlineAdapter);
                                        swingBottomInAnimationAdapter.setAbsListView(listView);
                                        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                                        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(1000);
                                        listView.setAdapter(swingBottomInAnimationAdapter);
                                        headlineAdapter.notifyDataSetChanged();
                                        loading_layout.setVisibility(View.GONE);
                                        labelLoadData.setVisibility(View.GONE);
                                        rippleView.setVisibility(View.GONE);
                                    }

                                    Calendar cal=Calendar.getInstance();
                                    date = new SimpleDateFormat("dd MMM yyyy");
                                    time = new SimpleDateFormat("HH:mm");
                                    String date_name = date.format(cal.getTime());
                                    String time_name = time.format(cal.getTime());
                                    lastUpdate.setText(date_name + " | " + time_name);
                                } catch (Exception e) {
                                    e.getMessage();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        loading_layout.setVisibility(View.GONE);
                        labelLoadData.setVisibility(View.GONE);
                        rippleView.setVisibility(View.VISIBLE);
                    }
                });
                stringRequest.setShouldCache(true);
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        Constant.TIME_OUT,
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Global.getInstance(getActivity()).getRequestQueue().getCache().invalidate(Constant.NEW_HEADLINE, true);
                Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_HEADLINE);
                Global.getInstance(getActivity()).addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
            } else {
                Toast.makeText(mActivity, R.string.title_no_connection, Toast.LENGTH_SHORT).show();
            }
        } else if(view.getId() == R.id.fab) {
            listView.setSelection(0);
        }
    }

    @Override
    public void onLoadMore() {
        Log.i(Constant.TAG, "Last Published : " + lastPublished);
        page += 1;
        if(isInternetPresent) {
            analytics.getAnalyticByATInternet(Constant.HEADLINE_PAGE + String.valueOf(page));
            analytics.getAnalyticByGoogleAnalytic(Constant.HEADLINE_PAGE + String.valueOf(page));
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.NEW_HEADLINE +
                    "published/" + lastPublished,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String volleyResponse) {
                            Log.i(Constant.TAG, volleyResponse);
                            try {
                                JSONObject jsonObject = new JSONObject(volleyResponse);
                                jsonArrayResponses = jsonObject.getJSONArray(Constant.response);
                                if(jsonArrayResponses != null) {
                                    JSONObject objHeadline = jsonArrayResponses.getJSONObject(0);
                                    if(objHeadline !=  null) {
                                        jsonArraySegmentHeadline = objHeadline.getJSONArray(HEADLINES);
                                        for(int i=0; i<jsonArraySegmentHeadline.length(); i++) {
                                            JSONObject jsonHeadline = jsonArraySegmentHeadline.getJSONObject(i);
                                            String id = jsonHeadline.getString(Constant.id);
                                            String title = jsonHeadline.getString(Constant.title);
                                            String slug = jsonHeadline.getString(Constant.slug);
                                            String kanal = jsonHeadline.getString(Constant.kanal);
                                            String url = jsonHeadline.getString(Constant.url);
                                            String image_url = jsonHeadline.getString(Constant.image_url);
                                            String date_publish = jsonHeadline.getString(Constant.date_publish);
                                            String source = jsonHeadline.getString(Constant.source);
                                            String timestamp = jsonHeadline.getString(Constant.timestamp);
                                            headlineArrayList.add(new Headline(id, title, slug, kanal,
                                                    image_url, date_publish, source, url, timestamp));
                                        }
                                    }
                                }

                                lastPublished = headlineArrayList.get(headlineArrayList.size()-1).getTimestamp();

                                if(headlineArrayList.size() > 0 || !headlineArrayList.isEmpty()) {
                                    swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(headlineAdapter);
                                    swingBottomInAnimationAdapter.setAbsListView(listView);
                                    assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                                    swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(1000);
                                    headlineAdapter.notifyDataSetChanged();
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
                    if (mActivity != null) {
                        Toast.makeText(mActivity, R.string.label_error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            stringRequest.setShouldCache(true);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    Constant.TIME_OUT,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Global.getInstance(getActivity()).getRequestQueue().getCache().invalidate(Constant.NEW_HEADLINE +
                    "published/" + lastPublished, true);
            Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_HEADLINE +
                    "published/" + lastPublished);
            Global.getInstance(getActivity()).addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
        } else {
            listView.onLoadMoreComplete();
            listView.setSelection(0);
            Toast.makeText(mActivity, R.string.title_no_connection, Toast.LENGTH_SHORT).show();
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
