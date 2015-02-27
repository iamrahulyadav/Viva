package id.co.viva.news.app.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.widget.Toast;

import id.co.viva.news.app.R;
import id.co.viva.news.app.adapter.DetailBeritaSekitarAdapter;
import id.co.viva.news.app.component.ZoomOutPageTransformer;
import id.co.viva.news.app.fragment.BeritaSekitarFragment;
import id.co.viva.news.app.model.BeritaSekitar;

/**
 * Created by reza on 27/02/15.
 */
public class ActDetailBeritaSekitar extends FragmentActivity {

    private String id;
    private ViewPager viewPager;
    private DetailBeritaSekitarAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");

        setContentView(R.layout.act_detail_main_article);

        setActionBar();

        int position = 0;
        if(BeritaSekitarFragment.beritaSekitarArrayList != null) {
            if(BeritaSekitarFragment.beritaSekitarArrayList.size() > 0) {
                for(BeritaSekitar beritaSekitar : BeritaSekitarFragment.beritaSekitarArrayList) {
                    if(beritaSekitar.getId().equals(id)) break;
                    position++;
                }
            }
            adapter = new DetailBeritaSekitarAdapter(getSupportFragmentManager(), BeritaSekitarFragment.beritaSekitarArrayList);
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
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setTitle("Berita Sekitar");
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
