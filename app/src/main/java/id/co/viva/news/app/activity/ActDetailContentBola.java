package id.co.viva.news.app.activity;

import android.graphics.drawable.ColorDrawable;
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
import id.co.viva.news.app.adapter.DetailContentAdapterBola;
import id.co.viva.news.app.component.ZoomOutPageTransformer;
import id.co.viva.news.app.model.ChannelBola;

/**
 * Created by reza on 24/10/14.
 */
public class ActDetailContentBola extends ActionBarActivity {

    private String id;
    private String channel_title;
    private ViewPager viewPager;
    private DetailContentAdapterBola adapter;
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
        channel_title = bundle.getString("channel_title");

        //Check internet connection
        isInternetPresent = Global.getInstance(this)
                .getConnectionStatus().isConnectingToInternet();

        setContentView(R.layout.act_detail_content);

        //Set Actionbar
        setActionBar();

        //Add ads if exists
        mParentLayout = (LinearLayout) findViewById(R.id.parent_layout);
        setAds(mParentLayout);

        //Set Detail Pager
        int position = 0;
        if(ActDetailChannelBola.channelBolaArrayList != null) {
            if(ActDetailChannelBola.channelBolaArrayList.size() > 0) {
                for(ChannelBola channelBola : ActDetailChannelBola.channelBolaArrayList) {
                    if(channelBola.getId().equals(id)) break;
                    position++;
                }
            }
            adapter = new DetailContentAdapterBola(getSupportFragmentManager(), ActDetailChannelBola.channelBolaArrayList);
            viewPager = (ViewPager)findViewById(R.id.vp_detail_content);
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
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(getResources().getColor(R.color.color_bola));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        if (channel_title != null) {
            getSupportActionBar().setTitle(channel_title);
        }
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
                    parentLayout.addView(publisherAdViewBottom);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
