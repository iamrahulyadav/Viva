package id.co.viva.news.app.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");
        channel_title = bundle.getString("channel_title");

        setContentView(R.layout.act_detail_content);

        setActionBar();

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
