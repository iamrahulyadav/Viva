package id.co.viva.news.app.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.R;
import id.co.viva.news.app.adapter.NavigationAdapter;
import id.co.viva.news.app.fragment.AboutFragment;
import id.co.viva.news.app.fragment.BolaFragment;
import id.co.viva.news.app.fragment.HeadlineFragment;
import id.co.viva.news.app.fragment.TerbaruFragment;
import id.co.viva.news.app.fragment.LifeFragment;
import id.co.viva.news.app.fragment.NewsFragment;
import id.co.viva.news.app.interfaces.Item;
import id.co.viva.news.app.model.NavigationItem;
import id.co.viva.news.app.model.NavigationSectionItem;

public class ActBase extends FragmentActivity {

    protected DrawerLayout mDrawerLayout;
    private CharSequence mTitle;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayList<Item> navDrawerItems;
    private NavigationAdapter adapter;
    private CharSequence mDrawerTitle;
    private android.support.v4.app.Fragment fragment = null;
    private android.support.v4.app.FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        mTitle = mDrawerTitle = getTitle();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<Item>();
        navDrawerItems.add(new NavigationItem("Headlines", R.drawable.icon_terbaru));
        navDrawerItems.add(new NavigationItem("Terbaru", R.drawable.icon_headline));
        navDrawerItems.add(new NavigationSectionItem("CHANNELS"));
        navDrawerItems.add(new NavigationItem("News", R.drawable.icon_viva_news));
        navDrawerItems.add(new NavigationItem("Bola", R.drawable.icon_viva_bola));
        navDrawerItems.add(new NavigationItem("Life", R.drawable.icon_viva_life));
        navDrawerItems.add(new NavigationSectionItem("PREFERENCES"));
        navDrawerItems.add(new NavigationItem("About", R.drawable.icon_about));

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
        adapter = new NavigationAdapter(getApplicationContext(), navDrawerItems);
        mDrawerList.setAdapter(adapter);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setIcon(R.drawable.logo_viva_coid_second);
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
    }

    private void displayView(int position) {
        switch (position) {
            case 0:
                fragment = new HeadlineFragment();
                break;
            case 1:
                fragment = new TerbaruFragment();
                break;
            case 3:
                fragment =  new NewsFragment();
                break;
            case 4:
                fragment =  new BolaFragment();
                break;
            case 5:
                fragment =  new LifeFragment();
                break;
            case 7:
                fragment =  new AboutFragment();
                break;
            default:
                break;
        }
        if(fragment != null) {
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.frame_container, fragment)
                    .commit();
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(mDrawerList)) {
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
           super.onBackPressed();
        }
    }

}
