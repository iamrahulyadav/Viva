package id.co.viva.news.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import id.co.viva.news.app.services.Analytics;
import id.co.viva.news.app.Constant;
import id.co.viva.news.app.R;
import id.co.viva.news.app.VivaApp;
import id.co.viva.news.app.activity.ActDetailContentDefault;
import id.co.viva.news.app.adapter.RelatedAdapter;
import id.co.viva.news.app.model.RelatedArticle;

/**
 * Created by reza on 24/10/14.
 */
public class DetailIndexContent extends Fragment implements AdapterView.OnItemClickListener {

    private String id;
    private String kanals;
    private String imageContent;
    private RelativeLayout headerRelated;
    private boolean isInternetPresent = false;
    private String cachedResponse;
    private RelativeLayout loading_layout;
    private TextView tvNoResult;
    private ArrayList<RelatedArticle> relatedArticleArrayList;
    private RelatedAdapter adapter;
    private ListView listView;
    private View imageContentLayout;
    private Analytics analytics;

    private String title;
    private String image_url;
    private String date_publish;
    private String content;
    private String reporter_name;
    private String url_shared;

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
        analytics = new Analytics();
        isInternetPresent = VivaApp.getInstance().getConnectionStatus().isConnectingToInternet();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_detail_content, container, false);

        setHasOptionsMenu(true);

        if(kanals != null) {
            if(kanals.equalsIgnoreCase("bola")) {
                analytics.getAnalyticByATInternet(Constant.DETAIL_CONTENT_BOLA_PAGE);
                analytics.getAnalyticByGoogleAnalytic(Constant.DETAIL_CONTENT_BOLA_PAGE);
            } else if(kanals.equalsIgnoreCase("vivalife")) {
                analytics.getAnalyticByATInternet(Constant.DETAIL_CONTENT_LIFE_PAGE);
                analytics.getAnalyticByGoogleAnalytic(Constant.DETAIL_CONTENT_LIFE_PAGE);
            } else {
                analytics.getAnalyticByATInternet(Constant.DETAIL_CONTENT_NEWS_PAGE);
                analytics.getAnalyticByGoogleAnalytic(Constant.DETAIL_CONTENT_NEWS_PAGE);
            }
        }

        loading_layout = (RelativeLayout) view.findViewById(R.id.loading_progress_layout);
        headerRelated = (RelativeLayout) view.findViewById(R.id.header_related_article);
        headerRelated.setVisibility(View.GONE);

        tvNoResult = (TextView) view.findViewById(R.id.text_no_result_detail_content);
        tvNoResult.setVisibility(View.GONE);

        listView = (ListView) view.findViewById(R.id.list_related_article);
        listView.setOnItemClickListener(this);
        relatedArticleArrayList = new ArrayList<RelatedArticle>();

        imageContentLayout = view.findViewById(R.id.image_content);

        final TextView tvTitleDetail = (TextView) view.findViewById(R.id.title_detail_content);
        final TextView tvDateDetail = (TextView) view.findViewById(R.id.date_detail_content);
        final TextView tvReporterDetail = (TextView) view.findViewById(R.id.reporter_detail_content);
        final TextView tvContentDetail = (TextView) view.findViewById(R.id.content_detail_content);

        final ImageView ivThumbDetail = (ImageView) view.findViewById(R.id.thumb_detail_content);
        ivThumbDetail.setFocusable(true);
        ivThumbDetail.setFocusableInTouchMode(true);
        ivThumbDetail.requestFocus();

        if(VivaApp.getInstance().getRequestQueue().getCache().get(Constant.URL_DETAIL + id) != null) {
            cachedResponse = new String(VivaApp.getInstance().
                    getRequestQueue().getCache().get(Constant.URL_DETAIL + id).data);
            Log.i(Constant.TAG, "CONTENT DETAIL CACHED : " + cachedResponse);
            try {
                JSONObject jsonObject = new JSONObject(cachedResponse);
                JSONObject response = jsonObject.getJSONObject(Constant.response);
                JSONObject detail = response.getJSONObject(Constant.detail);
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
                    Log.i(Constant.TAG, "RELATED ARTICLE CACHED : " + relatedArticleArrayList.get(i).getRelated_title());
                }

                tvTitleDetail.setText(title);
                tvDateDetail.setText(date_publish);
                tvContentDetail.setText(Html.fromHtml(content).toString());
                tvContentDetail.setMovementMethod(LinkMovementMethod.getInstance());

                Document doc = Jsoup.parse(content);
                Elements ele = doc.select("img");
                for (Element el : ele) {
                    ImageView imageView = new ImageView(VivaApp.getInstance());
                    imageContent = el.attr("src").replaceAll("[|?*<\">+\\[\\]']", "");
                    Log.i(Constant.TAG, "IMAGE CONTENT : " + imageContent);
                    Picasso.with(VivaApp.getInstance()).load(imageContent).into(imageView);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.gravity = Gravity.CENTER_HORIZONTAL;
                    imageView.setLayoutParams(params);
                    imageView.setPadding(0, 10, 0, 10);
                    ((LinearLayout) imageContentLayout).addView(imageView);
                }

                tvReporterDetail.setText(reporter_name);
                Picasso.with(VivaApp.getInstance()).load(image_url).into(ivThumbDetail);

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

                loading_layout.setVisibility(View.GONE);
            } catch (Exception e) {
                e.getMessage();
            }
        } else {
            if(isInternetPresent) {
                StringRequest request = new StringRequest(Request.Method.GET, Constant.URL_DETAIL + id,
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
                                        Log.i(Constant.TAG, "RELATED ARTICLE : " + relatedArticleArrayList.get(i).getRelated_title());
                                    }

                                    tvTitleDetail.setText(title);
                                    tvDateDetail.setText(date_publish);
                                    tvContentDetail.setText(Html.fromHtml(content).toString());
                                    tvContentDetail.setMovementMethod(LinkMovementMethod.getInstance());

                                    Document doc = Jsoup.parse(content);
                                    Elements ele = doc.select("img");
                                    for (Element el : ele) {
                                        ImageView imageView = new ImageView(VivaApp.getInstance());
                                        imageContent = el.attr("src").replaceAll("[|?*<\">+\\[\\]']", "");
                                        Log.i(Constant.TAG, "IMAGE CONTENT : " + imageContent);
                                        Picasso.with(VivaApp.getInstance()).load(imageContent).into(imageView);
                                        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT));
                                        imageView.setPadding(10, 10, 10, 10);
                                        ((LinearLayout) imageContentLayout).addView(imageView);
                                    }

                                    tvReporterDetail.setText(reporter_name);
                                    Picasso.with(VivaApp.getInstance()).load(image_url).into(ivThumbDetail);

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

                                if(VivaApp.getInstance().getRequestQueue().getCache().get(Constant.URL_DETAIL + id) != null) {
                                    cachedResponse = new String(VivaApp.getInstance().
                                            getRequestQueue().getCache().get(Constant.URL_DETAIL + id).data);
                                    Log.i(Constant.TAG, "CONTENT DETAIL CACHED : " + cachedResponse);
                                    try {
                                        JSONObject jsonObject = new JSONObject(cachedResponse);
                                        JSONObject response = jsonObject.getJSONObject(Constant.response);
                                        JSONObject detail = response.getJSONObject(Constant.detail);
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
                                            Log.i(Constant.TAG, "RELATED ARTICLE CACHED : " + relatedArticleArrayList.get(i).getRelated_title());
                                        }

                                        tvTitleDetail.setText(title);
                                        tvDateDetail.setText(date_publish);
                                        tvContentDetail.setText(Html.fromHtml(content).toString());
                                        tvContentDetail.setMovementMethod(LinkMovementMethod.getInstance());

                                        Document doc = Jsoup.parse(content);
                                        Elements ele = doc.select("img");
                                        for (Element el : ele) {
                                            ImageView imageView = new ImageView(VivaApp.getInstance());
                                            imageContent = el.attr("src").replaceAll("[|?*<\">+\\[\\]']", "");
                                            Log.i(Constant.TAG, "IMAGE CONTENT : " + imageContent);
                                            Picasso.with(VivaApp.getInstance()).load(imageContent).into(imageView);
                                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                            params.gravity = Gravity.CENTER_HORIZONTAL;
                                            imageView.setLayoutParams(params);
                                            imageView.setPadding(0, 10, 0, 10);
                                            ((LinearLayout) imageContentLayout).addView(imageView);
                                        }

                                        tvReporterDetail.setText(reporter_name);
                                        Picasso.with(VivaApp.getInstance()).load(image_url).into(ivThumbDetail);

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

                                        loading_layout.setVisibility(View.GONE);
                                    } catch (Exception e) {
                                        e.getMessage();
                                    }
                                } else {
                                    loading_layout.setVisibility(View.GONE);
                                    tvNoResult.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                request.setShouldCache(true);
                request.setRetryPolicy(new DefaultRetryPolicy(
                        Constant.TIME_OUT,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                VivaApp.getInstance().getRequestQueue().getCache().invalidate(Constant.URL_DETAIL + id, true);
                VivaApp.getInstance().getRequestQueue().getCache().get(Constant.URL_DETAIL + id);
                VivaApp.getInstance().addToRequestQueue(request, Constant.JSON_REQUEST);
            } else {
                Toast.makeText(VivaApp.getInstance(), R.string.title_no_connection, Toast.LENGTH_SHORT).show();
                loading_layout.setVisibility(View.GONE);
                tvNoResult.setVisibility(View.VISIBLE);
            }
        }

        return view;
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
            Intent intent = new Intent(VivaApp.getInstance(), ActDetailContentDefault.class);
            intent.putExtras(bundle);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_frag_detail, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        ShareActionProvider myShareActionProvider = (ShareActionProvider) item.getActionProvider();
        Intent myIntent = new Intent();
        myIntent.setAction(Intent.ACTION_SEND);
        myIntent.putExtra(Intent.EXTRA_TEXT, url_shared);
        myIntent.setType("text/plain");
        myShareActionProvider.setShareIntent(myIntent);
        super.onCreateOptionsMenu(menu, inflater);
    }

}
