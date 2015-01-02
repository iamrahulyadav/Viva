package id.co.viva.news.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.squareup.picasso.Picasso;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.services.Analytics;

/**
 * Created by reza on 24/11/14.
 */
public class ActDetailFavorite extends FragmentActivity implements View.OnClickListener {

    private Intent intent;
    private KenBurnsView imageDetail;
    private TextView tvTitle;
    private TextView tvDatePublish;
    private TextView tvContent;
    private TextView tvReportername;

    private Analytics analytics;
    private boolean isInternetPresent = false;

    private String title;
    private String image_url;
    private String date_publish;
    private String content;
    private String reporter_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isInternetPresent = Global.getInstance(this)
                .getConnectionStatus().isConnectingToInternet();

        intent = getIntent();
        title = intent.getStringExtra("title");
        image_url = intent.getStringExtra("image_url");
        date_publish = intent.getStringExtra("date_publish");
        content = intent.getStringExtra("content");
        reporter_name = intent.getStringExtra("reporter_name");

        if(isInternetPresent) {
            getAnalytics(title);
        }

        setContentView(R.layout.act_detail_favorite);

        getHeaderActionBar();

        imageDetail = (KenBurnsView)findViewById(R.id.thumb_detail_content_favorite);
        tvTitle = (TextView)findViewById(R.id.title_detail_content_favorite);
        tvDatePublish = (TextView)findViewById(R.id.date_detail_content_favorite);
        tvContent = (TextView)findViewById(R.id.content_detail_content_favorite);
        tvReportername = (TextView)findViewById(R.id.reporter_detail_content_favorite);

        try {
            if(image_url != null) {
                Picasso.with(this).load(image_url).into(imageDetail);
            }
        } catch (Exception e) {
            e.getMessage();
        }

        imageDetail.setOnClickListener(this);

        tvTitle.setText(title);
        tvDatePublish.setText(date_publish);
        tvContent.setText(Html.fromHtml(content).toString());
        tvReportername.setText(reporter_name);
    }

    private void getHeaderActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setTitle("Favorites");
    }

    private void getAnalytics(String title) {
        analytics = new Analytics(this);
        analytics.getAnalyticByATInternet(Constant.FAVORITES_PAGE_DETAIL + title);
        analytics.getAnalyticByGoogleAnalytic(Constant.FAVORITES_PAGE_DETAIL + title);
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
        if(view.getId() == R.id.thumb_detail_content_favorite) {
            if(image_url.length() > 0) {
                Bundle bundle = new Bundle();
                bundle.putString("photoUrl", image_url);
                Intent intent = new Intent(this, ActDetailPhotoThumb.class);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
            }
        }
    }

}
