package id.co.viva.news.app.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import id.co.viva.news.app.R;
import id.co.viva.news.app.VivaApp;
import id.co.viva.news.app.adapter.SearchResultAdapter;
import id.co.viva.news.app.model.SearchResult;

/**
 * Created by reza on 13/10/14.
 */
public class ActSearchResult extends FragmentActivity {

    private TextView tvSearchResult;
    private ListView listSearchResult;
    private String query;
    private boolean isInternetPresent = false;
    private JSONArray jsonArrayResponses;
    private ArrayList<SearchResult> resultArrayList;
    private SearchResultAdapter adapter;
    private AnimationAdapter mAnimAdapter;
    private RelativeLayout loading_layout;
    private TextView tvNoResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_search_result);

        getActionBar().setTitle("Pencarian");

        isInternetPresent = VivaApp.getInstance().getConnectionStatus().isConnectingToInternet();

        tvSearchResult = (TextView)findViewById(R.id.text_search_result);
        listSearchResult = (ListView)findViewById(R.id.list_search_result);
        tvNoResult = (TextView)findViewById(R.id.text_no_result);
        loading_layout = (RelativeLayout)findViewById(R.id.loading_progress_layout);

        if(isInternetPresent) {
            tvNoResult.setVisibility(View.GONE);
            loading_layout.setVisibility(View.VISIBLE);
            resultArrayList = new ArrayList<SearchResult>();
            handleIntent(getIntent());
        } else {
            Toast.makeText(VivaApp.getInstance(), R.string.title_no_connection, Toast.LENGTH_SHORT).show();
            loading_layout.setVisibility(View.GONE);
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

            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_SEARCH + query,
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
                                        String id = jsonHeadline.getString(Constant.id);
                                        String kanal = jsonHeadline.getString(Constant.kanal);
                                        String image_url = jsonHeadline.getString(Constant.image_url);
                                        String title = jsonHeadline.getString(Constant.title);
                                        String slug = jsonHeadline.getString(Constant.slug);
                                        String date_publish = jsonHeadline.getString(Constant.date_publish);
                                        resultArrayList.add(new SearchResult(id, kanal, image_url,
                                                title, slug, date_publish));
                                        Log.i(Constant.TAG, "SEARCH RESULTS : " + resultArrayList.get(i).getTitle());
                                    }
                                }
                                if(resultArrayList.size() > 0 || !resultArrayList.isEmpty()) {
                                    adapter = new SearchResultAdapter(VivaApp.getInstance(), resultArrayList);
                                    mAnimAdapter = new ScaleInAnimationAdapter(adapter);
                                    mAnimAdapter.setAbsListView(listSearchResult);
                                    listSearchResult.setAdapter(mAnimAdapter);
                                    adapter.notifyDataSetChanged();
                                    loading_layout.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                e.getMessage();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyError.getMessage();
                }
            });

            stringRequest.setShouldCache(true);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    3000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VivaApp.getInstance().getRequestQueue().getCache().invalidate(Constant.URL_SEARCH + query, true);
            VivaApp.getInstance().getRequestQueue().getCache().get(Constant.URL_SEARCH + query);
            VivaApp.getInstance().addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
        }
    }

}
