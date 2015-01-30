package id.co.viva.news.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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
import id.co.viva.news.app.coachmark.CoachmarkBuilder;
import id.co.viva.news.app.coachmark.CoachmarkView;
import id.co.viva.news.app.component.GoogleMusicDicesDrawable;
import id.co.viva.news.app.component.LoadMoreListView;
import id.co.viva.news.app.interfaces.CoachmarkListener;
import id.co.viva.news.app.interfaces.OnLoadMoreListener;
import id.co.viva.news.app.model.News;
import id.co.viva.news.app.services.Analytics;

/**
 * Created by root on 09/10/14.
 */
public class TerbaruFragment extends Fragment implements AdapterView.OnItemClickListener,
        OnLoadMoreListener, View.OnClickListener {

    final private static String NEWS = "terbaru";
    public static ArrayList<News> newsArrayList;

    private int dataSize = 0;
    private String data;
    private SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;
    private TerbaruAdapter terbaruAdapter;
    private LoadMoreListView listView;
    private TextView lastUpdate;
    private ProgressBar loading_layout;
    private TextView labelLoadData;
    private Boolean isInternetPresent = false;
    private JSONArray jsonArrayResponses, jsonArraySegmentNews;
    private TextView labelText;
    private SimpleDateFormat date, time;
    private Analytics analytics;
    private RippleView rippleView;
    private View coachmarkView, coachmarkViewSearch;
    private CoachmarkView showtips;
    private FloatingActionButton floatingActionButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isInternetPresent = Global.getInstance(getActivity()).getConnectionStatus().isConnectingToInternet();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(getResources().getColor(R.color.header_headline_terbaru_new));
        activity.getActionBar().setBackgroundDrawable(colorDrawable);
        activity.getActionBar().setIcon(R.drawable.logo_viva_coid_second);
    }

    private Drawable getProgressDrawable() {
        Drawable progressDrawable = null;
        progressDrawable = new GoogleMusicDicesDrawable.Builder().build();
        return progressDrawable;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_terbaru_headline, container, false);

        loading_layout = (ProgressBar) rootView.findViewById(R.id.loading_progress_layout_headline_terbaru);
        labelLoadData = (TextView) rootView.findViewById(R.id.text_loading_data);

        coachmarkView = rootView.findViewById(R.id.coachmark_nav_toggle);
        coachmarkViewSearch = rootView.findViewById(R.id.coachmark_search);

        Rect bounds = loading_layout.getIndeterminateDrawable().getBounds();
        loading_layout.setIndeterminateDrawable(getProgressDrawable());
        loading_layout.getIndeterminateDrawable().setBounds(bounds);

        labelLoadData.setVisibility(View.VISIBLE);
        loading_layout.setVisibility(View.VISIBLE);

        rippleView = (RippleView) rootView.findViewById(R.id.layout_ripple_view_headline_terbaru);
        rippleView.setVisibility(View.GONE);
        rippleView.setOnClickListener(this);

        analytics = new Analytics(getActivity());
        analytics.getAnalyticByATInternet(Constant.TERBARU_PAGE);
        analytics.getAnalyticByGoogleAnalytic(Constant.TERBARU_PAGE);

        labelText = (TextView) rootView.findViewById(R.id.text_terbaru_headline);
        labelText.setText(getString(R.string.label_terbaru));
        lastUpdate = (TextView) rootView.findViewById(R.id.date_terbaru_headline);

        newsArrayList = new ArrayList<News>();
        terbaruAdapter = new TerbaruAdapter(getActivity(), newsArrayList);

        listView = (LoadMoreListView) rootView.findViewById(R.id.list_terbaru_headline);
        listView.setOnItemClickListener(this);
        listView.setOnLoadMoreListener(this);

        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        floatingActionButton.setVisibility(View.GONE);
        floatingActionButton.attachToListView(listView, new FloatingActionButton.FabOnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                int firstIndex = listView.getFirstVisiblePosition();
                if(firstIndex > Constant.NUMBER_OF_TOP_LIST_ITEMS) {
                    floatingActionButton.setVisibility(View.VISIBLE);
                } else {
                    floatingActionButton.setVisibility(View.GONE);
                }
            }
        });
        floatingActionButton.setOnClickListener(this);

        parseJson(newsArrayList);

        return rootView;
    }

    private void parseJson(final ArrayList<News> news) {
        if(isInternetPresent) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.NEW_TERBARU,
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
                                        jsonArraySegmentNews = objTerbaru.getJSONArray(NEWS);
                                        for(int i=0; i<jsonArraySegmentNews.length(); i++) {
                                            JSONObject jsonTerbaru = jsonArraySegmentNews.getJSONObject(i);
                                            String id = jsonTerbaru.getString(Constant.id);
                                            String title = jsonTerbaru.getString(Constant.title);
                                            String slug = jsonTerbaru.getString(Constant.slug);
                                            String kanal = jsonTerbaru.getString(Constant.kanal);
                                            String url = jsonTerbaru.getString(Constant.url);
                                            String image_url = jsonTerbaru.getString(Constant.image_url);
                                            String date_publish = jsonTerbaru.getString(Constant.date_publish);
                                            news.add(new News(id, title, slug, kanal, url,
                                                    image_url, date_publish));
                                            Log.i(Constant.TAG, "NEWS : " + news.get(i).getTitle());
                                        }
                                    }
                                }

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

                                showCoachMark();

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
                        if(Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_TERBARU) != null) {
                            String cachedResponse = new String(Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_TERBARU).data);
                            Log.i(Constant.TAG, "From Cached : " + cachedResponse);
                            JSONObject jsonObject = new JSONObject(cachedResponse);
                            jsonArrayResponses = jsonObject.getJSONArray(Constant.response);
                            if(jsonArrayResponses != null) {
                                JSONObject objTerbaru = jsonArrayResponses.getJSONObject(0);
                                if(objTerbaru !=  null) {
                                    jsonArraySegmentNews = objTerbaru.getJSONArray(NEWS);
                                    for(int i=0; i<jsonArraySegmentNews.length(); i++) {
                                        JSONObject jsonTerbaru = jsonArraySegmentNews.getJSONObject(i);
                                        String id = jsonTerbaru.getString(Constant.id);
                                        String title = jsonTerbaru.getString(Constant.title);
                                        String slug = jsonTerbaru.getString(Constant.slug);
                                        String kanal = jsonTerbaru.getString(Constant.kanal);
                                        String url = jsonTerbaru.getString(Constant.url);
                                        String image_url = jsonTerbaru.getString(Constant.image_url);
                                        String date_publish = jsonTerbaru.getString(Constant.date_publish);
                                        news.add(new News(id, title, slug, kanal, url,
                                                image_url, date_publish));
                                        Log.i(Constant.TAG, "NEWS CACHED : " + news.get(i).getTitle());
                                    }
                                }
                            }

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
                            jsonArraySegmentNews = objTerbaru.getJSONArray(NEWS);
                            for(int i=0; i<jsonArraySegmentNews.length(); i++) {
                                JSONObject jsonTerbaru = jsonArraySegmentNews.getJSONObject(i);
                                String id = jsonTerbaru.getString(Constant.id);
                                String title = jsonTerbaru.getString(Constant.title);
                                String slug = jsonTerbaru.getString(Constant.slug);
                                String kanal = jsonTerbaru.getString(Constant.kanal);
                                String url = jsonTerbaru.getString(Constant.url);
                                String image_url = jsonTerbaru.getString(Constant.image_url);
                                String date_publish = jsonTerbaru.getString(Constant.date_publish);
                                news.add(new News(id, title, slug, kanal, url,
                                        image_url, date_publish));
                                Log.i(Constant.TAG, "NEWS CACHED : " + news.get(i).getTitle());
                            }
                        }
                    }

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
            Log.i(Constant.TAG, "ID : " + news.getId());
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
        data = String.valueOf(dataSize += 10);
        if(isInternetPresent) {
            analytics.getAnalyticByATInternet(Constant.TERBARU_PAGE + "_" + data);
            analytics.getAnalyticByGoogleAnalytic(Constant.TERBARU_PAGE + "_" + data);

                StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.NEW_TERBARU + "start/" + data,
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
                                            jsonArraySegmentNews = objTerbaru.getJSONArray(NEWS);
                                            for(int i=0; i<jsonArraySegmentNews.length(); i++) {
                                                JSONObject jsonTerbaru = jsonArraySegmentNews.getJSONObject(i);
                                                String id = jsonTerbaru.getString(Constant.id);
                                                String title = jsonTerbaru.getString(Constant.title);
                                                String slug = jsonTerbaru.getString(Constant.slug);
                                                String kanal = jsonTerbaru.getString(Constant.kanal);
                                                String url = jsonTerbaru.getString(Constant.url);
                                                String image_url = jsonTerbaru.getString(Constant.image_url);
                                                String date_publish = jsonTerbaru.getString(Constant.date_publish);
                                                newsArrayList.add(new News(id, title, slug, kanal, url,
                                                        image_url, date_publish));
                                            }
                                        }
                                    }

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
                Global.getInstance(getActivity()).getRequestQueue().getCache().invalidate(Constant.NEW_TERBARU + "start/" + data, true);
                Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_TERBARU + "start/" + data);
                Global.getInstance(getActivity()).addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
        } else {
            listView.onLoadMoreComplete();
            listView.setSelection(0);
            Toast.makeText(getActivity(), R.string.title_no_connection, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.layout_ripple_view_headline_terbaru) {
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
                                    if(jsonArrayResponses != null) {
                                        JSONObject objTerbaru = jsonArrayResponses.getJSONObject(0);
                                        if(objTerbaru !=  null) {
                                            jsonArraySegmentNews = objTerbaru.getJSONArray(NEWS);
                                            for(int i=0; i<jsonArraySegmentNews.length(); i++) {
                                                JSONObject jsonTerbaru = jsonArraySegmentNews.getJSONObject(i);
                                                String id = jsonTerbaru.getString(Constant.id);
                                                String title = jsonTerbaru.getString(Constant.title);
                                                String slug = jsonTerbaru.getString(Constant.slug);
                                                String kanal = jsonTerbaru.getString(Constant.kanal);
                                                String url = jsonTerbaru.getString(Constant.url);
                                                String image_url = jsonTerbaru.getString(Constant.image_url);
                                                String date_publish = jsonTerbaru.getString(Constant.date_publish);
                                                newsArrayList.add(new News(id, title, slug, kanal, url,
                                                        image_url, date_publish));
                                                Log.i(Constant.TAG, "NEWS : " + newsArrayList.get(i).getTitle());
                                            }
                                        }
                                    }

                                    if(newsArrayList.size() > 0 || !newsArrayList.isEmpty()) {
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

                                    showCoachMark();

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
                            if(Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_TERBARU) != null) {
                                String cachedResponse = new String(Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_TERBARU).data);
                                Log.i(Constant.TAG, "From Cached : " + cachedResponse);
                                JSONObject jsonObject = new JSONObject(cachedResponse);
                                jsonArrayResponses = jsonObject.getJSONArray(Constant.response);
                                if(jsonArrayResponses != null) {
                                    JSONObject objTerbaru = jsonArrayResponses.getJSONObject(0);
                                    if(objTerbaru !=  null) {
                                        jsonArraySegmentNews = objTerbaru.getJSONArray(NEWS);
                                        for(int i=0; i<jsonArraySegmentNews.length(); i++) {
                                            JSONObject jsonTerbaru = jsonArraySegmentNews.getJSONObject(i);
                                            String id = jsonTerbaru.getString(Constant.id);
                                            String title = jsonTerbaru.getString(Constant.title);
                                            String slug = jsonTerbaru.getString(Constant.slug);
                                            String kanal = jsonTerbaru.getString(Constant.kanal);
                                            String url = jsonTerbaru.getString(Constant.url);
                                            String image_url = jsonTerbaru.getString(Constant.image_url);
                                            String date_publish = jsonTerbaru.getString(Constant.date_publish);
                                            newsArrayList.add(new News(id, title, slug, kanal, url,
                                                    image_url, date_publish));
                                            Log.i(Constant.TAG, "NEWS CACHED : " + newsArrayList.get(i).getTitle());
                                        }
                                    }
                                }

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

    private void showCoachMark() {
        if(Global.getInstance(getActivity()).getSharedPreferences(getActivity()).getBoolean(Constant.FIRST_INSTALL, true)) {
            RelativeLayout relativeLayout = new RelativeLayout(getActivity());
            relativeLayout.setLayoutParams(new ViewGroup.LayoutParams(50, 50));
            ((RelativeLayout) coachmarkView).addView(relativeLayout);
            showtips = new CoachmarkBuilder(getActivity())
                    .setTarget(relativeLayout, 50, getHeightFocus(), 50)
                    .setTitle(getResources().getString(R.string.label_navigation_button))
                    .setDescription(getResources().getString(R.string.label_navigation_button_desc))
                    .setDelay(2000)
                    .setCallback(new CoachmarkListener() {
                        @Override
                        public void gotItClicked() {
                            RelativeLayout relativeLayout = new RelativeLayout(getActivity());
                            relativeLayout.setLayoutParams(new ViewGroup.LayoutParams(75, 75));
                            ((RelativeLayout) coachmarkViewSearch).addView(relativeLayout);
                            showtips = new CoachmarkBuilder(getActivity())
                                    .setTarget(relativeLayout, getWidthFocus(), getHeightFocus(), 65)
                                    .setTitle(getResources().getString(R.string.label_search_button))
                                    .setDescription(getResources().getString(R.string.label_search_button_desc))
                                    .build();
                            showtips.show(getActivity());
                        }
                    })
                    .build();
            showtips.show(getActivity());
            Global.getInstance(getActivity()).getSharedPreferences(getActivity()).
                    edit().putBoolean(Constant.FIRST_INSTALL, false).commit();
        }
    }

    private int getHeightFocus() {
        int actionBarHeight = 0;
        int heightFocus;
        TypedValue typedValue = new TypedValue();
        if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(typedValue.data,getResources().getDisplayMetrics());
        }
        heightFocus = 0 - (actionBarHeight / 2);
        return heightFocus;
    }

    private int getWidthFocus() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return width - 75;
    }

}
