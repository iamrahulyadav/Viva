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
        View rootView = LayoutInflater.from(context)
                .inflate(R.layout.item_channel_life_list, viewGroup, false);

        TextView title_item_channel_life = (TextView) rootView.findViewById(R.id.title_item_channel_life);
        TextView date_item_channel_life = (TextView) rootView.findViewById(R.id.date_item_channel_life);
        ImageView image_item_channel_life = (ImageView) rootView.findViewById(R.id.image_item_channel_life);

        ChannelLife channelLife = channelLifeArrayList.get(position);
        title_item_channel_life.setText(channelLife.getTitle());

        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = (Date)formatter.parse(channelLife.getDate_publish());
            date_item_channel_life.setText(Constant.getTimeAgo(date.getTime()));
        } catch (Exception e) {
            e.getMessage();
        }

        if(channelLife.getImage_url().length() > 0) {
            Picasso.with(context).load(channelLife.getImage_url()).into(image_item_channel_life);
        }

        return rootView;
    }

}
