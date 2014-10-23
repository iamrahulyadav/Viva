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
import id.co.viva.news.app.model.ChannelNews;

/**
 * Created by reza on 23/10/14.
 */
public class ChannelNewsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ChannelNews> channelNewsArrayList;

    public ChannelNewsAdapter(Context context, ArrayList<ChannelNews> channelNewsArrayList) {
        this.channelNewsArrayList = channelNewsArrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return channelNewsArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return channelNewsArrayList.get(position);
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
            view = inflater.inflate(R.layout.item_channel_news_list, null);
            holder = new ViewHolder();
            holder.title_item_channel_news = (TextView) view.findViewById(R.id.title_item_channel_news);
            holder.date_item_channel_news = (TextView) view.findViewById(R.id.date_item_channel_news);
            holder.image_item_channel_news = (ImageView) view.findViewById(R.id.image_item_channel_news);

            ChannelNews channelNews = channelNewsArrayList.get(position);
            holder.title_item_channel_news.setText(channelNews.getTitle());
            holder.date_item_channel_news.setText(channelNews.getDate_publish());
            if(channelNews.getImage_url().length() > 0) {
                Picasso.with(context).load(channelNews.getImage_url()).resize(90, 90).centerCrop().into(holder.image_item_channel_news);
            }

            view.setTag(holder);
        }
        return view;
    }

    private static class ViewHolder {
        private TextView title_item_channel_news;
        private TextView date_item_channel_news;
        private ImageView image_item_channel_news;
    }
}
