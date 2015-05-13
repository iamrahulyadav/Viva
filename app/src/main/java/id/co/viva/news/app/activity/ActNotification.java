package id.co.viva.news.app.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.LinePageIndicator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.adapter.ImageSliderAdapter;
import id.co.viva.news.app.adapter.RelatedAdapter;
import id.co.viva.news.app.ads.AdsConfig;
import id.co.viva.news.app.component.CropSquareTransformation;
import id.co.viva.news.app.component.ProgressWheel;
import id.co.viva.news.app.model.Ads;
import id.co.viva.news.app.model.Favorites;
import id.co.viva.news.app.model.RelatedArticle;
import id.co.viva.news.app.model.SliderContentImage;
import id.co.viva.news.app.model.Video;
import id.co.viva.news.app.services.Analytics;

/**
 * Created by reza on 20/01/15.
 */
public class ActNotification extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    //Parameter from notification
    private String kanalFromNotification;
    private String idFromNotification;

    //Internet Flag
    private boolean isInternetPresent = false;

    //Data List
    private ArrayList<SliderContentImage> sliderContentImages;
    private ArrayList<Favorites> favoritesArrayList;
    private ArrayList<RelatedArticle> relatedArticleArrayList;
    private ArrayList<Video> videoArrayList;
    private ArrayList<Ads> adsArrayList;

    //All Views
    private TextView tvTitleDetail;
    private TextView tvDateDetail;
    private TextView tvReporterDetail;
    private TextView tvContentDetail;
    private ImageView ivThumbDetail;
    private RelativeLayout headerRelated;
    private RelatedAdapter adapter;
    private TextView textLinkVideo;
    private ListView listView;
    private ImageSliderAdapter imageSliderAdapter;
    private ViewPager viewPager;
    private LinePageIndicator linePageIndicator;
    private ProgressWheel progressWheel;
    private RippleView rippleView;
    private Button btnRetry;
    private TextView tvNoResult;

    //Analytic
    private Analytics analytics;

    //Ads AdMob DFP
    private LinearLayout mParentLayout;
    private PublisherAdView publisherAdViewBottom;
    private PublisherAdView publisherAdViewTop;

    //JSON Data
    private String title;
    private String image_url;
    private String date_publish;
    private String content;
    private String reporter_name;
    private String image_caption;
    private String sliderPhotoUrl;
    private String sliderTitle;
    private String urlVideo;
    private String shared_url;
    private String channel_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get some param from notification
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idFromNotification = extras.containsKey(Constant.id) ? extras.getString(Constant.id) : "";
            kanalFromNotification = extras.containsKey(Constant.kanal) ? extras.getString(Constant.kanal) : "";
        }

        setContentView(R.layout.act_notification);

        //Define All Views
        defineViews();

        //Set Color Theme
        setHeaderActionbar(kanalFromNotification);

        //Checking current internet connection
        isInternetPresent = Global.getInstance(this)
                .getConnectionStatus().isConnectingToInternet();

        //Fetch data
        getContent();
    }

    private void defineViews() {
        //Add ads if exists
        mParentLayout = (LinearLayout) findViewById(R.id.parent_layout);

        viewPager = (ViewPager) findViewById(R.id.horizontal_list);
        viewPager.setVisibility(View.GONE);

        linePageIndicator = (LinePageIndicator) findViewById(R.id.indicator);
        linePageIndicator.setVisibility(View.GONE);

        progressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        btnRetry = (Button) findViewById(R.id.btn_retry);

        rippleView = (RippleView) findViewById(R.id.layout_ripple_view_detail_subkanal);
        rippleView.setVisibility(View.GONE);
        rippleView.setOnClickListener(this);

        tvNoResult = (TextView) findViewById(R.id.text_no_result_detail_content);
        tvNoResult.setVisibility(View.GONE);

        relatedArticleArrayList = new ArrayList<>();
        sliderContentImages = new ArrayList<>();
        videoArrayList = new ArrayList<>();
        adsArrayList = new ArrayList<>();

        listView = (ListView) findViewById(R.id.list_related_article_notification);
        listView.setOnItemClickListener(this);

        headerRelated = (RelativeLayout) findViewById(R.id.header_related_article);
        headerRelated.setVisibility(View.GONE);

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

    @Override
    public void onResume() {
        super.onResume();
        if (publisherAdViewBottom != null) {
            publisherAdViewBottom.resume();
        }
        if (publisherAdViewTop != null) {
            publisherAdViewTop.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (publisherAdViewBottom != null) {
            publisherAdViewBottom.pause();
        }
        if (publisherAdViewTop != null) {
            publisherAdViewTop.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (publisherAdViewBottom != null) {
            publisherAdViewBottom.destroy();
        }
        if (publisherAdViewTop != null) {
            publisherAdViewTop.destroy();
        }
    }

    private void goDetailPhoto() {
        if (image_url != null) {
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

    private void moveVideoPage(String mUrl) {
        Bundle bundle = new Bundle();
        bundle.putString("urlVideo", mUrl);
        Intent intent = new Intent(this, ActVideo.class);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    private void moveBrowserPage(String url) {
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        Intent intent = new Intent(this, ActBrowser.class);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.layout_ripple_view_detail_subkanal) {
            getContent();
        } else if(view.getId() == R.id.thumb_detail_content) {
            goDetailPhoto();
        } else if(view.getId() == R.id.text_move_video) {
            moveVideoPage(urlVideo);
        }
    }

    private void getContent() {
        if (isInternetPresent) {
            StringRequest request = new StringRequest(Request.Method.GET, Constant.NEW_DETAIL + "id/" + idFromNotification,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String volleyResponse) {
                            Log.i(Constant.TAG, volleyResponse);
                            try {
                                JSONObject jsonObject = new JSONObject(volleyResponse);
                                JSONObject response = jsonObject.getJSONObject(Constant.response);
                                //Get content detail
                                JSONObject detail = response.getJSONObject(Constant.detail);
                                title = detail.getString(Constant.title);
                                image_url = detail.getString(Constant.image_url);
                                date_publish = detail.getString(Constant.date_publish);
                                content = detail.getString(Constant.content);
                                reporter_name = detail.getString(Constant.reporter_name);
                                image_caption = detail.getString(Constant.image_caption);
                                shared_url = detail.getString(Constant.url);
                                channel_id = detail.getString((Constant.channel_id));
                                //Get image content
                                JSONArray sliderImageArray = detail.getJSONArray(Constant.content_images);
                                if(sliderImageArray != null) {
                                    for(int i=0; i<sliderImageArray.length(); i++) {
                                        JSONObject objSlider = sliderImageArray.getJSONObject(i);
                                        sliderPhotoUrl = objSlider.getString("src");
                                        sliderTitle = objSlider.getString("title");
                                        sliderContentImages.add(new SliderContentImage(sliderPhotoUrl, sliderTitle));
                                    }
                                }
                                //Get video content
                                JSONArray content_video = detail.getJSONArray(Constant.content_video);
                                if(content_video != null && content_video.length() > 0) {
                                    for(int i=0; i<content_video.length(); i++) {
                                        JSONObject objVideo = content_video.getJSONObject(i);
                                        urlVideo = objVideo.getString("src_1");
                                        videoArrayList.add(new Video(urlVideo, null, null));
                                    }
                                }
                                //Get related article
                                JSONArray related_article = response.getJSONArray(Constant.related_article);
                                for (int i=0; i<related_article.length(); i++) {
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
                                }
                                //Get ads list
                                JSONArray ad_list = response.getJSONArray(Constant.adses);
                                if (ad_list.length() > 0) {
                                    for (int i=0; i<ad_list.length(); i++) {
                                        JSONObject jsonAds = ad_list.getJSONObject(i);
                                        String name = jsonAds.getString(Constant.name);
                                        int position = jsonAds.getInt(Constant.position);
                                        int type = jsonAds.getInt(Constant.type);
                                        String unit_id = jsonAds.getString(Constant.unit_id);
                                        adsArrayList.add(new Ads(name, type, position, unit_id));
                                    }
                                }
                                //Send Analytic
                                getAnalytics(title);
                                //Set data to view
                                tvTitleDetail.setText(title);
                                tvDateDetail.setText(date_publish);
                                setTextViewHTML(tvContentDetail, content);
                                tvReporterDetail.setText(reporter_name);
                                Picasso.with(ActNotification.this).load(image_url)
                                        .transform(new CropSquareTransformation()).into(ivThumbDetail);
                                //Checking for image content
                                if (sliderContentImages.size() > 0) {
                                    imageSliderAdapter = new ImageSliderAdapter(getSupportFragmentManager(), sliderContentImages);
                                    viewPager.setAdapter(imageSliderAdapter);
                                    viewPager.setCurrentItem(0);
                                    imageSliderAdapter.notifyDataSetChanged();
                                    linePageIndicator.setViewPager(viewPager);
                                    viewPager.setVisibility(View.VISIBLE);
                                    linePageIndicator.setVisibility(View.VISIBLE);
                                }
                                //Checking for related article
                                if (relatedArticleArrayList.size() > 0 || !relatedArticleArrayList.isEmpty()) {
                                    adapter = new RelatedAdapter(ActNotification.this, relatedArticleArrayList);
                                    listView.setAdapter(adapter);
                                    Constant.setListViewHeightBasedOnChildren(listView);
                                    adapter.notifyDataSetChanged();
                                    headerRelated.setVisibility(View.VISIBLE);
                                    if (kanalFromNotification != null) {
                                        if (kanalFromNotification.equalsIgnoreCase("bola")) {
                                            headerRelated.setBackgroundResource(R.color.color_bola);
                                        } else if (kanalFromNotification.equalsIgnoreCase("vivalife")) {
                                            headerRelated.setBackgroundResource(R.color.color_life);
                                        } else {
                                            headerRelated.setBackgroundResource(R.color.color_news);
                                        }
                                    } else {
                                        headerRelated.setBackgroundResource(R.color.header_grey);
                                    }
                                }
                                //Hide 'Retry' button
                                if (rippleView.getVisibility() == View.VISIBLE) {
                                    rippleView.setVisibility(View.GONE);
                                }
                                //Hide progress bar
                                progressWheel.setVisibility(View.GONE);
                                //For updating content
                                invalidateOptionsMenu();
                                //Show url video if it exist
                                if (urlVideo.length() > 0) {
                                    textLinkVideo.setVisibility(View.VISIBLE);
                                }
                                //Show Ads
                                showAds();
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
                            setButtonRetry(kanalFromNotification);

                        }
                    });
            request.setShouldCache(true);
            request.setRetryPolicy(new DefaultRetryPolicy(
                    Constant.TIME_OUT_REGISTRATION,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Global.getInstance(ActNotification.this).getRequestQueue()
                    .getCache().invalidate(Constant.NEW_DETAIL + "id/" + idFromNotification, true);
            Global.getInstance(ActNotification.this)
                    .getRequestQueue().getCache().get(Constant.NEW_DETAIL + "id/" + idFromNotification);
            Global.getInstance(ActNotification.this).addToRequestQueue(request, Constant.JSON_REQUEST);
        } else {
            if (tvNoResult.getVisibility() == View.VISIBLE) {
                tvNoResult.setVisibility(View.GONE);
            } else {
                tvNoResult.setVisibility(View.VISIBLE);
                progressWheel.setVisibility(View.GONE);
            }
            Toast.makeText(this, R.string.title_no_connection, Toast.LENGTH_SHORT).show();
        }
    }

    private void setHeaderActionbar(String fromkanal) {
        ColorDrawable colorDrawable = new ColorDrawable();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if(fromkanal != null) {
            if(fromkanal.equalsIgnoreCase("bola")) {
                colorDrawable.setColor(getResources().getColor(R.color.color_bola));
                getSupportActionBar().setBackgroundDrawable(colorDrawable);
                getSupportActionBar().setTitle(R.string.label_item_navigation_bola);
                progressWheel.setBarColor(getResources().getColor(R.color.color_bola));
            } else if(fromkanal.equalsIgnoreCase("vivalife")) {
                colorDrawable.setColor(getResources().getColor(R.color.color_life));
                getSupportActionBar().setBackgroundDrawable(colorDrawable);
                getSupportActionBar().setTitle(R.string.label_item_navigation_life);
                progressWheel.setBarColor(getResources().getColor(R.color.color_life));
            } else {
                colorDrawable.setColor(getResources().getColor(R.color.color_news));
                getSupportActionBar().setBackgroundDrawable(colorDrawable);
                getSupportActionBar().setTitle(R.string.label_item_navigation_news);
                progressWheel.setBarColor(getResources().getColor(R.color.color_news));
            }
        } else {
            colorDrawable.setColor(getResources().getColor(R.color.header_headline_terbaru_new));
            getSupportActionBar().setBackgroundDrawable(colorDrawable);
            progressWheel.setBarColor(getResources().getColor(R.color.blue));
        }
    }

    private void setButtonRetry(String kanals) {
        if(kanals != null) {
            if(kanals.equalsIgnoreCase("bola")) {
                btnRetry.setBackgroundResource(R.drawable.shadow_button_bola);
            } else if(kanals.equalsIgnoreCase("vivalife")) {
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

    private void moveCommentPage() {
        Bundle bundle = new Bundle();
        bundle.putString("imageurl", image_url);
        bundle.putString("title", title);
        bundle.putString("article_id", idFromNotification);
        bundle.putString("type_kanal", kanalFromNotification);
        Intent intent = new Intent(this, ActComment.class);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    private void moveRatingPage() {
        Bundle bundles = new Bundle();
        bundles.putString("imageurl", image_url);
        bundles.putString("title", title);
        bundles.putString("article_id", idFromNotification);
        bundles.putString("type_kanal", kanalFromNotification);
        Intent intents = new Intent(this, ActRating.class);
        intents.putExtras(bundles);
        startActivity(intents);
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    private void doFavorites() {
        String favoriteList = Global.getInstance(this).getSharedPreferences(this)
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
                        favoritesArrayList.add(new Favorites(idFromNotification, title, channel_id, kanalFromNotification,
                                image_url, date_publish, reporter_name, shared_url, content, image_caption, sliderContentImages));
                        String favorite = Global.getInstance(ActNotification.this).getInstanceGson().toJson(favoritesArrayList);
                        Global.getInstance(ActNotification.this).getDefaultEditor().putString(Constant.FAVORITES_LIST, favorite);
                        Global.getInstance(ActNotification.this).getDefaultEditor().putInt(Constant.FAVORITES_LIST_SIZE, favoritesArrayList.size());
                        Global.getInstance(ActNotification.this).getDefaultEditor().commit();
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
                goFirstFlow();
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(shared_url == null || shared_url.length() < 1) {
            try {
                if (Global.getInstance(this).getRequestQueue().getCache().get(Constant.NEW_DETAIL + "id/" + idFromNotification) != null) {
                    String cachedResponse = new String(Global.getInstance(this).
                            getRequestQueue().getCache().get(Constant.NEW_DETAIL + "/id/" + idFromNotification).data);
                    JSONObject jsonObject = new JSONObject(cachedResponse);
                    JSONObject response = jsonObject.getJSONObject(Constant.response);
                    JSONObject detail = response.getJSONObject(Constant.detail);
                    shared_url = detail.getString(Constant.url);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_frag_detail, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        android.support.v7.widget.ShareActionProvider myShareActionProvider =
                (android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent myIntent = new Intent();
        myIntent.setAction(Intent.ACTION_SEND);
        myIntent.putExtra(Intent.EXTRA_TEXT, shared_url);
        myIntent.setType("text/plain");
        myShareActionProvider.setShareIntent(myIntent);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goFirstFlow();
    }

    private void getAnalytics(String title) {
        analytics = new Analytics(this);
        analytics.getAnalyticByATInternetFromNotification(Constant.ARTICLE_FROM_NOTIFICATION + "_"
                + title.toUpperCase(), "Push Notifications::" + Constant.ARTICLE_FROM_NOTIFICATION + "_" + title.toUpperCase());
        analytics.getAnalyticByGoogleAnalytic(Constant.ARTICLE_FROM_NOTIFICATION + "_" + title.toUpperCase());
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        ListView listview = (ListView) adapterView;
        if (listview.getId() == R.id.list_related_article_notification) {
            if (relatedArticleArrayList.size() > 0) {
                RelatedArticle relatedArticles = relatedArticleArrayList.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("id", relatedArticles.getRelated_article_id());
                bundle.putString("kanal", relatedArticles.getKanal());
                bundle.putString("shared_url", relatedArticles.getShared_url());
                Intent intent = new Intent(this, ActDetailContentDefault.class);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
            }
        }
    }

    private void setTextViewHTML(TextView text, String html) {
        CharSequence sequence = Html.fromHtml(html);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for(URLSpan span : urls) {
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

    private void handleClickBodyText(String url) {
        if (isInternetPresent) {
            if (url.contains(Constant.LINK_YOUTUBE)) {
                moveVideoPage(url);
            } else if (url.contains(Constant.LINK_ARTICLE_VIVA)) {
                if (url != null) {
                    if (url.length() > 0) {
                        Bundle bundle = new Bundle();
                        bundle.putString("id", Constant.getArticleViva(url));
                        Intent intent = new Intent(ActNotification.this, ActDetailContentDefault.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
                    }
                }
            } else if (url.contains(Constant.LINK_VIDEO_VIVA)) {
                moveBrowserPage(url);
            } else {
                moveBrowserPage(url);
            }
        } else {
            Toast.makeText(this, R.string.title_no_connection, Toast.LENGTH_SHORT).show();
        }
    }

    private void showAds() {
        if (this != null) {
            if (adsArrayList != null) {
                if (adsArrayList.size() > 0) {
                    AdsConfig adsConfig = new AdsConfig();
                    for (int i=0; i<adsArrayList.size(); i++) {
                        if (adsArrayList.get(i).getmPosition() == Constant.POSITION_BANNER_TOP) {
                            if (publisherAdViewTop == null) {
                                publisherAdViewTop = new PublisherAdView(this);
                                adsConfig.setAdsBanner(publisherAdViewTop,
                                        adsArrayList.get(i).getmUnitId(), Constant.POSITION_BANNER_TOP, mParentLayout);
                            }
                        } else if (adsArrayList.get(i).getmPosition() == Constant.POSITION_BANNER_BOTTOM) {
                            if (publisherAdViewBottom == null) {
                                publisherAdViewBottom = new PublisherAdView(this);
                                adsConfig.setAdsBanner(publisherAdViewBottom,
                                        adsArrayList.get(i).getmUnitId(), Constant.POSITION_BANNER_BOTTOM, mParentLayout);
                            }
                        }
                    }
                }
            }
        }
    }

}
