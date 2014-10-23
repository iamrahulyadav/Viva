package id.co.viva.news.app.activity;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import id.co.viva.news.app.R;
import id.co.viva.news.app.adapter.DetailChannelBolaAdapter;
import id.co.viva.news.app.component.ZoomOutPageTransformer;
import id.co.viva.news.app.fragment.BolaFragment;
import id.co.viva.news.app.model.FeaturedBola;

/**
 * Created by reza on 23/10/14.
 */
public class ActDetailChannelBola extends FragmentActivity {

    private String id;
    private ViewPager viewPager;
    private DetailChannelBolaAdapter adapter;
    private PagerTabStrip pagerTabStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");

        setContentView(R.layout.act_detail_channel_bola);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setIcon(R.drawable.logo_viva_coid_second);

        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(getResources().getColor(R.color.color_bola));
        getActionBar().setBackgroundDrawable(colorDrawable);

        int position = 0;
        for(FeaturedBola featuredBola : BolaFragment.featuredNewsArrayList) {
            if(featuredBola.getId().equals(id)) break;
            position++;
        }

        pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_detail_channel_bola);
        pagerTabStrip.setDrawFullUnderline(false);
        pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.color_bola));
        pagerTabStrip.setTextColor(getResources().getColor(R.color.color_bola));

        adapter = new DetailChannelBolaAdapter(getSupportFragmentManager(), BolaFragment.featuredNewsArrayList);
        viewPager = (ViewPager)findViewById(R.id.vp_detail_channel_bola);
        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.setCurrentItem(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

}