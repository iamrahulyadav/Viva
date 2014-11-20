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
import android.widget.AdapterView;
import android.widget.ListView;
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
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import id.co.viva.news.app.coachmark.CoachmarkBuilder;
import id.co.viva.news.app.coachmark.CoachmarkView;
import id.co.viva.news.app.component.GoogleMusicDicesDrawable;
import id.co.viva.news.app.interfaces.CoachmarkListener;
import id.co.viva.news.app.services.Analytics;
import id.co.viva.news.app.Constant;
import id.co.viva.news.app.R;
import id.co.viva.news.app.VivaApp;
import id.co.viva.news.app.activity.ActDetailHeadline;
import id.co.viva.news.app.adapter.HeadlineAdapter;
import id.co.viva.news.app.model.Headline;

/**
 * Created by reza on 28/10/14.
 */
public class HeadlineFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private static String HEADLINES = "headlines";
    public static ArrayList<Headline> headlineArrayList;

    private SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;
    private HeadlineAdapter headlineAdapter;
    private ListView listView;
    private TextView lastUpdate;
    private TextView labelLoadData;
    private ProgressBar loading_layout;
    private Boolean isInternetPresent = false;
    private JSONArray jsonArrayResponses, jsonArraySegmentHeadline;
    private TextView labelText;
    private SimpleDateFormat date, time;
    private Analytics analytics;
    private RippleView rippleView;
    private View coachmarkView, coachmarkViewSearch;
    private CoachmarkView showtips;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isInternetPresent = VivaApp.getInstance().getConnectionStatus().isConnectingToInternet();
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

        coachmarkView = rootView.findViewById(R.id.coachmark_nav_toggle);
        coachmarkViewSearch = rootView.findViewById(R.id.coachmark_search);

        loading_layout = (ProgressBar) rootView.findViewById(R.id.loading_progress_layout_headline_terbaru);
        labelLoadData = (TextView) rootView.findViewById(R.id.text_loading_data);

        Rect bounds = loading_layout.getIndeterminateDrawable().getBounds();
        loading_layout.setIndeterminateDrawable(getProgressDrawable());
        loading_layout.getIndeterminateDrawable().setBounds(bounds);

        labelLoadData.setVisibility(View.VISIBLE);
        loading_layout.setVisibility(View.VISIBLE);

        rippleView = (RippleView) rootView.findViewById(R.id.layout_ripple_view_headline_terbaru);
        rippleView.setVisibility(View.GONE);
        rippleView.setOnClickListener(this);

        analytics = new Analytics();
        analytics.getAnalyticByATInternet(Constant.HEADLINE_PAGE);
        analytics.getAnalyticByGoogleAnalytic(Constant.HEADLINE_PAGE);

        lastUpdate = (TextView) rootView.findViewById(R.id.date_terbaru_headline);
        labelText = (TextView) rootView.findViewById(R.id.text_terbaru_headline);
        labelText.setText(getString(R.string.label_headline));

        listView = (ListView) rootView.findViewById(R.id.list_terbaru_headline);
        listView.setOnItemClickListener(this);

        headlineArrayList = new ArrayList<Headline>();
        parseJson(headlineArrayList);

        return rootView;
    }

    private void parseJson(final ArrayList<Headline> headlines) {
        if(isInternetPresent) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_HOMEPAGE,
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
                                            headlines.add(new Headline(id, title, slug, kanal,
                                                    image_url, date_publish, source, url));
                                            Log.i(Constant.TAG, "HEADLINES : " + headlines.get(i).getTitle());
                                        }
                                    }
                                }

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

                                    if(Constant.getSharedPreferences(VivaApp.getInstance()).getBoolean(Constant.FIRST_INSTALL, true)) {
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
                                        Constant.getSharedPreferences(VivaApp.getInstance()).edit().putBoolean(Constant.FIRST_INSTALL, false).commit();
                                    }
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
                        if(VivaApp.getInstance().getRequestQueue().getCache().get(Constant.URL_HOMEPAGE) != null) {
                            String cachedResponse = new String(VivaApp.getInstance().getRequestQueue().getCache().get(Constant.URL_HOMEPAGE).data);
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
                                        headlines.add(new Headline(id, title, slug, kanal,
                                                image_url, date_publish, source, url));
                                        Log.i(Constant.TAG, "HEADLINES CACHED : " + headlines.get(i).getTitle());
                                    }
                                }
                            }

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
            VivaApp.getInstance().getRequestQueue().getCache().invalidate(Constant.URL_HOMEPAGE, true);
            VivaApp.getInstance().getRequestQueue().getCache().get(Constant.URL_HOMEPAGE);
            VivaApp.getInstance().addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
        } else {
            try {
                if(VivaApp.getInstance().getRequestQueue().getCache().get(Constant.URL_HOMEPAGE) != null) {
                    String cachedResponse = new String(VivaApp.getInstance().getRequestQueue().getCache().get(Constant.URL_HOMEPAGE).data);
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
                                headlines.add(new Headline(id, title, slug, kanal,
                                        image_url, date_publish, source, url));
                                Log.i(Constant.TAG, "HEADLINES CACHED : " + headlines.get(i).getTitle());
                            }
                        }
                    }

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
            Toast.makeText(VivaApp.getInstance(), R.string.title_no_connection, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if(headlineArrayList.size() > 0) {
            Headline headline = headlineArrayList.get(position);
            Log.i(Constant.TAG, "ID : " + headline.getId());
            Bundle bundle = new Bundle();
            bundle.putString("id", headline.getId());
            Intent intent = new Intent(VivaApp.getInstance(), ActDetailHeadline.class);
            intent.putExtras(bundle);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.layout_ripple_view_headline_terbaru) {
            if(isInternetPresent) {
                loading_layout.setVisibility(View.VISIBLE);
                labelLoadData.setVisibility(View.VISIBLE);
                rippleView.setVisibility(View.GONE);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_HOMEPAGE,
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
                                                headlineArrayList.add(new Headline(id, title, slug, kanal,
                                                        image_url, date_publish, source, url));
                                                Log.i(Constant.TAG, "HEADLINES : " + headlineArrayList.get(i).getTitle());
                                            }
                                        }
                                    }

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

                                        if(Constant.getSharedPreferences(VivaApp.getInstance()).getBoolean(Constant.FIRST_INSTALL, true)) {
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
                                            Constant.getSharedPreferences(VivaApp.getInstance()).edit().putBoolean(Constant.FIRST_INSTALL, false).commit();
                                        }
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
                        volleyError.getMessage();
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
                VivaApp.getInstance().getRequestQueue().getCache().invalidate(Constant.URL_HOMEPAGE, true);
                VivaApp.getInstance().getRequestQueue().getCache().get(Constant.URL_HOMEPAGE);
                VivaApp.getInstance().addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
            } else {
                Toast.makeText(VivaApp.getInstance(), R.string.title_no_connection, Toast.LENGTH_SHORT).show();
            }
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
