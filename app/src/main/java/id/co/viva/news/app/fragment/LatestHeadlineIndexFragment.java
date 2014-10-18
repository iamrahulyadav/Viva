package id.co.viva.news.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.R;
import id.co.viva.news.app.VivaApp;
import id.co.viva.news.app.activity.ActDetailHeadline;

/**
 * Created by rezarachman on 02/10/14.
 */
public class LatestHeadlineIndexFragment extends Fragment {

    private String title;
    private String url_image;
    private String id;
    private String url_shared;

    public static LatestHeadlineIndexFragment newInstance(String id, String title, String url_image, String url_shared) {
        LatestHeadlineIndexFragment latestHeadlineIndexFragment = new LatestHeadlineIndexFragment();
        Bundle args = new Bundle();
        args.putString("id", id);
        args.putString("title", title);
        args.putString("url_image", url_image);
        args.putString("url_shared", url_shared);
        latestHeadlineIndexFragment.setArguments(args);
        return latestHeadlineIndexFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getArguments().getString("id");
        title = getArguments().getString("title");
        url_image = getArguments().getString("url_image");
        url_shared = getArguments().getString("url_shared");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_latest_headline, container, false);
        TextView tvTitleHeadline = (TextView) view.findViewById(R.id.title_headline);
        ImageView ivImageHeadline = (ImageView) view.findViewById(R.id.image_headline);
        if(title != null) {
            tvTitleHeadline.setText(title);
        }
        if(url_image != null) {
            Picasso.with(getActivity()).load(url_image).into(ivImageHeadline);
            ivImageHeadline.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ivImageHeadline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(Constant.TAG, "Clicked : " + id + " " + title);
                    Bundle bundle = new Bundle();
                    bundle.putString("id", id);
                    bundle.putString("url_shared", url_shared);
                    Intent intent = new Intent(VivaApp.getInstance(), ActDetailHeadline.class);
                    intent.putExtras(bundle);
                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
                }
            });
        }
        return view;
    }

}
