package id.co.viva.news.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.activity.ActDetailChannelLife;
import id.co.viva.news.app.activity.ActDetailContentDefault;
import id.co.viva.news.app.adapter.FeaturedLifeAdapter;
import id.co.viva.news.app.ads.AdsConfig;
import id.co.viva.news.app.component.CropSquareTransformation;
import id.co.viva.news.app.component.ExpandableHeightGridView;
import id.co.viva.news.app.component.ProgressWheel;
import id.co.viva.news.app.model.Ads;
import id.co.viva.news.app.model.FeaturedLife;
import id.co.viva.news.app.services.Analytics;

/**
 * Created by reza on 22/10/14.
 */
public class LifeFragment extends Fragment implements View.OnClickListener {

    private ArrayList<FeaturedLife> featuredNewsArrayList;
    private ArrayList<Ads> adsArrayList;
    private SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;
    private boolean isInternetPresent = false;
    private PublisherAdView publisherAdViewBottom;
    private PublisherAdView publisherAdViewTop;
    private LinearLayout mParentLayout;
    private ExpandableHeightGridView gridLife;
    private String cachedResponse;
    private ProgressWheel progressWheel;
    private TextView tvNoResult;
    private TextView textHeader;
    private ImageView imageHeader;
    private Analytics analytics;
    private RippleView rippleView;
    private String channel_title_header_grid;
    private String id_header_grid;
    private String image_url_header_grid;
    private RelativeLayout layoutTransparentHeader;

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
        colorDrawable.setColor(getResources().getColor(R.color.color_life));
        ActionBarActivity mActivity = (ActionBarActivity) activity;
        if (mActivity != null) {
            mActivity.getSupportActionBar().setBackgroundDrawable(colorDrawable);
            mActivity.getSupportActionBar().setIcon(R.drawable.logo_viva_coid_second);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_life, container, false);

        mParentLayout = (LinearLayout) rootView.findViewById(R.id.parent_layout);

        analytics = new Analytics(getActivity());
        analytics.getAnalyticByATInternet(Constant.KANAL_LIFE_PAGE);
        analytics.getAnalyticByGoogleAnalytic(Constant.KANAL_LIFE_PAGE);

        tvNoResult = (TextView) rootView.findViewById(R.id.text_no_result);
        tvNoResult.setVisibility(View.GONE);

        textHeader = (TextView) rootView.findViewById(R.id.header_title_kanal_life);
        imageHeader = (ImageView) rootView.findViewById(R.id.header_grid_life);
        imageHeader.setOnClickListener(this);
        imageHeader.setFocusableInTouchMode(true);

        layoutTransparentHeader = (RelativeLayout) rootView.findViewById(R.id.header_grid_life_transparent);
        layoutTransparentHeader.setVisibility(View.GONE);

        if (Constant.isTablet(getActivity())) {
            imageHeader.getLayoutParams().height = Constant.getDynamicImageSize(getActivity(), Constant.DYNAMIC_SIZE_GRID_TYPE);
            layoutTransparentHeader.getLayoutParams().height = Constant.getDynamicImageSize(getActivity(), Constant.DYNAMIC_SIZE_GRID_TYPE);
        }

        progressWheel = (ProgressWheel) rootView.findViewById(R.id.progress_wheel);
        progressWheel.setVisibility(View.VISIBLE);

        rippleView = (RippleView) rootView.findViewById(R.id.layout_ripple_view);
        rippleView.setVisibility(View.GONE);
        rippleView.setOnClickListener(this);

        gridLife = (ExpandableHeightGridView) rootView.findViewById(R.id.grid_life);
        gridLife.setExpanded(true);
        gridLife.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (featuredNewsArrayList.size() > 0) {
                    FeaturedLife featuredLife = featuredNewsArrayList.get(position);
                    Bundle bundle = new Bundle();
                    int lastIndex = featuredNewsArrayList.size() - 1;
                    if (position == lastIndex) {
                        Intent intent = new Intent(getActivity(), ActDetailContentDefault.class);
                        bundle.putString("id", featuredLife.getId());
                        bundle.putString("kanal", featuredLife.getKanal());
                        bundle.putString("shared_url", featuredLife.getUrl());
                        bundle.putString("type", "editor_choice");
                        intent.putExtras(bundle);
                        getActivity().startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
                    } else {
                        Intent intent = new Intent(getActivity(), ActDetailChannelLife.class);
                        bundle.putString("id", featuredLife.getChannel_id());
                        bundle.putString("channel_title", featuredLife.getChannel_title());
                        intent.putExtras(bundle);
                        getActivity().startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
                    }
                }
            }
        });

        featuredNewsArrayList = new ArrayList<>();
        adsArrayList = new ArrayList<>();

        if (isInternetPresent) {
            StringRequest request = new StringRequest(Request.Method.GET, Constant.NEW_LIFE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            Log.i(Constant.TAG, "LIFE RESPONSE : " + s);
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                JSONArray response = jsonObject.getJSONArray(Constant.response);
                                //Get News Index
                                int lastIndex = response.length() - 1;
                                JSONObject objs = response.getJSONObject(lastIndex);
                                if(objs != null) {
                                    JSONArray objKanal = objs.getJSONArray("all");
                                    for(int j=0; j<objKanal.length(); j++) {
                                        JSONObject field = objKanal.getJSONObject(j);
                                        id_header_grid = field.getString("channel_id");
                                        channel_title_header_grid = field.getString("channel_title");
                                        image_url_header_grid = field.getString("image_url");
                                    }
                                    textHeader.setText(channel_title_header_grid.toUpperCase());
                                    layoutTransparentHeader.setVisibility(View.VISIBLE);
                                    Picasso.with(getActivity()).load(image_url_header_grid)
                                            .transform(new CropSquareTransformation()).into(imageHeader);
                                }
                                //Get each channel
                                for(int i=0; i<response.length()-1; i++) {
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
                                            String url = field.getString("url");
                                            featuredNewsArrayList.add(new FeaturedLife(channel_title, id,
                                                    channel_id, level, title, kanal, image_url, url));
                                            Log.i(Constant.TAG, "Channel Title : " + featuredNewsArrayList.get(i).getChannel_title());
                                        }
                                    }
                                }
                                //Check Ads if exists
                                JSONArray adsList = jsonObject.getJSONArray(Constant.adses);
                                if (adsList.length() > 0) {
                                    for (int j=0; j<adsList.length(); j++) {
                                        JSONObject jsonAds = adsList.getJSONObject(j);
                                        String name = jsonAds.getString(Constant.name);
                                        int position = jsonAds.getInt(Constant.position);
                                        int type = jsonAds.getInt(Constant.type);
                                        String unit_id = jsonAds.getString(Constant.unit_id);
                                        adsArrayList.add(new Ads(name, type, position, unit_id));
                                        Log.i(Constant.TAG, "ADS : " + adsArrayList.get(j).getmUnitId());
                                    }
                                }

                                //Populate content grid type
                                if (featuredNewsArrayList.size() > 0 || !featuredNewsArrayList.isEmpty()) {
                                    swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
                                            new FeaturedLifeAdapter(getActivity(), featuredNewsArrayList));
                                    swingBottomInAnimationAdapter.setAbsListView(gridLife);
                                    assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                                    swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(0000);
                                    gridLife.setAdapter(swingBottomInAnimationAdapter);
                                }
                                //End of process
                                progressWheel.setVisibility(View.GONE);
                                //Show ads
                                showAds();
                            } catch (Exception e) {
                                e.getMessage();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            progressWheel.setVisibility(View.GONE);
                            rippleView.setVisibility(View.VISIBLE);
                        }
                    });
            request.setShouldCache(true);
            request.setRetryPolicy(new DefaultRetryPolicy(
                    Constant.TIME_OUT,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Global.getInstance(getActivity()).getRequestQueue().getCache().invalidate(Constant.NEW_LIFE, true);
            Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_LIFE);
            Global.getInstance(getActivity()).addToRequestQueue(request, Constant.JSON_REQUEST);
        } else {
            Toast.makeText(getActivity(), R.string.title_no_connection, Toast.LENGTH_SHORT).show();
            if (Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_LIFE) != null) {
                cachedResponse = new String(Global.getInstance(getActivity()).
                        getRequestQueue().getCache().get(Constant.NEW_LIFE).data);
                Log.i(Constant.TAG, "KANAL LIFE CACHED : " + cachedResponse);
                try {
                    JSONObject jsonObject = new JSONObject(cachedResponse);
                    JSONArray response = jsonObject.getJSONArray(Constant.response);

                    int lastIndex = response.length() - 1;
                    JSONObject objs = response.getJSONObject(lastIndex);
                    if (objs != null) {
                        JSONArray objKanal = objs.getJSONArray("all");
                        for (int j=0; j<objKanal.length(); j++) {
                            JSONObject field = objKanal.getJSONObject(j);
                            id_header_grid = field.getString("channel_id");
                            channel_title_header_grid = field.getString("channel_title");
                            image_url_header_grid = field.getString("image_url");
                        }
                        textHeader.setText(channel_title_header_grid.toUpperCase());
                        layoutTransparentHeader.setVisibility(View.VISIBLE);
                        Picasso.with(getActivity()).load(image_url_header_grid)
                                .transform(new CropSquareTransformation()).into(imageHeader);
                    }

                    for(int i=0; i<response.length()-1; i++) {
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
                                String url = field.getString("url");
                                featuredNewsArrayList.add(new FeaturedLife(channel_title, id,
                                        channel_id, level, title, kanal, image_url, url));
                                Log.i(Constant.TAG, "Channel Title : " + featuredNewsArrayList.get(i).getChannel_title());
                            }
                        }
                    }

                    if (featuredNewsArrayList.size() > 0 || !featuredNewsArrayList.isEmpty()) {
                        swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
                                new FeaturedLifeAdapter(getActivity(), featuredNewsArrayList));
                        swingBottomInAnimationAdapter.setAbsListView(gridLife);
                        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(0000);
                        gridLife.setAdapter(swingBottomInAnimationAdapter);
                    }

                    progressWheel.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.getMessage();
                }
            } else {
                progressWheel.setVisibility(View.GONE);
                tvNoResult.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), R.string.title_no_connection, Toast.LENGTH_SHORT).show();
            }
        }

        return rootView;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.layout_ripple_view) {
            if (isInternetPresent) {
                rippleView.setVisibility(View.GONE);
                progressWheel.setVisibility(View.VISIBLE);
                StringRequest request = new StringRequest(Request.Method.GET, Constant.NEW_LIFE,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                Log.i(Constant.TAG, "KANAL LIFE RESPONSE : " + s);
                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    JSONArray response = jsonObject.getJSONArray(Constant.response);
                                    //Get News Index
                                    int lastIndex = response.length() - 1;
                                    JSONObject objs = response.getJSONObject(lastIndex);
                                    if (objs != null) {
                                        JSONArray objKanal = objs.getJSONArray("all");
                                        for (int j=0; j<objKanal.length(); j++) {
                                            JSONObject field = objKanal.getJSONObject(j);
                                            id_header_grid = field.getString("channel_id");
                                            channel_title_header_grid = field.getString("channel_title");
                                            image_url_header_grid = field.getString("image_url");
                                        }
                                        textHeader.setText(channel_title_header_grid.toUpperCase());
                                        layoutTransparentHeader.setVisibility(View.VISIBLE);
                                        Picasso.with(getActivity()).load(image_url_header_grid)
                                                .transform(new CropSquareTransformation()).into(imageHeader);
                                    }
                                    //Get each channel
                                    for (int i=0; i<response.length()-1; i++) {
                                        JSONObject obj = response.getJSONObject(i);
                                        if (obj != null) {
                                            JSONArray objKanal = obj.getJSONArray("news");
                                            for (int j=0; j<objKanal.length(); j++) {
                                                JSONObject field = objKanal.getJSONObject(j);
                                                String channel_title = field.getString("channel_title");
                                                String id = field.getString("id");
                                                String channel_id = field.getString("channel_id");
                                                String level = field.getString("level");
                                                String title = field.getString("title");
                                                String kanal = field.getString("kanal");
                                                String image_url = field.getString("image_url");
                                                String url = field.getString("url");
                                                featuredNewsArrayList.add(new FeaturedLife(channel_title, id,
                                                        channel_id, level, title, kanal, image_url, url));
                                                Log.i(Constant.TAG, "Channel Title : " + featuredNewsArrayList.get(i).getChannel_title());
                                            }
                                        }
                                    }
                                    //Check Ads if exists
                                    JSONArray adsList = jsonObject.getJSONArray(Constant.adses);
                                    if (adsList.length() > 0) {
                                        for (int j=0; j<adsList.length(); j++) {
                                            JSONObject jsonAds = adsList.getJSONObject(j);
                                            String name = jsonAds.getString(Constant.name);
                                            int position = jsonAds.getInt(Constant.position);
                                            int type = jsonAds.getInt(Constant.type);
                                            String unit_id = jsonAds.getString(Constant.unit_id);
                                            adsArrayList.add(new Ads(name, type, position, unit_id));
                                            Log.i(Constant.TAG, "ADS : " + adsArrayList.get(j).getmUnitId());
                                        }
                                    }
                                    //Populate content grid type
                                    if (featuredNewsArrayList.size() > 0 || !featuredNewsArrayList.isEmpty()) {
                                        swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
                                                new FeaturedLifeAdapter(getActivity(), featuredNewsArrayList));
                                        swingBottomInAnimationAdapter.setAbsListView(gridLife);
                                        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                                        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(0000);
                                        gridLife.setAdapter(swingBottomInAnimationAdapter);
                                    }
                                    //End of process
                                    progressWheel.setVisibility(View.GONE);
                                    //Show ads
                                    showAds();
                                } catch (Exception e) {
                                    e.getMessage();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                progressWheel.setVisibility(View.GONE);
                                rippleView.setVisibility(View.VISIBLE);
                            }
                        });
                request.setShouldCache(true);
                request.setRetryPolicy(new DefaultRetryPolicy(
                        Constant.TIME_OUT,
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Global.getInstance(getActivity()).getRequestQueue().getCache().invalidate(Constant.NEW_LIFE, true);
                Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_LIFE);
                Global.getInstance(getActivity()).addToRequestQueue(request, Constant.JSON_REQUEST);
            } else {
                Toast.makeText(getActivity(), R.string.title_no_connection, Toast.LENGTH_SHORT).show();
            }
        } else if(view.getId() == R.id.header_grid_life) {
            if (id_header_grid != null & channel_title_header_grid != null) {
                Bundle bundle = new Bundle();
                bundle.putString("id", id_header_grid);
                bundle.putString("channel_title", channel_title_header_grid);
                Intent intent = new Intent(getActivity(), ActDetailChannelLife.class);
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
            }
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
