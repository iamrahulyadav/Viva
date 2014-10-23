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
import id.co.viva.news.app.model.ChannelBola;

/**
 * Created by reza on 23/10/14.
 */
public class ChannelBolaAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ChannelBola> channelBolaArrayList;

    public ChannelBolaAdapter(Context context, ArrayList<ChannelBola> channelBolaArrayList) {
        this.channelBolaArrayList = channelBolaArrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return channelBolaArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return channelBolaArrayList.get(position);
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
            view = inflater.inflate(R.layout.item_channel_bola_list, null);
            holder = new ViewHolder();
            holder.title_item_channel_bola = (TextView) view.findViewById(R.id.title_item_channel_bola);
            holder.date_item_channel_bola = (TextView) view.findViewById(R.id.date_item_channel_bola);
            holder.image_item_channel_bola = (ImageView) view.findViewById(R.id.image_item_channel_bola);

            ChannelBola channelBola = channelBolaArrayList.get(position);
            holder.title_item_channel_bola.setText(channelBola.getTitle());
            holder.date_item_channel_bola.setText(channelBola.getDate_publish());
            if(channelBola.getImage_url().length() > 0) {
                Picasso.with(context).load(channelBola.getImage_url()).resize(90, 90).centerCrop().into(holder.image_item_channel_bola);
            }

            view.setTag(holder);
        }
        return view;
    }

    private static class ViewHolder {
        private TextView title_item_channel_bola;
        private TextView date_item_channel_bola;
        private ImageView image_item_channel_bola;
    }

}
