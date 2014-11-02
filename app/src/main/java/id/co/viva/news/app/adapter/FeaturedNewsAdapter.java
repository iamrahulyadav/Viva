package id.co.viva.news.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import id.co.viva.news.app.R;
import id.co.viva.news.app.model.FeaturedNews;
import id.co.viva.news.app.model.News;

/**
 * Created by reza on 15/10/14.
 */
public class FeaturedNewsAdapter extends BaseAdapter {

    private ArrayList<FeaturedNews> subNewsArrayList;
    private Context context;

    public FeaturedNewsAdapter(Context context, ArrayList<FeaturedNews> subNewsArrayList) {
        this.context = context;
        this.subNewsArrayList = subNewsArrayList;
    }

    @Override
    public int getCount() {
        return subNewsArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return subNewsArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View rootView = LayoutInflater.from(context)
                .inflate(R.layout.item_grid_news, viewGroup, false);
        ImageView thumb_featured = (ImageView) rootView.findViewById(R.id.item_thumb_news);
        TextView title_kanal = (TextView) rootView.findViewById(R.id.item_title_kanal_news);
        FeaturedNews featuredNews = subNewsArrayList.get(position);
        title_kanal.setText(featuredNews.getChannel_title().toUpperCase());
        if(featuredNews.getImage_url().length() > 0) {
            Picasso.with(context).load(featuredNews.getImage_url()).into(thumb_featured);
        } else {
            thumb_featured.setImageResource(R.drawable.default_image);
        }
        return rootView;
    }

}
