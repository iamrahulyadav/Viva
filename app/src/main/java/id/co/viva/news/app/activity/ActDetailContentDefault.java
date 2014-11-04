package id.co.viva.news.app.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
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
import id.co.viva.news.app.adapter.RelatedAdapter;
import id.co.viva.news.app.model.RelatedArticle;

/**
 * Created by reza on 27/10/14.
 */
public class ActDetailContentDefault extends FragmentActivity implements AdapterView.OnItemClickListener {

    private String title;
    private String image_url;
    private String date_publish;
    private String content;
    private String reporter_name;

    private TextView tvTitleDetail;
    private TextView tvDateDetail;
    private TextView tvReporterDetail;
    private TextView tvContentDetail;
    private ImageView ivThumbDetail;

    private String id;
    private String imageContent;
    private String cachedResponse;
    private String typeFrom;
    private String fromkanal;
    private String shared_url;

    private View imageContentLayout;
    private RelativeLayout headerRelated;
    private boolean isInternetPresent = false;
    private TextView tvNoResult;
    private ArrayList<RelatedArticle> relatedArticleArrayList;
    private RelatedAdapter adapter;
    private ListView listView;
    private Analytics analytics;
//    private RelativeLayout loading_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        typeFrom = intent.getStringExtra("type");
        fromkanal = intent.getStringExtra("kanal");
        shared_url = intent.getStringExtra("shared_url");

        isInternetPresent = VivaApp.getInstance().
                getConnectionStatus().isConnectingToInternet();

        analytics = new Analytics();
        if(typeFrom != null) {
            if(typeFrom.equalsIgnoreCase("search")) {
                analytics.getAnalyticByATInternet(Constant.FROM_SEARCH_RESULT_DETAIL_CONTENT + fromkanal.toUpperCase());
                analytics.getAnalyticByGoogleAnalytic(Constant.FROM_SEARCH_RESULT_DETAIL_CONTENT + fromkanal.toUpperCase());
            }
        } else {
            analytics.getAnalyticByATInternet(Constant.FROM_RELATED_ARTICLE_DETAIL_CONTENT + "RELATED_ARTICLE");
            analytics.getAnalyticByGoogleAnalytic(Constant.FROM_RELATED_ARTICLE_DETAIL_CONTENT + "RELATED_ARTICLE");
        }

        setContentView(R.layout.item_detail_content_default);

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
            colorDrawable.setColor(getResources().getColor(R.color.header_grey));
            getActionBar().setBackgroundDrawable(colorDrawable);
        }

//        loading_layout = (RelativeLayout) findViewById(R.id.loading_progress_layout);
        headerRelated = (RelativeLayout) findViewById(R.id.header_related_article);
        headerRelated.setVisibility(View.GONE);
        tvNoResult = (TextView) findViewById(R.id.text_no_result_detail_content);
        tvNoResult.setVisibility(View.GONE);

        listView = (ListView) findViewById(R.id.list_related_article_default);
        listView.setOnItemClickListener(this);
        relatedArticleArrayList = new ArrayList<RelatedArticle>();

        imageContentLayout = findViewById(R.id.image_content);

        tvTitleDetail = (TextView) findViewById(R.id.title_detail_content_default);
        tvDateDetail = (TextView) findViewById(R.id.date_detail_content_default);
        tvReporterDetail = (TextView) findViewById(R.id.reporter_detail_content_default);
        tvContentDetail = (TextView) findViewById(R.id.content_detail_content_default);

        ivThumbDetail = (ImageView) findViewById(R.id.thumb_detail_content_default);
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

//                loading_layout.setVisibility(View.GONE);
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
                                        adapter = new RelatedAdapter(VivaApp.getInstance(), relatedArticleArrayList);
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

//                                    loading_layout.setVisibility(View.GONE);
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
                                Toast.makeText(VivaApp.getInstance(), R.string.title_no_connection, Toast.LENGTH_SHORT).show();
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
//                loading_layout.setVisibility(View.GONE);
                tvNoResult.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail_headline, menu);
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
            bundle.putString("id", relatedArticles.getId());
            bundle.putString("type", typeFrom);
            bundle.putString("kanal", relatedArticles.getKanal());
            bundle.putString("shared_url", relatedArticles.getShared_url());
            Intent intent = new Intent(VivaApp.getInstance(), ActDetailContentDefault.class);
            intent.putExtras(bundle);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
        }
    }

}
