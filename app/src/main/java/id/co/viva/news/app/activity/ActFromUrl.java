package id.co.viva.news.app.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.flaviofaria.kenburnsview.KenBurnsView;
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

/**
 * Created by reza on 20/01/15.
 */
public class ActFromUrl extends ActionBarActivity implements View.OnClickListener {

    private boolean isInternetPresent = false;
    private int pageCount = 0;

    private ViewPager viewPager;
    private LinearLayout mPagingButtonLayout;
    private LinePageIndicator linePageIndicator;
    private ProgressWheel progressWheel;
    private RippleView rippleView;
    private Button btnRetry;
    private TextView tvNoResult;
    private TextView tvTitleDetail;
    private TextView tvDateDetail;
    private TextView tvReporterDetail;
    private TextView tvContentDetail;
    private TextView textPageNext;
    private TextView textPagePrevious;
    private KenBurnsView ivThumbDetail;
    private TextView textLinkVideo;
    private Button btnComment;

    private ArrayList<String> pagingContents;
    private ArrayList<SliderContentImage> sliderContentImages;
    private ImageSliderAdapter imageSliderAdapter;

    private String title;
    private String channel;
    private String image_url;
    private String date_publish;
    private String reporter_name;
    private String image_caption;
    private String sliderPhotoUrl;
    private String sliderTitle;
    private String urlVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_notification);
        //Define all views
        defineViews();
        //Check current connection
        isInternetPresent = Global.getInstance(this)
                .getConnectionStatus().isConnectingToInternet();
        //Retrieve content
        getContent();
    }

    private void defineViews() {
        mPagingButtonLayout = (LinearLayout) findViewById(R.id.layout_button_next_previous);

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
        pagingContents = new ArrayList<>();

        tvTitleDetail = (TextView) findViewById(R.id.title_detail_content);
        tvDateDetail = (TextView) findViewById(R.id.date_detail_content);
        tvReporterDetail = (TextView) findViewById(R.id.reporter_detail_content);
        tvContentDetail = (TextView) findViewById(R.id.content_detail_content);

        ivThumbDetail = (KenBurnsView) findViewById(R.id.thumb_detail_content);
        ivThumbDetail.setOnClickListener(this);
        ivThumbDetail.setFocusableInTouchMode(true);

        textLinkVideo = (TextView) findViewById(R.id.text_move_video);
        textLinkVideo.setOnClickListener(this);
        textLinkVideo.setVisibility(View.GONE);

        textPageNext = (TextView) findViewById(R.id.text_page_next);
        textPagePrevious = (TextView) findViewById(R.id.text_page_previous);
        textPageNext.setOnClickListener(this);
        textPagePrevious.setOnClickListener(this);
        textPagePrevious.setEnabled(false);

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

    private void moveVideoPage(String video) {
        Bundle bundle = new Bundle();
        bundle.putString("urlVideo", video);
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
        bundle.putString("type_kanal", channel);
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
            moveVideoPage(urlVideo);
        } else if (view.getId() == R.id.btn_comment) {
            moveCommentPage();
        } else if (view.getId() == R.id.text_page_next) {
            showPagingNext();
        } else if (view.getId() == R.id.text_page_previous) {
            showPagingPrevious();
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
                                //Get detail content
                                JSONObject detail = response.getJSONObject(Constant.detail);
                                title = detail.getString(Constant.title);
                                image_url = detail.getString(Constant.image_url);
                                date_publish = detail.getString(Constant.date_publish);
                                reporter_name = detail.getString(Constant.reporter_name);
                                image_caption = detail.getString(Constant.image_caption);
                                channel = detail.getString(Constant.kanal);
                                //Get detail article(s)
                                JSONArray content = detail.getJSONArray(Constant.content);
                                if (content.length() > 0) {
                                    for (int i=0; i<content.length(); i++) {
                                        String detailContent = content.getString(i);
                                        pagingContents.add(detailContent);
                                    }
                                }
                                //Set color theme
                                setThemes(channel);
                                //Get list image content
                                JSONArray sliderImageArray = detail.getJSONArray(Constant.content_images);
                                if (sliderImageArray != null) {
                                    for (int i=0; i<sliderImageArray.length(); i++) {
                                        JSONObject objSlider = sliderImageArray.getJSONObject(i);
                                        sliderPhotoUrl = objSlider.getString("src");
                                        sliderTitle = objSlider.getString("title");
                                        sliderContentImages.add(new SliderContentImage(sliderPhotoUrl, sliderTitle));
                                    }
                                }
                                //Get video content
                                JSONArray content_video = detail.getJSONArray(Constant.content_video);
                                JSONObject objVideo = content_video.getJSONObject(0);
                                urlVideo = objVideo.getString("src_1");
                                //Set data to views
                                tvTitleDetail.setText(title);
                                tvDateDetail.setText(date_publish);
                                if (pagingContents.size() > 0) {
                                    setTextViewHTML(tvContentDetail, pagingContents.get(0));
                                    if (pagingContents.size() > 1) {
                                        mPagingButtonLayout.setVisibility(View.VISIBLE);
                                    }
                                }
                                tvContentDetail.setMovementMethod(LinkMovementMethod.getInstance());
                                tvReporterDetail.setText(reporter_name);
                                Picasso.with(ActFromUrl.this).load(image_url).transform(new CropSquareTransformation()).into(ivThumbDetail);
                                //Image content
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
                            setButtonRetry(channel);
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

    private void setTextViewHTML(TextView text, String html) {
        CharSequence sequence = Html.fromHtml(html);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for (URLSpan span : urls) {
            makeLinkClickable(strBuilder, span);
        }
        text.setText(strBuilder);
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span) {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                String url = span.getURL();
                handleClickBodyText(url);
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    private void handleClickBodyText(String mUrl) {
        if (isInternetPresent) {
            if (mUrl.contains(Constant.LINK_YOUTUBE)) {
                moveVideoPage(mUrl);
            } else if (mUrl.contains(Constant.LINK_ARTICLE_VIVA)) {
                if (mUrl.length() > 0) {
                    Bundle bundle = new Bundle();
                    bundle.putString("id", Constant.getArticleViva(mUrl));
                    Intent intent = new Intent(ActFromUrl.this, ActDetailContentDefault.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
                }
            }
        } else {
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
            } else if (mChannel.equalsIgnoreCase("otomotif")) {
                colorDrawable.setColor(getResources().getColor(R.color.color_auto));
                getSupportActionBar().setBackgroundDrawable(colorDrawable);
                getSupportActionBar().setTitle(R.string.label_item_navigation_otomotif);
                btnComment.setBackgroundColor(getResources().getColor(R.color.color_auto));
                progressWheel.setBarColor(getResources().getColor(R.color.color_auto));
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

    private void setButtonRetry(String mChannel) {
        if (mChannel != null) {
            if (mChannel.equalsIgnoreCase("bola") || mChannel.equalsIgnoreCase("sport")) {
                btnRetry.setBackgroundResource(R.drawable.shadow_button_bola);
            } else if (mChannel.equalsIgnoreCase("vivalife")) {
                btnRetry.setBackgroundResource(R.drawable.shadow_button_life);
            } else if (mChannel.equalsIgnoreCase("otomotif")) {
                btnRetry.setBackgroundResource(R.drawable.shadow_button_otomotif);
            } else {
                btnRetry.setBackgroundResource(R.drawable.shadow_button_news);
            }
        }
    }

    private void showPagingNext() {
        pageCount += 1;
        if (pageCount > 0) {
            textPagePrevious.setEnabled(true);
            textPagePrevious.setTextColor(getResources().getColor(R.color.new_base_color));
        }
        if (pageCount < pagingContents.size()) {
            ivThumbDetail.requestFocus();
            setTextViewHTML(tvContentDetail, pagingContents.get(pageCount));
        }
        if (pageCount == pagingContents.size() - 1) {
            textPageNext.setEnabled(false);
            textPageNext.setTextColor(getResources().getColor(R.color.switch_thumb_normal_material_dark));
        }
    }

    private void showPagingPrevious() {
        pageCount -= 1;
        if (pageCount < pagingContents.size() - 1) {
            textPageNext.setEnabled(true);
            textPageNext.setTextColor(getResources().getColor(R.color.new_base_color));
        }
        if (pageCount == 0) {
            ivThumbDetail.requestFocus();
            setTextViewHTML(tvContentDetail, pagingContents.get(pageCount));
            textPagePrevious.setEnabled(false);
            textPagePrevious.setTextColor(getResources().getColor(R.color.switch_thumb_normal_material_dark));
        } else {
            textPagePrevious.setEnabled(true);
            if (pageCount > -1 && pageCount < pagingContents.size()) {
                ivThumbDetail.requestFocus();
                setTextViewHTML(tvContentDetail, pagingContents.get(pageCount));
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
        return separated[0];
    }

}
