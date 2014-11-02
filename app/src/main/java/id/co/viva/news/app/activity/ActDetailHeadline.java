package id.co.viva.news.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;

import id.co.viva.news.app.R;
import id.co.viva.news.app.adapter.DetailHeadlineAdapter;
import id.co.viva.news.app.component.ZoomOutPageTransformer;
import id.co.viva.news.app.fragment.HeadlineFragment;
import id.co.viva.news.app.fragment.TerbaruFragment;
import id.co.viva.news.app.model.Headline;

/**
 * Created by rezarachman on 07/10/14.
 */
public class ActDetailHeadline extends FragmentActivity {

    private String id;
    private ViewPager viewPager;
    private DetailHeadlineAdapter adapter;
    private String url_shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");
        url_shared = bundle.getString("url_shared");

        setContentView(R.layout.frag_detail_headline);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setTitle("Headlines");

        int position = 0;
        for(Headline headline : HeadlineFragment.headlineArrayList) {
            if(headline.getId().equals(id)) break;
            position++;
        }

        adapter = new DetailHeadlineAdapter(getSupportFragmentManager(), HeadlineFragment.headlineArrayList);
        viewPager = (ViewPager)findViewById(R.id.vp_headline_detail);
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
        getMenuInflater().inflate(R.menu.menu_detail_headline, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        ShareActionProvider myShareActionProvider = (ShareActionProvider) item.getActionProvider();
        Intent myIntent = new Intent();
        myIntent.setAction(Intent.ACTION_SEND);
        myIntent.putExtra(Intent.EXTRA_TEXT, url_shared);
        myIntent.setType("text/plain");
        myShareActionProvider.setShareIntent(myIntent);
        return true;
    }

}
