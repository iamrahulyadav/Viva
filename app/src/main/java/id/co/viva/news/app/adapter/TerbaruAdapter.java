package id.co.viva.news.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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

    private static class ViewHolder {
        public ImageView icon_item_news;
        public TextView title_item_news;
        public TextView date_item_news;
        public TextView subkanal_item_news;
        private RelativeLayout subkanal_item_news_layout;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.item_latest_terbaru, null);
            holder = new ViewHolder();
            holder.icon_item_news = (ImageView) view.findViewById(R.id.image_item_news);
            holder.title_item_news = (TextView) view.findViewById(R.id.title_item_latest);
            holder.date_item_news = (TextView) view.findViewById(R.id.date_item_news);
            holder.subkanal_item_news = (TextView) view.findViewById(R.id.subkanal_item_news);
            holder.subkanal_item_news_layout = (RelativeLayout) view.findViewById(R.id.subkanal_item_news_layout);

            News news = newsArrayList.get(position);
            Picasso.with(context).load(news.getImage_url()).resize(89, 89).centerCrop().into(holder.icon_item_news);
            holder.title_item_news.setText(news.getTitle());
            holder.date_item_news.setText(news.getDate_publish());
            holder.subkanal_item_news.setText(news.getKanal());

            if(news.getKanal().equalsIgnoreCase("bisnis")) {
                holder.subkanal_item_news_layout.setBackgroundResource(R.color.color_news);
            } else if(news.getKanal().equalsIgnoreCase("nasional")) {
                holder.subkanal_item_news_layout.setBackgroundResource(R.color.color_news);
            } else if(news.getKanal().equalsIgnoreCase("metro")) {
                holder.subkanal_item_news_layout.setBackgroundResource(R.color.color_jualbeli);
            } else if(news.getKanal().equalsIgnoreCase("politik")) {
                holder.subkanal_item_news_layout.setBackgroundResource(R.color.color_jualbeli);
            } else if(news.getKanal().equalsIgnoreCase("dunia")) {
                holder.subkanal_item_news_layout.setBackgroundResource(R.color.color_log);
            } else if(news.getKanal().equalsIgnoreCase("sainstek")) {
                holder.subkanal_item_news_layout.setBackgroundResource(R.color.color_log);
            } else if(news.getKanal().equalsIgnoreCase("sorot")) {
                holder.subkanal_item_news_layout.setBackgroundResource(R.color.color_forum);
            } else if(news.getKanal().equalsIgnoreCase("wawancara")) {
                holder.subkanal_item_news_layout.setBackgroundResource(R.color.color_forum);
            } else if(news.getKanal().equalsIgnoreCase("fokus")) {
                holder.subkanal_item_news_layout.setBackgroundResource(R.color.color_auto);
            } else if(news.getKanal().equalsIgnoreCase("bola")) {
                holder.subkanal_item_news_layout.setBackgroundResource(R.color.color_bola);
            } else if(news.getKanal().equalsIgnoreCase("vlog")) {
                holder.subkanal_item_news_layout.setBackgroundResource(R.color.color_life);
            } else if(news.getKanal().equalsIgnoreCase("lifestyle")) {
                holder.subkanal_item_news_layout.setBackgroundResource(R.color.color_log);
            } else if(news.getKanal().equalsIgnoreCase("otomotif")) {
                holder.subkanal_item_news_layout.setBackgroundResource(R.color.color_news);
            }
            view.setTag(holder);
        }
        return view;
    }

}
