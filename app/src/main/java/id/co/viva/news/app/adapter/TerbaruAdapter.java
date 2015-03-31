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

        News news = newsArrayList.get(position);

        if (news.getImage_url().length() > 0) {
            Picasso.with(context).load(news.getImage_url())
                    .transform(new CropSquareTransformation()).into(holder.icon_item_news);
            if (Constant.isTablet(context)) {
                holder.icon_item_news.getLayoutParams().height = Constant.getDynamicImageSize(context);
                holder.icon_item_news.requestLayout();
            }
        }

        holder.title_item_news.setText(news.getTitle());

        if (news.getKanal().equalsIgnoreCase("bola")) {
            holder.icon_item_viva_news.setImageResource(R.drawable.icon_viva_bola);
        } else if(news.getKanal().equalsIgnoreCase("vivalife")) {
            holder.icon_item_viva_news.setImageResource(R.drawable.icon_viva_life);
        } else {
            holder.icon_item_viva_news.setImageResource(R.drawable.icon_viva_news);
        }

        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = formatter.parse(news.getDate_publish());
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
