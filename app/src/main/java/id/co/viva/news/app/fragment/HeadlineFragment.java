package id.co.viva.news.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
public class HeadlineFragment extends Fragment {

    private static String HEADLINES = "headlines";
    public static ArrayList<Headline> headlineArrayList;

    private SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;
    private HeadlineAdapter headlineAdapter;
    private ListView listView;
    private TextView lastUpdate;
    private RelativeLayout loading_layout;
    private Boolean isInternetPresent = false;
    private JSONArray jsonArrayResponses, jsonArraySegmentHeadline;
    private TextView labelText;
    private SimpleDateFormat date, time;
    private Analytics analytics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isInternetPresent = VivaApp.getInstance().getConnectionStatus().isConnectingToInternet();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(getResources().getColor(R.color.header_headline_terbaru));
        activity.getActionBar().setBackgroundDrawable(colorDrawable);
        activity.getActionBar().setIcon(R.drawable.logo_viva_coid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_terbaru_headline, container, false);

        analytics = new Analytics();
        analytics.getAnalyticByATInternet(Constant.HEADLINE_PAGE);
        analytics.getAnalyticByGoogleAnalytic(Constant.HEADLINE_PAGE);

        loading_layout = (RelativeLayout) rootView.findViewById(R.id.loading_progress_layout);
        loading_layout.setVisibility(View.VISIBLE);

        lastUpdate = (TextView) rootView.findViewById(R.id.date_terbaru_headline);
        labelText = (TextView) rootView.findViewById(R.id.text_terbaru_headline);
        labelText.setText(getString(R.string.label_headline));

        listView = (ListView) rootView.findViewById(R.id.list_terbaru_headline);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(headlineArrayList.size() > 0) {
                    Headline headline = headlineArrayList.get(position);
                    Log.i(Constant.TAG, "ID : " + headline.getId());
                    Bundle bundle = new Bundle();
                    bundle.putString("id", headline.getId());
                    bundle.putString("url_shared", headline.getUrl());
                    Intent intent = new Intent(VivaApp.getInstance(), ActDetailHeadline.class);
                    intent.putExtras(bundle);
                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
                }
            }
        });
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
                            }

                            lastUpdate.setText(R.string.label_content_not_update);
                        }
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
            });
            stringRequest.setShouldCache(true);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    Constant.TIME_OUT,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
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
                    }

                    lastUpdate.setText(R.string.label_content_not_update);
                }
            } catch (Exception e) {
                e.getMessage();
            }
            Toast.makeText(VivaApp.getInstance(), R.string.title_no_connection, Toast.LENGTH_SHORT).show();
        }
    }

}
