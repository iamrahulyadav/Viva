package id.co.viva.news.app.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.TextView;
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
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.adapter.SearchResultAdapter;
import id.co.viva.news.app.component.LoadMoreListView;
import id.co.viva.news.app.component.ProgressWheel;
import id.co.viva.news.app.interfaces.OnLoadMoreListener;
import id.co.viva.news.app.model.SearchResult;
import id.co.viva.news.app.services.Analytics;

/**
 * Created by reza on 13/10/14.
 */
public class ActSearchResult extends FragmentActivity implements
        AdapterView.OnItemClickListener, OnLoadMoreListener {

    private TextView tvSearchResult;
    private LoadMoreListView listSearchResult;
    private String query;
    private boolean isInternetPresent = false;
    private JSONArray jsonArrayResponses;
    private ArrayList<SearchResult> resultArrayList;
    private TextView tvNoResult;
    private AnimationAdapter mAnimAdapter;
    private Analytics analytics;
    private Menu mMenu;
    private int dataSize = 0;
    private String data;
    private ProgressWheel progressWheel;
    private SearchResultAdapter searchResultAdapter;

    private String id ;
    private String kanal ;
    private String image_url ;
    private String title ;
    private String slug ;
    private String date_publish ;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_search_result);

        getActionBar().setTitle("Pencarian");

        isInternetPresent = Global.getInstance(this).
                getConnectionStatus().isConnectingToInternet();

        resultArrayList = new ArrayList<SearchResult>();
        searchResultAdapter = new SearchResultAdapter(this, resultArrayList);

        tvSearchResult = (TextView)findViewById(R.id.text_search_result);
        listSearchResult = (LoadMoreListView)findViewById(R.id.list_search_result);
        listSearchResult.setOnItemClickListener(this);
        listSearchResult.setOnLoadMoreListener(this);
        tvNoResult = (TextView)findViewById(R.id.text_no_result);
        progressWheel = (ProgressWheel)findViewById(R.id.progress_wheel);

        if(isInternetPresent) {
            tvNoResult.setVisibility(View.GONE);
            progressWheel.setVisibility(View.VISIBLE);
            handleIntent(getIntent());
        } else {
            Toast.makeText(this, R.string.title_no_connection, Toast.LENGTH_SHORT).show();
            progressWheel.setVisibility(View.GONE);
            tvNoResult.setVisibility(View.VISIBLE);
            tvSearchResult.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equalsIgnoreCase(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            tvSearchResult.setText("Hasil Pencarian : " + query);

            analytics = new Analytics(this);
            analytics.getAnalyticByATInternet(Constant.SEARCH_RESULT_PAGE + query.toUpperCase());
            analytics.getAnalyticByGoogleAnalytic(Constant.SEARCH_RESULT_PAGE + query.toUpperCase());

            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.NEW_SEARCH + "q/" + query + "/s/0",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String volleyResponse) {
                            Log.i(Constant.TAG, "SEARCH RESPONSES : " + volleyResponse);
                            try {
                                JSONObject jsonObject = new JSONObject(volleyResponse);
                                JSONObject response = jsonObject.getJSONObject(Constant.response);
                                jsonArrayResponses = response.getJSONArray(Constant.search);
                                if(jsonArrayResponses != null) {
                                    for(int i=0; i<jsonArrayResponses.length(); i++) {
                                        JSONObject jsonHeadline = jsonArrayResponses.getJSONObject(i);
                                        id = jsonHeadline.getString(Constant.id);
                                        kanal = jsonHeadline.getString(Constant.kanal);
                                        image_url = jsonHeadline.getString(Constant.image_url);
                                        title = jsonHeadline.getString(Constant.title);
                                        slug = jsonHeadline.getString(Constant.slug);
                                        date_publish = jsonHeadline.getString(Constant.date_publish);
                                        url = jsonHeadline.getString(Constant.url);
                                        resultArrayList.add(new SearchResult(id, kanal, image_url,
                                                title, slug, date_publish, url));
                                        Log.i(Constant.TAG, "SEARCH RESULTS : " + resultArrayList.get(i).getTitle());
                                    }
                                }
                                if (resultArrayList.size() > 0 || !resultArrayList.isEmpty()) {
                                    mAnimAdapter = new ScaleInAnimationAdapter(searchResultAdapter);
                                    mAnimAdapter.setAbsListView(listSearchResult);
                                    listSearchResult.setAdapter(mAnimAdapter);
                                    mAnimAdapter.notifyDataSetChanged();
                                    progressWheel.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                e.getMessage();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyError.getMessage();
                    progressWheel.setVisibility(View.GONE);
                    SearchView searchView =
                            (SearchView) mMenu.findItem(R.id.action_search).getActionView();
                    searchView.setIconified(false);
                }
            });
            stringRequest.setShouldCache(true);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    Constant.TIME_OUT,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Global.getInstance(this).getRequestQueue().getCache().invalidate(Constant.NEW_SEARCH + "q/" + query + "/s/0", true);
            Global.getInstance(this).getRequestQueue().getCache().get(Constant.NEW_SEARCH + "q/" + query + "/s/0");
            Global.getInstance(this).addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if(resultArrayList.size() > 0) {
            SearchResult searchResult = resultArrayList.get(position);
            Log.i(Constant.TAG, "ID : " + searchResult.getId());
            Bundle bundle = new Bundle();
            bundle.putString("id", searchResult.getId());
            bundle.putString("type", "search");
            bundle.putString("kanal", searchResult.getKanal());
            bundle.putString("shared_url", searchResult.getUrl());
            Intent intent = new Intent(this, ActDetailContentDefault.class);
            intent.putExtras(bundle);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_frag_default, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        int searchTextViewId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView searchTextView = (TextView) searchView.findViewById(searchTextViewId);
        searchTextView.setHintTextColor(getResources().getColor(R.color.white));
        return true;
    }

    @Override
    public void onLoadMore() {
        data = String.valueOf(dataSize += 10);
        if(isInternetPresent) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.NEW_SEARCH + "q/" + query + "/s/" + data,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String volleyResponse) {
                            Log.i(Constant.TAG, "LOAD MORE SEARCH RESPONSES : " + Constant.NEW_SEARCH + "q/" + query + "/s/" + data);
                            try {
                                JSONObject jsonObject = new JSONObject(volleyResponse);
                                JSONObject response = jsonObject.getJSONObject(Constant.response);
                                jsonArrayResponses = response.getJSONArray(Constant.search);
                                if(jsonArrayResponses != null) {
                                    for(int i=0; i<jsonArrayResponses.length(); i++) {
                                        JSONObject jsonHeadline = jsonArrayResponses.getJSONObject(i);
                                        id = jsonHeadline.getString(Constant.id);
                                        kanal = jsonHeadline.getString(Constant.kanal);
                                        image_url = jsonHeadline.getString(Constant.image_url);
                                        title = jsonHeadline.getString(Constant.title);
                                        slug = jsonHeadline.getString(Constant.slug);
                                        date_publish = jsonHeadline.getString(Constant.date_publish);
                                        url = jsonHeadline.getString(Constant.url);
                                        resultArrayList.add(new SearchResult(id, kanal, image_url,
                                                title, slug, date_publish, url));
                                        Log.i(Constant.TAG, "LOAD MORE SEARCH RESULTS : " + resultArrayList.get(i).getTitle());
                                    }
                                }
                                if (resultArrayList.size() > 0 || !resultArrayList.isEmpty()) {
                                    mAnimAdapter = new ScaleInAnimationAdapter(searchResultAdapter);
                                    mAnimAdapter.setAbsListView(listSearchResult);
                                    mAnimAdapter.notifyDataSetChanged();
                                    listSearchResult.onLoadMoreComplete();
                                }
                            } catch (Exception e) {
                                e.getMessage();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(ActSearchResult.this, R.string.label_error, Toast.LENGTH_SHORT).show();
                }
            });
            stringRequest.setShouldCache(true);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    Constant.TIME_OUT,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Global.getInstance(this).getRequestQueue().getCache().invalidate(Constant.NEW_SEARCH + "q/" + query + "/s/" + data, true);
            Global.getInstance(this).getRequestQueue().getCache().get(Constant.NEW_SEARCH + "q/" + query + "/s/" + data);
            Global.getInstance(this).addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
        } else {
            Toast.makeText(this, R.string.title_no_connection, Toast.LENGTH_SHORT).show();
        }
    }

}
