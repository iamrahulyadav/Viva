package id.co.viva.news.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.R;
import id.co.viva.news.app.adapter.DetailTerbaruAdapter;
import id.co.viva.news.app.component.ZoomOutPageTransformer;
import id.co.viva.news.app.fragment.LatestFragment;
import id.co.viva.news.app.model.News;

/**
 * Created by reza on 14/10/14.
 */
public class ActDetailTerbaru extends FragmentActivity {

    private String id;
    private String url_shared;
    private ViewPager viewPager;
    private DetailTerbaruAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");
        url_shared = bundle.getString("url_shared");

        setContentView(R.layout.frag_detail_terbaru);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setTitle("Terbaru");

        int position = 0;
        //TODO Handle go back from open url in browser
        for(News news : LatestFragment.newsArrayList) {
            if(news.getId().equals(id)) break;
            position++;
        }

        adapter = new DetailTerbaruAdapter(getSupportFragmentManager(), LatestFragment.newsArrayList);
        viewPager = (ViewPager)findViewById(R.id.vp_news_detail);
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
