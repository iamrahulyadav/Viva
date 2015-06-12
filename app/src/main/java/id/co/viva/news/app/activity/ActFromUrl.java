package id.co.viva.news.app.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.LinePageIndicator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.adapter.ImageSliderAdapter;
import id.co.viva.news.app.component.CropSquareTransformation;
import id.co.viva.news.app.component.ProgressWheel;
import id.co.viva.news.app.model.SliderContentImage;
import id.co.viva.news.app.model.Video;

/**
 * Created by reza on 20/01/15.
 */
public class ActFromUrl extends ActionBarActivity implements View.OnClickListener {

    private boolean isInternetPresent = false;
    private ViewPager viewPager;
    private LinePageIndicator linePageIndicator;
    private ProgressWheel progressWheel;
    private RippleView rippleView;
    private Button btnRetry;
    private TextView tvNoResult;
    private ArrayList<SliderContentImage> sliderContentImages;
    private ArrayList<Video> videoArrayList;
    private TextView tvTitleDetail;
    private TextView tvDateDetail;
    private TextView tvReporterDetail;
    private TextView tvContentDetail;
    private ImageView ivThumbDetail;
    private TextView textLinkVideo;
    private Button btnComment;
    private String title;
    private String kanal;
    private String image_url;
    private String date_publish;
    private String content;
    private String reporter_name;
    private String image_caption;
    private String sliderPhotoUrl;
    private String sliderTitle;
    private String urlVideo;
    private ImageSliderAdapter imageSliderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_notification);
        defineViews();

        isInternetPresent = Global.getInstance(this)
                .getConnectionStatus().isConnectingToInternet();

        getContent();
    }

    private void defineViews() {
        viewPager = (ViewPager) findViewById(R.id.horizontal_list);
        viewPager.setVisibility(View.GONE);

        btnComment = (Button) findViewById(R.id.btn_comment);
        btnComment.setOnClickListener(this);
        btnComment.setTransformationMethod(null);

        linePageIndicator = (LinePageIndicator) findViewById(R.id.indicator);
        linePageIndicator.setVisibility(View.GONE);

        progressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        btnRetry = (Button) findViewById(R.id.btn_retry);

        rippleView = (RippleView) findViewById(R.id.layout_ripple_view_detail_subkanal);
        rippleView.setVisibility(View.GONE);
        rippleView.setOnClickListener(this);

        tvNoResult = (TextView) findViewById(R.id.text_no_result_detail_content);
        tvNoResult.setVisibility(View.GONE);

        sliderContentImages = new ArrayList<>();
        videoArrayList = new ArrayList<>();

        tvTitleDetail = (TextView) findViewById(R.id.title_detail_content);
        tvDateDetail = (TextView) findViewById(R.id.date_detail_content);
        tvReporterDetail = (TextView) findViewById(R.id.reporter_detail_content);
        tvContentDetail = (TextView) findViewById(R.id.content_detail_content);

        ivThumbDetail = (ImageView) findViewById(R.id.thumb_detail_content);
        ivThumbDetail.setOnClickListener(this);
        ivThumbDetail.setFocusableInTouchMode(true);

        textLinkVideo = (TextView) findViewById(R.id.text_move_video);
        textLinkVideo.setOnClickListener(this);
        textLinkVideo.setVisibility(View.GONE);

        if (Constant.isTablet(this)) {
            ivThumbDetail.getLayoutParams().height =
                    Constant.getDynamicImageSize(this, Constant.DYNAMIC_SIZE_GRID_TYPE);
            viewPager.getLayoutParams().height =
                    Constant.getDynamicImageSize(this, Constant.DYNAMIC_SIZE_SLIDER_TYPE);
        }
    }

    private void goDetailPhoto() {
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

    private void moveVideoPage() {
        Bundle bundle = new Bundle();
        bundle.putString("urlVideo", urlVideo);
        Intent intent = new Intent(this, ActVideo.class);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    private void moveCommentPage() {
        Bundle bundle = new Bundle();
        bundle.putString("imageurl", image_url);
        bundle.putString("title", title);
        bundle.putString("article_id", getIdFromUrl());
        bundle.putString("type_kanal", kanal);
        Intent intent = new Intent(this, ActComment.class);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.layout_ripple_view_detail_subkanal) {
            getContent();
        } else if (view.getId() == R.id.thumb_detail_content) {
            goDetailPhoto();
        } else if (view.getId() == R.id.text_move_video) {
            moveVideoPage();
        } else if (view.getId() == R.id.btn_comment) {
            moveCommentPage();
        }
    }

    private void getContent() {
        if (isInternetPresent) {
            StringRequest request = new StringRequest(Request.Method.GET, Constant.NEW_DETAIL + "/id/" + getIdFromUrl(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String volleyResponse) {
                            Log.i(Constant.TAG, volleyResponse);
                            try {
                                JSONObject jsonObject = new JSONObject(volleyResponse);
                                JSONObject response = jsonObject.getJSONObject(Constant.response);
                                JSONObject detail = response.getJSONObject(Constant.detail);
                                title = detail.getString(Constant.title);
                                image_url = detail.getString(Constant.image_url);
                                date_publish = detail.getString(Constant.date_publish);
                                content = detail.getString(Constant.content);
                                reporter_name = detail.getString(Constant.reporter_name);
                                image_caption = detail.getString(Constant.image_caption);
                                kanal = detail.getString(Constant.kanal);

                                setThemes(kanal);

                                JSONArray sliderImageArray = detail.getJSONArray(Constant.content_images);
                                if (sliderImageArray != null) {
                                    for (int i=0; i<sliderImageArray.length(); i++) {
                                        JSONObject objSlider = sliderImageArray.getJSONObject(i);
                                        sliderPhotoUrl = objSlider.getString("src");
                                        sliderTitle = objSlider.getString("title");
                                        sliderContentImages.add(new SliderContentImage(sliderPhotoUrl, sliderTitle));
                                    }
                                }

                                JSONArray content_video = detail.getJSONArray(Constant.content_video);
                                if (content_video != null && content_video.length() > 0) {
                                    for (int i=0; i<content_video.length(); i++) {
                                        JSONObject objVideo = content_video.getJSONObject(i);
                                        urlVideo = objVideo.getString("src_1");
                                        videoArrayList.add(new Video(urlVideo, null, null));
                                    }
                                }

                                tvTitleDetail.setText(title);
                                tvDateDetail.setText(date_publish);
                                tvContentDetail.setText(Html.fromHtml(content).toString());
                                tvContentDetail.setMovementMethod(LinkMovementMethod.getInstance());
                                tvReporterDetail.setText(reporter_name);
                                Picasso.with(ActFromUrl.this).load(image_url).transform(new CropSquareTransformation()).into(ivThumbDetail);

                                if (sliderContentImages.size() > 0) {
                                    imageSliderAdapter = new ImageSliderAdapter(getSupportFragmentManager(), sliderContentImages);
                                    viewPager.setAdapter(imageSliderAdapter);
                                    viewPager.setCurrentItem(0);
                                    imageSliderAdapter.notifyDataSetChanged();
                                    linePageIndicator.setViewPager(viewPager);
                                    viewPager.setVisibility(View.VISIBLE);
                                    linePageIndicator.setVisibility(View.VISIBLE);
                                }

                                btnComment.setVisibility(View.VISIBLE);

                                if (rippleView.getVisibility() == View.VISIBLE) {
                                    rippleView.setVisibility(View.GONE);
                                }
                                progressWheel.setVisibility(View.GONE);

                                if (urlVideo.length() > 0) {
                                    textLinkVideo.setVisibility(View.VISIBLE);
                                }
                            } catch (Exception e) {
                                e.getMessage();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            progressWheel.setVisibility(View.GONE);
                            rippleView.setVisibility(View.VISIBLE);
                            setButtonRetry(kanal);
                        }
                    });
            request.setShouldCache(true);
            request.setRetryPolicy(new DefaultRetryPolicy(
                    Constant.TIME_OUT,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Global.getInstance(ActFromUrl.this).getRequestQueue()
                    .getCache().invalidate(Constant.NEW_DETAIL + "/id/" + getIdFromUrl(), true);
            Global.getInstance(ActFromUrl.this)
                    .getRequestQueue().getCache().get(Constant.NEW_DETAIL + "/id/" + getIdFromUrl());
            Global.getInstance(ActFromUrl.this).addToRequestQueue(request, Constant.JSON_REQUEST);
        } else {
            if (tvNoResult.getVisibility() == View.VISIBLE) {
                tvNoResult.setVisibility(View.GONE);
            } else {
                tvNoResult.setVisibility(View.VISIBLE);
            }
            Toast.makeText(this, R.string.title_no_connection, Toast.LENGTH_SHORT).show();
        }
    }

    private void setThemes(String mChannel) {
        ColorDrawable colorDrawable = new ColorDrawable();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if (mChannel != null) {
            if (mChannel.equalsIgnoreCase("bola")) {
                colorDrawable.setColor(getResources().getColor(R.color.color_bola));
                getSupportActionBar().setBackgroundDrawable(colorDrawable);
                getSupportActionBar().setTitle(R.string.label_item_navigation_bola);
                btnComment.setBackgroundColor(getResources().getColor(R.color.color_bola));
                progressWheel.setBarColor(getResources().getColor(R.color.color_bola));
            } else if (mChannel.equalsIgnoreCase("vivalife")) {
                colorDrawable.setColor(getResources().getColor(R.color.color_life));
                getSupportActionBar().setBackgroundDrawable(colorDrawable);
                getSupportActionBar().setTitle(R.string.label_item_navigation_life);
                btnComment.setBackgroundColor(getResources().getColor(R.color.color_life));
                progressWheel.setBarColor(getResources().getColor(R.color.color_life));
            } else {
                colorDrawable.setColor(getResources().getColor(R.color.color_news));
                getSupportActionBar().setBackgroundDrawable(colorDrawable);
                getSupportActionBar().setTitle(R.string.label_item_navigation_news);
                btnComment.setBackgroundColor(getResources().getColor(R.color.color_news));
                progressWheel.setBarColor(getResources().getColor(R.color.color_news));
            }
        } else {
            colorDrawable.setColor(getResources().getColor(R.color.new_base_color));
            getSupportActionBar().setBackgroundDrawable(colorDrawable);
            btnComment.setBackgroundColor(getResources().getColor(R.color.new_base_color));
            progressWheel.setBarColor(getResources().getColor(R.color.new_base_color));
        }
    }

    private void setButtonRetry(String kanals) {
        if (kanals != null) {
            if (kanals.equalsIgnoreCase("bola")) {
                btnRetry.setBackgroundResource(R.drawable.shadow_button_bola);
            } else if (kanals.equalsIgnoreCase("vivalife")) {
                btnRetry.setBackgroundResource(R.drawable.shadow_button_life);
            } else {
                btnRetry.setBackgroundResource(R.drawable.shadow_button_news);
            }
        }
    }

    private void goFirstFlow() {
        finish();
        Intent intent = new Intent(this, ActLanding.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goFirstFlow();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goFirstFlow();
    }

    private String getIdFromUrl() {
        String idFromUrl = null;
        Uri data = getIntent().getData();
        if (data != null) {
            List params = data.getPathSegments();
            idFromUrl = params.get(2).toString();
        }
        return splitId(idFromUrl);
    }

    private String splitId(String id) {
        String[] separated = id.split("-");
        String ids = separated[0];
        return ids;
    }

}
