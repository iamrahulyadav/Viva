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
            view = inflater.inflate(R.layout.item_channel_list, null);
            holder = new ViewHolder();
            holder.image_item_channel_life = (ImageView) view.findViewById(R.id.image_item_channel);
            holder.title_item_channel_life = (TextView) view.findViewById(R.id.title_item_channel);
            holder.date_item_channel_life = (TextView) view.findViewById(R.id.date_item_channel);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        ChannelLife channelLife = channelLifeArrayList.get(position);
        holder.title_item_channel_life.setText(channelLife.getTitle());

        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = formatter.parse(channelLife.getDate_publish());
            holder.date_item_channel_life.setText(Constant.getTimeAgo(date.getTime()));
        } catch (Exception e) {
            e.getMessage();
        }

        if(channelLife.getImage_url().length() > 0) {
            Picasso.with(context).load(channelLife.getImage_url())
                    .transform(new CropSquareTransformation()).into(holder.image_item_channel_life);
        }

        return view;
    }

    private static class ViewHolder {
        public ImageView image_item_channel_life;
        public TextView title_item_channel_life;
        public TextView date_item_channel_life;
    }

}
