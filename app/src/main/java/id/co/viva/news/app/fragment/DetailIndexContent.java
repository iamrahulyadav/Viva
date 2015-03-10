package id.co.viva.news.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
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
import id.co.viva.news.app.activity.ActComment;
import id.co.viva.news.app.activity.ActDetailContentDefault;
import id.co.viva.news.app.activity.ActDetailPhotoThumb;
import id.co.viva.news.app.activity.ActRating;
import id.co.viva.news.app.activity.ActVideo;
import id.co.viva.news.app.adapter.ImageSliderAdapter;
import id.co.viva.news.app.adapter.RelatedAdapter;
import id.co.viva.news.app.component.CropSquareTransformation;
import id.co.viva.news.app.component.ProgressWheel;
import id.co.viva.news.app.model.Comment;
import id.co.viva.news.app.model.Favorites;
import id.co.viva.news.app.model.RelatedArticle;
import id.co.viva.news.app.model.SliderContentImage;
import id.co.viva.news.app.model.Video;
import id.co.viva.news.app.services.Analytics;

/**
 * Created by reza on 24/10/14.
 */
public class DetailIndexContent extends Fragment implements
        AdapterView.OnItemClickListener, View.OnClickListener {

    private String id;
    private String kanals;
    private RelativeLayout headerRelated;
    private boolean isInternetPresent = false;
    private String cachedResponse;
    private ProgressWheel progressWheel;
    private TextView tvNoResult;
    private ArrayList<RelatedArticle> relatedArticleArrayList;
    private RelatedAdapter adapter;
    private ImageSliderAdapter imageSliderAdapter;
    private ListView listView;
    private Analytics analytics;
    private RippleView rippleView;
    private String favoriteList;

    private ArrayList<Favorites> favoritesArrayList;
    private ArrayList<Comment> commentArrayList;
    private ArrayList<SliderContentImage> sliderContentImages;
    private ArrayList<Video> videoArrayList;

    private TextView tvTitleDetail;
    private TextView tvDateDetail;
    private TextView tvReporterDetail;
    private TextView tvContentDetail;
    private KenBurnsView ivThumbDetail;
    private Button btnRetry;
    private ViewPager viewPager;
    private LinePageIndicator linePageIndicator;
    private TextView tvPreviewCommentUser;
    private TextView tvPreviewCommentContent;
    private LinearLayout layoutCommentPreview;
    private int count = 0;

    private String ids;
    private String title;
    private String channel_id;
    private String kanal;
    private String image_url;
    private String date_publish;
    private String content;
    private String urlVideo;
    private String widthVideo;
    private String heightVideo;
    private String reporter_name;
    private String url_shared;
    private String image_caption;
    private String sliderPhotoUrl;
    private String sliderTitle;
    private TextView textLinkVideo;

    public static DetailIndexContent newInstance(String id, String kanals) {
        DetailIndexContent detailIndexContent = new DetailIndexContent();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("kanals", kanals);
        detailIndexContent.setArguments(bundle);
        return detailIndexContent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getArguments().getString("id");
        kanals = getArguments().getString("kanals");
        analytics = new Analytics(getActivity());
        isInternetPresent = Global.getInstance(getActivity()).getConnectionStatus().isConnectingToInternet();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_detail_content, container, false);

        setHasOptionsMenu(true);

        if(viewPager == null) {
            viewPager = (ViewPager) view.findViewById(R.id.horizontal_list);
            view.setTag(viewPager);
        } else {
            viewPager = (ViewPager) view.getTag();
        }
        viewPager.setVisibility(View.GONE);

        if(linePageIndicator == null) {
            linePageIndicator = (LinePageIndicator) view.findViewById(R.id.indicator);
            view.setTag(linePageIndicator);
        } else {
            linePageIndicator = (LinePageIndicator) view.getTag();
        }
        linePageIndicator.setVisibility(View.GONE);

        if(progressWheel == null) {
            progressWheel = (ProgressWheel) view.findViewById(R.id.progress_wheel);
            view.setTag(progressWheel);
        } else {
            progressWheel = (ProgressWheel) view.getTag();
        }

        if(headerRelated == null) {
            headerRelated = (RelativeLayout) view.findViewById(R.id.header_related_article);
            view.setTag(headerRelated);
        } else {
            headerRelated = (RelativeLayout) view.getTag();
        }
        headerRelated.setVisibility(View.GONE);

        if(rippleView == null) {
            rippleView = (RippleView) view.findViewById(R.id.layout_ripple_view_detail_subkanal);
            view.setTag(rippleView);
        } else {
            rippleView = (RippleView) view.getTag();
        }
        rippleView.setVisibility(View.GONE);
        rippleView.setOnClickListener(this);

        if(btnRetry == null) {
            btnRetry = (Button) view.findViewById(R.id.btn_retry);
            view.setTag(btnRetry);
        } else {
            btnRetry = (Button) view.getTag();
        }

        if(tvNoResult == null) {
            tvNoResult = (TextView) view.findViewById(R.id.text_no_result_detail_content);
            view.setTag(tvNoResult);
        } else {
            tvNoResult = (TextView) view.getTag();
        }
        tvNoResult.setVisibility(View.GONE);

        if(listView == null) {
            listView = (ListView) view.findViewById(R.id.list_related_article);
            view.setTag(listView);
        } else {
            listView = (ListView) view.getTag();
        }
        listView.setOnItemClickListener(this);

        if(relatedArticleArrayList == null) {
            relatedArticleArrayList = new ArrayList<RelatedArticle>();
        }

        if(commentArrayList == null) {
            commentArrayList = new ArrayList<Comment>();
        }

        if(sliderContentImages == null) {
            sliderContentImages = new ArrayList<SliderContentImage>();
        }

        if(videoArrayList == null) {
            videoArrayList = new ArrayList<Video>();
        }

        if(layoutCommentPreview == null) {
            layoutCommentPreview = (LinearLayout) view.findViewById(R.id.layout_preview_comment_list);
            view.setTag(layoutCommentPreview);
        } else {
            layoutCommentPreview = (LinearLayout) view.getTag();
        }
        layoutCommentPreview.setOnClickListener(this);
        layoutCommentPreview.setVisibility(View.GONE);

        if(tvPreviewCommentContent == null) {
            tvPreviewCommentContent = (TextView) view.findViewById(R.id.text_preview_comment_content);
            view.setTag(tvPreviewCommentContent);
        } else {
            tvPreviewCommentContent = (TextView) view.getTag();
        }

        if(tvPreviewCommentUser == null) {
            tvPreviewCommentUser = (TextView) view.findViewById(R.id.text_preview_comment_user);
            view.setTag(tvPreviewCommentUser);
        } else {
            tvPreviewCommentUser = (TextView) view.getTag();
        }

        if(tvTitleDetail == null) {
            tvTitleDetail = (TextView) view.findViewById(R.id.title_detail_content);
            view.setTag(tvTitleDetail);
        } else {
            tvTitleDetail = (TextView) view.getTag();
        }

        if(tvDateDetail == null) {
            tvDateDetail = (TextView) view.findViewById(R.id.date_detail_content);
            view.setTag(tvDateDetail);
        } else {
            tvDateDetail = (TextView) view.getTag();
        }

        if(tvReporterDetail == null) {
            tvReporterDetail = (TextView) view.findViewById(R.id.reporter_detail_content);
            view.setTag(tvReporterDetail);
        } else {
            tvReporterDetail = (TextView) view.getTag();
        }

        if(tvContentDetail == null) {
            tvContentDetail = (TextView) view.findViewById(R.id.content_detail_content);
            view.setTag(tvContentDetail);
        } else {
            tvContentDetail = (TextView) view.getTag();
        }

        if(ivThumbDetail == null) {
            ivThumbDetail = (KenBurnsView) view.findViewById(R.id.thumb_detail_content);
            view.setTag(ivThumbDetail);
        } else {
            ivThumbDetail = (KenBurnsView) view.getTag();
        }
        ivThumbDetail.setOnClickListener(this);
        ivThumbDetail.setFocusableInTouchMode(true);

        if(textLinkVideo == null) {
            textLinkVideo = (TextView) view.findViewById(R.id.text_move_video);
            view.setTag(textLinkVideo);
        } else {
            textLinkVideo = (TextView) view.getTag();
        }
        textLinkVideo.setOnClickListener(this);
        textLinkVideo.setVisibility(View.GONE);

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

                                setAnalytics(kanal, ids, title);

                                tvTitleDetail.setText(title);
                                tvDateDetail.setText(date_publish);
                                tvContentDetail.setText(Html.fromHtml(content).toString());
                                tvContentDetail.setMovementMethod(LinkMovementMethod.getInstance());
                                tvReporterDetail.setText(reporter_name);
                                Picasso.with(getActivity()).load(image_url).transform(new CropSquareTransformation()).into(ivThumbDetail);

                                if(sliderContentImages.size() > 0) {
                                    imageSliderAdapter = new ImageSliderAdapter(getFragmentManager(), sliderContentImages);
                                    viewPager.setAdapter(imageSliderAdapter);
                                    viewPager.setCurrentItem(0);
                                    imageSliderAdapter.notifyDataSetChanged();
                                    linePageIndicator.setViewPager(viewPager);
                                    viewPager.setVisibility(View.VISIBLE);
                                    linePageIndicator.setVisibility(View.VISIBLE);
                                }

                                if(relatedArticleArrayList.size() > 0 || !relatedArticleArrayList.isEmpty()) {
                                    adapter = new RelatedAdapter(getActivity(), relatedArticleArrayList);
                                    listView.setAdapter(adapter);
                                    Constant.setListViewHeightBasedOnChildren(listView);
                                    adapter.notifyDataSetChanged();
                                    headerRelated.setVisibility(View.VISIBLE);
                                    if(kanals != null) {
                                        if(kanals.equalsIgnoreCase("bola")) {
                                            headerRelated.setBackgroundResource(R.color.color_bola);
                                        } else if(kanals.equalsIgnoreCase("vivalife")) {
                                            headerRelated.setBackgroundResource(R.color.color_life);
                                        } else {
                                            headerRelated.setBackgroundResource(R.color.color_news);
                                        }
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
                                                    if(getActivity() == null) {
                                                        return;
                                                    }
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            tvPreviewCommentUser.setText(commentArrayList.get(count).getUsername());
                                                            tvPreviewCommentContent.setText(commentArrayList.get(count).getComment_text());
                                                            count++;
                                                            if(count >= commentArrayList.size()) {
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

                                getActivity().invalidateOptionsMenu();

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

                            if(Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_DETAIL + "/id/" + id) != null) {
                                cachedResponse = new String(Global.getInstance(getActivity()).
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
                                    Picasso.with(getActivity()).load(image_url).transform(new CropSquareTransformation()).into(ivThumbDetail);

                                    if(sliderContentImages.size() > 0) {
                                        imageSliderAdapter = new ImageSliderAdapter(getFragmentManager(), sliderContentImages);
                                        viewPager.setAdapter(imageSliderAdapter);
                                        viewPager.setCurrentItem(0);
                                        imageSliderAdapter.notifyDataSetChanged();
                                        linePageIndicator.setViewPager(viewPager);
                                        viewPager.setVisibility(View.VISIBLE);
                                        linePageIndicator.setVisibility(View.VISIBLE);
                                    }

                                    if(relatedArticleArrayList.size() > 0 || !relatedArticleArrayList.isEmpty()) {
                                        adapter = new RelatedAdapter(getActivity(), relatedArticleArrayList);
                                        listView.setAdapter(adapter);
                                        Constant.setListViewHeightBasedOnChildren(listView);
                                        adapter.notifyDataSetChanged();
                                        headerRelated.setVisibility(View.VISIBLE);
                                        Log.i(Constant.TAG, "KANALS : " + kanals);
                                        if(kanals != null) {
                                            if(kanals.equalsIgnoreCase("bola")) {
                                                headerRelated.setBackgroundResource(R.color.color_bola);
                                            } else if(kanals.equalsIgnoreCase("vivalife")) {
                                                headerRelated.setBackgroundResource(R.color.color_life);
                                            } else {
                                                headerRelated.setBackgroundResource(R.color.color_news);
                                            }
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
                                                        if(getActivity() == null) {
                                                            return;
                                                        }
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                tvPreviewCommentUser.setText(commentArrayList.get(count).getUsername());
                                                                tvPreviewCommentContent.setText(commentArrayList.get(count).getComment_text());
                                                                count++;
                                                                if(count >= commentArrayList.size()) {
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
                                progressWheel.setVisibility(View.GONE);
                                rippleView.setVisibility(View.VISIBLE);
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
                        }
                    });
            request.setShouldCache(true);
            request.setRetryPolicy(new DefaultRetryPolicy(
                    Constant.TIME_OUT,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Global.getInstance(getActivity()).getRequestQueue().getCache().invalidate(Constant.NEW_DETAIL + "/id/" + id, true);
            Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_DETAIL + "/id/" + id);
            Global.getInstance(getActivity()).addToRequestQueue(request, Constant.JSON_REQUEST);
        } else {
            if(Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_DETAIL + "/id/" + id) != null) {
                cachedResponse = new String(Global.getInstance(getActivity()).
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
                    Picasso.with(getActivity()).load(image_url).transform(new CropSquareTransformation()).into(ivThumbDetail);

                    if(sliderContentImages.size() > 0) {
                        imageSliderAdapter = new ImageSliderAdapter(getFragmentManager(), sliderContentImages);
                        viewPager.setAdapter(imageSliderAdapter);
                        viewPager.setCurrentItem(0);
                        imageSliderAdapter.notifyDataSetChanged();
                        linePageIndicator.setViewPager(viewPager);
                        viewPager.setVisibility(View.VISIBLE);
                        linePageIndicator.setVisibility(View.VISIBLE);
                    }

                    if(relatedArticleArrayList.size() > 0 || !relatedArticleArrayList.isEmpty()) {
                        adapter = new RelatedAdapter(getActivity(), relatedArticleArrayList);
                        listView.setAdapter(adapter);
                        Constant.setListViewHeightBasedOnChildren(listView);
                        adapter.notifyDataSetChanged();
                        headerRelated.setVisibility(View.VISIBLE);
                        Log.i(Constant.TAG, "KANALS : " + kanals);
                        if(kanals != null) {
                            if(kanals.equalsIgnoreCase("bola")) {
                                headerRelated.setBackgroundResource(R.color.color_bola);
                            } else if(kanals.equalsIgnoreCase("vivalife")) {
                                headerRelated.setBackgroundResource(R.color.color_life);
                            } else {
                                headerRelated.setBackgroundResource(R.color.color_news);
                            }
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
                                        if(getActivity() == null) {
                                            return;
                                        }
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                tvPreviewCommentUser.setText(commentArrayList.get(count).getUsername());
                                                tvPreviewCommentContent.setText(commentArrayList.get(count).getComment_text());
                                                count++;
                                                if(count >= commentArrayList.size()) {
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
                Toast.makeText(getActivity(), R.string.title_no_connection, Toast.LENGTH_SHORT).show();
                progressWheel.setVisibility(View.GONE);
                tvNoResult.setVisibility(View.VISIBLE);
            }
        }

        return view;
    }

    private void moveCommentPage() {
        Bundle bundle = new Bundle();
        bundle.putString("imageurl", image_url);
        bundle.putString("title", title);
        bundle.putString("article_id", ids);
        bundle.putString("type_kanal", kanal);
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
        bundles.putString("type_kanal", kanal);
        Intent intents = new Intent(getActivity(), ActRating.class);
        intents.putExtras(bundles);
        startActivity(intents);
        getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    private void doFavorites() {
        favoriteList = Global.getInstance(getActivity()).getSharedPreferences(getActivity())
                .getString(Constant.FAVORITES_LIST, "");
        if(favoriteList == null || favoriteList.length() <= 0) {
            favoritesArrayList = Global.getInstance(getActivity()).getFavoritesList();
        } else {
            favoritesArrayList = Global.getInstance(getActivity()).getInstanceGson().
                    fromJson(favoriteList, Global.getInstance(getActivity()).getType());
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

    private void moveVideoPage() {
        Bundle bundle = new Bundle();
        bundle.putString("urlVideo", urlVideo);
        Intent intent = new Intent(getActivity(), ActVideo.class);
        intent.putExtras(bundle);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if(relatedArticleArrayList.size() > 0) {
            RelatedArticle relatedArticle = relatedArticleArrayList.get(position);
            Log.i(Constant.TAG, "ID : " + relatedArticle.getRelated_article_id());
            Bundle bundle = new Bundle();
            bundle.putString("id", relatedArticle.getRelated_article_id());
            bundle.putString("kanal", relatedArticle.getKanal());
            bundle.putString("shared_url", relatedArticle.getShared_url());
            Intent intent = new Intent(getActivity(), ActDetailContentDefault.class);
            intent.putExtras(bundle);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(url_shared == null || url_shared.length() < 1) {
            try {
                if(Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_DETAIL + "/id/" + id) != null) {
                    cachedResponse = new String(Global.getInstance(getActivity()).
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
        ShareActionProvider myShareActionProvider = (ShareActionProvider)
                item.getActionProvider();
        Intent myIntent = new Intent();
        myIntent.setAction(Intent.ACTION_SEND);
        myIntent.putExtra(Intent.EXTRA_TEXT, url_shared);
        myIntent.setType("text/plain");
        myShareActionProvider.setShareIntent(myIntent);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.layout_ripple_view_detail_subkanal) {
            if(isInternetPresent) {
                rippleView.setVisibility(View.GONE);
                progressWheel.setVisibility(View.VISIBLE);
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

                                    setAnalytics(kanal, ids, title);

                                    tvTitleDetail.setText(title);
                                    tvDateDetail.setText(date_publish);
                                    tvContentDetail.setText(Html.fromHtml(content).toString());
                                    tvContentDetail.setMovementMethod(LinkMovementMethod.getInstance());
                                    tvReporterDetail.setText(reporter_name);
                                    Picasso.with(getActivity()).load(image_url).transform(new CropSquareTransformation()).into(ivThumbDetail);

                                    if(sliderContentImages.size() > 0) {
                                        imageSliderAdapter = new ImageSliderAdapter(getFragmentManager(), sliderContentImages);
                                        viewPager.setAdapter(imageSliderAdapter);
                                        viewPager.setCurrentItem(0);
                                        imageSliderAdapter.notifyDataSetChanged();
                                        linePageIndicator.setViewPager(viewPager);
                                        viewPager.setVisibility(View.VISIBLE);
                                        linePageIndicator.setVisibility(View.VISIBLE);
                                    }

                                    if(relatedArticleArrayList.size() > 0 || !relatedArticleArrayList.isEmpty()) {
                                        adapter = new RelatedAdapter(getActivity(), relatedArticleArrayList);
                                        listView.setAdapter(adapter);
                                        Constant.setListViewHeightBasedOnChildren(listView);
                                        adapter.notifyDataSetChanged();
                                        headerRelated.setVisibility(View.VISIBLE);
                                        if(kanals != null) {
                                            if(kanals.equalsIgnoreCase("bola")) {
                                                headerRelated.setBackgroundResource(R.color.color_bola);
                                            } else if(kanals.equalsIgnoreCase("vivalife")) {
                                                headerRelated.setBackgroundResource(R.color.color_life);
                                            } else {
                                                headerRelated.setBackgroundResource(R.color.color_news);
                                            }
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
                                                        if(getActivity() == null) {
                                                            return;
                                                        }
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                tvPreviewCommentUser.setText(commentArrayList.get(count).getUsername());
                                                                tvPreviewCommentContent.setText(commentArrayList.get(count).getComment_text());
                                                                count++;
                                                                if(count >= commentArrayList.size()) {
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

                                    getActivity().invalidateOptionsMenu();

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
                                progressWheel.setVisibility(View.GONE);
                                rippleView.setVisibility(View.VISIBLE);
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
                        });
                request.setShouldCache(true);
                request.setRetryPolicy(new DefaultRetryPolicy(
                        Constant.TIME_OUT,
                        0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Global.getInstance(getActivity()).getRequestQueue().getCache().invalidate(Constant.NEW_DETAIL + "/id/" + id, true);
                Global.getInstance(getActivity()).getRequestQueue().getCache().get(Constant.NEW_DETAIL + "/id/" + id);
                Global.getInstance(getActivity()).addToRequestQueue(request, Constant.JSON_REQUEST);
            } else {
                Toast.makeText(getActivity(), R.string.title_no_connection, Toast.LENGTH_SHORT).show();
                progressWheel.setVisibility(View.GONE);
                tvNoResult.setVisibility(View.VISIBLE);
            }
        } else if(view.getId() == R.id.thumb_detail_content) {
            if(image_url != null) {
                if(image_url.length() > 0) {
                    Bundle bundle = new Bundle();
                    bundle.putString("photoUrl", image_url);
                    bundle.putString("image_caption", image_caption);
                    Intent intent = new Intent(getActivity(), ActDetailPhotoThumb.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
                }
            }
        } else if(view.getId() == R.id.layout_preview_comment_list) {
            moveCommentPage();
        } else if(view.getId() == R.id.text_move_video) {
            moveVideoPage();
        }
    }

    private void setAnalytics(String channel_title, String id, String title) {
        if(kanals != null) {
            if(kanals.equalsIgnoreCase("bola")) {
                analytics.getAnalyticByATInternet(Constant.DETAIL_CONTENT_BOLA_PAGE
                        + channel_title.toUpperCase()
                        + "_"
                        + id
                        + "_"
                        + title.toUpperCase());
                analytics.getAnalyticByGoogleAnalytic(Constant.DETAIL_CONTENT_BOLA_PAGE
                        + channel_title.toUpperCase()
                        + "_"
                        + id
                        + "_"
                        + title.toUpperCase());
            } else if(kanals.equalsIgnoreCase("vivalife")) {
                analytics.getAnalyticByATInternet(Constant.DETAIL_CONTENT_LIFE_PAGE
                        + channel_title.toUpperCase()
                        + "_"
                        + id
                        + "_"
                        + title.toUpperCase());
                analytics.getAnalyticByGoogleAnalytic(Constant.DETAIL_CONTENT_LIFE_PAGE
                        + channel_title.toUpperCase()
                        + "_"
                        + id
                        + "_"
                        + title.toUpperCase());
            } else {
                analytics.getAnalyticByATInternet(Constant.DETAIL_CONTENT_NEWS_PAGE
                        + channel_title.toUpperCase()
                        + "_"
                        + id
                        + "_"
                        + title.toUpperCase());
                analytics.getAnalyticByGoogleAnalytic(Constant.DETAIL_CONTENT_NEWS_PAGE
                        + channel_title.toUpperCase()
                        + "_"
                        + id
                        + "_"
                        + title.toUpperCase());
            }
        }
    }

}
