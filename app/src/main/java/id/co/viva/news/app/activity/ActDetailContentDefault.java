package id.co.viva.news.app.activity;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

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

import cn.pedant.SweetAlert.SweetAlertDialog;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.adapter.ImageSliderAdapter;
import id.co.viva.news.app.coachmark.CoachmarkBuilder;
import id.co.viva.news.app.coachmark.CoachmarkView;
import id.co.viva.news.app.component.ProgressWheel;
import id.co.viva.news.app.model.Comment;
import id.co.viva.news.app.model.Favorites;
import id.co.viva.news.app.model.SliderContentImage;
import id.co.viva.news.app.model.Video;
import id.co.viva.news.app.services.Analytics;
import id.co.viva.news.app.Constant;
import id.co.viva.news.app.adapter.RelatedAdapter;
import id.co.viva.news.app.model.RelatedArticle;

/**
 * Created by reza on 27/10/14.
 */
public class ActDetailContentDefault extends FragmentActivity
        implements AdapterView.OnItemClickListener, View.OnClickListener {

    private String ids;
    private String title;
    private String channel_id;
    private String kanal;
    private String image_url;
    private String date_publish;
    private String content;
    private String reporter_name;
    private String url_shared;
    private String image_caption;
    private String urlVideo;
    private String widthVideo;
    private String heightVideo;
    private String id;
    private String cachedResponse;
    private String typeFrom;
    private String fromkanal;
    private String shared_url;
    private String sliderPhotoUrl;
    private String sliderTitle;
    private int count = 0;

    private TextView tvTitleDetail;
    private TextView tvDateDetail;
    private TextView tvReporterDetail;
    private TextView tvContentDetail;
    private KenBurnsView ivThumbDetail;
    private TextView tvPreviewCommentUser;
    private TextView tvPreviewCommentContent;
    private LinearLayout layoutCommentPreview;

    private RelativeLayout headerRelated;
    private boolean isInternetPresent = false;
    private TextView tvNoResult;
    private RelatedAdapter adapter;
    private ImageSliderAdapter imageSliderAdapter;
    private ListView listView;
    private ViewPager viewPager;
    private LinePageIndicator linePageIndicator;
    private Analytics analytics;
    private ProgressWheel progressWheel;
    private String favoriteList;

    private ArrayList<Favorites> favoritesArrayList;
    private ArrayList<RelatedArticle> relatedArticleArrayList;
    private ArrayList<Comment> commentArrayList;
    private ArrayList<SliderContentImage> sliderContentImages;
    private ArrayList<Video> videoArrayList;

    private View coachmarkView;
    private CoachmarkView showtips;
    private TextView textLinkVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        typeFrom = intent.getStringExtra("type");
        fromkanal = intent.getStringExtra("kanal");
        shared_url = intent.getStringExtra("shared_url");

        isInternetPresent = Global.getInstance(this).
                getConnectionStatus().isConnectingToInternet();

        analytics = new Analytics(this);
        if(typeFrom != null) {
            if(typeFrom.equalsIgnoreCase("search")) {
                analytics.getAnalyticByATInternet(Constant.FROM_SEARCH_RESULT_DETAIL_CONTENT + fromkanal.toUpperCase());
                analytics.getAnalyticByGoogleAnalytic(Constant.FROM_SEARCH_RESULT_DETAIL_CONTENT + fromkanal.toUpperCase());
            } else if(typeFrom.equalsIgnoreCase("editor_choice")) {
                analytics.getAnalyticByATInternet(Constant.FROM_EDITOR_CHOICE + "EDITOR_CHOICE");
                analytics.getAnalyticByGoogleAnalytic(Constant.FROM_EDITOR_CHOICE + "EDITOR_CHOICE");
            }
        } else {
            analytics.getAnalyticByATInternet(Constant.FROM_RELATED_ARTICLE_DETAIL_CONTENT + "RELATED_ARTICLE");
            analytics.getAnalyticByGoogleAnalytic(Constant.FROM_RELATED_ARTICLE_DETAIL_CONTENT + "RELATED_ARTICLE");
        }

        setContentView(R.layout.item_detail_content_default);

        setHeaderActionbar();

        coachmarkView = findViewById(R.id.coachmark_detail);
        viewPager = (ViewPager) findViewById(R.id.horizontal_list);
        viewPager.setVisibility(View.GONE);

        linePageIndicator = (LinePageIndicator)findViewById(R.id.indicator);
        linePageIndicator.setVisibility(View.GONE);

        progressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        headerRelated = (RelativeLayout) findViewById(R.id.header_related_article);
        headerRelated.setVisibility(View.GONE);
        tvNoResult = (TextView) findViewById(R.id.text_no_result_detail_content);
        tvNoResult.setVisibility(View.GONE);

        listView = (ListView) findViewById(R.id.list_related_article_default);
        listView.setOnItemClickListener(this);

        layoutCommentPreview = (LinearLayout) findViewById(R.id.layout_preview_comment_list);
        layoutCommentPreview.setOnClickListener(this);
        layoutCommentPreview.setVisibility(View.GONE);
        tvPreviewCommentContent = (TextView) findViewById(R.id.text_preview_comment_content);
        tvPreviewCommentUser = (TextView) findViewById(R.id.text_preview_comment_user);

        relatedArticleArrayList = new ArrayList<RelatedArticle>();
        commentArrayList = new ArrayList<Comment>();
        sliderContentImages = new ArrayList<SliderContentImage>();
        videoArrayList = new ArrayList<Video>();

        tvTitleDetail = (TextView) findViewById(R.id.title_detail_content_default);
        tvDateDetail = (TextView) findViewById(R.id.date_detail_content_default);
        tvReporterDetail = (TextView) findViewById(R.id.reporter_detail_content_default);
        tvContentDetail = (TextView) findViewById(R.id.content_detail_content_default);

        ivThumbDetail = (KenBurnsView) findViewById(R.id.thumb_detail_content_default);
        ivThumbDetail.setOnClickListener(this);
        ivThumbDetail.setFocusable(true);
        ivThumbDetail.setFocusableInTouchMode(true);
        ivThumbDetail.requestFocus();

        textLinkVideo = (TextView)findViewById(R.id.text_move_video);
        textLinkVideo.setOnClickListener(this);
        textLinkVideo.setVisibility(View.GONE);

        showCoachMark();

        if(isInternetPresent) {
            StringRequest request = new StringRequest(Request.Method.GET, Constant.NEW_DETAIL + "/id/" + id,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String volleyResponse) {
                            Log.i(Constant.TAG, volleyResponse);
                            try {
                                JSONObject jsonObject = new JSONObject(volleyResponse);
                                JSONObject response = jsonObject.getJSONObject(Constant.response);
                                JSONObject detail = response.getJSONObject(Constant.detail);
                                ids = detail.getString(Constant.id);
                                channel_id = detail.getString(Constant.channel_id);
                                kanal = detail.getString(Constant.kanal);
                                title = detail.getString(Constant.title);
                                image_url = detail.getString(Constant.image_url);
                                date_publish = detail.getString(Constant.date_publish);
                                content = detail.getString(Constant.content);
                                reporter_name = detail.getString(Constant.reporter_name);
                                url_shared = detail.getString(Constant.url);
                                image_caption = detail.getString(Constant.image_caption);

                                JSONArray sliderImageArray = detail.getJSONArray(Constant.content_images);
                                if(sliderImageArray != null) {
                                    for(int i=0; i<sliderImageArray.length(); i++) {
                                        JSONObject objSlider = sliderImageArray.getJSONObject(i);
                                        sliderPhotoUrl = objSlider.getString("src");
                                        sliderTitle = objSlider.getString("title");
                                        sliderContentImages.add(new SliderContentImage(sliderPhotoUrl, sliderTitle));
                                    }
                                }

                                JSONArray content_video = detail.getJSONArray(Constant.content_video);
                                if(content_video != null && content_video.length() > 0) {
                                    for(int i=0; i<content_video.length(); i++) {
                                        JSONObject objVideo = content_video.getJSONObject(i);
                                        urlVideo = objVideo.getString("src_1");
                                        widthVideo = objVideo.getString("src_2");
                                        heightVideo = objVideo.getString("src_3");
                                        videoArrayList.add(new Video(urlVideo, widthVideo, heightVideo));
                                    }
                                }

                                JSONArray related_article = response.getJSONArray(Constant.related_article);
                                for(int i=0; i<related_article.length(); i++) {
                                    JSONObject objRelated = related_article.getJSONObject(i);
                                    String id = objRelated.getString(Constant.id);
                                    String article_id = objRelated.getString(Constant.article_id);
                                    String related_article_id = objRelated.getString(Constant.related_article_id);
                                    String related_title = objRelated.getString(Constant.related_title);
                                    String related_channel_level_1_id = objRelated.getString(Constant.related_channel_level_1_id);
                                    String channel_id = objRelated.getString(Constant.channel_id);
                                    String related_date_publish = objRelated.getString(Constant.related_date_publish);
                                    String image = objRelated.getString(Constant.image);
                                    String kanal = objRelated.getString(Constant.kanal);
                                    String shared_url = objRelated.getString(Constant.url);
                                    relatedArticleArrayList.add(new RelatedArticle(id, article_id, related_article_id, related_title,
                                            related_channel_level_1_id, channel_id, related_date_publish, image, kanal, shared_url));
                                    Log.i(Constant.TAG, "RELATED ARTICLE : " + relatedArticleArrayList.get(i).getRelated_title());
                                }

                                JSONArray comment_list = response.getJSONArray(Constant.comment_list);
                                for(int i=0; i<comment_list.length(); i++) {
                                    JSONObject objRelated = comment_list.getJSONObject(i);
                                    String id = objRelated.getString(Constant.id);
                                    String name = objRelated.getString(Constant.name);
                                    String comment_text = objRelated.getString(Constant.comment_text);
                                    commentArrayList.add(new Comment(id, null, name, null, comment_text, null, null, null));
                                    Log.i(Constant.TAG, "COMMENTS PREVIEW : " + commentArrayList.get(i).getComment_text());
                                }

                                tvTitleDetail.setText(title);
                                tvDateDetail.setText(date_publish);
                                tvContentDetail.setText(Html.fromHtml(content).toString());
                                tvContentDetail.setMovementMethod(LinkMovementMethod.getInstance());
                                tvReporterDetail.setText(reporter_name);
                                Picasso.with(ActDetailContentDefault.this).load(image_url).into(ivThumbDetail);

                                if(sliderContentImages.size() > 0) {
                                    imageSliderAdapter = new ImageSliderAdapter(getSupportFragmentManager(), sliderContentImages);
                                    viewPager.setAdapter(imageSliderAdapter);
                                    viewPager.setCurrentItem(0);
                                    imageSliderAdapter.notifyDataSetChanged();
                                    linePageIndicator.setViewPager(viewPager);
                                    viewPager.setVisibility(View.VISIBLE);
                                    linePageIndicator.setVisibility(View.VISIBLE);
                                }

                                if(relatedArticleArrayList.size() > 0 || !relatedArticleArrayList.isEmpty()) {
                                    adapter = new RelatedAdapter(ActDetailContentDefault.this, relatedArticleArrayList);
                                    listView.setAdapter(adapter);
                                    Constant.setListViewHeightBasedOnChildren(listView);
                                    adapter.notifyDataSetChanged();
                                    headerRelated.setVisibility(View.VISIBLE);
                                    if(fromkanal != null) {
                                        if(fromkanal.equalsIgnoreCase("bola")) {
                                            headerRelated.setBackgroundResource(R.color.color_bola);
                                        } else if(fromkanal.equalsIgnoreCase("vivalife")) {
                                            headerRelated.setBackgroundResource(R.color.color_life);
                                        } else {
                                            headerRelated.setBackgroundResource(R.color.color_news);
                                        }
                                    } else {
                                        headerRelated.setBackgroundResource(R.color.header_grey);
                                    }
                                }

                                if(commentArrayList.size() > 0) {
                                    layoutCommentPreview.setVisibility(View.VISIBLE);

                                    Thread thread = new Thread() {
                                        @Override
                                        public void run() {
                                            try {
                                                while (true) {
                                                    Thread.sleep(3000);
                                                    if(ActDetailContentDefault.this == null) {
                                                        return;
                                                    }
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            tvPreviewCommentUser.setText(commentArrayList.get(count).getUsername());
                                                            tvPreviewCommentContent.setText(commentArrayList.get(count).getComment_text());
                                                            count++;
                                                            if (count >= commentArrayList.size()) {
                                                                count = 0;
                                                            }
                                                        }
                                                    });
                                                }
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    };
                                    thread.start();
                                }

                                progressWheel.setVisibility(View.GONE);

                                if(urlVideo.length() > 0) {
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
                            volleyError.getMessage();
                            onBackPressed();
                            Toast.makeText(ActDetailContentDefault.this, R.string.title_no_connection, Toast.LENGTH_SHORT).show();
                        }
                    });
            request.setShouldCache(true);
            request.setRetryPolicy(new DefaultRetryPolicy(
                    Constant.TIME_OUT,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Global.getInstance(this).getRequestQueue().getCache().invalidate(Constant.NEW_DETAIL + "/id/" + id, true);
            Global.getInstance(this).getRequestQueue().getCache().get(Constant.NEW_DETAIL + "/id/" + id);
            Global.getInstance(this).addToRequestQueue(request, Constant.JSON_REQUEST);
        } else {
            if(Global.getInstance(this).getRequestQueue().getCache().get(Constant.NEW_DETAIL + "/id/" + id) != null) {
                cachedResponse = new String(Global.getInstance(this).
                        getRequestQueue().getCache().get(Constant.NEW_DETAIL + "/id/" + id).data);
                Log.i(Constant.TAG, "CONTENT DETAIL CACHED : " + cachedResponse);
                try {
                    JSONObject jsonObject = new JSONObject(cachedResponse);
                    JSONObject response = jsonObject.getJSONObject(Constant.response);
                    JSONObject detail = response.getJSONObject(Constant.detail);
                    ids = detail.getString(Constant.id);
                    channel_id = detail.getString(Constant.channel_id);
                    kanal = detail.getString(Constant.kanal);
                    title = detail.getString(Constant.title);
                    image_url = detail.getString(Constant.image_url);
                    date_publish = detail.getString(Constant.date_publish);
                    content = detail.getString(Constant.content);
                    reporter_name = detail.getString(Constant.reporter_name);
                    url_shared = detail.getString(Constant.url);
                    image_caption = detail.getString(Constant.image_caption);

                    JSONArray sliderImageArray = detail.getJSONArray(Constant.content_images);
                    if(sliderImageArray != null) {
                        for(int i=0; i<sliderImageArray.length(); i++) {
                            JSONObject objSlider = sliderImageArray.getJSONObject(i);
                            sliderPhotoUrl = objSlider.getString("src");
                            sliderTitle = objSlider.getString("title");
                            sliderContentImages.add(new SliderContentImage(sliderPhotoUrl, sliderTitle));
                        }
                    }

                    JSONArray related_article = response.getJSONArray(Constant.related_article);
                    for(int i=0; i<related_article.length(); i++) {
                        JSONObject objRelated = related_article.getJSONObject(i);
                        String id = objRelated.getString(Constant.id);
                        String article_id = objRelated.getString(Constant.article_id);
                        String related_article_id = objRelated.getString(Constant.related_article_id);
                        String related_title = objRelated.getString(Constant.related_title);
                        String related_channel_level_1_id = objRelated.getString(Constant.related_channel_level_1_id);
                        String channel_id = objRelated.getString(Constant.channel_id);
                        String related_date_publish = objRelated.getString(Constant.related_date_publish);
                        String image = objRelated.getString(Constant.image);
                        String kanal = objRelated.getString(Constant.kanal);
                        String shared_url = objRelated.getString(Constant.url);
                        relatedArticleArrayList.add(new RelatedArticle(id, article_id, related_article_id, related_title,
                                related_channel_level_1_id, channel_id, related_date_publish, image, kanal, shared_url));
                        Log.i(Constant.TAG, "RELATED ARTICLE CACHED : " + relatedArticleArrayList.get(i).getRelated_title());
                    }

                    JSONArray comment_list = response.getJSONArray(Constant.comment_list);
                    for(int i=0; i<comment_list.length(); i++) {
                        JSONObject objRelated = comment_list.getJSONObject(i);
                        String id = objRelated.getString(Constant.id);
                        String name = objRelated.getString(Constant.name);
                        String comment_text = objRelated.getString(Constant.comment_text);
                        commentArrayList.add(new Comment(id, null, name, null, comment_text, null, null, null));
                        Log.i(Constant.TAG, "COMMENTS PREVIEW : " + commentArrayList.get(i).getComment_text());
                    }

                    tvTitleDetail.setText(title);
                    tvDateDetail.setText(date_publish);
                    tvContentDetail.setText(Html.fromHtml(content).toString());
                    tvContentDetail.setMovementMethod(LinkMovementMethod.getInstance());
                    tvReporterDetail.setText(reporter_name);
                    Picasso.with(this).load(image_url).into(ivThumbDetail);

                    if(sliderContentImages.size() > 0) {
                        imageSliderAdapter = new ImageSliderAdapter(getSupportFragmentManager(), sliderContentImages);
                        viewPager.setAdapter(imageSliderAdapter);
                        viewPager.setCurrentItem(0);
                        imageSliderAdapter.notifyDataSetChanged();
                        linePageIndicator.setViewPager(viewPager);
                        viewPager.setVisibility(View.VISIBLE);
                        linePageIndicator.setVisibility(View.VISIBLE);
                    }

                    if(relatedArticleArrayList.size() > 0 || !relatedArticleArrayList.isEmpty()) {
                        adapter = new RelatedAdapter(this, relatedArticleArrayList);
                        listView.setAdapter(adapter);
                        Constant.setListViewHeightBasedOnChildren(listView);
                        adapter.notifyDataSetChanged();
                        headerRelated.setVisibility(View.VISIBLE);
                        if(fromkanal != null) {
                            if(fromkanal.equalsIgnoreCase("bola")) {
                                headerRelated.setBackgroundResource(R.color.color_bola);
                            } else if(fromkanal.equalsIgnoreCase("vivalife")) {
                                headerRelated.setBackgroundResource(R.color.color_life);
                            } else {
                                headerRelated.setBackgroundResource(R.color.color_news);
                            }
                        } else {
                            headerRelated.setBackgroundResource(R.color.header_grey);
                        }
                    }

                    if(commentArrayList.size() > 0) {
                        layoutCommentPreview.setVisibility(View.VISIBLE);
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    while (true) {
                                        Thread.sleep(3000);
                                        if(ActDetailContentDefault.this == null) {
                                            return;
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                tvPreviewCommentUser.setText(commentArrayList.get(count).getUsername());
                                                tvPreviewCommentContent.setText(commentArrayList.get(count).getComment_text());
                                                count++;
                                                if (count >= commentArrayList.size()) {
                                                    count = 0;
                                                }
                                            }
                                        });
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        thread.start();
                    }

                    progressWheel.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(this, R.string.title_no_connection, Toast.LENGTH_SHORT).show();
                progressWheel.setVisibility(View.GONE);
                tvNoResult.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setHeaderActionbar() {
        ColorDrawable colorDrawable = new ColorDrawable();
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setIcon(R.drawable.logo_viva_coid_second);
        if(fromkanal != null) {
            if(fromkanal.equalsIgnoreCase("bola")) {
                colorDrawable.setColor(getResources().getColor(R.color.color_bola));
                getActionBar().setBackgroundDrawable(colorDrawable);
            } else if(fromkanal.equalsIgnoreCase("vivalife")) {
                colorDrawable.setColor(getResources().getColor(R.color.color_life));
                getActionBar().setBackgroundDrawable(colorDrawable);
            } else {
                colorDrawable.setColor(getResources().getColor(R.color.color_news));
                getActionBar().setBackgroundDrawable(colorDrawable);
            }
        } else {
            colorDrawable.setColor(getResources().getColor(R.color.header_headline_terbaru_new));
            getActionBar().setBackgroundDrawable(colorDrawable);
        }
    }

    private void showCoachMark() {
        if(Global.getInstance(this).getSharedPreferences(this).getBoolean(Constant.FIRST_INSTALL_DETAIL, true)) {
            RelativeLayout relativeLayout = new RelativeLayout(this);
            relativeLayout.setLayoutParams(new ViewGroup.LayoutParams(70, 70));
            ((RelativeLayout) coachmarkView).addView(relativeLayout);
            showtips = new CoachmarkBuilder(this)
                    .setTarget(relativeLayout, getWidthFocus(), getHeightFocus(), 60)
                    .setTitle(getResources().getString(R.string.label_action_menu))
                    .setDescription(getResources().getString(R.string.label_action_menu_desc))
                    .build();
            showtips.show(this);
            Global.getInstance(this).getSharedPreferences(this).
                    edit().putBoolean(Constant.FIRST_INSTALL_DETAIL, false).commit();
        }
    }

    private int getHeightFocus() {
        int actionBarHeight = 0;
        int heightFocus;
        TypedValue typedValue = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(typedValue.data,getResources().getDisplayMetrics());
        }
        heightFocus = 0 - (actionBarHeight / 2);
        return heightFocus;
    }

    private int getWidthFocus() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return width - 75;
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
        bundle.putString("article_id", ids);
        bundle.putString("type_kanal", kanal);
        Intent intent = new Intent(this, ActComment.class);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    private void moveRatingPage() {
        Bundle bundles = new Bundle();
        bundles.putString("imageurl", image_url);
        bundles.putString("title", title);
        bundles.putString("article_id", ids);
        bundles.putString("type_kanal", kanal);
        Intent intents = new Intent(this, ActRating.class);
        intents.putExtras(bundles);
        startActivity(intents);
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    private void doFavorites() {
        favoriteList = Global.getInstance(this).getSharedPreferences(this)
                .getString(Constant.FAVORITES_LIST, "");
        if(favoriteList == null || favoriteList.length() <= 0) {
            favoritesArrayList = Global.getInstance(this).getFavoritesList();
        } else {
            favoritesArrayList = Global.getInstance(this).getInstanceGson().
                    fromJson(favoriteList, Global.getInstance(this).getType());
        }
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getResources().getString(R.string.label_favorite_navigation_title))
                .setContentText(title)
                .setConfirmText(getResources().getString(R.string.label_favorite_ok))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        favoritesArrayList.add(new Favorites(ids, title, channel_id, kanal,
                                image_url, date_publish, reporter_name, url_shared, content, image_caption, sliderContentImages));
                        String favorite = Global.getInstance(ActDetailContentDefault.this).getInstanceGson().toJson(favoritesArrayList);
                        Global.getInstance(ActDetailContentDefault.this).getDefaultEditor().putString(Constant.FAVORITES_LIST, favorite);
                        Global.getInstance(ActDetailContentDefault.this).getDefaultEditor().putInt(Constant.FAVORITES_LIST_SIZE, favoritesArrayList.size());
                        Global.getInstance(ActDetailContentDefault.this).getDefaultEditor().commit();
                        sDialog.setTitleText(getResources().getString(R.string.label_favorite_navigation_title_confirm))
                                .setContentText(getResources().getString(R.string.label_favorite_navigation_content))
                                .setConfirmText(getResources().getString(R.string.label_favorite_ok))
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    }
                })
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.subaction_rate:
                moveRatingPage();
                return true;
            case R.id.subaction_comments:
                moveCommentPage();
                return true;
            case R.id.subaction_favorites:
                doFavorites();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_frag_detail, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        ShareActionProvider myShareActionProvider = (ShareActionProvider) item.getActionProvider();
        Intent myIntent = new Intent();
        myIntent.setAction(Intent.ACTION_SEND);
        myIntent.putExtra(Intent.EXTRA_TEXT, shared_url);
        myIntent.setType("text/plain");
        myShareActionProvider.setShareIntent(myIntent);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if(relatedArticleArrayList.size() > 0) {
            RelatedArticle relatedArticles = relatedArticleArrayList.get(position);
            Log.i(Constant.TAG, "ID : " + relatedArticles.getId());
            Bundle bundle = new Bundle();
            bundle.putString("id", relatedArticles.getRelated_article_id());
            bundle.putString("type", typeFrom);
            bundle.putString("kanal", relatedArticles.getKanal());
            bundle.putString("shared_url", relatedArticles.getShared_url());
            Intent intent = new Intent(this, ActDetailContentDefault.class);
            intent.putExtras(bundle);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
        }
    }

    private void toDetailThumbnail() {
        Bundle bundle = new Bundle();
        bundle.putString("photoUrl", image_url);
        bundle.putString("image_caption", image_caption);
        Intent intent = new Intent(this, ActDetailPhotoThumb.class);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.thumb_detail_content_default) {
            if(image_url.length() > 0) {
                toDetailThumbnail();
            }
        } else if(view.getId() == R.id.layout_preview_comment_list) {
            moveCommentPage();
        } else if(view.getId() == R.id.text_move_video) {
            moveVideoPage();
        }
    }

}
