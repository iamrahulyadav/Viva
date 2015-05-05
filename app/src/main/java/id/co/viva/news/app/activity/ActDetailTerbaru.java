package id.co.viva.news.app.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.doubleclick.PublisherAdView;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.adapter.DetailTerbaruAdapter;
import id.co.viva.news.app.ads.AdsConfig;
import id.co.viva.news.app.component.ZoomOutPageTransformer;
import id.co.viva.news.app.fragment.TerbaruFragment;
import id.co.viva.news.app.model.News;

/**
 * Created by reza on 14/10/14.
 */
public class ActDetailTerbaru extends ActionBarActivity {

    private String id;
    private ViewPager viewPager;
    private DetailTerbaruAdapter adapter;
    private Boolean isInternetPresent = false;
    private LinearLayout mParentLayout;
    private PublisherAdView publisherAdViewBottom;
    private PublisherAdView publisherAdViewTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get parameter
        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");

        //Check internet connection
        isInternetPresent = Global.getInstance(this)
                .getConnectionStatus().isConnectingToInternet();

        setContentView(R.layout.act_detail_main_article);

        //Set ActionBar
        setActionBar();

        //Add ads if exists
        mParentLayout = (LinearLayout) findViewById(R.id.parent_layout);
        setAds(mParentLayout);

        //Set Detail Pager
        int position = 0;
        if(TerbaruFragment.newsArrayList != null) {
            if(TerbaruFragment.newsArrayList.size() > 0) {
                for(News news : TerbaruFragment.newsArrayList) {
                    if(news.getId().equals(id)) break;
                    position++;
                }
            }
            adapter = new DetailTerbaruAdapter(getSupportFragmentManager(), TerbaruFragment.newsArrayList);
            viewPager = (ViewPager)findViewById(R.id.vp_detail_main_article);
            viewPager.setAdapter(adapter);
            viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
            viewPager.setCurrentItem(position);
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, R.string.label_error, Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    private void setActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.label_terbaru_detail));
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

    private void setAds(LinearLayout parentLayout) {
        if (isInternetPresent) {
            if (this != null) {
                publisherAdViewTop = new PublisherAdView(this);
                publisherAdViewBottom = new PublisherAdView(this);
                AdsConfig adsConfig = new AdsConfig();
                adsConfig.setAdsBanner(publisherAdViewTop, Constant.unitIdTop, Constant.POSITION_BANNER_TOP, parentLayout);
                adsConfig.setAdsBanner(publisherAdViewBottom, Constant.unitIdBottom, Constant.POSITION_BANNER_BOTTOM, parentLayout);
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
