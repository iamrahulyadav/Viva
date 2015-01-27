package id.co.viva.news.app.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.widget.Toast;

import id.co.viva.news.app.R;
import id.co.viva.news.app.adapter.DetailHeadlineAdapter;
import id.co.viva.news.app.component.ZoomOutPageTransformer;
import id.co.viva.news.app.fragment.HeadlineFragment;
import id.co.viva.news.app.model.Headline;

/**
 * Created by rezarachman on 07/10/14.
 */
public class ActDetailHeadline extends FragmentActivity {

    private String id;
    private ViewPager viewPager;
    private DetailHeadlineAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");

        setContentView(R.layout.frag_detail_headline);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setTitle("Headlines");

        int position = 0;
        if(HeadlineFragment.headlineArrayList != null) {
            if(HeadlineFragment.headlineArrayList.size() > 0) {
                for(Headline headline : HeadlineFragment.headlineArrayList) {
                    if(headline.getId().equals(id)) break;
                    position++;
                }
            }
        } else {
            Toast.makeText(this, R.string.label_error, Toast.LENGTH_SHORT).show();
            onBackPressed();
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

}
