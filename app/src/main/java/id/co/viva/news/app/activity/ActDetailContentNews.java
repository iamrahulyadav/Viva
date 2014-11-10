package id.co.viva.news.app.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.widget.Toast;

import id.co.viva.news.app.R;
import id.co.viva.news.app.adapter.DetailContentAdapterNews;
import id.co.viva.news.app.component.ZoomOutPageTransformer;
import id.co.viva.news.app.model.ChannelNews;

/**
 * Created by reza on 24/10/14.
 */
public class ActDetailContentNews extends FragmentActivity {

    private String id;
    private ViewPager viewPager;
    private DetailContentAdapterNews adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");

        setContentView(R.layout.act_detail_content);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(getResources().getColor(R.color.color_news));
        getActionBar().setBackgroundDrawable(colorDrawable);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setIcon(R.drawable.logo_viva_coid_second);

        int position = 0;
        if(ActDetailChannelNews.channelNewsArrayList.size() > 0) {
            //TODO Handle go back from open url in browser
            for(ChannelNews channelnews : ActDetailChannelNews.channelNewsArrayList) {
                if(channelnews.getId().equals(id)) break;
                position++;
            }
        } else {
            Toast.makeText(this, R.string.label_error, Toast.LENGTH_SHORT).show();
            onBackPressed();
        }

        adapter = new DetailContentAdapterNews(getSupportFragmentManager(), ActDetailChannelNews.channelNewsArrayList);
        viewPager = (ViewPager)findViewById(R.id.vp_detail_content);
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

}
