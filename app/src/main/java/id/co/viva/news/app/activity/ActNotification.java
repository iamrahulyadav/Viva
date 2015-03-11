package id.co.viva.news.app.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
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

import cn.pedant.SweetAlert.SweetAlertDialog;
import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.adapter.ImageSliderAdapter;
import id.co.viva.news.app.adapter.RelatedAdapter;
import id.co.viva.news.app.component.CropSquareTransformation;
import id.co.viva.news.app.component.ProgressWheel;
import id.co.viva.news.app.model.Favorites;
import id.co.viva.news.app.model.RelatedArticle;
import id.co.viva.news.app.model.SliderContentImage;
import id.co.viva.news.app.model.Video;
import id.co.viva.news.app.services.Analytics;

/**
 * Created by reza on 20/01/15.
 */
public class ActNotification extends FragmentActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private String kanalFromNotification;
    private String idFromNotification;

    private boolean isInternetPresent = false;
    private ViewPager viewPager;
    private LinePageIndicator linePageIndicator;
    private ProgressWheel progressWheel;
    private RippleView rippleView;
    private Button btnRetry;
    private TextView tvNoResult;
    private ArrayList<SliderContentImage> sliderContentImages;
    private ArrayList<Favorites> favoritesArrayList;
    private ArrayList<RelatedArticle> relatedArticleArrayList;
    private ArrayList<Video> videoArrayList;
    private TextView tvTitleDetail;
    private TextView tvDateDetail;
    private TextView tvReporterDetail;
    private TextView tvContentDetail;
    private KenBurnsView ivThumbDetail;
    private RelativeLayout headerRelated;
    private RelatedAdapter adapter;
    private TextView textLinkVideo;
    private ListView listView;
    private ImageSliderAdapter imageSliderAdapter;
    private Analytics analytics;

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
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            idFromNotification = extras.containsKey(Constant.id) ? extras.getString(Constant.id) : "";
            kanalFromNotification = extras.containsKey(Constant.kanal) ? extras.getString(Constant.kanal) : "";
        }

        setContentView(R.layout.act_notification);
        setHeaderActionbar(kanalFromNotification);
        defineViews();

        isInternetPresent = Global.getInstance(this)
                .getConnectionStatus().isConnectingToInternet();

        getContent();
    }

    private void defineViews() {
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

        relatedArticleArrayList = new ArrayList<RelatedArticle>();
        sliderContentImages = new ArrayList<SliderContentImage>();
        videoArrayList = new ArrayList<Video>();

        listView = (ListView) findViewById(R.id.list_related_article_notification);
        listView.setOnItemClickListener(this);

        headerRelated = (RelativeLayout) findViewById(R.id.header_related_article);
        headerRelated.setVisibility(View.GONE);

        tvTitleDetail = (TextView) findViewById(R.id.title_detail_content);
        tvDateDetail = (TextView) findViewById(R.id.date_detail_content);
        tvReporterDetail = (TextView) findViewById(R.id.reporter_detail_content);
        tvContentDetail = (TextView) findViewById(R.id.content_detail_content);

        ivThumbDetail = (KenBurnsView) findViewById(R.id.thumb_detail_content);
        ivThumbDetail.setOnClickListener(this);
        ivThumbDetail.setFocusable(true);
        ivThumbDetail.setFocusableInTouchMode(true);
        ivThumbDetail.requestFocus();

        textLinkVideo = (TextView) findViewById(R.id.text_move_video);
        textLinkVideo.setOnClickListener(this);
        textLinkVideo.setVisibility(View.GONE);
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

    private void moveVideoPage() {
        Bundle bundle = new Bundle();
        bundle.putString("urlVideo", urlVideo);
        Intent intent = new Intent(this, ActVideo.class);
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
            moveVideoPage();
        }
    }

    private void getContent() {
        if(isInternetPresent) {
            StringRequest request = new StringRequest(Request.Method.GET, Constant.NEW_DETAIL + "/id/" + idFromNotification,
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
                                shared_url = detail.getString(Constant.url);
                                channel_id = detail.getString((Constant.channel_id));

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
                                        videoArrayList.add(new Video(urlVideo, null, null));
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
                                }

                                getAnalytics(title);

                                tvTitleDetail.setText(title);
                                tvDateDetail.setText(date_publish);
                                tvContentDetail.setText(Html.fromHtml(content).toString());
                                tvContentDetail.setMovementMethod(LinkMovementMethod.getInstance());
                                tvReporterDetail.setText(reporter_name);
                                Picasso.with(ActNotification.this).load(image_url).transform(new CropSquareTransformation()).into(ivThumbDetail);

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
                                    adapter = new RelatedAdapter(ActNotification.this, relatedArticleArrayList);
                                    listView.setAdapter(adapter);
                                    Constant.setListViewHeightBasedOnChildren(listView);
                                    adapter.notifyDataSetChanged();
                                    headerRelated.setVisibility(View.VISIBLE);
                                    if(kanalFromNotification != null) {
                                        if(kanalFromNotification.equalsIgnoreCase("bola")) {
                                            headerRelated.setBackgroundResource(R.color.color_bola);
                                        } else if(kanalFromNotification.equalsIgnoreCase("vivalife")) {
                                            headerRelated.setBackgroundResource(R.color.color_life);
                                        } else {
                                            headerRelated.setBackgroundResource(R.color.color_news);
                                        }
                                    } else {
                                        headerRelated.setBackgroundResource(R.color.header_grey);
                                    }
                                }

                                if(rippleView.getVisibility() == View.VISIBLE) {
                                    rippleView.setVisibility(View.GONE);
                                }

                                progressWheel.setVisibility(View.GONE);

                                //For updating content
                                invalidateOptionsMenu();

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
                            progressWheel.setVisibility(View.GONE);
                            rippleView.setVisibility(View.VISIBLE);
                            setButtonRetry(kanalFromNotification);

                        }
                    });
            request.setShouldCache(true);
            request.setRetryPolicy(new DefaultRetryPolicy(
                    Constant.TIME_OUT,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Global.getInstance(ActNotification.this).getRequestQueue()
                    .getCache().invalidate(Constant.NEW_DETAIL + "/id/" + idFromNotification, true);
            Global.getInstance(ActNotification.this)
                    .getRequestQueue().getCache().get(Constant.NEW_DETAIL + "/id/" + idFromNotification);
            Global.getInstance(ActNotification.this).addToRequestQueue(request, Constant.JSON_REQUEST);
        } else {
            if(tvNoResult.getVisibility() == View.VISIBLE) {
                tvNoResult.setVisibility(View.GONE);
            } else {
                tvNoResult.setVisibility(View.VISIBLE);
            }
            Toast.makeText(this, R.string.title_no_connection, Toast.LENGTH_SHORT).show();
        }
    }

    private void setHeaderActionbar(String fromkanal) {
        ColorDrawable colorDrawable = new ColorDrawable();
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setHomeButtonEnabled(true);
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(shared_url == null || shared_url.length() < 1) {
            try {
                if(Global.getInstance(this).getRequestQueue().getCache().get(Constant.NEW_DETAIL + "/id/" + idFromNotification) != null) {
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
        ShareActionProvider myShareActionProvider = (ShareActionProvider) item.getActionProvider();
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
        analytics.getAnalyticByATInternetFromNotification(Constant.ARTICLE_FROM_NOTIFICATION + "_" + title.toUpperCase(), "Push Notification");
        analytics.getAnalyticByGoogleAnalytic(Constant.ARTICLE_FROM_NOTIFICATION + "_" + title.toUpperCase());
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if(relatedArticleArrayList.size() > 0) {
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
