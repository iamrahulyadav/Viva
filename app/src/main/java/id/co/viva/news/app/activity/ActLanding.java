package id.co.viva.news.app.activity;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.adapter.NavigationAdapter;
import id.co.viva.news.app.component.CropSquareTransformation;
import id.co.viva.news.app.component.ProgressWheel;
import id.co.viva.news.app.fragment.AboutFragment;
import id.co.viva.news.app.fragment.BeritaSekitarFragment;
import id.co.viva.news.app.fragment.BolaFragment;
import id.co.viva.news.app.fragment.FavoritesFragment;
import id.co.viva.news.app.fragment.HeadlineFragment;
import id.co.viva.news.app.fragment.LifeFragment;
import id.co.viva.news.app.fragment.NewsFragment;
import id.co.viva.news.app.fragment.TerbaruFragment;
import id.co.viva.news.app.model.NavigationItem;
import info.hoang8f.widget.FButton;

public class ActLanding extends ActionBarActivity implements View.OnClickListener {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;
    private ArrayList<NavigationItem> navDrawerItems;
    private NavigationAdapter adapter;
    private android.support.v4.app.Fragment fragment = null;
    private android.support.v4.app.FragmentManager fragmentManager;
    private String mFullName;
    private String mEmail;
    private String mPhotoUrl;
    private RelativeLayout mNavLayout;
    private ImageView mBackground;
    private ImageView mImgProfile;
    private TextView mNameProfile;
    private TextView mEmailProfile;
    private boolean isInternetPresent = false;
    private FButton btnRetryList;
    private ProgressWheel progressWheel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        //Check Connection
        isInternetPresent = Global.getInstance(this)
                .getConnectionStatus().isConnectingToInternet();

        //Check User Profile
        getProfile();

        //Define All Views
        defineViews();

        //Set Header
        showHeaderActionBar();

        //Define Item Navigation List
        populateList();

        //Set default view
        if (savedInstanceState == null) {
            displayView(0);
        }
    }

    private void populateList() {
        StringRequest request = new StringRequest(Request.Method.GET, Constant.MAIN_CONFIG,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        getResponse(s);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (Global.getInstance(ActLanding.this)
                        .getRequestQueue().getCache().get(Constant.MAIN_CONFIG) != null) {
                    String cachedResponse = new String(Global.getInstance(ActLanding.this).
                            getRequestQueue().getCache().get(Constant.MAIN_CONFIG).data);
                    getResponse(cachedResponse);
                } else {
                    progressWheel.setVisibility(View.GONE);
                    btnRetryList.setVisibility(View.VISIBLE);
                }
            }
        });
        request.setShouldCache(true);
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Global.getInstance(this).getRequestQueue().getCache().invalidate(Constant.MAIN_CONFIG, true);
        Global.getInstance(this).getRequestQueue().getCache().get(Constant.MAIN_CONFIG);
        Global.getInstance(this).addToRequestQueue(request, Constant.JSON_REQUEST);
    }

    private void getResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            //Get menu list
            JSONArray listMenus = jsonObject.getJSONArray(Constant.menus);
            if (listMenus.length() > 0) {
                for (int i=0; i<listMenus.length(); i++) {
                    JSONObject data = listMenus.getJSONObject(i);
                    String name = data.getString(Constant.name);
                    int type = data.getInt(Constant.type);
                    String screen = data.getString(Constant.screen);
                    String hit_url = data.getString(Constant.hit_url);
                    String asset_url = data.getString(Constant.asset_url);
                    navDrawerItems.add(new NavigationItem(name, type, screen, hit_url, asset_url));
                }
                if (navDrawerItems.size() > 0) {
                    adapter = new NavigationAdapter(ActLanding.this, navDrawerItems);
                    mDrawerList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    progressWheel.setVisibility(View.GONE);
                    mDrawerList.setVisibility(View.VISIBLE);
                }
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    private void displayView(int position) {
        switch (position) {
            case 0:
                fragment = new TerbaruFragment();
                break;
            case 1:
                fragment = new HeadlineFragment();
                break;
            case 2:
                fragment = new BeritaSekitarFragment();
                break;
            case 3:
                fragment = new FavoritesFragment();
                break;
            case 4:
                scanNews();
                break;
            case 6:
                fragment =  new NewsFragment();
                break;
            case 7:
                fragment =  new BolaFragment();
                break;
            case 8:
                fragment =  new LifeFragment();
                break;
            case 10:
                fragment =  new AboutFragment();
                break;
            case 11:
                rateApp();
                break;
            case 12:
                sendEmail();
                break;
            default:
                break;
        }
        if (fragment != null) {
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.frame_container, fragment, "fragment")
                    .commit();
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            mDrawerLayout.closeDrawer(mNavLayout);
        } else {
            Log.e(Constant.TAG, "Error creating fragment..");
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.profile_bg) {
            if (mFullName.length() > 0 && mEmail.length() > 0) {
                Intent intent = new Intent(this, ActUserProfile.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
                mDrawerLayout.closeDrawer(mNavLayout);
            } else {
                Intent intent = new Intent(this, ActLogin.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
                mDrawerLayout.closeDrawer(mNavLayout);
            }
        } else if (view.getId() == R.id.btn_retry_list_menu) {
            if (btnRetryList.getVisibility() == View.VISIBLE) {
                btnRetryList.setVisibility(View.GONE);
            }
            progressWheel.setVisibility(View.VISIBLE);
            populateList();
        }
    }

    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//            displayView(position);
        }
    }

    private void showHeaderActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo_viva_coid_second);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                Constant.EMAIL_SCHEME, Constant.SUPPORT_EMAIL, null));
        startActivity(Intent.createChooser(emailIntent, "Send Email"));
    }

    private void scanNews() {
        Intent intent = new Intent(this, ActScanner.class);
        startActivity(intent);
    }

    private void rateApp() {
        Uri uri = Uri.parse(getResources().getString(R.string.url_google_play) + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.label_failed_found_store, Toast.LENGTH_LONG).show();
        }
    }

    private void defineViews() {
        //Menu collection
        navDrawerItems = new ArrayList<>();
        //Slider menu
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //Menu list
        mDrawerList = (ListView) findViewById(R.id.list_slider_menu);
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
        //Toggle for slider list menu
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this,
                mDrawerLayout,
                R.string.app_title_empty,
                R.string.app_title_empty) {
            public void onDrawerClosed(View view) {}
            public void onDrawerOpened(View drawerView) {}
            @Override
            public void setDrawerIndicatorEnabled(boolean enable) {
                super.setDrawerIndicatorEnabled(enable);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        //Progress view
        progressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        //Button for retry getting data
        btnRetryList = (FButton) findViewById(R.id.btn_retry_list_menu);
        btnRetryList.setOnClickListener(this);
        //Profile background
        mBackground = (ImageView) findViewById(R.id.profile_bg);
        mNavLayout = (RelativeLayout) findViewById(R.id.nav_layout);
        mImgProfile = (ImageView) findViewById(R.id.img_profile);
        mBackground.setOnClickListener(this);
        mNameProfile = (TextView) findViewById(R.id.tv_username);
        mEmailProfile = (TextView) findViewById(R.id.tv_user_email);
        if (mFullName.length() > 0 && mEmail.length() > 0) {
            mNameProfile.setText(mFullName);
            mEmailProfile.setText(mEmail);
        } else {
            mNameProfile.setText(getResources().getString(R.string.label_not_logged_in));
        }
        if (mPhotoUrl.length() > 0) {
            if (isInternetPresent) {
                Picasso.with(this).load(mPhotoUrl).transform(new CropSquareTransformation()).into(mImgProfile);
                Picasso.with(this).load(mPhotoUrl).transform(new CropSquareTransformation()).into(target);
            } else {
                mImgProfile.setImageResource(R.drawable.ic_profile);
            }
        } else {
            mImgProfile.setImageResource(R.drawable.ic_profile);
        }
    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            try {
                BitmapDrawable bitmapDrawable = new BitmapDrawable(Constant.blur(ActLanding.this, bitmap));
                mBackground.setBackgroundDrawable(bitmapDrawable);
            } catch (NoClassDefFoundError e) {
                e.getMessage();
            }
        }
        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            mBackground.setBackgroundDrawable(errorDrawable);
        }
        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            mBackground.setBackgroundDrawable(placeHolderDrawable);
        }
    };

    private void getProfile() {
        Global.getInstance(this).getDefaultEditor();
        mFullName = Global.getInstance(this).getSharedPreferences(this)
                .getString(Constant.LOGIN_STATES_FULLNAME, "");
        mEmail = Global.getInstance(this).getSharedPreferences(this)
                .getString(Constant.LOGIN_STATES_EMAIL, "");
        mPhotoUrl = Global.getInstance(this).getSharedPreferences(this)
                .getString(Constant.LOGIN_STATES_URL_PHOTO, "");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_frag_default, menu);
        //SearchView OnClick
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        android.support.v7.widget.SearchView searchView =
                (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(searchItem);
        if (searchView != null) {
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mNavLayout)) {
            mDrawerLayout.closeDrawer(mNavLayout);
        } else {
            if (fragment != null) {
                if (fragment.getClass().toString().equals(Constant.fragment_terbaru)) {
                    //Load ads
//                    if (isInternetPresent) {
//                        InterstitialAd interstitialAd = new InterstitialAd(this);
//                        AdsConfig adsConfig = new AdsConfig();
//                        adsConfig.setAdsInterstitial(this, interstitialAd,
//                                Constant.unitIdInterstitialClose, null, Constant.ADS_TYPE_CLOSING,
//                                fragment, ActLanding.this);
//                    } else {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .remove(fragment).commit();
                        finish();
//                    }
                } else {
                    backToFirst();
                }
            } else {
                finish();
            }
        }
    }

    private void backToFirst() {
        fragment = new TerbaruFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.frame_container, fragment, "fragment")
                .commit();
        mDrawerList.setItemChecked(0, true);
        mDrawerList.setSelection(0);
    }

}
