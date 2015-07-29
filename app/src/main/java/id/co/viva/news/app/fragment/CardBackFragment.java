package id.co.viva.news.app.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import id.co.viva.news.app.R;
import id.co.viva.news.app.component.CropSquareTransformation;

/**
 * Created by reza on 19/12/14.
 */
public class CardBackFragment extends Fragment {

    private String photoUrl;
    private Context context;

    public CardBackFragment(String photoUrl, Context context) {
        this.photoUrl = photoUrl;
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_card_back, container, false);
        ImageView mFrontImage = (ImageView) v.findViewById(R.id.image_card_back);
        if (photoUrl.length() > 0) {
            Picasso.with(context).load(photoUrl)
                    .transform(new CropSquareTransformation()).into(mFrontImage);
        }
        return v;
    }

}
