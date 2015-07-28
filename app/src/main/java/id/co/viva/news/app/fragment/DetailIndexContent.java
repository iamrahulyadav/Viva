package id.co.viva.news.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.nirhart.parallaxscroll.views.ParallaxScrollView;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.LinePageIndicator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.activity.ActBrowser;
import id.co.viva.news.app.activity.ActComment;
import id.co.viva.news.app.activity.ActDetailContentDefault;
import id.co.viva.news.app.activity.ActDetailPhotoThumb;
import id.co.viva.news.app.activity.ActRating;
import id.co.viva.news.app.activity.ActVideo;
import id.co.viva.news.app.adapter.ImageSliderAdapter;
import id.co.viva.news.app.adapter.RelatedAdapter;
import id.co.viva.news.app.ads.AdsConfig;
import id.co.viva.news.app.component.CropSquareTransformation;
import id.co.viva.news.app.component.ProgressWheel;
import id.co.viva.news.app.model.Ads;
import id.co.viva.news.app.model.Comment;
import id.co.viva.news.app.model.Favorites;
import id.co.viva.news.app.model.RelatedArticle;
import id.co.viva.news.app.model.SliderContentImage;
import id.co.viva.news.app.services.Analytics;

/**
 * Created by reza on 24/10/14.
 */
public class DetailIndexContent extends Fragment implements
        AdapterView.OnItemClickListener, View.OnClickListener {

    private String id;
    private String channels;

    private boolean isInternetPresent = false;
    private int count = 0;
    private int pageCount = 0;

    private ArrayList<RelatedArticle> relatedArticleArrayList;
    private ArrayList<Favorites> favoritesArrayList;
    private ArrayList<Comment> commentArrayList;
    private ArrayList<SliderContentImage> sliderContentImages;
    private ArrayList<Ads> adsArrayList;
    private ArrayList<String> pagingContents;

    private ProgressWheel progressWheel;
    private TextView tvNoResult;
    private Button btnComment;
    private ListView listView;
    private Analytics analytics;
    private RippleView rippleView;
    private RelativeLayout headerRelated;
    private TextView tvTitleDetail;
    private TextView tvDateDetail;
    private TextView tvReporterDetail;
    private TextView tvContentDetail;
    private KenBurnsView ivThumbDetail;
    private Button btnRetry;
    private ViewPager viewPager;
    private LinePageIndicator linePageIndicator;
    private LinearLayout mParentLayout;
    private ParallaxScrollView scrollView;
    private LinearLayout mPagingButtonLayout;
    private PublisherAdView publisherAdViewBottom;
    private PublisherAdView publisherAdViewTop;
    private TextView tvPreviewCommentUser;
    private TextView tvPreviewCommentContent;
    private TextView textLinkVideo;
    private TextView textPageIndex;
    private TextView textPageSize;
    private LinearLayout layoutCommentPreview;
    private ActionBarActivity mActivity;
    private ImageView previous, previousStart;
    private ImageView next, nextEnd;

    private String ids;
    private String title;
    private String channel_id;
    private String channel;
    private String image_url;
    private String date_publish;
    private String urlVideo;
    private String reporter_name;
    private String url_shared;
    private String image_caption;
    private String mChannelTitle;

    public static DetailIndexContent newInstance(String id, String channel, String channel_title) {
        DetailIndexContent detailIndexContent = new DetailIndexContent();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("channel", channel);
        bundle.putString("channel_title", channel_title);
        detailIndexContent.setArguments(bundle);
        return detailIndexContent;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (ActionBarActivity) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getArguments().getString("id");
        channels = getArguments().getString("channel");
        mChannelTitle = getArguments().getString("channel_title");
        analytics = new Analytics(getActivity());
        isInternetPresent = Global.getInstance(getActivity())
                .getConnectionStatus().isConnectingToInternet();
    }

    private void defineViews(View view) {
        //Root layout
        mParentLayout = (LinearLayout) view.findViewById(R.id.parent_layout);
        scrollView = (ParallaxScrollView) view.findViewById(R.id.scroll_layout);

        mPagingButtonLayout = (LinearLayout) view.findViewById(R.id.layout_button_next_previous);

        //Pager
        viewPager = (ViewPager) view.findViewById(R.id.horizontal_list);
        viewPager.setVisibility(View.GONE);

        //Indicator Pager
        linePageIndicator = (LinePageIndicator) view.findViewById(R.id.indicator);
        linePageIndicator.setVisibility(View.GONE);

        //Button comment
        btnComment = (Button) view.findViewById(R.id.btn_comment);
        btnComment.setOnClickListener(this);
        btnComment.setTransformationMethod(null);

        //Loading Progress
        progressWheel = (ProgressWheel) view.findViewById(R.id.progress_wheel);
        if (channels != null) {
            if (channels.equalsIgnoreCase("bola") || channels.equalsIgnoreCase("sport")) {
                progressWheel.setBarColor(getResources().getColor(R.color.color_bola));
            } else if (channels.equalsIgnoreCase("vivalife")) {
                progressWheel.setBarColor(getResources().getColor(R.color.color_life));
            } else if (channels.equalsIgnoreCase("otomotif")) {
                progressWheel.setBarColor(getResources().getColor(R.color.color_auto));
            } else {
                progressWheel.setBarColor(getResources().getColor(R.color.color_news));
            }
        } else {
            progressWheel.setBarColor(getResources().getColor(R.color.new_base_color));
        }

        //Header Related Article
        headerRelated = (RelativeLayout) view.findViewById(R.id.header_related_article);
        headerRelated.setVisibility(View.GONE);

        //Ripple Effect Layout
        rippleView = (RippleView) view.findViewById(R.id.layout_ripple_view_detail_subkanal);
        rippleView.setVisibility(View.GONE);
        rippleView.setOnClickListener(this);

        //Retry Button
        btnRetry = (Button) view.findViewById(R.id.btn_retry);

        tvNoResult = (TextView) view.findViewById(R.id.text_no_result_detail_content);
        tvNoResult.setVisibility(View.GONE);

        listView = (ListView) view.findViewById(R.id.list_related_article);
        listView.setOnItemClickListener(this);

        relatedArticleArrayList = new ArrayList<>();
        commentArrayList = new ArrayList<>();
        sliderContentImages = new ArrayList<>();
        adsArrayList = new ArrayList<>();
        pagingContents = new ArrayList<>();

        layoutCommentPreview = (LinearLayout) view.findViewById(R.id.layout_preview_comment_list);
        layoutCommentPreview.setOnClickListener(this);
        layoutCommentPreview.setVisibility(View.GONE);

        tvPreviewCommentContent = (TextView) view.findViewById(R.id.text_preview_comment_content);
        tvPreviewCommentUser = (TextView) view.findViewById(R.id.text_preview_comment_user);
        tvTitleDetail = (TextView) view.findViewById(R.id.title_detail_content);
        tvDateDetail = (TextView) view.findViewById(R.id.date_detail_content);
        tvReporterDetail = (TextView) view.findViewById(R.id.reporter_detail_content);
        tvContentDetail = (TextView) view.findViewById(R.id.content_detail_content);

        ivThumbDetail = (KenBurnsView) view.findViewById(R.id.thumb_detail_content);
        ivThumbDetail.setOnClickListener(this);
        ivThumbDetail.setFocusableInTouchMode(true);

        textLinkVideo = (TextView) view.findViewById(R.id.text_move_video);
        textLinkVideo.setOnClickListener(this);
        textLinkVideo.setVisibility(View.GONE);

        next = (ImageView) view.findViewById(R.id.page_next);
        nextEnd = (ImageView) view.findViewById(R.id.page_next_end);
        next.setOnClickListener(this);
        nextEnd.setOnClickListener(this);

        previous = (ImageView) view.findViewById(R.id.page_previous);
        previousStart = (ImageView) view.findViewById(R.id.page_previous_start);
        previous.setOnClickListener(this);
        previousStart.setOnClickListener(this);
        previous.setEnabled(false);
        previousStart.setEnabled(false);

        textPageIndex = (TextView) view.findViewById(R.id.text_page_index);
        textPageSize = (TextView) view.findViewById(R.id.text_page_size);

        if (Constant.isTablet(mActivity)) {
            ivThumbDetail.getLayoutParams().height =
                    Constant.getDynamicImageSize(mActivity, Constant.DYNAMIC_SIZE_GRID_TYPE);
            viewPager.getLayoutParams().height =
                    Constant.getDynamicImageSize(mActivity, Constant.DYNAMIC_SIZE_SLIDER_TYPE);
        }
    }

    private void retrieveData() {
        StringRequest request = new StringRequest(Request.Method.GET, Constant.NEW_DETAIL + "id/" + id
                + "/screen/" + channels + "_" + mChannelTitle.replace(" ", "_").toLowerCase() + "_detail_screen",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String volleyResponse) {
                        parseData(volleyResponse);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        checkCache();
                    }
                });
        request.setShouldCache(true);
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Global.getInstance(getActivity()).getRequestQueue().getCache().invalidate(Constant.NEW_DETAIL + "id/" + id
                + "/screen/" + channels + "_" + mChannelTitle.replace(" ", "_").toLowerCase() + "_detail_screen", true);
        Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_DETAIL + "id/" + id
                + "/screen/" + channels + "_" + mChannelTitle.replace(" ", "_").toLowerCase() + "_detail_screen");
        Global.getInstance(getActivity()).addToRequestQueue(request, Constant.JSON_REQUEST);
    }

    private void checkCache() {
        if (Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_DETAIL + "id/" + id
                + "/screen/" + channels + "_" + mChannelTitle.replace(" ", "_").toLowerCase() + "_detail_screen") != null) {
            String cachedResponse = new String(Global.getInstance(getActivity()).
                    getRequestQueue().getCache().get(Constant.NEW_DETAIL + "id/" + id
                    + "/screen/" + channels + "_" + mChannelTitle.replace(" ", "_").toLowerCase() + "_detail_screen").data);
            parseData(cachedResponse);
        } else {
            progressWheel.setVisibility(View.GONE);
            rippleView.setVisibility(View.VISIBLE);
            if (channels != null) {
                if (channels.equalsIgnoreCase("bola") || channels.equalsIgnoreCase("sport")) {
                    btnRetry.setBackgroundResource(R.drawable.shadow_button_bola);
                } else if (channels.equalsIgnoreCase("vivalife")) {
                    btnRetry.setBackgroundResource(R.drawable.shadow_button_life);
                } else if (channels.equalsIgnoreCase("otomotif")) {
                    btnRetry.setBackgroundResource(R.drawable.shadow_button_otomotif);
                } else {
                    btnRetry.setBackgroundResource(R.drawable.shadow_button_news);
                }
            }
        }
    }

    private void parseData(String mResponse) {
        try {
            JSONObject jsonObject = new JSONObject(mResponse);
            JSONObject response = jsonObject.getJSONObject(Constant.response);
            //Get detail content
            JSONObject detail = response.getJSONObject(Constant.detail);
            ids = detail.getString(Constant.id);
            channel_id = detail.getString(Constant.channel_id);
            channel = detail.getString(Constant.kanal);
            title = detail.getString(Constant.title);
            image_url = detail.getString(Constant.image_url);
            date_publish = detail.getString(Constant.date_publish);
            reporter_name = detail.getString(Constant.reporter_name);
            url_shared = detail.getString(Constant.url);
            image_caption = detail.getString(Constant.image_caption);
            //Get detail content(s)
            JSONArray content = detail.getJSONArray(Constant.content);
            if (content.length() > 0) {
                for (int i=0; i<content.length(); i++) {
                    String detailContent = content.getString(i);
                    pagingContents.add(detailContent);
                }
            }
            //Get list image content
            JSONArray sliderImageArray = detail.getJSONArray(Constant.content_images);
            if (sliderImageArray.length() > 0) {
                for (int i=0; i<sliderImageArray.length(); i++) {
                    JSONObject objSlider = sliderImageArray.getJSONObject(i);
                    String sliderPhotoUrl = objSlider.getString("src");
                    String sliderTitle = objSlider.getString("title");
                    sliderContentImages.add(new SliderContentImage(sliderPhotoUrl, sliderTitle));
                }
            }
            //Get video content
            if (isInternetPresent) {
                JSONArray content_video = detail.getJSONArray(Constant.content_video);
                if (content_video.length() > 0) {
                    JSONObject objVideo = content_video.getJSONObject(0);
                    urlVideo = objVideo.getString("src_1");
                }
            }
            //Get related article
            JSONArray related_article = response.getJSONArray(Constant.related_article);
            if (related_article.length() > 0) {
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
                    String channel = objRelated.getString(Constant.kanal);
                    String shared_url = objRelated.getString(Constant.url);
                    relatedArticleArrayList.add(new RelatedArticle(id, article_id, related_article_id, related_title,
                            related_channel_level_1_id, channel_id, related_date_publish, image, channel, shared_url));
                }
            }
            //Get comment list
            JSONArray comment_list = response.getJSONArray(Constant.comment_list);
            if (comment_list.length() > 0) {
                for (int i=0; i<comment_list.length(); i++) {
                    JSONObject objRelated = comment_list.getJSONObject(i);
                    String id = objRelated.getString(Constant.id);
                    String name = objRelated.getString(Constant.name);
                    String comment_text = objRelated.getString(Constant.comment_text);
                    commentArrayList.add(new Comment(id, null, name, null, comment_text, null, null, null));
                }
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
            //Send analytic
            setAnalytics(mChannelTitle, channels, title, ids);
            //Set data to view
            tvTitleDetail.setText(title);
            tvDateDetail.setText(date_publish);
            //Paging check process
            if (pagingContents.size() > 0) {
                setTextViewHTML(tvContentDetail, pagingContents.get(0));
                if (pagingContents.size() > 1) {
                    textPageIndex.setText(String.valueOf(pageCount + 1));
                    textPageSize.setText(String.valueOf(pagingContents.size()));
                    mPagingButtonLayout.setVisibility(View.VISIBLE);
                }
            }
            tvReporterDetail.setText(reporter_name);
            Picasso.with(getActivity()).load(image_url)
                    .transform(new CropSquareTransformation()).into(ivThumbDetail);
            //Checking for image content
            if (sliderContentImages.size() > 0) {
                ImageSliderAdapter imageSliderAdapter = new ImageSliderAdapter(getFragmentManager(), sliderContentImages);
                viewPager.setAdapter(imageSliderAdapter);
                viewPager.setCurrentItem(0);
                imageSliderAdapter.notifyDataSetChanged();
                linePageIndicator.setViewPager(viewPager);
                viewPager.setVisibility(View.VISIBLE);
                linePageIndicator.setVisibility(View.VISIBLE);
            }
            //Checking for related article
            if (relatedArticleArrayList.size() > 0 || !relatedArticleArrayList.isEmpty()) {
                RelatedAdapter adapter = new RelatedAdapter(getActivity(), relatedArticleArrayList);
                listView.setAdapter(adapter);
                Constant.setListViewHeightBasedOnChildren(listView);
                adapter.notifyDataSetChanged();
                headerRelated.setVisibility(View.VISIBLE);
                if (channels != null) {
                    if (channels.equalsIgnoreCase("bola") || (channels.equalsIgnoreCase("sport"))) {
                        headerRelated.setBackgroundResource(R.color.color_bola);
                    } else if (channels.equalsIgnoreCase("vivalife")) {
                        headerRelated.setBackgroundResource(R.color.color_life);
                    } else if (channels.equalsIgnoreCase("otomotif")) {
                        headerRelated.setBackgroundResource(R.color.color_auto);
                    } else {
                        headerRelated.setBackgroundResource(R.color.color_news);
                    }
                }
            }
            //Animate comment list
            if (commentArrayList.size() > 0) {
                layoutCommentPreview.setVisibility(View.VISIBLE);
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            while (true) {
                                Thread.sleep(3000);
                                if (getActivity() == null) {
                                    return;
                                }
                                getActivity().runOnUiThread(new Runnable() {
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
            } else {
                btnComment.setVisibility(View.VISIBLE);
                if (channels != null) {
                    if (channels.equalsIgnoreCase("bola") || channels.equalsIgnoreCase("sport")) {
                        btnComment.setBackgroundColor(getResources().getColor(R.color.color_bola));
                    } else if(channels.equalsIgnoreCase("vivalife")) {
                        btnComment.setBackgroundColor(getResources().getColor(R.color.color_life));
                    } else if(channels.equalsIgnoreCase("otomotif")) {
                        btnComment.setBackgroundColor(getResources().getColor(R.color.color_auto));
                    } else {
                        btnComment.setBackgroundColor(getResources().getColor(R.color.color_news));
                    }
                }
            }
            //Update content
            getActivity().invalidateOptionsMenu();
            //Hide progress bar
            progressWheel.setVisibility(View.GONE);
            //Check ads & video
            if (isInternetPresent) {
                showAds();
                if (urlVideo.length() > 0) {
                    textLinkVideo.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_detail_content, container, false);
        //Enable menu
        setHasOptionsMenu(true);
        //Define all views
        defineViews(view);
        //Get data
        if (isInternetPresent) {
            retrieveData();
        } else {
            checkCache();
        }
        return view;
    }

    private void moveCommentPage() {
        Bundle bundle = new Bundle();
        bundle.putString("imageurl", image_url);
        bundle.putString("title", title);
        bundle.putString("article_id", ids);
        bundle.putString("type_kanal", channel);
        Intent intent = new Intent(getActivity(), ActComment.class);
        intent.putExtras(bundle);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    private void moveRatingPage() {
        Bundle bundles = new Bundle();
        bundles.putString("imageurl", image_url);
        bundles.putString("title", title);
        bundles.putString("article_id", ids);
        bundles.putString("type_kanal", channel);
        Intent intents = new Intent(getActivity(), ActRating.class);
        intents.putExtras(bundles);
        startActivity(intents);
        getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    private void doFavorites() {
        String favoriteList = Global.getInstance(getActivity()).getSharedPreferences(getActivity())
                .getString(Constant.FAVORITES_LIST, "");
        if (favoriteList == null || favoriteList.length() <= 0) {
            favoritesArrayList = Global.getInstance(getActivity()).getFavoritesList();
        } else {
            favoritesArrayList = Global.getInstance(getActivity()).getInstanceGson().
                    fromJson(favoriteList, Global.getInstance(getActivity()).getTypeFavorites());
        }
        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getResources().getString(R.string.label_favorite_navigation_title))
                .setContentText(title)
                .setConfirmText(getResources().getString(R.string.label_favorite_ok))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        String contents = Global.getInstance(getActivity()).getInstanceGson().toJson(pagingContents);
                        favoritesArrayList.add(new Favorites(ids, title, channel_id, channel,
                                image_url, date_publish, reporter_name, url_shared, contents, image_caption, sliderContentImages));
                        String favorite = Global.getInstance(getActivity()).getInstanceGson().toJson(favoritesArrayList);
                        Global.getInstance(getActivity()).getDefaultEditor().putString(Constant.FAVORITES_LIST, favorite);
                        Global.getInstance(getActivity()).getDefaultEditor().putInt(Constant.FAVORITES_LIST_SIZE, favoritesArrayList.size());
                        Global.getInstance(getActivity()).getDefaultEditor().commit();
                        sDialog.setTitleText(getResources().getString(R.string.label_favorite_navigation_title_confirm))
                                .setContentText(getResources().getString(R.string.label_favorite_navigation_content))
                                .setConfirmText(getResources().getString(R.string.label_favorite_ok))
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    }
                })
                .show();
    }

    private void moveBrowserPage(String url) {
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("channel", channel);
        Intent intent = new Intent(mActivity, ActBrowser.class);
        intent.putExtras(bundle);
        startActivity(intent);
        mActivity.overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    private void moveVideoPage(String url) {
        Bundle bundle = new Bundle();
        bundle.putString("urlVideo", url);
        Intent intent = new Intent(getActivity(), ActVideo.class);
        intent.putExtras(bundle);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    private void showPagingNext() {
        pageCount += 1;
        if (pageCount > 0) {
            previous.setEnabled(true);
            previousStart.setEnabled(true);
        }
        if (pageCount < pagingContents.size()) {
            setTextViewHTML(tvContentDetail, pagingContents.get(pageCount));
            scrollView.smoothScrollTo(0, 0);
            textPageIndex.setText(String.valueOf(pageCount + 1));
        }
        if (pageCount == pagingContents.size() - 1) {
            next.setEnabled(false);
            nextEnd.setEnabled(false);
        }
    }

    private void showPagingPrevious() {
        pageCount -= 1;
        if (pageCount < pagingContents.size() - 1) {
            next.setEnabled(true);
            nextEnd.setEnabled(true);
        }
        if (pageCount == 0) {
            setTextViewHTML(tvContentDetail, pagingContents.get(pageCount));
            scrollView.smoothScrollTo(0, 0);
            previous.setEnabled(false);
            previousStart.setEnabled(false);
            textPageIndex.setText(String.valueOf(pageCount + 1));
        } else {
            previous.setEnabled(true);
            previousStart.setEnabled(true);
            if (pageCount > -1 && pageCount < pagingContents.size()) {
                setTextViewHTML(tvContentDetail, pagingContents.get(pageCount));
                scrollView.smoothScrollTo(0, 0);
                textPageIndex.setText(String.valueOf(pageCount + 1));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.subaction_rate:
                moveRatingPage();
                return true;
            case R.id.subaction_comments:
                moveCommentPage();
                return true;
            case R.id.subaction_favorites:
                doFavorites();
                return true;
            case R.id.subaction_browser:
                moveBrowserPage(url_shared);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        ListView listview = (ListView) adapterView;
        if (listview.getId() == R.id.list_related_article) {
            if (relatedArticleArrayList.size() > 0) {
                RelatedArticle relatedArticles = relatedArticleArrayList.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("id", relatedArticles.getRelated_article_id());
                bundle.putString("kanal", relatedArticles.getKanal());
                bundle.putString("shared_url", relatedArticles.getShared_url());
                Intent intent = new Intent(mActivity, ActDetailContentDefault.class);
                intent.putExtras(bundle);
                startActivity(intent);
                mActivity.overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
            }
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (url_shared == null || url_shared.length() < 1) {
            try {
                if (Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_DETAIL + "id/" + id
                        + "/screen/" + channels + "_" + mChannelTitle.replace(" ", "_").toLowerCase() + "_detail_screen") != null) {
                    String cachedResponse = new String(Global.getInstance(getActivity()).
                            getRequestQueue().getCache().get(Constant.NEW_DETAIL + "id/" + id
                            + "/screen/" + channels + "_" + mChannelTitle.replace(" ", "_").toLowerCase() + "_detail_screen").data);
                    JSONObject jsonObject = new JSONObject(cachedResponse);
                    JSONObject response = jsonObject.getJSONObject(Constant.response);
                    JSONObject detail = response.getJSONObject(Constant.detail);
                    url_shared = detail.getString(Constant.url);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_frag_detail, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        android.support.v7.widget.ShareActionProvider myShareActionProvider =
                (android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent myIntent = new Intent();
        myIntent.setAction(Intent.ACTION_SEND);
        myIntent.putExtra(Intent.EXTRA_TEXT, url_shared);
        myIntent.setType("text/plain");
        myShareActionProvider.setShareIntent(myIntent);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.layout_ripple_view_detail_subkanal) {
            if (isInternetPresent) {
                rippleView.setVisibility(View.GONE);
                progressWheel.setVisibility(View.VISIBLE);
                retrieveData();
            } else {
                Toast.makeText(getActivity(), R.string.title_no_connection, Toast.LENGTH_SHORT).show();
                progressWheel.setVisibility(View.GONE);
                tvNoResult.setVisibility(View.VISIBLE);
            }
        } else if (view.getId() == R.id.thumb_detail_content) {
            if (image_url != null) {
                if (image_url.length() > 0) {
                    Bundle bundle = new Bundle();
                    bundle.putString("photoUrl", image_url);
                    bundle.putString("image_caption", image_caption);
                    Intent intent = new Intent(getActivity(), ActDetailPhotoThumb.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
                }
            }
        } else if (view.getId() == R.id.layout_preview_comment_list) {
            moveCommentPage();
        } else if (view.getId() == R.id.text_move_video) {
            moveVideoPage(urlVideo);
        } else if (view.getId() == R.id.btn_comment) {
            moveCommentPage();
        } else if (view.getId() == R.id.page_next) {
            showPagingNext();
        } else if (view.getId() == R.id.page_previous) {
            showPagingPrevious();
        } else if (view.getId() == R.id.page_next_end) {
            if (pageCount < pagingContents.size() - 1) {
                pageCount = pagingContents.size() - 1;
                setTextViewHTML(tvContentDetail, pagingContents.get(pageCount));
                scrollView.smoothScrollTo(0, 0);
                next.setEnabled(false);
                nextEnd.setEnabled(false);
                previous.setEnabled(true);
                previousStart.setEnabled(true);
                textPageIndex.setText(String.valueOf(pageCount + 1));
            }
        } else if (view.getId() == R.id.page_previous_start) {
            if (pageCount > 0) {
                pageCount = 0;
                setTextViewHTML(tvContentDetail, pagingContents.get(pageCount));
                scrollView.smoothScrollTo(0, 0);
                previous.setEnabled(false);
                previousStart.setEnabled(false);
                next.setEnabled(true);
                nextEnd.setEnabled(true);
                textPageIndex.setText(String.valueOf(pageCount + 1));
            }
        }
    }

    private void setAnalytics(String mChannelTitle, String mChannel, String mTitle, String mId) {
        if (isInternetPresent) {
            analytics.getAnalyticByATInternet(mChannel.toLowerCase().replace(" ", "_")
                    + "_" + mChannelTitle.toLowerCase().replace(" ", "_")
                    + "_" + mId + "_" + mTitle
                    + "_detail_screen");
            analytics.getAnalyticByGoogleAnalytic(mChannel.toLowerCase().replace(" ", "_")
                    + "_" + mChannelTitle.toLowerCase().replace(" ", "_")
                    + "_" + mId + "_" + mTitle
                    + "_detail_screen");
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
                if (url.length() > 0) {
                    Bundle bundle = new Bundle();
                    bundle.putString("id", Constant.getArticleViva(url));
                    Intent intent = new Intent(mActivity, ActDetailContentDefault.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
                }
            } else if (url.contains(Constant.LINK_VIDEO_VIVA)) {
                moveBrowserPage(url);
            } else {
                moveBrowserPage(url);
            }
        } else {
            Toast.makeText(mActivity, R.string.title_no_connection, Toast.LENGTH_SHORT).show();
        }
    }

    private void showAds() {
        if (getActivity() != null) {
            if (adsArrayList != null) {
                if (adsArrayList.size() > 0) {
                    AdsConfig adsConfig = new AdsConfig();
                    for (int i=0; i<adsArrayList.size(); i++) {
                        if (adsArrayList.get(i).getmPosition() == Constant.POSITION_BANNER_TOP) {
                            if (publisherAdViewTop == null) {
                                publisherAdViewTop = new PublisherAdView(getActivity());
                                adsConfig.setAdsBanner(publisherAdViewTop,
                                        adsArrayList.get(i).getmUnitId(), Constant.POSITION_BANNER_TOP, mParentLayout);
                            }
                        } else if (adsArrayList.get(i).getmPosition() == Constant.POSITION_BANNER_BOTTOM) {
                            if (publisherAdViewBottom == null) {
                                publisherAdViewBottom = new PublisherAdView(getActivity());
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
