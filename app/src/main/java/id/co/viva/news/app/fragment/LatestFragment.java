package id.co.viva.news.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

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

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.R;
import id.co.viva.news.app.VivaApp;
import id.co.viva.news.app.activity.ActDetailTerbaru;
import id.co.viva.news.app.adapter.HeadlineAdapter;
import id.co.viva.news.app.adapter.TerbaruAdapter;
import id.co.viva.news.app.model.Headline;
import id.co.viva.news.app.model.News;

/**
 * Created by root on 09/10/14.
 */
public class LatestFragment extends Fragment {

    public static ArrayList<Headline> headlineArrayList;
    public static ArrayList<News> newsArrayList;

    private HeadlineAdapter headlineAdapter;
    private TerbaruAdapter terbaruAdapter;
    private ViewPager viewPager;
    private PagerTabStrip pagerTabStrip;
    private ListView listView;
    private RelativeLayout loading_layout;
    private AnimationAdapter mAnimAdapter;
    private Boolean isInternetPresent = false;
    private JSONArray jsonArrayResponses,
            jsonArraySegmentHeadline, jsonArraySegmentNews;

    final private static String HEADLINES = "headlines";
    final private static String NEWS = "terbaru";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isInternetPresent = VivaApp.getInstance().getConnectionStatus().isConnectingToInternet();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_latest, container, false);
            loading_layout = (RelativeLayout) rootView.findViewById(R.id.loading_progress_layout);
            loading_layout.setVisibility(View.VISIBLE);

            viewPager = (ViewPager) rootView.findViewById(R.id.vp_headline);

            pagerTabStrip = (PagerTabStrip) rootView.findViewById(R.id.pager_header);
            pagerTabStrip.setDrawFullUnderline(true);
            pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.header_grey));
            pagerTabStrip.setTextColor(getResources().getColor(R.color.header_grey));

            listView = (ListView) rootView.findViewById(R.id.list_news);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    if(newsArrayList.size() > 0) {
                        News news = newsArrayList.get(position);
                        Log.i(Constant.TAG, "ID : " + news.getId());
                        Bundle bundle = new Bundle();
                        bundle.putString("id", news.getId());
                        bundle.putString("url_shared", news.getUrl());
                        Intent intent = new Intent(VivaApp.getInstance(), ActDetailTerbaru.class);
                        intent.putExtras(bundle);
                        getActivity().startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
                    }
                }
            });

            headlineArrayList = new ArrayList<Headline>();
            newsArrayList = new ArrayList<News>();

            parseJson(headlineArrayList, newsArrayList);
        return rootView;
    }

    private void parseJson(final ArrayList<Headline> headlines, final ArrayList<News> news) {
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
                                    JSONObject objTerbaru = jsonArrayResponses.getJSONObject(1);
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
                                if(headlines.size() > 0 || !headlines.isEmpty()) {
                                    headlineAdapter = new HeadlineAdapter(getFragmentManager(), headlines);
                                    viewPager.setAdapter(headlineAdapter);
                                    headlineAdapter.notifyDataSetChanged();
                                }
                                if(news.size() > 0 || !news.isEmpty()) {
                                    terbaruAdapter = new TerbaruAdapter(getActivity(), news);
                                    mAnimAdapter = new ScaleInAnimationAdapter(terbaruAdapter);
                                    mAnimAdapter.setAbsListView(listView);
                                    listView.setAdapter(mAnimAdapter);
                                    terbaruAdapter.notifyDataSetChanged();
                                    loading_layout.setVisibility(View.GONE);
                                }
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
                                JSONObject objTerbaru = jsonArrayResponses.getJSONObject(1);
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
                            if(headlines.size() > 0 || !headlines.isEmpty()) {
                                headlineAdapter = new HeadlineAdapter(getFragmentManager(), headlines);
                                viewPager.setAdapter(headlineAdapter);
                                headlineAdapter.notifyDataSetChanged();
                            }
                            if(news.size() > 0 || !news.isEmpty()) {
                                terbaruAdapter = new TerbaruAdapter(getActivity(), news);
                                mAnimAdapter = new ScaleInAnimationAdapter(terbaruAdapter);
                                mAnimAdapter.setAbsListView(listView);
                                listView.setAdapter(mAnimAdapter);
                                terbaruAdapter.notifyDataSetChanged();
                                loading_layout.setVisibility(View.GONE);
                            }
                        }
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
            });
            stringRequest.setShouldCache(true);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    3000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
                        JSONObject objTerbaru = jsonArrayResponses.getJSONObject(1);
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
                    if(headlines.size() > 0 || !headlines.isEmpty()) {
                        headlineAdapter = new HeadlineAdapter(getFragmentManager(), headlines);
                        viewPager.setAdapter(headlineAdapter);
                        headlineAdapter.notifyDataSetChanged();
                    }
                    if(news.size() > 0 || !news.isEmpty()) {
                        terbaruAdapter = new TerbaruAdapter(getActivity(), news);
                        mAnimAdapter = new ScaleInAnimationAdapter(terbaruAdapter);
                        mAnimAdapter.setAbsListView(listView);
                        listView.setAdapter(mAnimAdapter);
                        terbaruAdapter.notifyDataSetChanged();
                        loading_layout.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.getMessage();
            }
            Toast.makeText(VivaApp.getInstance(), R.string.title_no_connection, Toast.LENGTH_SHORT).show();
        }
    }

}
