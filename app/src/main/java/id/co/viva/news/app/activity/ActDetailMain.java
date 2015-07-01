package id.co.viva.news.app.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.Toast;

import id.co.viva.news.app.R;
import id.co.viva.news.app.adapter.DetailMainAdapter;
import id.co.viva.news.app.component.ZoomOutPageTransformer;
import id.co.viva.news.app.fragment.ListMainFragment;
import id.co.viva.news.app.model.EntityMain;

/**
 * Created by reza on 14/10/14.
 */
public class ActDetailMain extends ActionBarActivity {

    private String id;
    private ViewPager viewPager;
    private DetailMainAdapter adapter;
    private String detailParam;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get parameter
        getParameters();

        setContentView(R.layout.act_detail_main_article);

        //Set ActionBar
        setActionBar();

        //Set Detail Pager
        int position = 0;
        if (ListMainFragment.entityList != null) {
            if (ListMainFragment.entityList.size() > 0) {
                for (EntityMain main : ListMainFragment.entityList) {
                    if (main.getId().equals(id)) break;
                    position++;
                }
            }
            adapter = new DetailMainAdapter(getSupportFragmentManager(), ListMainFragment.entityList, detailParam, name);
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

    private void getParameters() {
        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");
        detailParam = bundle.getString("screen");
        name = bundle.getString("name");
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

}
