package id.co.viva.news.app.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.R;
import id.co.viva.news.app.adapter.NavigationAdapter;
import id.co.viva.news.app.fragment.BolaFragment;
import id.co.viva.news.app.fragment.LatestFragment;
import id.co.viva.news.app.fragment.LifeFragment;
import id.co.viva.news.app.fragment.NewsFragment;
import id.co.viva.news.app.model.NavigationItem;

public class ActMain extends FragmentActivity {

    private CharSequence mTitle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private TypedArray navMenuIcons;
//    private String[] navMenuTitles;
    private ArrayList<NavigationItem> navDrawerItems;
    private NavigationAdapter adapter;
    private CharSequence mDrawerTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        mTitle = mDrawerTitle = getTitle();
//        navMenuTitles = getResources()
//                .getStringArray(R.array.nav_drawer_items);
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.header_navigation, mDrawerList, false);
        mDrawerList.addHeaderView(header, null, false);

        navDrawerItems = new ArrayList<NavigationItem>();
        navDrawerItems.add(new NavigationItem(navMenuIcons.getResourceId(0, -1)));
        navDrawerItems.add(new NavigationItem(navMenuIcons.getResourceId(1, -1)));
        navDrawerItems.add(new NavigationItem(navMenuIcons.getResourceId(2, -1)));
        navDrawerItems.add(new NavigationItem(navMenuIcons.getResourceId(3, -1)));
        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
        adapter = new NavigationAdapter(getApplicationContext(), navDrawerItems);
        mDrawerList.setAdapter(adapter);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setIcon(R.drawable.logo_viva_coid);
        getActionBar().setDisplayShowTitleEnabled(false);

        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                R.drawable.ic_drawer,
                R.string.app_name,
                R.string.app_name) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }
            @Override
            public void setDrawerIndicatorEnabled(boolean enable) {
                super.setDrawerIndicatorEnabled(enable);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if(savedInstanceState == null) {
            displayView(1);
        }
    }

    private void displayView(int position) {
        android.support.v4.app.Fragment fragment = null;
        switch (position) {
            case 1:
                fragment = new LatestFragment();
                break;
            case 2:
                fragment = new NewsFragment();
                break;
            case 3:
                fragment =  new BolaFragment();
                break;
            case 4:
                fragment =  new LifeFragment();
                break;
            default:
                break;
        }
        if (fragment != null) {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.frame_container, fragment)
                    .commit();
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
//            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            Log.e(Constant.TAG, "Error creating fragment..");
        }
    }

    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            displayView(position);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
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
        inflater.inflate(R.menu.menu_frag_headline, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return false;
    }

}
