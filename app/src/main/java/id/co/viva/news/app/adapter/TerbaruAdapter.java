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
import id.co.viva.news.app.model.News;

/**
 * Created by rezarachman on 01/10/14.
 */
public class TerbaruAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<News> newsArrayList;

    public TerbaruAdapter(Context context, ArrayList<News> newsArrayList) {
        this.context = context;
        this.newsArrayList = newsArrayList;
    }

    @Override
    public int getCount() {
        return newsArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return newsArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View rootView = LayoutInflater.from(context)
                .inflate(R.layout.item_latest_terbaru, viewGroup, false);

        ImageView icon_item_news = (ImageView) rootView.findViewById(R.id.image_item_news);
        ImageView icon_item_viva_news = (ImageView) rootView.findViewById(R.id.icon_headline_terbaru);
        TextView title_item_news = (TextView) rootView.findViewById(R.id.title_item_latest);
        TextView date_item_news = (TextView) rootView.findViewById(R.id.date_item_news);

        News news = newsArrayList.get(position);
        Picasso.with(context).load(news.getImage_url()).into(icon_item_news);
        title_item_news.setText(news.getTitle());

        if(news.getKanal().equalsIgnoreCase("bola")) {
            icon_item_viva_news.setImageResource(R.drawable.icon_viva_bola);
        } else if(news.getKanal().equalsIgnoreCase("vivalife")) {
            icon_item_viva_news.setImageResource(R.drawable.icon_viva_life);
        } else {
            icon_item_viva_news.setImageResource(R.drawable.icon_viva_news);
        }

        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = (Date)formatter.parse(news.getDate_publish());
            date_item_news.setText(Constant.getTimeAgo(date.getTime(), context));
        } catch (Exception e) {
            e.getMessage();
        }

        return rootView;
    }

}
