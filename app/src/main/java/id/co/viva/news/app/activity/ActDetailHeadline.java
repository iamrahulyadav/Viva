package id.co.viva.news.app.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.adapter.DetailHeadlineAdapter;
import id.co.viva.news.app.component.ZoomOutPageTransformer;
import id.co.viva.news.app.fragment.HeadlineFragment;
import id.co.viva.news.app.model.Headline;

/**
 * Created by rezarachman on 07/10/14.
 */
public class ActDetailHeadline extends ActionBarActivity {

    private String id;
    private ViewPager viewPager;
    private DetailHeadlineAdapter adapter;
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

        //Set Actionbar
        setActionBar();

        //Add ads if exists
        mParentLayout = (LinearLayout) findViewById(R.id.parent_layout);
        setAds(mParentLayout);

        //Set Detail Pager
        int position = 0;
        if(HeadlineFragment.headlineArrayList != null) {
            if(HeadlineFragment.headlineArrayList.size() > 0) {
                for(Headline headline : HeadlineFragment.headlineArrayList) {
                    if(headline.getId().equals(id)) break;
                    position++;
                }
            }
            adapter = new DetailHeadlineAdapter(getSupportFragmentManager(), HeadlineFragment.headlineArrayList);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.label_headline_detail));
    }

    private void setAds(LinearLayout parentLayout) {
        if (isInternetPresent) {
            PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
            //Ad Top
            if (Constant.unitIdTop != null) {
                if (Constant.unitIdTop.length() > 0) {
                    publisherAdViewTop = new PublisherAdView(this);
                    publisherAdViewTop.setAdUnitId(Constant.unitIdTop);
                    publisherAdViewTop.setAdSizes(AdSize.SMART_BANNER);
                    parentLayout.addView(publisherAdViewTop, 0);
                    publisherAdViewTop.loadAd(adRequest);
                }
            }
            //Ad Bottom
            if (Constant.unitIdBottom != null) {
                if (Constant.unitIdBottom.length() > 0) {
                    publisherAdViewBottom = new PublisherAdView(this);
                    publisherAdViewBottom.setAdUnitId(Constant.unitIdBottom);
                    publisherAdViewBottom.setAdSizes(AdSize.SMART_BANNER);
                    mParentLayout.addView(publisherAdViewBottom);
                    publisherAdViewBottom.loadAd(adRequest);
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
