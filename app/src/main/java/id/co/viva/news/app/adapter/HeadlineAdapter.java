package id.co.viva.news.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.R;
import id.co.viva.news.app.component.CropSquareTransformation;
import id.co.viva.news.app.model.Headline;

/**
 * Created by rezarachman on 01/10/14.
 */
public class HeadlineAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Headline> headlineArrayList;

    public HeadlineAdapter(Context context, ArrayList<Headline> headlineArrayList) {
        this.context = context;
        this.headlineArrayList = headlineArrayList;
    }

    @Override
    public int getCount() {
        return headlineArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return headlineArrayList.get(position);
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
            view = inflater.inflate(R.layout.item_latest_terbaru, null);
            holder = new ViewHolder();
            holder.icon_item_news = (ImageView) view.findViewById(R.id.image_item_news);
            holder.icon_item_viva_news = (ImageView) view.findViewById(R.id.icon_headline_terbaru);
            holder.title_item_news = (TextView) view.findViewById(R.id.title_item_latest);
            holder.date_item_news = (TextView) view.findViewById(R.id.date_item_news);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Headline headline = headlineArrayList.get(position);
        if(headline.getImage_url().length() > 0) {
            Picasso.with(context).load(headline.getImage_url())
                    .transform(new CropSquareTransformation()).into(holder.icon_item_news);
        }
        holder.title_item_news.setText(headline.getTitle());

        if(headline.getKanal().equalsIgnoreCase("bola")) {
            holder.icon_item_viva_news.setImageResource(R.drawable.icon_viva_bola);
        } else if(headline.getKanal().equalsIgnoreCase("vivalife")) {
            holder.icon_item_viva_news.setImageResource(R.drawable.icon_viva_life);
        } else {
            holder.icon_item_viva_news.setImageResource(R.drawable.icon_viva_news);
        }

        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = (Date)formatter.parse(headline.getDate_publish());
            holder.date_item_news.setText(Constant.getTimeAgo(date.getTime()));
        } catch (Exception e) {
            e.getMessage();
        }

        return view;
    }

    private static class ViewHolder {
        public TextView title_item_news;
        public TextView date_item_news;
        public ImageView icon_item_news;
        public ImageView icon_item_viva_news;
    }

}
