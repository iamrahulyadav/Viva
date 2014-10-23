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
import id.co.viva.news.app.model.ChannelLife;

/**
 * Created by reza on 23/10/14.
 */
public class ChannelLifeAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ChannelLife> channelLifeArrayList;

    public ChannelLifeAdapter(Context context, ArrayList<ChannelLife> channelLifeArrayList) {
        this.channelLifeArrayList = channelLifeArrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return channelLifeArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return channelLifeArrayList.get(position);
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
            view = inflater.inflate(R.layout.item_channel_life_list, null);
            holder = new ViewHolder();
            holder.title_item_channel_life = (TextView) view.findViewById(R.id.title_item_channel_life);
            holder.date_item_channel_life = (TextView) view.findViewById(R.id.date_item_channel_life);
            holder.image_item_channel_life = (ImageView) view.findViewById(R.id.image_item_channel_life);

            ChannelLife channelLife = channelLifeArrayList.get(position);
            holder.title_item_channel_life.setText(channelLife.getTitle());
            holder.date_item_channel_life.setText(channelLife.getDate_publish());
            if(channelLife.getImage_url().length() > 0) {
                Picasso.with(context).load(channelLife.getImage_url()).resize(90, 90).centerCrop().into(holder.image_item_channel_life);
            }

            view.setTag(holder);
        }
        return view;
    }

    private static class ViewHolder {
        private TextView title_item_channel_life;
        private TextView date_item_channel_life;
        private ImageView image_item_channel_life;
    }

}
