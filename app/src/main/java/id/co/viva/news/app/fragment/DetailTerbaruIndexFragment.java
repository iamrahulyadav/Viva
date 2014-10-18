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
 * Created by reza on 15/10/14.
 */
public class DetailTerbaruIndexFragment extends Fragment {

    private String id;
    private RelativeLayout layoutSubkanal;
    private Boolean isInternetPresent = false;

    private String title;
    private String kanal;
    private String image_url;
    private String date_publish;
    private String content;
    private String reporter_name;

    public static DetailTerbaruIndexFragment newInstance(String id) {
        DetailTerbaruIndexFragment detailHeadlineIndexFragment = new DetailTerbaruIndexFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        detailHeadlineIndexFragment.setArguments(bundle);
        return detailHeadlineIndexFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getArguments().getString("id");
        isInternetPresent = VivaApp.getInstance().getConnectionStatus().isConnectingToInternet();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_detail_latest, container, false);

        layoutSubkanal = (RelativeLayout) view.findViewById(R.id.layout_subkanal_detail_news);
        final TextView tvTitleNewsDetail = (TextView) view.findViewById(R.id.title_detail_news);
        final TextView tvSubkanalTitleNewsDetail = (TextView) view.findViewById(R.id.title_subkanal_detail_news);
        final TextView tvDateNewsDetail = (TextView) view.findViewById(R.id.date_detail_news);
        final TextView tvReporterNewsDetail = (TextView) view.findViewById(R.id.reporter_detail_news);
        final TextView tvContentNewsDetail = (TextView) view.findViewById(R.id.content_detail_news);
        final ImageView ivThumbDetailNews = (ImageView) view.findViewById(R.id.thumb_detail_news);

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

                                tvTitleNewsDetail.setText(title);
                                tvSubkanalTitleNewsDetail.setText(kanal);
                                tvDateNewsDetail.setText(date_publish);
                                tvContentNewsDetail.setText(Html.fromHtml(content));
                                tvContentNewsDetail.setMovementMethod(LinkMovementMethod.getInstance());
                                tvReporterNewsDetail.setText(reporter_name);
                                Picasso.with(VivaApp.getInstance()).load(image_url).into(ivThumbDetailNews);

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
                    3000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VivaApp.getInstance().getRequestQueue().getCache().invalidate(Constant.URL_HOMEPAGE, true);
            VivaApp.getInstance().getRequestQueue().getCache().get(Constant.URL_HOMEPAGE);
            VivaApp.getInstance().addToRequestQueue(request, Constant.JSON_REQUEST);
        }

        return view;
    }
}
