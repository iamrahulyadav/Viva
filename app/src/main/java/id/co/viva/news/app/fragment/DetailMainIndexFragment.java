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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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
 * Created by root on 07/10/14.
 */
public class DetailMainIndexFragment extends Fragment implements View.OnClickListener,
        AdapterView.OnItemClickListener {

    private RelativeLayout headerRelated;
    private boolean isInternetPresent = false;
    private ProgressWheel progressWheel;
    private TextView tvNoResult;
    private ActionBarActivity mActivity;

    private ArrayList<RelatedArticle> relatedArticleArrayList;
    private ArrayList<Comment> commentArrayList;
    private ArrayList<SliderContentImage> sliderContentImages;
    private ArrayList<Favorites> favoritesArrayList;
    private ArrayList<Ads> adsArrayList;

    private ListView listView;
    private Analytics analytics;
    private RippleView rippleView;

    private TextView tvTitleMainDetail;
    private TextView tvDateMainDetail;
    private TextView tvReporterMainDetail;
    private TextView tvContentMainDetail;
    private KenBurnsView ivThumbDetailMain;
    private TextView tvPreviewCommentUser;
    private TextView tvPreviewCommentContent;
    private LinearLayout layoutCommentPreview;
    private Button btnComment;
    private ViewPager viewPager;
    private LinePageIndicator linePageIndicator;
    private int count = 0;
    private TextView textLinkVideo;

    private PublisherAdView publisherAdViewBottom;
    private PublisherAdView publisherAdViewTop;
    private LinearLayout mParentLayout;

    private String ids;
    private String id;
    private String image_caption;
    private String title;
    private String channel_id;
    private String urlVideo;
    private String kanal;
    private String image_url;
    private String date_publish;
    private String content;
    private String reporter_name;
    private String url_shared;
    private String analyticType;
    private String detailParam;

    public static DetailMainIndexFragment newInstance(String id, String analyticType, String detailParam) {
        DetailMainIndexFragment detailMainIndexFragment = new DetailMainIndexFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("analytic", analyticType);
        bundle.putString("detail_param", detailParam);
        detailMainIndexFragment.setArguments(bundle);
        return detailMainIndexFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (ActionBarActivity) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isInternetPresent = Global.getInstance(getActivity())
                .getConnectionStatus().isConnectingToInternet();
        id = getArguments().getString("id");
        analyticType = getArguments().getString("analytic");
        detailParam = getArguments().getString("detail_param");
    }

    private void defineViews(View view) {
        //Root layout
        mParentLayout = (LinearLayout) view.findViewById(R.id.parent_layout);

        //Viewpager image & text
        viewPager = (ViewPager) view.findViewById(R.id.horizontal_list);
        viewPager.setVisibility(View.GONE);

        //Indicator Slider Image
        linePageIndicator = (LinePageIndicator) view.findViewById(R.id.indicator);
        linePageIndicator.setVisibility(View.GONE);

        //Button comment
        btnComment = (Button) view.findViewById(R.id.btn_comment);
        btnComment.setBackgroundColor(getResources().getColor(R.color.new_base_color));
        btnComment.setOnClickListener(this);
        btnComment.setTransformationMethod(null);

        //Layout Comment
        layoutCommentPreview = (LinearLayout) view.findViewById(R.id.layout_preview_comment_list);
        layoutCommentPreview.setOnClickListener(this);
        layoutCommentPreview.setVisibility(View.GONE);

        //Preview Comment
        tvPreviewCommentContent = (TextView) view.findViewById(R.id.text_preview_comment_content);

        //Preview Comment User
        tvPreviewCommentUser = (TextView) view.findViewById(R.id.text_preview_comment_user);

        relatedArticleArrayList = new ArrayList<>();
        commentArrayList = new ArrayList<>();
        sliderContentImages = new ArrayList<>();
        adsArrayList = new ArrayList<>();

        progressWheel = (ProgressWheel) view.findViewById(R.id.progress_wheel);

        headerRelated = (RelativeLayout) view.findViewById(R.id.header_related_article);
        headerRelated.setVisibility(View.GONE);

        tvNoResult = (TextView) view.findViewById(R.id.text_no_result_detail);
        tvNoResult.setVisibility(View.GONE);

        //Material Effect View
        rippleView = (RippleView) view.findViewById(R.id.layout_ripple_view);
        rippleView.setVisibility(View.GONE);
        rippleView.setOnClickListener(this);

        //List Related Article
        listView = (ListView) view.findViewById(R.id.list_related_article);
        listView.setOnItemClickListener(this);

        tvTitleMainDetail = (TextView) view.findViewById(R.id.title_detail);
        tvDateMainDetail = (TextView) view.findViewById(R.id.date_detail);
        tvReporterMainDetail = (TextView) view.findViewById(R.id.reporter_detail);
        tvContentMainDetail = (TextView) view.findViewById(R.id.content_detail);

        ivThumbDetailMain = (KenBurnsView) view.findViewById(R.id.thumb_detail);
        ivThumbDetailMain.setOnClickListener(this);
        ivThumbDetailMain.setFocusableInTouchMode(true);

        textLinkVideo = (TextView) view.findViewById(R.id.text_move_video);
        textLinkVideo.setOnClickListener(this);
        textLinkVideo.setVisibility(View.GONE);

        if (Constant.isTablet(mActivity)) {
            ivThumbDetailMain.getLayoutParams().height =
                    Constant.getDynamicImageSize(mActivity, Constant.DYNAMIC_SIZE_GRID_TYPE);
            viewPager.getLayoutParams().height =
                    Constant.getDynamicImageSize(mActivity, Constant.DYNAMIC_SIZE_SLIDER_TYPE);
        }
    }

    private void retrieveData() {
        StringRequest request = new StringRequest(Request.Method.GET,
                Constant.NEW_DETAIL + "id/" + id + "/screen/" + detailParam,
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
                + "/screen/" + detailParam, true);
        Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_DETAIL + "id/" + id
                + "/screen/" + detailParam);
        Global.getInstance(getActivity()).addToRequestQueue(request, Constant.JSON_REQUEST);
    }

    private void parseData(String mResponse) {
        try {
            JSONObject jsonObject = new JSONObject(mResponse);
            JSONObject response = jsonObject.getJSONObject(Constant.response);
            //Get detail content
            JSONObject detail = response.getJSONObject(Constant.detail);
            ids = detail.getString(Constant.id);
            channel_id = detail.getString(Constant.channel_id);
            kanal = detail.getString(Constant.kanal);
            title = detail.getString(Constant.title);
            image_url = detail.getString(Constant.image_url);
            date_publish = detail.getString(Constant.date_publish);
            reporter_name = detail.getString(Constant.reporter_name);
            url_shared = detail.getString(Constant.url);
            image_caption = detail.getString(Constant.image_caption);

            JSONArray content = detail.getJSONArray(Constant.content);
            Log.i(Constant.TAG, "Content Size : " + content.length());
            if (content.length() > 0) {
                for (int i=0; i<content.length(); i++) {
                    String detailContent = content.getString(i);
                    Log.i(Constant.TAG, "Content : " + detailContent);
                }
            }

            //Get image content
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
                JSONObject objVideo = content_video.getJSONObject(0);
                urlVideo = objVideo.getString("src_1");
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
                String channel_id_related_article = objRelated.getString(Constant.channel_id);
                String related_date_publish = objRelated.getString(Constant.related_date_publish);
                String image = objRelated.getString(Constant.image);
                String channel_related_article = objRelated.getString(Constant.kanal);
                String shared_url = objRelated.getString(Constant.url);
                relatedArticleArrayList.add(new RelatedArticle(id, article_id, related_article_id, related_title,
                        related_channel_level_1_id, channel_id_related_article, related_date_publish, image, channel_related_article, shared_url));
            }
            //Get comment list
            JSONArray comment_list = response.getJSONArray(Constant.comment_list);
            for (int i=0; i<comment_list.length(); i++) {
                JSONObject objRelated = comment_list.getJSONObject(i);
                String id = objRelated.getString(Constant.id);
                String name = objRelated.getString(Constant.name);
                String comment_text = objRelated.getString(Constant.comment_text);
                commentArrayList.add(new Comment(id, null, name, null, comment_text, null, null, null));
            }
            //Get ads list
            if (isInternetPresent) {
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
            }
            //Send analytic
            setAnalytics(ids, title, analyticType);
            //Set view
            tvTitleMainDetail.setText(title);
            tvDateMainDetail.setText(date_publish);
//            setTextViewHTML(tvContentMainDetail, content);
            tvReporterMainDetail.setText(reporter_name);
            Picasso.with(getActivity()).load(image_url).transform(new CropSquareTransformation()).into(ivThumbDetailMain);
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
            }
            //Update by calling invalidateOptionsMenu()
            getActivity().invalidateOptionsMenu();
            //Hide progress bar
            progressWheel.setVisibility(View.GONE);

            if (isInternetPresent) {
                //Show Ads
                showAds();
                //Show video link
                if (urlVideo.length() > 0) {
                    textLinkVideo.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void checkCache() {
        if (Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_DETAIL
                + "id/" + id + "/screen/" + detailParam) != null) {
            String cachedResponse = new String(Global.getInstance(getActivity()).
                    getRequestQueue().getCache().get(Constant.NEW_DETAIL + "id/" + id + "/screen/" + detailParam).data);
            parseData(cachedResponse);
        } else {
            progressWheel.setVisibility(View.GONE);
            rippleView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_detail, container, false);
        //Enable menu
        setHasOptionsMenu(true);
        //Define all views
        defineViews(view);
        //Get data
        if (isInternetPresent) {
            retrieveData();
        } else {
            checkCache();
            Toast.makeText(getActivity(), R.string.title_no_connection,
                    Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void moveCommentPage() {
        Bundle bundle = new Bundle();
        bundle.putString("imageurl", image_url);
        bundle.putString("title", title);
        bundle.putString("article_id", ids);
        Intent intent = new Intent(getActivity(), ActComment.class);
        intent.putExtras(bundle);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    private void moveVideoPage(String mUrl) {
        Bundle bundle = new Bundle();
        bundle.putString("urlVideo", mUrl);
        Intent intent = new Intent(getActivity(), ActVideo.class);
        intent.putExtras(bundle);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    private void moveBrowserPage(String url) {
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("channel", kanal);
        Intent intent = new Intent(mActivity, ActBrowser.class);
        intent.putExtras(bundle);
        startActivity(intent);
        mActivity.overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    private void moveRatePage() {
        Bundle bundles = new Bundle();
        bundles.putString("imageurl", image_url);
        bundles.putString("title", title);
        bundles.putString("article_id", ids);
        Intent intents = new Intent(getActivity(), ActRating.class);
        intents.putExtras(bundles);
        startActivity(intents);
        getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    private void doFavorite() {
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
                        favoritesArrayList.add(new Favorites(ids, title, channel_id, kanal,
                                image_url, date_publish, reporter_name, url_shared, content, image_caption, sliderContentImages));
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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (url_shared == null || url_shared.length() < 1) {
            try {
                if (Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_DETAIL
                        + "id/" + id + "/screen/" + detailParam) != null) {
                    String cachedResponse = new String(Global.getInstance(getActivity()).
                            getRequestQueue().getCache().get(Constant.NEW_DETAIL + "/id/" + id).data);
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
        android.support.v7.widget.ShareActionProvider myShareActionProvider = (android.support.v7.widget.ShareActionProvider)
                MenuItemCompat.getActionProvider(item);
        Intent myIntent = new Intent();
        myIntent.setAction(Intent.ACTION_SEND);
        myIntent.putExtra(Intent.EXTRA_TEXT, url_shared);
        myIntent.setType("text/plain");
        myShareActionProvider.setShareIntent(myIntent);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.subaction_rate:
                moveRatePage();
                return true;
            case R.id.subaction_comments:
                moveCommentPage();
                return true;
            case R.id.subaction_favorites:
                doFavorite();
                return true;
            case R.id.subaction_browser:
                moveBrowserPage(url_shared);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_ripple_view:
                if (isInternetPresent) {
                    rippleView.setVisibility(View.GONE);
                    progressWheel.setVisibility(View.VISIBLE);
                    retrieveData();
                } else {
                    Toast.makeText(getActivity(), R.string.title_no_connection,
                            Toast.LENGTH_SHORT).show();
                    progressWheel.setVisibility(View.GONE);
                    tvNoResult.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.thumb_detail:
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
                break;
            case R.id.layout_preview_comment_list:
                moveCommentPage();
                break;
            case R.id.text_move_video:
                moveVideoPage(urlVideo);
                break;
            case R.id.btn_comment:
                moveCommentPage();
                break;
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

    private void setAnalytics(String id, String title, String analytic) {
        if (isInternetPresent) {
            if (analytics == null) {
                analytics = new Analytics(getActivity());
            }
            analytics.getAnalyticByATInternet(analytic + "_" + id + "_" + title);
            analytics.getAnalyticByGoogleAnalytic(analytic + "_" + id + "_" + title);
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

    private void handleClickBodyText(String url) {
        if (isInternetPresent) {
            if (url.contains(Constant.LINK_YOUTUBE)) {
                moveVideoPage(url);
            } else if (url.contains(Constant.LINK_ARTICLE_VIVA)) {
                if (url != null) {
                    if (url.length() > 0) {
                        Bundle bundle = new Bundle();
                        bundle.putString("id", Constant.getArticleViva(url));
                        Intent intent = new Intent(mActivity, ActDetailContentDefault.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
                    }
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
