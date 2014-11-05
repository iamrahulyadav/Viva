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
import android.widget.GridView;
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

import java.util.ArrayList;

import id.co.viva.news.app.services.Analytics;
import id.co.viva.news.app.Constant;
import id.co.viva.news.app.R;
import id.co.viva.news.app.VivaApp;
import id.co.viva.news.app.activity.ActDetailChannelLife;
import id.co.viva.news.app.adapter.FeaturedLifeAdapter;
import id.co.viva.news.app.model.FeaturedLife;

/**
 * Created by reza on 22/10/14.
 */
public class LifeFragment extends Fragment {

    public static ArrayList<FeaturedLife> featuredNewsArrayList;
    private SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;
    private boolean isInternetPresent = false;
    private GridView gridNews;
    private String cachedResponse;
    private RelativeLayout loading_layout;
    private TextView tvNoResult;
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
        colorDrawable.setColor(getResources().getColor(R.color.color_life));
        activity.getActionBar().setBackgroundDrawable(colorDrawable);
        activity.getActionBar().setIcon(R.drawable.logo_viva_coid_second);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_life, container, false);

        analytics = new Analytics();
        analytics.getAnalyticByATInternet(Constant.KANAL_LIFE_PAGE);
        analytics.getAnalyticByGoogleAnalytic(Constant.KANAL_LIFE_PAGE);

        tvNoResult = (TextView) rootView.findViewById(R.id.text_no_result);
        tvNoResult.setVisibility(View.GONE);

        loading_layout = (RelativeLayout) rootView.findViewById(R.id.loading_progress_layout);
        loading_layout.setVisibility(View.VISIBLE);

        gridNews = (GridView) rootView.findViewById(R.id.grid_life);
        gridNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(featuredNewsArrayList.size() > 0) {
                    FeaturedLife featuredLife = featuredNewsArrayList.get(position);
                    Log.i(Constant.TAG, "ID : " + featuredLife.getChannel_id());
                    Bundle bundle = new Bundle();
                    bundle.putString("id", featuredLife.getChannel_id());
                    bundle.putString("channel_title", featuredLife.getChannel_title());
                    Intent intent = new Intent(VivaApp.getInstance(), ActDetailChannelLife.class);
                    intent.putExtras(bundle);
                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
                }
            }
        });

        featuredNewsArrayList = new ArrayList<FeaturedLife>();

        if(isInternetPresent) {
            StringRequest request = new StringRequest(Request.Method.GET, Constant.URL_KANAL_LIFE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            Log.i(Constant.TAG, "KANAL LIFE RESPONSE : " + s);
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                JSONArray response = jsonObject.getJSONArray(Constant.response);
                                for(int i=0; i<response.length(); i++) {
                                    JSONObject obj = response.getJSONObject(i);
                                    if(obj != null) {
                                        JSONArray objKanal = obj.getJSONArray("news");
                                        for(int j=0; j<objKanal.length(); j++) {
                                            JSONObject field = objKanal.getJSONObject(j);
                                            String channel_title = field.getString("channel_title");
                                            String id = field.getString("id");
                                            String channel_id = field.getString("channel_id");
                                            String level = field.getString("level");
                                            String title = field.getString("title");
                                            String kanal = field.getString("kanal");
                                            String image_url = field.getString("image_url");
                                            featuredNewsArrayList.add(new FeaturedLife(channel_title, id,
                                                    channel_id, level, title, kanal, image_url));
                                            Log.i(Constant.TAG, "Channel Title : " + featuredNewsArrayList.get(i).getChannel_title());
                                        }
                                    }
                                }
                                if(featuredNewsArrayList.size() > 0 || !featuredNewsArrayList.isEmpty()) {
                                    swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
                                            new FeaturedLifeAdapter(getActivity(), featuredNewsArrayList));
                                    swingBottomInAnimationAdapter.setAbsListView(gridNews);
                                    assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                                    swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(0000);
                                    gridNews.setAdapter(swingBottomInAnimationAdapter);
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
                        }
                    });
            request.setShouldCache(true);
            request.setRetryPolicy(new DefaultRetryPolicy(
                    Constant.TIME_OUT,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VivaApp.getInstance().getRequestQueue().getCache().invalidate(Constant.URL_KANAL_LIFE, true);
            VivaApp.getInstance().getRequestQueue().getCache().get(Constant.URL_KANAL_LIFE);
            VivaApp.getInstance().addToRequestQueue(request, Constant.JSON_REQUEST);
        } else {
            Toast.makeText(VivaApp.getInstance(), R.string.title_no_connection, Toast.LENGTH_SHORT).show();
            if(VivaApp.getInstance().getRequestQueue().getCache().get(Constant.URL_KANAL_LIFE) != null) {
                cachedResponse = new String(VivaApp.getInstance().
                        getRequestQueue().getCache().get(Constant.URL_KANAL_LIFE).data);
                Log.i(Constant.TAG, "KANAL LIFE CACHED : " + cachedResponse);
                try {
                    JSONObject jsonObject = new JSONObject(cachedResponse);
                    JSONArray response = jsonObject.getJSONArray(Constant.response);
                    for(int i=0; i<response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        if(obj != null) {
                            JSONArray objKanal = obj.getJSONArray("news");
                            for(int j=0; j<objKanal.length(); j++) {
                                JSONObject field = objKanal.getJSONObject(j);
                                String channel_title = field.getString("channel_title");
                                String id = field.getString("id");
                                String channel_id = field.getString("channel_id");
                                String level = field.getString("level");
                                String title = field.getString("title");
                                String kanal = field.getString("kanal");
                                String image_url = field.getString("image_url");
                                featuredNewsArrayList.add(new FeaturedLife(channel_title, id,
                                        channel_id, level, title, kanal, image_url));
                                Log.i(Constant.TAG, "Channel Title : " + featuredNewsArrayList.get(i).getChannel_title());
                            }
                        }
                    }
                    if(featuredNewsArrayList.size() > 0 || !featuredNewsArrayList.isEmpty()) {
                        swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
                                new FeaturedLifeAdapter(getActivity(), featuredNewsArrayList));
                        swingBottomInAnimationAdapter.setAbsListView(gridNews);
                        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(0000);
                        gridNews.setAdapter(swingBottomInAnimationAdapter);
                        loading_layout.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            } else {
                loading_layout.setVisibility(View.GONE);
                tvNoResult.setVisibility(View.VISIBLE);
                Toast.makeText(VivaApp.getInstance(), R.string.title_no_connection, Toast.LENGTH_SHORT).show();
            }
        }

        return rootView;
    }

}
