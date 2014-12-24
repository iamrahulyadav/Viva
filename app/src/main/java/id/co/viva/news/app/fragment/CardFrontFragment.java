package id.co.viva.news.app.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import id.co.viva.news.app.R;

/**
 * Created by reza on 19/12/14.
 */
public class CardFrontFragment extends Fragment {

    private ImageView mFrontImage;
    private String photoUrl;
    private Context context;

    public CardFrontFragment(String photoUrl, Context context) {
        this.photoUrl = photoUrl;
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_card_front, container, false);
        mFrontImage = (ImageView) v.findViewById(R.id.image_card_front);
        if(photoUrl.length() > 0) {
            Picasso.with(context).load(photoUrl).into(mFrontImage);
        }
        return v;
    }

}
