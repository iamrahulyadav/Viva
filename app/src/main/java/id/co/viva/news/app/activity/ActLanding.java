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

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.adapter.NavigationAdapter;
import id.co.viva.news.app.component.CropSquareTransformation;
import id.co.viva.news.app.fragment.AboutFragment;
import id.co.viva.news.app.fragment.BeritaSekitarFragment;
import id.co.viva.news.app.fragment.BolaFragment;
import id.co.viva.news.app.fragment.FavoritesFragment;
import id.co.viva.news.app.fragment.HeadlineFragment;
import id.co.viva.news.app.fragment.LifeFragment;
import id.co.viva.news.app.fragment.NewsFragment;
import id.co.viva.news.app.fragment.TerbaruFragment;
import id.co.viva.news.app.interfaces.Item;
import id.co.viva.news.app.model.NavigationItem;
import id.co.viva.news.app.model.NavigationSectionItem;

public class ActLanding extends ActionBarActivity implements View.OnClickListener {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;
    private ArrayList<Item> navDrawerItems;
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

        //Define Item Navigation List
        defineItemList();

        //All About List
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
        adapter = new NavigationAdapter(getApplicationContext(), navDrawerItems);
        mDrawerList.setAdapter(adapter);

        //Set Header
        showHeaderActionBar();

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

        if(savedInstanceState == null) {
            displayView(0);
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
        if(fragment != null) {
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
        }
    }

    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            displayView(position);
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

    private void defineItemList() {
        navDrawerItems = new ArrayList<>();
        navDrawerItems.add(new NavigationItem(getResources().getString(R.string.label_item_navigation_terbaru), R.drawable.icon_headline));
        navDrawerItems.add(new NavigationItem(getResources().getString(R.string.label_item_navigation_headline), R.drawable.icon_terbaru));
        navDrawerItems.add(new NavigationItem(getResources().getString(R.string.label_item_navigation_search_by_location), R.drawable.icon_search_location));
        navDrawerItems.add(new NavigationItem(getResources().getString(R.string.label_item_navigation_favorites), R.drawable.icon_favorites));
        navDrawerItems.add(new NavigationItem(getResources().getString(R.string.label_item_navigation_scan_berita), R.drawable.icon_qrcode));
        navDrawerItems.add(new NavigationSectionItem(getResources().getString(R.string.label_section_navigation_channel)));
        navDrawerItems.add(new NavigationItem(getResources().getString(R.string.label_item_navigation_news), R.drawable.icon_viva_news));
        navDrawerItems.add(new NavigationItem(getResources().getString(R.string.label_item_navigation_bola), R.drawable.icon_viva_bola));
        navDrawerItems.add(new NavigationItem(getResources().getString(R.string.label_item_navigation_life), R.drawable.icon_viva_life));
        navDrawerItems.add(new NavigationSectionItem(getResources().getString(R.string.label_section_navigation_preferences)));
        navDrawerItems.add(new NavigationItem(getResources().getString(R.string.label_item_navigation_about), R.drawable.icon_about));
        navDrawerItems.add(new NavigationItem(getResources().getString(R.string.label_item_navigation_rateapp), R.drawable.icon_rateapp));
        navDrawerItems.add(new NavigationItem(getResources().getString(R.string.label_item_navigation_email), R.drawable.icon_mail));
    }

    private void defineViews() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
        mBackground = (ImageView) findViewById(R.id.profile_bg);
        mNavLayout = (RelativeLayout) findViewById(R.id.nav_layout);
        mImgProfile = (ImageView) findViewById(R.id.img_profile);
        mBackground.setOnClickListener(this);
        mNameProfile = (TextView) findViewById(R.id.tv_username);
        mEmailProfile = (TextView) findViewById(R.id.tv_user_email);
        if(mFullName.length() > 0 && mEmail.length() > 0) {
            mNameProfile.setText(mFullName);
            mEmailProfile.setText(mEmail);
        } else {
            mNameProfile.setText(getResources().getString(R.string.label_not_logged_in));
        }
        if(mPhotoUrl.length() > 0) {
            if(isInternetPresent) {
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (fragment != null) {
            if (fragment.getClass().toString().equals(Constant.fragment_bola)
                    || fragment.getClass().toString().equals(Constant.fragment_life)
                    || fragment.getClass().toString().equals(Constant.fragment_news)) {
                if (mDrawerLayout.isDrawerOpen(mNavLayout)) {
                    menu.findItem(R.id.action_change_layout).setEnabled(false);
                } else {
                    menu.findItem(R.id.action_change_layout).setEnabled(true);
                }
            }
        }
        return super.onPrepareOptionsMenu(menu);
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
        if (item.getItemId() == R.id.action_change_layout) {
            if (this != null) {
                invalidateOptionsMenu();
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(mNavLayout)) {
            mDrawerLayout.closeDrawer(mNavLayout);
        } else {
            if(fragment != null) {
                if(fragment.getClass().toString().equals(Constant.fragment_terbaru)) {
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
