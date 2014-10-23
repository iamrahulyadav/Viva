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
import id.co.viva.news.app.model.FeaturedLife;

/**
 * Created by reza on 22/10/14.
 */
public class FeaturedLifeAdapter extends BaseAdapter {

    private ArrayList<FeaturedLife> subNewsArrayList;
    private Context context;

    public FeaturedLifeAdapter(Context context, ArrayList<FeaturedLife> subNewsArrayList) {
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
        ViewHolder holder;
        if(view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.item_grid_life, null);
            holder = new ViewHolder();

            holder.title_kanal = (TextView) view.findViewById(R.id.item_title_kanal_life);
            holder.thumb_featured = (ImageView) view.findViewById(R.id.item_thumb_life);

            FeaturedLife featuredNews = subNewsArrayList.get(position);
            holder.title_kanal.setText(featuredNews.getChannel_title().toUpperCase());
            if(featuredNews.getImage_url().length() > 0) {
                Picasso.with(context).load(featuredNews.getImage_url()).into(holder.thumb_featured);
            }

            view.setTag(holder);
        }
        return view;
    }

    private static class ViewHolder {
        private ImageView thumb_featured;
        private TextView title_kanal;
    }

}
