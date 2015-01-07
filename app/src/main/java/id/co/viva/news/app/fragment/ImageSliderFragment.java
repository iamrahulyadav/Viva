package id.co.viva.news.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import id.co.viva.news.app.R;
import id.co.viva.news.app.activity.ActDetailPhotoThumb;

/**
 * Created by reza on 06/01/15.
 */
public class ImageSliderFragment extends Fragment implements View.OnClickListener {

    private ImageView imageView;
    private TextView textView;
    private String mPhotoUrl;
    private String mTitle;

    public static ImageSliderFragment newInstance(String photo_url, String title) {
        ImageSliderFragment imageSliderFragment = new ImageSliderFragment();
        Bundle bundle = new Bundle();
        bundle.putString("photo_url", photo_url);
        bundle.putString("title", title);
        imageSliderFragment.setArguments(bundle);
        return imageSliderFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotoUrl = getArguments().getString("photo_url");
        mTitle = getArguments().getString("title");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_slider_detail_image, container, false);
        imageView = (ImageView) rootView.findViewById(R.id.image_item_slider_detail);
        imageView.setOnClickListener(this);
        textView = (TextView) rootView.findViewById(R.id.title_image_item_slider_detail);
        if(mPhotoUrl.length() > 0) {
            Picasso.with(getActivity()).load(mPhotoUrl).into(imageView);
        }
        if(mTitle.length() > 0) {
            textView.setText(mTitle);
        }
        return rootView;
    }

    private void toDetailThumbnail() {
        Bundle bundle = new Bundle();
        bundle.putString("photoUrl", mPhotoUrl);
        bundle.putString("image_caption", mTitle);
        Intent intent = new Intent(getActivity(), ActDetailPhotoThumb.class);
        intent.putExtras(bundle);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.image_item_slider_detail) {
            toDetailThumbnail();
        }
    }

}
