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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import id.co.viva.news.app.activity.ActDetailChannelNews;
import id.co.viva.news.app.adapter.ChannelListTypeAdapter;
import id.co.viva.news.app.adapter.FeaturedNewsAdapter;
import id.co.viva.news.app.ads.AdsConfig;
import id.co.viva.news.app.component.CropSquareTransformation;
import id.co.viva.news.app.component.ExpandableHeightGridView;
import id.co.viva.news.app.component.ProgressWheel;
import id.co.viva.news.app.model.Ads;
import id.co.viva.news.app.model.FeaturedNews;
import id.co.viva.news.app.services.Analytics;

/**
 * Created by reza on 15/10/14.
 */
public class NewsFragment extends Fragment implements View.OnClickListener {

    private ArrayList<FeaturedNews> featuredNewsArrayList;
    private ArrayList<FeaturedNews> featuredNewsArrayListTypeList;
    private ArrayList<Ads> adsArrayList;
    private SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;
    private boolean isInternetPresent = false;
    private ExpandableHeightGridView gridNews;
    private PublisherAdView publisherAdViewBottom;
    private PublisherAdView publisherAdViewTop;
    private LinearLayout mParentLayout;
    private ListView listNews;
    private ChannelListTypeAdapter channelListTypeAdapter;
    private String cachedResponse;
    private ProgressWheel progressWheel;
    private TextView tvNoResult;
    private Analytics analytics;
    private RippleView rippleView;
    private ImageView imageHeader;
    private String channel_title_header_grid;
    private String image_url_header_grid;
    private String id_header_grid;
    private RelativeLayout layoutTransparentHeader;
    private TextView textHeader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isInternetPresent = Global.getInstance(getActivity())
                .getConnectionStatus().isConnectingToInternet();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setHasOptionsMenu(true);
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(getResources().getColor(R.color.color_news));
        ActionBarActivity mActivity = (ActionBarActivity) activity;
        if (mActivity != null) {
            mActivity.getSupportActionBar().setBackgroundDrawable(colorDrawable);
            mActivity.getSupportActionBar().setIcon(R.drawable.logo_viva_coid_second);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_news, container, false);

        //Parent Layout
        mParentLayout = (LinearLayout) rootView.findViewById(R.id.parent_layout);

        //Analytic
        analytics = new Analytics(getActivity());
        analytics.getAnalyticByATInternet(Constant.KANAL_NEWS_PAGE);
        analytics.getAnalyticByGoogleAnalytic(Constant.KANAL_NEWS_PAGE);

        //Label when no result
        tvNoResult = (TextView) rootView.findViewById(R.id.text_no_result);
        tvNoResult.setVisibility(View.GONE);

        //Text on the header
        textHeader = (TextView) rootView.findViewById(R.id.header_title_kanal_news);
        imageHeader = (ImageView) rootView.findViewById(R.id.header_grid_news);
        imageHeader.setOnClickListener(this);
        imageHeader.setFocusableInTouchMode(true);

        //Transparent main image
        layoutTransparentHeader = (RelativeLayout) rootView.findViewById(R.id.header_grid_news_transparent);
        layoutTransparentHeader.setVisibility(View.GONE);

        //For tablet version
        if (Constant.isTablet(getActivity())) {
            imageHeader.getLayoutParams().height = Constant.getDynamicImageSize(getActivity(), Constant.DYNAMIC_SIZE_GRID_TYPE);
            layoutTransparentHeader.getLayoutParams().height = Constant.getDynamicImageSize(getActivity(), Constant.DYNAMIC_SIZE_GRID_TYPE);
        }

        //Loading Progress
        progressWheel = (ProgressWheel) rootView.findViewById(R.id.progress_wheel);
        progressWheel.setVisibility(View.VISIBLE);

        //Button Retry
        rippleView = (RippleView) rootView.findViewById(R.id.layout_ripple_view);
        rippleView.setVisibility(View.GONE);
        rippleView.setOnClickListener(this);

        //List Mode
        listNews = (ListView) rootView.findViewById(R.id.list_news);
        listNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (featuredNewsArrayListTypeList.size() > 0) {
                    FeaturedNews featuredNews = featuredNewsArrayListTypeList.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putString("id", featuredNews.getChannel_id());
                    bundle.putString("channel_title", featuredNews.getChannel_title());
                    Intent intent = new Intent(getActivity(), ActDetailChannelNews.class);
                    intent.putExtras(bundle);
                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
                }
            }
        });

        //Grid Mode
        gridNews = (ExpandableHeightGridView) rootView.findViewById(R.id.grid_news);
        gridNews.setVisibility(View.GONE);
        gridNews.setExpanded(true);
        gridNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(featuredNewsArrayList.size() > 0) {
                    FeaturedNews featuredNews = featuredNewsArrayList.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putString("id", featuredNews.getChannel_id());
                    bundle.putString("channel_title", featuredNews.getChannel_title());
                    Intent intent = new Intent(getActivity(), ActDetailChannelNews.class);
                    intent.putExtras(bundle);
                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
                }
            }
        });

        //Data collection
        adsArrayList = new ArrayList<>();
        featuredNewsArrayList = new ArrayList<>();
        featuredNewsArrayListTypeList = new ArrayList<>();

        if (isInternetPresent) {
            StringRequest request = new StringRequest(Request.Method.GET, Constant.NEW_NEWS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            Log.i(Constant.TAG, "NEWS RESPONSE : " + s);
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
                                            featuredNewsArrayList.add(new FeaturedNews(channel_title, id,
                                                    channel_id, level, title, kanal, image_url));
                                            Log.i(Constant.TAG, "Channel Title : " + featuredNewsArrayList.get(i).getChannel_title());
                                        }
                                    }
                                }
                                //Getting for list type
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
                                            featuredNewsArrayListTypeList.add(new FeaturedNews(channel_title, id,
                                                    channel_id, level, title, kanal, image_url));
                                            Log.i(Constant.TAG, "Title List : " + featuredNewsArrayListTypeList.get(j).getChannel_title());
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
                                //Populate data into list type
                                featuredNewsArrayListTypeList.add(0, new FeaturedNews(channel_title_header_grid,
                                        null, id_header_grid, null, null, null, image_url_header_grid));
                                //Show grid
                                if(featuredNewsArrayList.size() > 0 || !featuredNewsArrayList.isEmpty()) {
                                    swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
                                            new FeaturedNewsAdapter(getActivity(), featuredNewsArrayList));
                                    swingBottomInAnimationAdapter.setAbsListView(gridNews);
                                    assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                                    swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(0000);
                                    gridNews.setAdapter(swingBottomInAnimationAdapter);
                                }
                                //Show list
                                if(featuredNewsArrayListTypeList.size() > 0 || !featuredNewsArrayListTypeList.isEmpty()) {
                                    if(channelListTypeAdapter == null) {
                                        channelListTypeAdapter = new ChannelListTypeAdapter(
                                                getActivity(), null, null, featuredNewsArrayListTypeList, Constant.ADAPTER_CHANNEL_NEWS);
                                    }
                                    //End of process
                                    listNews.setAdapter(channelListTypeAdapter);
                                    Constant.setListViewHeightBasedOnChildren(listNews);
                                    channelListTypeAdapter.notifyDataSetChanged();
                                    progressWheel.setVisibility(View.GONE);
                                }
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
            Global.getInstance(getActivity()).getRequestQueue().getCache().invalidate(Constant.NEW_NEWS, true);
            Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_NEWS);
            Global.getInstance(getActivity()).addToRequestQueue(request, Constant.JSON_REQUEST);
        } else {
            Toast.makeText(getActivity(), R.string.title_no_connection, Toast.LENGTH_SHORT).show();
            if(Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_NEWS) != null) {
                cachedResponse = new String(Global.getInstance(getActivity()).
                        getRequestQueue().getCache().get(Constant.NEW_NEWS).data);
                Log.i(Constant.TAG, "NEWS CACHED : " + cachedResponse);
                try {
                    JSONObject jsonObject = new JSONObject(cachedResponse);
                    JSONArray response = jsonObject.getJSONArray(Constant.response);

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
                        Picasso.with(getActivity()).load(image_url_header_grid).transform(new CropSquareTransformation()).into(imageHeader);
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
                                featuredNewsArrayList.add(new FeaturedNews(channel_title, id,
                                        channel_id, level, title, kanal, image_url));
                                Log.i(Constant.TAG, "Channel Title : " + featuredNewsArrayList.get(i).getChannel_title());
                            }
                        }
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
                                featuredNewsArrayListTypeList.add(new FeaturedNews(channel_title, id,
                                        channel_id, level, title, kanal, image_url));
                                Log.i(Constant.TAG, "Title List : " + featuredNewsArrayListTypeList.get(j).getChannel_title());
                            }
                        }
                    }

                    featuredNewsArrayListTypeList.add(0, new FeaturedNews(channel_title_header_grid,
                            null, id_header_grid, null, null, null, image_url_header_grid));

                    if(featuredNewsArrayList.size() > 0 || !featuredNewsArrayList.isEmpty()) {
                        swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
                                new FeaturedNewsAdapter(getActivity(), featuredNewsArrayList));
                        swingBottomInAnimationAdapter.setAbsListView(gridNews);
                        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(0000);
                        gridNews.setAdapter(swingBottomInAnimationAdapter);
                    }

                    if(featuredNewsArrayListTypeList.size() > 0 || !featuredNewsArrayListTypeList.isEmpty()) {
                        if(channelListTypeAdapter == null) {
                            channelListTypeAdapter = new ChannelListTypeAdapter(
                                    getActivity(), null, null, featuredNewsArrayListTypeList, Constant.ADAPTER_CHANNEL_NEWS);
                        }
                        listNews.setAdapter(channelListTypeAdapter);
                        Constant.setListViewHeightBasedOnChildren(listNews);
                        channelListTypeAdapter.notifyDataSetChanged();
                        progressWheel.setVisibility(View.GONE);
                    }
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
        if(view.getId() == R.id.layout_ripple_view) {
            if(isInternetPresent) {
                rippleView.setVisibility(View.GONE);
                progressWheel.setVisibility(View.VISIBLE);
                StringRequest request = new StringRequest(Request.Method.GET, Constant.NEW_NEWS,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                Log.i(Constant.TAG, "NEWS RESPONSE : " + s);
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
                                                featuredNewsArrayList.add(new FeaturedNews(channel_title, id,
                                                        channel_id, level, title, kanal, image_url));
                                                Log.i(Constant.TAG, "Channel Title : " + featuredNewsArrayList.get(i).getChannel_title());
                                            }
                                        }
                                    }
                                    //Getting for list type
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
                                                featuredNewsArrayListTypeList.add(new FeaturedNews(channel_title, id,
                                                        channel_id, level, title, kanal, image_url));
                                                Log.i(Constant.TAG, "Title List : " + featuredNewsArrayListTypeList.get(j).getChannel_title());
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
                                    //Add data to collection
                                    featuredNewsArrayListTypeList.add(0, new FeaturedNews(channel_title_header_grid,
                                            null, id_header_grid, null, null, null, image_url_header_grid));
                                    //Show grid type
                                    if(featuredNewsArrayList.size() > 0 || !featuredNewsArrayList.isEmpty()) {
                                        swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
                                                new FeaturedNewsAdapter(getActivity(), featuredNewsArrayList));
                                        swingBottomInAnimationAdapter.setAbsListView(gridNews);
                                        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                                        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(0000);
                                        gridNews.setAdapter(swingBottomInAnimationAdapter);
                                    }
                                    //Show list type
                                    if(featuredNewsArrayListTypeList.size() > 0 || !featuredNewsArrayListTypeList.isEmpty()) {
                                        if(channelListTypeAdapter == null) {
                                            channelListTypeAdapter = new ChannelListTypeAdapter(
                                                    getActivity(), null, null, featuredNewsArrayListTypeList, Constant.ADAPTER_CHANNEL_NEWS);
                                        }
                                        listNews.setAdapter(channelListTypeAdapter);
                                        Constant.setListViewHeightBasedOnChildren(listNews);
                                        channelListTypeAdapter.notifyDataSetChanged();
                                        progressWheel.setVisibility(View.GONE);
                                    }
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
                Global.getInstance(getActivity()).getRequestQueue().getCache().invalidate(Constant.NEW_NEWS, true);
                Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_NEWS);
                Global.getInstance(getActivity()).addToRequestQueue(request, Constant.JSON_REQUEST);
            } else {
                Toast.makeText(getActivity(), R.string.title_no_connection, Toast.LENGTH_SHORT).show();
            }
        } else if(view.getId() == R.id.header_grid_news) {
            if (id_header_grid != null && channel_title_header_grid != null) {
                Bundle bundle = new Bundle();
                bundle.putString("id", id_header_grid);
                bundle.putString("channel_title", channel_title_header_grid);
                Intent intent = new Intent(getActivity(), ActDetailChannelNews.class);
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_change_layout) {
            if (getActivity() != null) {
                getActivity().invalidateOptionsMenu();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (listNews.getVisibility() == View.VISIBLE) {
            listNews.setVisibility(View.GONE);
            layoutTransparentHeader.setVisibility(View.VISIBLE);
            layoutTransparentHeader.setBackgroundColor(getResources().getColor(R.color.transparent));
            gridNews.setVisibility(View.VISIBLE);
            imageHeader.setVisibility(View.VISIBLE);
            textHeader.setVisibility(View.VISIBLE);
            imageHeader.requestFocus();
            if (menu != null) {
                if (menu.hasVisibleItems()) {
                    if (menu.findItem(R.id.action_change_layout) != null) {
                        menu.removeItem(R.id.action_change_layout);
                    }
                }
            }
            MenuItem mi = menu.add(Menu.NONE, R.id.action_change_layout, 2, "");
            mi.setIcon(R.drawable.ic_preview_small);
            mi.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        } else {
            listNews.setVisibility(View.VISIBLE);
            layoutTransparentHeader.setVisibility(View.GONE);
            gridNews.setVisibility(View.GONE);
            imageHeader.setVisibility(View.GONE);
            textHeader.setVisibility(View.GONE);
            if (menu != null) {
                if (menu.hasVisibleItems()) {
                    if (menu.findItem(R.id.action_change_layout) != null) {
                        menu.removeItem(R.id.action_change_layout);
                    }
                }
            }
            MenuItem mi = menu.add(Menu.NONE, R.id.action_change_layout, 2, "");
            mi.setIcon(R.drawable.ic_preview_big);
            mi.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_frag_channel, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
