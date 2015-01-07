package id.co.viva.news.app.fragment;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
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

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.activity.ActComment;
import id.co.viva.news.app.activity.ActDetailContentDefault;
import id.co.viva.news.app.activity.ActDetailPhotoThumb;
import id.co.viva.news.app.activity.ActRating;
import id.co.viva.news.app.adapter.RelatedAdapter;
import id.co.viva.news.app.coachmark.CoachmarkBuilder;
import id.co.viva.news.app.coachmark.CoachmarkView;
import id.co.viva.news.app.model.Comment;
import id.co.viva.news.app.model.Favorites;
import id.co.viva.news.app.model.RelatedArticle;
import id.co.viva.news.app.services.Analytics;

/**
 * Created by root on 07/10/14.
 */
public class DetailHeadlineIndexFragment extends Fragment implements View.OnClickListener,
        AdapterView.OnItemClickListener {

    private String id;
    private String imageContent;
    private View imageContentLayout;
    private RelativeLayout headerRelated;
    private boolean isInternetPresent = false;
    private String cachedResponse;
    private RelativeLayout loading_layout;
    private TextView tvNoResult;
    private ArrayList<RelatedArticle> relatedArticleArrayList;
    private ArrayList<Comment> commentArrayList;
    private RelatedAdapter adapter;
    private ListView listView;
    private Analytics analytics;
    private RippleView rippleView;
    private String favoriteList;
    private ArrayList<Favorites> favoritesArrayList;

    private TextView tvTitleHeadlineDetail;
    private TextView tvDateHeadlineDetail;
    private TextView tvReporterHeadlineDetail;
    private TextView tvContentHeadlineDetail;
    private KenBurnsView ivThumbDetailHeadline;
    private TextView tvPreviewCommentUser;
    private TextView tvPreviewCommentContent;
    private LinearLayout layoutCommentPreview;
    private int count = 0;

    private View coachmarkView;
    private CoachmarkView showtips;

    private String ids;
    private String title;
    private String channel_id;
    private String kanal;
    private String image_url;
    private String date_publish;
    private String content;
    private String reporter_name;
    private String url_shared;

    public static DetailHeadlineIndexFragment newInstance(String id) {
        DetailHeadlineIndexFragment detailHeadlineIndexFragment = new DetailHeadlineIndexFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        detailHeadlineIndexFragment.setArguments(bundle);
        return detailHeadlineIndexFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isInternetPresent = Global.getInstance(getActivity()).getConnectionStatus().isConnectingToInternet();
        id = getArguments().getString("id");
    }

    private void defineViews(View view) {
        layoutCommentPreview = (LinearLayout) view.findViewById(R.id.layout_preview_comment_list);
        layoutCommentPreview.setOnClickListener(this);
        layoutCommentPreview.setVisibility(View.GONE);
        tvPreviewCommentContent = (TextView) view.findViewById(R.id.text_preview_comment_content);
        tvPreviewCommentUser = (TextView) view.findViewById(R.id.text_preview_comment_user);
        relatedArticleArrayList = new ArrayList<RelatedArticle>();
        commentArrayList = new ArrayList<Comment>();
        loading_layout = (RelativeLayout) view.findViewById(R.id.loading_progress_layout);
        headerRelated = (RelativeLayout) view.findViewById(R.id.header_related_article_headline);
        headerRelated.setVisibility(View.GONE);
        tvNoResult = (TextView) view.findViewById(R.id.text_no_result_detail_headline);
        tvNoResult.setVisibility(View.GONE);
        rippleView = (RippleView) view.findViewById(R.id.layout_ripple_view_headline_terbaru);
        rippleView.setVisibility(View.GONE);
        rippleView.setOnClickListener(this);
        imageContentLayout = view.findViewById(R.id.image_content);
        listView = (ListView) view.findViewById(R.id.list_related_article_headline);
        listView.setOnItemClickListener(this);
        coachmarkView = view.findViewById(R.id.coachmark_detail);
        tvTitleHeadlineDetail = (TextView) view.findViewById(R.id.title_detail_headline);
        tvDateHeadlineDetail = (TextView) view.findViewById(R.id.date_detail_headline);
        tvReporterHeadlineDetail = (TextView) view.findViewById(R.id.reporter_detail_headline);
        tvContentHeadlineDetail = (TextView) view.findViewById(R.id.content_detail_headline);
        ivThumbDetailHeadline = (KenBurnsView) view.findViewById(R.id.thumb_detail_headline);
        ivThumbDetailHeadline.setOnClickListener(this);
        ivThumbDetailHeadline.setFocusable(true);
        ivThumbDetailHeadline.setFocusableInTouchMode(true);
        ivThumbDetailHeadline.requestFocus();
        showCoachMark();
    }

    private void getAnalytics() {
        analytics = new Analytics(getActivity());
        analytics.getAnalyticByATInternet(Constant.HEADLINE_DETAIL_PAGE);
        analytics.getAnalyticByGoogleAnalytic(Constant.HEADLINE_DETAIL_PAGE);
    }

    private void showCoachMark() {
        if(Global.getInstance(getActivity()).getSharedPreferences(getActivity()).getBoolean(Constant.FIRST_INSTALL_DETAIL, true)) {
            RelativeLayout relativeLayout = new RelativeLayout(getActivity());
            relativeLayout.setLayoutParams(new ViewGroup.LayoutParams(70, 70));
            ((RelativeLayout) coachmarkView).addView(relativeLayout);
            showtips = new CoachmarkBuilder(getActivity())
                    .setTarget(relativeLayout, getWidthFocus(), getHeightFocus(), 60)
                    .setTitle(getResources().getString(R.string.label_action_menu))
                    .setDescription(getResources().getString(R.string.label_action_menu_desc))
                    .build();
            showtips.show(getActivity());
            Global.getInstance(getActivity()).getSharedPreferences(getActivity()).
                    edit().putBoolean(Constant.FIRST_INSTALL_DETAIL, false).commit();
        }
    }

    private int getHeightFocus() {
        int actionBarHeight = 0;
        int heightFocus;
        TypedValue typedValue = new TypedValue();
        if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(typedValue.data,getResources().getDisplayMetrics());
        }
        heightFocus = 0 - (actionBarHeight / 2);
        return heightFocus;
    }

    private int getWidthFocus() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return width - 75;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_detail_headline, container, false);

        setHasOptionsMenu(true);
        defineViews(view);
        getAnalytics();

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
                                    Log.i(Constant.TAG, "RELATED ARTICLE HEADLNE : " + relatedArticleArrayList.get(i).getRelated_title());
                                }

                                tvTitleHeadlineDetail.setText(title);
                                tvDateHeadlineDetail.setText(date_publish);
                                tvContentHeadlineDetail.setText(Html.fromHtml(content).toString());
                                tvContentHeadlineDetail.setMovementMethod(LinkMovementMethod.getInstance());

                                Document doc = Jsoup.parse(content);
                                Elements ele = doc.select("img");
                                for (Element el : ele) {
                                    ImageView imageView = new ImageView(getActivity());
                                    imageContent = el.attr("src").replaceAll("[|?*<\">+\\[\\]']", "");
                                    Log.i(Constant.TAG, "IMAGE CONTENT : " + imageContent);
                                    Picasso.with(getActivity()).load(imageContent).into(imageView);
                                    imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT));
                                    imageView.setPadding(10, 10, 10, 10);
                                    ((LinearLayout) imageContentLayout).addView(imageView);
                                }

                                tvReporterHeadlineDetail.setText(reporter_name);
                                Picasso.with(getActivity()).load(image_url).into(ivThumbDetailHeadline);

                                if(relatedArticleArrayList.size() > 0 || !relatedArticleArrayList.isEmpty()) {
                                    adapter = new RelatedAdapter(getActivity(), relatedArticleArrayList);
                                    listView.setAdapter(adapter);
                                    Constant.setListViewHeightBasedOnChildren(listView);
                                    adapter.notifyDataSetChanged();
                                    headerRelated.setVisibility(View.VISIBLE);
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
                                loading_layout.setVisibility(View.GONE);
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
                                Log.i(Constant.TAG, "HEADLINES DETAIL CACHED : " + cachedResponse);
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
                                        Log.i(Constant.TAG, "RELATED ARTICLE HEADLINE CACHED : " + relatedArticleArrayList.get(i).getRelated_title());
                                    }

                                    tvTitleHeadlineDetail.setText(title);
                                    tvDateHeadlineDetail.setText(date_publish);
                                    tvContentHeadlineDetail.setText(Html.fromHtml(content).toString());
                                    tvContentHeadlineDetail.setMovementMethod(LinkMovementMethod.getInstance());

                                    Document doc = Jsoup.parse(content);
                                    Elements ele = doc.select("img");
                                    for (Element el : ele) {
                                        ImageView imageView = new ImageView(getActivity());
                                        imageContent = el.attr("src").replaceAll("[|?*<\">+\\[\\]']", "");
                                        Log.i(Constant.TAG, "IMAGE CONTENT : " + imageContent);
                                        Picasso.with(getActivity()).load(imageContent).into(imageView);
                                        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT));
                                        imageView.setPadding(10, 10, 10, 10);
                                        ((LinearLayout) imageContentLayout).addView(imageView);
                                    }

                                    tvReporterHeadlineDetail.setText(reporter_name);
                                    Picasso.with(getActivity()).load(image_url).into(ivThumbDetailHeadline);

                                    if(relatedArticleArrayList.size() > 0 || !relatedArticleArrayList.isEmpty()) {
                                        adapter = new RelatedAdapter(getActivity(), relatedArticleArrayList);
                                        listView.setAdapter(adapter);
                                        Constant.setListViewHeightBasedOnChildren(listView);
                                        adapter.notifyDataSetChanged();
                                        headerRelated.setVisibility(View.VISIBLE);
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

                                    loading_layout.setVisibility(View.GONE);
                                } catch (Exception e) {
                                    e.getMessage();
                                }
                            } else {
                                loading_layout.setVisibility(View.GONE);
                                rippleView.setVisibility(View.VISIBLE);
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
                Log.i(Constant.TAG, "HEADLINES DETAIL CACHED : " + cachedResponse);
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
                        Log.i(Constant.TAG, "RELATED ARTICLE HEADLINE CACHED : " + relatedArticleArrayList.get(i).getRelated_title());
                    }

                    tvTitleHeadlineDetail.setText(title);
                    tvDateHeadlineDetail.setText(date_publish);
                    tvContentHeadlineDetail.setText(Html.fromHtml(content).toString());
                    tvContentHeadlineDetail.setMovementMethod(LinkMovementMethod.getInstance());

                    Document doc = Jsoup.parse(content);
                    Elements ele = doc.select("img");
                    for (Element el : ele) {
                        ImageView imageView = new ImageView(getActivity());
                        imageContent = el.attr("src").replaceAll("[|?*<\">+\\[\\]']", "");
                        Log.i(Constant.TAG, "IMAGE CONTENT : " + imageContent);
                        Picasso.with(getActivity()).load(imageContent).into(imageView);
                        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                        imageView.setPadding(10, 10, 10, 10);
                        ((LinearLayout) imageContentLayout).addView(imageView);
                    }

                    tvReporterHeadlineDetail.setText(reporter_name);
                    Picasso.with(getActivity()).load(image_url).into(ivThumbDetailHeadline);

                    if(relatedArticleArrayList.size() > 0 || !relatedArticleArrayList.isEmpty()) {
                        adapter = new RelatedAdapter(getActivity(), relatedArticleArrayList);
                        listView.setAdapter(adapter);
                        Constant.setListViewHeightBasedOnChildren(listView);
                        adapter.notifyDataSetChanged();
                        headerRelated.setVisibility(View.VISIBLE);
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

                    loading_layout.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(getActivity(), R.string.title_no_connection, Toast.LENGTH_SHORT).show();
                loading_layout.setVisibility(View.GONE);
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
        Intent intent = new Intent(getActivity(), ActComment.class);
        intent.putExtras(bundle);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
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
                                image_url, date_publish, reporter_name, url_shared, content));
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.layout_ripple_view_headline_terbaru) {
            if(isInternetPresent) {
                rippleView.setVisibility(View.GONE);
                loading_layout.setVisibility(View.VISIBLE);
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
                                        Log.i(Constant.TAG, "RELATED ARTICLE HEADLNE : " + relatedArticleArrayList.get(i).getRelated_title());
                                    }

                                    tvTitleHeadlineDetail.setText(title);
                                    tvDateHeadlineDetail.setText(date_publish);
                                    tvContentHeadlineDetail.setText(Html.fromHtml(content).toString());
                                    tvContentHeadlineDetail.setMovementMethod(LinkMovementMethod.getInstance());

                                    Document doc = Jsoup.parse(content);
                                    Elements ele = doc.select("img");
                                    for (Element el : ele) {
                                        ImageView imageView = new ImageView(getActivity());
                                        imageContent = el.attr("src").replaceAll("[|?*<\">+\\[\\]']", "");
                                        Log.i(Constant.TAG, "IMAGE CONTENT : " + imageContent);
                                        Picasso.with(getActivity()).load(imageContent).into(imageView);
                                        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT));
                                        imageView.setPadding(10, 10, 10, 10);
                                        ((LinearLayout) imageContentLayout).addView(imageView);
                                    }

                                    tvReporterHeadlineDetail.setText(reporter_name);
                                    Picasso.with(getActivity()).load(image_url).into(ivThumbDetailHeadline);

                                    if(relatedArticleArrayList.size() > 0 || !relatedArticleArrayList.isEmpty()) {
                                        adapter = new RelatedAdapter(getActivity(), relatedArticleArrayList);
                                        listView.setAdapter(adapter);
                                        Constant.setListViewHeightBasedOnChildren(listView);
                                        adapter.notifyDataSetChanged();
                                        headerRelated.setVisibility(View.VISIBLE);
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
                                    loading_layout.setVisibility(View.GONE);
                                } catch (Exception e) {
                                    e.getMessage();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                volleyError.getMessage();
                                loading_layout.setVisibility(View.GONE);
                                rippleView.setVisibility(View.VISIBLE);
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
                loading_layout.setVisibility(View.GONE);
                tvNoResult.setVisibility(View.VISIBLE);
            }
        } else if(view.getId() == R.id.thumb_detail_headline) {
            if(image_url.length() > 0) {
                Bundle bundle = new Bundle();
                bundle.putString("photoUrl", image_url);
                Intent intent = new Intent(getActivity(), ActDetailPhotoThumb.class);
                intent.putExtras(bundle);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
            }
        } else if(view.getId() == R.id.layout_preview_comment_list) {
            moveCommentPage();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if(relatedArticleArrayList.size() > 0) {
            RelatedArticle relatedArticle = relatedArticleArrayList.get(position);
            Log.i(Constant.TAG, "ID : " + relatedArticle.getId());
            Bundle bundle = new Bundle();
            bundle.putString("id", relatedArticle.getRelated_article_id());
            bundle.putString("kanal", relatedArticle.getKanal());
            bundle.putString("shared_url", relatedArticle.getShared_url());
            Intent intent = new Intent(getActivity(), ActDetailContentDefault.class);
            intent.putExtras(bundle);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
        }
    }

}
