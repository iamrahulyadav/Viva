package id.co.viva.news.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.R;
import id.co.viva.news.app.VivaApp;

/**
 * Created by root on 07/10/14.
 */
public class DetailHeadlineIndexFragment extends Fragment {

    private String id;
    private RelativeLayout layoutSubkanal;
    private boolean isInternetPresent = false;
    private String cachedResponse;
    private RelativeLayout loading_layout;
    private TextView tvNoResult;

    private String title;
    private String kanal;
    private String image_url;
    private String date_publish;
    private String content;
    private String reporter_name;

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
        isInternetPresent = VivaApp.getInstance().getConnectionStatus().isConnectingToInternet();
        id = getArguments().getString("id");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_detail_headline, container, false);

        loading_layout = (RelativeLayout) view.findViewById(R.id.loading_progress_layout);
        tvNoResult = (TextView) view.findViewById(R.id.text_no_result_detail_headline);
        tvNoResult.setVisibility(View.GONE);

        layoutSubkanal = (RelativeLayout) view.findViewById(R.id.layout_subkanal_detail_headline);
        final TextView tvTitleHeadlineDetail = (TextView) view.findViewById(R.id.title_detail_headline);
        final TextView tvSubkanalTitleHeadlineDetail = (TextView) view.findViewById(R.id.title_subkanal_detail_headline);
        final TextView tvDateHeadlineDetail = (TextView) view.findViewById(R.id.date_detail_headline);
        final TextView tvReporterHeadlineDetail = (TextView) view.findViewById(R.id.reporter_detail_headline);
        final TextView tvContentHeadlineDetail = (TextView) view.findViewById(R.id.content_detail_headline);
        final ImageView ivThumbDetailHeadline = (ImageView) view.findViewById(R.id.thumb_detail_headline);

        if(VivaApp.getInstance().getRequestQueue().getCache().get(Constant.URL_DETAIL + id) != null) {
            cachedResponse = new String(VivaApp.getInstance().
                    getRequestQueue().getCache().get(Constant.URL_DETAIL + id).data);
            Log.i(Constant.TAG, "HEADLINES DETAIL CACHED : " + cachedResponse);
            try {
                JSONObject jsonObject = new JSONObject(cachedResponse);
                JSONObject response = jsonObject.getJSONObject(Constant.response);
                JSONObject detail = response.getJSONObject(Constant.detail);
                title = detail.getString(Constant.title);
                kanal = detail.getString(Constant.kanal);
                image_url = detail.getString(Constant.image_url);
                date_publish = detail.getString(Constant.date_publish);
                content = detail.getString(Constant.content);
                reporter_name = detail.getString(Constant.reporter_name);

                tvTitleHeadlineDetail.setText(title);
                tvSubkanalTitleHeadlineDetail.setText(kanal);
                tvDateHeadlineDetail.setText(date_publish);
                tvContentHeadlineDetail.setText(Html.fromHtml(content));
                tvContentHeadlineDetail.setMovementMethod(LinkMovementMethod.getInstance());
                tvReporterHeadlineDetail.setText(reporter_name);
                Picasso.with(VivaApp.getInstance()).load(image_url).into(ivThumbDetailHeadline);

                if(kanal.equals("politik")) {
                    layoutSubkanal.setBackgroundResource(R.color.color_news);
                } else if(kanal.equals("bisnis")) {
                    layoutSubkanal.setBackgroundResource(R.color.color_news);
                } else if(kanal.equals("nasional")) {
                    layoutSubkanal.setBackgroundResource(R.color.color_auto);
                } else if(kanal.equals("metro")) {
                    layoutSubkanal.setBackgroundResource(R.color.color_auto);
                } else if(kanal.equals("dunia")) {
                    layoutSubkanal.setBackgroundResource(R.color.color_log);
                } else if(kanal.equals("sorot")) {
                    layoutSubkanal.setBackgroundResource(R.color.color_life);
                } else if(kanal.equals("fokus")) {
                    layoutSubkanal.setBackgroundResource(R.color.color_jualbeli);
                } else if(kanal.equals("wawancara")) {
                    layoutSubkanal.setBackgroundResource(R.color.color_forum);
                } else if(kanal.equals("sainstek")) {
                    layoutSubkanal.setBackgroundResource(R.color.color_bola);
                } else if(kanal.equals("bola")) {
                    layoutSubkanal.setBackgroundResource(R.color.color_bola);
                } else if(kanal.equals("vlog")) {
                    layoutSubkanal.setBackgroundResource(R.color.color_log);
                } else if(kanal.equals("lifestyle")) {
                    layoutSubkanal.setBackgroundResource(R.color.color_log);
                } else if(kanal.equals("travel")) {
                    layoutSubkanal.setBackgroundResource(R.color.color_life);
                } else if(kanal.equals("foto")) {
                    layoutSubkanal.setBackgroundResource(R.color.color_jualbeli);
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
                                    kanal = detail.getString(Constant.kanal);
                                    image_url = detail.getString(Constant.image_url);
                                    date_publish = detail.getString(Constant.date_publish);
                                    content = detail.getString(Constant.content);
                                    reporter_name = detail.getString(Constant.reporter_name);

                                    tvTitleHeadlineDetail.setText(title);
                                    tvSubkanalTitleHeadlineDetail.setText(kanal);
                                    tvDateHeadlineDetail.setText(date_publish);
                                    tvContentHeadlineDetail.setText(Html.fromHtml(content));
                                    tvContentHeadlineDetail.setMovementMethod(LinkMovementMethod.getInstance());
                                    tvReporterHeadlineDetail.setText(reporter_name);
                                    Picasso.with(VivaApp.getInstance()).load(image_url).into(ivThumbDetailHeadline);

                                    if(kanal.equals("politik")) {
                                        layoutSubkanal.setBackgroundResource(R.color.color_news);
                                    } else if(kanal.equals("bisnis")) {
                                        layoutSubkanal.setBackgroundResource(R.color.color_news);
                                    } else if(kanal.equals("nasional")) {
                                        layoutSubkanal.setBackgroundResource(R.color.color_auto);
                                    } else if(kanal.equals("metro")) {
                                        layoutSubkanal.setBackgroundResource(R.color.color_auto);
                                    } else if(kanal.equals("dunia")) {
                                        layoutSubkanal.setBackgroundResource(R.color.color_log);
                                    } else if(kanal.equals("sorot")) {
                                        layoutSubkanal.setBackgroundResource(R.color.color_life);
                                    } else if(kanal.equals("fokus")) {
                                        layoutSubkanal.setBackgroundResource(R.color.color_jualbeli);
                                    } else if(kanal.equals("wawancara")) {
                                        layoutSubkanal.setBackgroundResource(R.color.color_forum);
                                    } else if(kanal.equals("sainstek")) {
                                        layoutSubkanal.setBackgroundResource(R.color.color_bola);
                                    } else if(kanal.equals("bola")) {
                                        layoutSubkanal.setBackgroundResource(R.color.color_bola);
                                    } else if(kanal.equals("vlog")) {
                                        layoutSubkanal.setBackgroundResource(R.color.color_log);
                                    } else if(kanal.equals("lifestyle")) {
                                        layoutSubkanal.setBackgroundResource(R.color.color_log);
                                    } else if(kanal.equals("travel")) {
                                        layoutSubkanal.setBackgroundResource(R.color.color_life);
                                    } else if(kanal.equals("foto")) {
                                        layoutSubkanal.setBackgroundResource(R.color.color_jualbeli);
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

}
