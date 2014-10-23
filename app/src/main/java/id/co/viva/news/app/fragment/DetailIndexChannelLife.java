package id.co.viva.news.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import id.co.viva.news.app.adapter.ChannelLifeAdapter;
import id.co.viva.news.app.model.ChannelLife;

/**
 * Created by reza on 23/10/14.
 */
public class DetailIndexChannelLife extends Fragment {

    private boolean isInternetPresent = false;
    private RelativeLayout loading_layout;
    private TextView tvNoResult;
    private ListView listView;
    private String cachedResponse;
    private ChannelLifeAdapter adapter;
    private AnimationAdapter mAnimAdapter;
    private String id;
    private ArrayList<ChannelLife> channelLifeArrayList;
    private JSONArray jsonArrayResponses, jsonArraySegmentHeadline;

    public static DetailIndexChannelLife newInstance(String id) {
        DetailIndexChannelLife indexChannelLife = new DetailIndexChannelLife();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        indexChannelLife.setArguments(bundle);
        return indexChannelLife;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isInternetPresent = VivaApp.getInstance().getConnectionStatus().isConnectingToInternet();
        id = getArguments().getString("id");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_detail_channel_life, container, false);

        loading_layout = (RelativeLayout) view.findViewById(R.id.loading_progress_layout);
        tvNoResult = (TextView) view.findViewById(R.id.text_no_result_detail_channel_life);
        tvNoResult.setVisibility(View.GONE);
        listView = (ListView) view.findViewById(R.id.list_channel_life);
        channelLifeArrayList = new ArrayList<ChannelLife>();

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
                                    adapter = new ChannelLifeAdapter(getActivity(), channelLifeArrayList);
                                    mAnimAdapter = new ScaleInAnimationAdapter(adapter);
                                    mAnimAdapter.setAbsListView(listView);
                                    listView.setAdapter(mAnimAdapter);
                                    adapter.notifyDataSetChanged();
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
            stringRequest.setShouldCache(true);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    Constant.TIME_OUT,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
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
                        adapter = new ChannelLifeAdapter(getActivity(), channelLifeArrayList);
                        mAnimAdapter = new ScaleInAnimationAdapter(adapter);
                        mAnimAdapter.setAbsListView(listView);
                        listView.setAdapter(mAnimAdapter);
                        adapter.notifyDataSetChanged();
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

        return view;
    }

}