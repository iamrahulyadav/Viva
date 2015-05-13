package id.co.viva.news.app.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.melnykov.fab.FloatingActionButton;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.activity.ActDetailBeritaSekitar;
import id.co.viva.news.app.adapter.BeritaSekitarAdapter;
import id.co.viva.news.app.ads.AdsConfig;
import id.co.viva.news.app.component.GoogleMusicDicesDrawable;
import id.co.viva.news.app.component.LoadMoreListView;
import id.co.viva.news.app.interfaces.LocationResult;
import id.co.viva.news.app.interfaces.OnGPSListener;
import id.co.viva.news.app.interfaces.OnLoadMoreListener;
import id.co.viva.news.app.location.LocationFinder;
import id.co.viva.news.app.model.Ads;
import id.co.viva.news.app.model.BeritaSekitar;
import id.co.viva.news.app.services.Analytics;

/**
 * Created by reza on 23/02/15.
 */
public class BeritaSekitarFragment extends Fragment implements View.OnClickListener,
        OnLoadMoreListener, AdapterView.OnItemClickListener, LocationResult, OnGPSListener {

    public static ArrayList<BeritaSekitar> beritaSekitarArrayList;
    private ArrayList<Ads> adsArrayList;
    private SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;
    private boolean isInternetPresent = false;
    private boolean isResume;
    private String lastPaging;
    private int dataSize = 0;
    private String mCitySubLocality;
    private LoadMoreListView listView;
    private LinearLayout mParentLayout;
    private PublisherAdView publisherAdViewBottom;
    private PublisherAdView publisherAdViewTop;
    private Analytics analytics;
    private RippleView rippleView;
    private ProgressBar loading_layout;
    private TextView labelLoadData;
    private FloatingActionButton floatingActionButton;
    private TextView labelText;
    private TextView lastUpdate;
    private LocationFinder locationFinder;
    private JSONArray jsonArrayResponses, jsonArrayResponsesAds;
    private BeritaSekitarAdapter adapter;
    private ActionBarActivity mActivity;
    private int page = 1;
    //Type Number
    final private static int LOCATION_LOCALITY = 0;
    final private static int LOCATION_SUB_LOCALITY = 1;
    final private static int LOCATION_ADMIN_AREA = 2;

    private void setAnalytics(String page) {
        if (analytics == null) {
            analytics = new Analytics(mActivity);
        }
        analytics.getAnalyticByATInternet(
                Constant.BERITA_SEKITAR_PAGE + String.valueOf(page));
        analytics.getAnalyticByGoogleAnalytic(
                Constant.BERITA_SEKITAR_PAGE + String.valueOf(page));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (ActionBarActivity) activity;
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(getResources().getColor(R.color.new_base_color));
        mActivity.getSupportActionBar().setBackgroundDrawable(colorDrawable);
        mActivity.getSupportActionBar().setIcon(R.drawable.logo_viva_coid_second);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isInternetPresent = Global.getInstance(getActivity())
                .getConnectionStatus().isConnectingToInternet();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_berita_sekitar, container, false);
        //Check whether from outside app or not
        isResume = false;
        setViews(rootView);
        return rootView;
    }

    private Drawable getProgressDrawable() {
        Drawable progressDrawable;
        progressDrawable = new GoogleMusicDicesDrawable.Builder().build();
        return progressDrawable;
    }

    private void setViews(View rootView) {
        mParentLayout = (LinearLayout) rootView.findViewById(R.id.parent_layout);

        //Loading label and progress
        labelLoadData = (TextView) rootView.findViewById(R.id.text_loading_data);
        loading_layout = (ProgressBar) rootView.findViewById(R.id.loading_progress_layout_berita_sekitar);
        Rect bounds = loading_layout.getIndeterminateDrawable().getBounds();
        loading_layout.setIndeterminateDrawable(getProgressDrawable());
        loading_layout.getIndeterminateDrawable().setBounds(bounds);

        //Button Retry
        rippleView = (RippleView) rootView.findViewById(R.id.layout_ripple_view_berita_sekitar);
        rippleView.setVisibility(View.GONE);
        rippleView.setOnClickListener(this);

        //Last update label
        lastUpdate = (TextView) rootView.findViewById(R.id.date_berita_sekitar);

        //Label Page
        labelText = (TextView) rootView.findViewById(R.id.text_berita_sekitar);
        labelText.setText(getString(R.string.label_berita_sekitar));

        //List Content
        listView = (LoadMoreListView) rootView.findViewById(R.id.list_berita_sekitar);
        listView.setOnItemClickListener(this);
        listView.setOnLoadMoreListener(this);

        //'Go to top' button
        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        floatingActionButton.setVisibility(View.GONE);
        floatingActionButton.setOnClickListener(this);
        floatingActionButton.attachToListView(listView, new FloatingActionButton.FabOnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                int firstIndex = listView.getFirstVisiblePosition();
                if (firstIndex > Constant.NUMBER_OF_TOP_LIST_ITEMS) {
                    floatingActionButton.setVisibility(View.VISIBLE);
                } else {
                    floatingActionButton.setVisibility(View.GONE);
                }
            }
        });

        //Assign array
        adsArrayList = new ArrayList<>();
        beritaSekitarArrayList = new ArrayList<>();
        if (getActivity() != null) {
            adapter = new BeritaSekitarAdapter(getActivity(), beritaSekitarArrayList);
        } else {
            adapter = new BeritaSekitarAdapter(mActivity, beritaSekitarArrayList);
        }

        //Checking process when find some locations
        if (isInternetPresent) {
            loading_layout.setVisibility(View.VISIBLE);
            labelLoadData.setVisibility(View.VISIBLE);
            labelLoadData.setText(getResources().getString(R.string.label_get_location));
            setAnalytics(String.valueOf(page));
            getLocationFinder();
        } else {
            loading_layout.setVisibility(View.INVISIBLE);
            labelLoadData.setVisibility(View.VISIBLE);
            labelLoadData.setText(getResources().getString(R.string.title_no_connection_and_no_gps));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isResume = true;
        if (publisherAdViewBottom != null) {
            publisherAdViewBottom.pause();
        }
        if (publisherAdViewTop != null) {
            publisherAdViewTop.pause();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isResume", isResume);
    }

    @Override
    public void onResume() {
        if (isResume) {
            if (isInternetPresent) {
                getLocationFinder();
            }
        }
        if (publisherAdViewBottom != null) {
            publisherAdViewBottom.resume();
        }
        if (publisherAdViewTop != null) {
            publisherAdViewTop.resume();
        }
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.layout_ripple_view_berita_sekitar) {
            if (isInternetPresent) {
                rippleView.setVisibility(View.GONE);
                loading_layout.setVisibility(View.VISIBLE);
                labelLoadData.setVisibility(View.VISIBLE);
                labelLoadData.setText(getResources().getString(R.string.label_get_location));
                StringRequest request = new StringRequest(Request.Method.GET,
                        Constant.BERITA_SEKITAR_URL + "p/" + getParamLocation(mCitySubLocality, LOCATION_LOCALITY).replace(" ", "%20")
                                + "/q/" + getParamLocation(mCitySubLocality, LOCATION_SUB_LOCALITY).replace(" ", "%20")
                                + "/r/" + getParamLocation(mCitySubLocality, LOCATION_ADMIN_AREA).replace(" ", "%20") + "/s/0",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    JSONObject response = jsonObject.getJSONObject(Constant.response);
                                    //Get content list
                                    jsonArrayResponses = response.getJSONArray(Constant.search);
                                    if (jsonArrayResponses.length() > 0) {
                                        for (int i=0; i<jsonArrayResponses.length(); i++) {
                                            JSONObject jsonHeadline = jsonArrayResponses.getJSONObject(i);
                                            String id = jsonHeadline.getString(Constant.id);
                                            String kanal = jsonHeadline.getString(Constant.kanal);
                                            String image_url = jsonHeadline.getString(Constant.image_url);
                                            String title = jsonHeadline.getString(Constant.title);
                                            String url = jsonHeadline.getString(Constant.url);
                                            String date_publish = jsonHeadline.getString(Constant.date_publish);
                                            beritaSekitarArrayList.add(new BeritaSekitar(id, kanal, image_url,
                                                    title, url, date_publish));
                                        }
                                    }
                                    //Get ads list
                                    jsonArrayResponsesAds = response.getJSONArray(Constant.adses);
                                    if (jsonArrayResponsesAds.length() > 0) {
                                        for (int j=0; j<jsonArrayResponsesAds.length(); j++) {
                                            JSONObject jsonAds = jsonArrayResponsesAds.getJSONObject(j);
                                            String name = jsonAds.getString(Constant.name);
                                            int position = jsonAds.getInt(Constant.position);
                                            int type = jsonAds.getInt(Constant.type);
                                            String unit_id = jsonAds.getString(Constant.unit_id);
                                            adsArrayList.add(new Ads(name, type, position, unit_id));
                                        }
                                    }
                                    //Show content list
                                    if (beritaSekitarArrayList.size() > 0 || !beritaSekitarArrayList.isEmpty()) {
                                        swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(adapter);
                                        swingBottomInAnimationAdapter.setAbsListView(listView);
                                        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                                        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(1000);
                                        listView.setAdapter(swingBottomInAnimationAdapter);
                                        adapter.notifyDataSetChanged();
                                        loading_layout.setVisibility(View.GONE);
                                        labelLoadData.setVisibility(View.GONE);
                                        lastUpdate.setText(getParamLocation(mCitySubLocality, LOCATION_LOCALITY));
                                    }
                                    //Show Ads
                                    showAds();
                                } catch (Exception e) {
                                    e.getMessage();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (isAdded()) {
                            Toast.makeText(mActivity, getResources()
                                    .getString(R.string.title_failed_get_location), Toast.LENGTH_SHORT).show();
                        }
                        Log.i(Constant.TAG, "URL : " + Constant.BERITA_SEKITAR_URL + "p/" + getParamLocation(mCitySubLocality, LOCATION_LOCALITY)
                                + "/q/" + getParamLocation(mCitySubLocality, LOCATION_SUB_LOCALITY)
                                + "/r/" + getParamLocation(mCitySubLocality, LOCATION_ADMIN_AREA) + "/s/0");
                        loading_layout.setVisibility(View.GONE);
                        labelLoadData.setVisibility(View.GONE);
                        rippleView.setVisibility(View.VISIBLE);
                    }
                });
                request.setShouldCache(false);
                request.setRetryPolicy(new DefaultRetryPolicy(
                        Constant.TIME_OUT,
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                if (getActivity() != null) {
                    Global.getInstance(getActivity())
                            .addToRequestQueue(request, Constant.JSON_REQUEST);
                }
            }
        } else if(view.getId() == R.id.fab) {
            listView.setSelection(0);
        }
    }

    @Override
    public void onLoadMore() {
        page += 1;
        lastPaging = String.valueOf(dataSize += 10);
        if (isInternetPresent) {
            setAnalytics(String.valueOf(page));
            StringRequest request = new StringRequest(Request.Method.GET,
                    Constant.BERITA_SEKITAR_URL + "p/" + getParamLocation(mCitySubLocality, LOCATION_LOCALITY).replace(" ", "%20")
                            + "/q/" + getParamLocation(mCitySubLocality, LOCATION_SUB_LOCALITY).replace(" ", "%20")
                            + "/r/" + getParamLocation(mCitySubLocality, LOCATION_ADMIN_AREA).replace(" ", "%20") + "/s/" + lastPaging,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                JSONObject response = jsonObject.getJSONObject(Constant.response);
                                jsonArrayResponses = response.getJSONArray(Constant.search);
                                if(jsonArrayResponses != null) {
                                    for(int i=0; i<jsonArrayResponses.length(); i++) {
                                        JSONObject jsonHeadline = jsonArrayResponses.getJSONObject(i);
                                        String id = jsonHeadline.getString(Constant.id);
                                        String kanal = jsonHeadline.getString(Constant.kanal);
                                        String image_url = jsonHeadline.getString(Constant.image_url);
                                        String title = jsonHeadline.getString(Constant.title);
                                        String url = jsonHeadline.getString(Constant.url);
                                        String date_publish = jsonHeadline.getString(Constant.date_publish);
                                        beritaSekitarArrayList.add(new BeritaSekitar(id, kanal, image_url,
                                                title, url, date_publish));
                                    }
                                }
                                if (beritaSekitarArrayList.size() > 0 || !beritaSekitarArrayList.isEmpty()) {
                                    swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(adapter);
                                    swingBottomInAnimationAdapter.setAbsListView(listView);
                                    assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                                    swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(1000);
                                    adapter.notifyDataSetChanged();
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
                    if (isAdded()) {
                        Toast.makeText(mActivity,
                                R.string.label_error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            request.setShouldCache(false);
            request.setRetryPolicy(new DefaultRetryPolicy(
                    Constant.TIME_OUT,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            if (getActivity() != null) {
                Global.getInstance(getActivity())
                        .addToRequestQueue(request, Constant.JSON_REQUEST);
            } else {
                Global.getInstance(mActivity)
                        .addToRequestQueue(request, Constant.JSON_REQUEST);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (beritaSekitarArrayList.size() > 0) {
            BeritaSekitar beritaSekitar = beritaSekitarArrayList.get(position);
            Bundle bundle = new Bundle();
            bundle.putString("id", beritaSekitar.getId());
            bundle.putString(Constant.berita_sekitar_detail_screen, Constant.berita_sekitar_detail_screen);
            Intent intent = new Intent(getActivity(), ActDetailBeritaSekitar.class);
            intent.putExtras(bundle);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
        }
    }

    private void getLocationFinder() {
        if (getActivity() != null) {
            if (locationFinder == null) {
                locationFinder = new LocationFinder();
            }
            locationFinder.getLocation(getActivity(), this, this);
        }
    }

    private void refreshContent() {
        if (getActivity() != null) {
            getActivity().finish();
            startActivity(getActivity().getIntent());
        }
    }

    private String getPlaces(Location location) {
        String city = null;
        String subLocality = null;
        String adminArea = null;
        Geocoder gcd;
        List<Address> addresses;
        //Check Instance
        if (getActivity() != null) {
            gcd = new Geocoder(getActivity(), Locale.getDefault());
        } else {
            gcd = new Geocoder(mActivity, Locale.getDefault());
        }
        try {
            addresses = gcd.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);
            if (addresses != null) {
                if (addresses.size() > 0) {
                    city = addresses.get(0).getLocality();
                    subLocality = addresses.get(0).getSubLocality();
                    adminArea = addresses.get(0).getSubAdminArea();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return city + "-" + subLocality + "-" + adminArea;
    }

    private String getParamLocation(String result, int type) {
        if (result != null) {
            if (result.length() > 0) {
                String[] results = result.split("-");
                switch (type) {
                    case LOCATION_LOCALITY:
                        String city = results[0];
                        return city;
                    case LOCATION_SUB_LOCALITY:
                        String subLocality = results[1];
                        return subLocality;
                    case LOCATION_ADMIN_AREA:
                        String adminArea = results[2];
                        return adminArea;
                    default:
                        break;
                }
            }
        }
        return null;
    }

    @Override
    public void getLocation(Location location) {
        if (isInternetPresent) {
            if (location != null) {
                mCitySubLocality = getPlaces(location);
                if (mCitySubLocality != null) {
                    if (mCitySubLocality.length() > 0) {
                        StringRequest request = new StringRequest(Request.Method.GET,
                                Constant.BERITA_SEKITAR_URL + "p/" + getParamLocation(mCitySubLocality, LOCATION_LOCALITY).replace(" ", "%20")
                                + "/q/" + getParamLocation(mCitySubLocality, LOCATION_SUB_LOCALITY).replace(" ", "%20")
                                + "/r/" + getParamLocation(mCitySubLocality, LOCATION_ADMIN_AREA).replace(" ", "%20") + "/s/0",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String s) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(s);
                                            JSONObject response = jsonObject.getJSONObject(Constant.response);
                                            //Get content list
                                            jsonArrayResponses = response.getJSONArray(Constant.search);
                                            if(jsonArrayResponses != null) {
                                                for(int i=0; i<jsonArrayResponses.length(); i++) {
                                                    JSONObject jsonHeadline = jsonArrayResponses.getJSONObject(i);
                                                    String id = jsonHeadline.getString(Constant.id);
                                                    String kanal = jsonHeadline.getString(Constant.kanal);
                                                    String image_url = jsonHeadline.getString(Constant.image_url);
                                                    String title = jsonHeadline.getString(Constant.title);
                                                    String url = jsonHeadline.getString(Constant.url);
                                                    String date_publish = jsonHeadline.getString(Constant.date_publish);
                                                    beritaSekitarArrayList.add(new BeritaSekitar(id, kanal, image_url,
                                                            title, url, date_publish));
                                                }
                                            }
                                            //Get ads list
                                            jsonArrayResponsesAds = response.getJSONArray(Constant.adses);
                                            if (jsonArrayResponsesAds.length() > 0) {
                                                for (int j=0; j<jsonArrayResponsesAds.length(); j++) {
                                                    JSONObject jsonAds = jsonArrayResponsesAds.getJSONObject(j);
                                                    String name = jsonAds.getString(Constant.name);
                                                    int position = jsonAds.getInt(Constant.position);
                                                    int type = jsonAds.getInt(Constant.type);
                                                    String unit_id = jsonAds.getString(Constant.unit_id);
                                                    adsArrayList.add(new Ads(name, type, position, unit_id));
                                                }
                                            }
                                            //Show content list
                                            if (beritaSekitarArrayList.size() > 0 || !beritaSekitarArrayList.isEmpty()) {
                                                swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(adapter);
                                                swingBottomInAnimationAdapter.setAbsListView(listView);
                                                assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                                                swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(1000);
                                                listView.setAdapter(swingBottomInAnimationAdapter);
                                                adapter.notifyDataSetChanged();
                                                loading_layout.setVisibility(View.GONE);
                                                labelLoadData.setVisibility(View.GONE);
                                                lastUpdate.setText(getParamLocation(mCitySubLocality, LOCATION_LOCALITY));
                                            }
                                            //Show Ads
                                            showAds();
                                        } catch (Exception e) {
                                            e.getMessage();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                if (isAdded()) {
                                    Toast.makeText(mActivity, getResources()
                                            .getString(R.string.title_failed_get_location), Toast.LENGTH_SHORT).show();
                                }
                                loading_layout.setVisibility(View.GONE);
                                labelLoadData.setVisibility(View.GONE);
                                rippleView.setVisibility(View.VISIBLE);
                            }
                        });
                        request.setShouldCache(false);
                        request.setRetryPolicy(new DefaultRetryPolicy(
                                Constant.TIME_OUT,
                                0,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        if (getActivity() != null) {
                            Global.getInstance(getActivity())
                                    .addToRequestQueue(request, Constant.JSON_REQUEST);
                        }
                    }
                } else {
                    if (isAdded()) {
                        Toast.makeText(mActivity, getResources()
                                .getString(R.string.title_failed_get_location), Toast.LENGTH_SHORT).show();
                    }
                    refreshContent();
                }
            } else {
                if (isAdded()) {
                    Toast.makeText(mActivity, getResources()
                            .getString(R.string.title_failed_get_location), Toast.LENGTH_SHORT).show();
                }
                refreshContent();
            }
        } else {
            loading_layout.setVisibility(View.GONE);
            labelLoadData.setVisibility(View.GONE);
            rippleView.setVisibility(View.VISIBLE);
        }
    }

    private void alertEnableGPS() {
        if(getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getResources().getString(R.string.label_title_gps))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.label_option_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.label_option_no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            refreshContent();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
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
    public void onAlertGPS() {
        if (isInternetPresent) {
            alertEnableGPS();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationFinder != null) {
            locationFinder.removeLocationListener();
        }
        if (publisherAdViewBottom != null) {
            publisherAdViewBottom.destroy();
        }
        if (publisherAdViewTop != null) {
            publisherAdViewTop.destroy();
        }
    }

}
