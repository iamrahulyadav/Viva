package id.co.viva.news.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.LinePageIndicator;

import java.util.ArrayList;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.adapter.ImageSliderAdapter;
import id.co.viva.news.app.component.CropSquareTransformation;
import id.co.viva.news.app.model.SliderContentImage;
import id.co.viva.news.app.services.Analytics;

/**
 * Created by reza on 24/11/14.
 */
public class ActDetailFavorite extends ActionBarActivity implements View.OnClickListener {

    private KenBurnsView imageDetail;
    private TextView tvTitle;
    private TextView tvDatePublish;
    private TextView tvContent;
    private TextView tvReporterName;
    private ViewPager viewPager;
    private LinePageIndicator linePageIndicator;

    private boolean isInternetPresent = false;
    private ArrayList<SliderContentImage> sliderContentImages;

    private String title;
    private String image_url;
    private String date_publish;
    private String content;
    private String reporter_name;
    private String image_caption;
    private int thumbSize;
    private String sThumbList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Current connection
        isInternetPresent = Global.getInstance(this)
                .getConnectionStatus().isConnectingToInternet();
        //Get parameters
        getParams();
        //Check connection
        getAnalytics(title);

        setContentView(R.layout.act_detail_favorite);

        //Set header
        getHeaderActionBar();
        //Define all views
        defineViews();

        try {
            if (image_url != null) {
                Picasso.with(this).load(image_url)
                        .transform(new CropSquareTransformation()).into(imageDetail);
            }
        } catch (Exception e) {
            e.getMessage();
        }

        imageDetail.setOnClickListener(this);

        tvTitle.setText(title);
        tvDatePublish.setText(date_publish);
        tvContent.setText(Html.fromHtml(content).toString());
        tvReporterName.setText(reporter_name);

        if (thumbSize > 0 && sliderContentImages != null) {
            ImageSliderAdapter imageSliderAdapter = new ImageSliderAdapter(getSupportFragmentManager(), sliderContentImages);
            viewPager.setAdapter(imageSliderAdapter);
            viewPager.setCurrentItem(0);
            imageSliderAdapter.notifyDataSetChanged();
            linePageIndicator.setViewPager(viewPager);
            viewPager.setVisibility(View.VISIBLE);
            linePageIndicator.setVisibility(View.VISIBLE);
        }
    }

    private void getParams() {
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        image_url = intent.getStringExtra("image_url");
        date_publish = intent.getStringExtra("date_publish");
        content = intent.getStringExtra("content");
        reporter_name = intent.getStringExtra("reporter_name");
        image_caption = intent.getStringExtra("image_caption");
        thumbSize = intent.getIntExtra("list_thumbnail_body_size", 0);
        sThumbList = intent.getStringExtra("list_thumbnail_body");
    }

    private void defineViews() {
        sliderContentImages = Global.getInstance(this).getInstanceGson().
                fromJson(sThumbList, Global.getInstance(this).getTypeSlider());
        imageDetail = (KenBurnsView)findViewById(R.id.thumb_detail_content_favorite);
        tvTitle = (TextView)findViewById(R.id.title_detail_content_favorite);
        tvDatePublish = (TextView)findViewById(R.id.date_detail_content_favorite);
        tvContent = (TextView)findViewById(R.id.content_detail_content_favorite);
        tvReporterName = (TextView)findViewById(R.id.reporter_detail_content_favorite);
        viewPager = (ViewPager) findViewById(R.id.horizontal_list);
        linePageIndicator = (LinePageIndicator)findViewById(R.id.indicator);
        linePageIndicator.setVisibility(View.GONE);
        if (Constant.isTablet(this)) {
            imageDetail.getLayoutParams().height =
                    Constant.getDynamicImageSize(this, Constant.DYNAMIC_SIZE_GRID_TYPE);
            viewPager.getLayoutParams().height =
                    Constant.getDynamicImageSize(this, Constant.DYNAMIC_SIZE_SLIDER_TYPE);
        }
    }

    private void getHeaderActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Favorites");
    }

    private void getAnalytics(String title) {
        if (isInternetPresent) {
            Analytics analytics = new Analytics(this);
            analytics.getAnalyticByATInternet(Constant.FAVORITES_PAGE_DETAIL + title);
            analytics.getAnalyticByGoogleAnalytic(Constant.FAVORITES_PAGE_DETAIL + title);
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.thumb_detail_content_favorite) {
            if (image_url.length() > 0) {
                Bundle bundle = new Bundle();
                bundle.putString("photoUrl", image_url);
                bundle.putString("image_caption", image_caption);
                Intent intent = new Intent(this, ActDetailPhotoThumb.class);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
            }
        }
    }

}
