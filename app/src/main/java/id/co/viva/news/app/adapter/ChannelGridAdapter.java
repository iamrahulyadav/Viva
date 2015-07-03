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
import id.co.viva.news.app.component.CropSquareTransformation;
import id.co.viva.news.app.model.Channel;

/**
 * Created by reza on 22/10/14.
 */
public class ChannelGridAdapter extends BaseAdapter {

    private ArrayList<Channel> channelArrayList;
    private Context context;

    public ChannelGridAdapter(Context context, ArrayList<Channel> channelArrayList) {
        this.context = context;
        this.channelArrayList = channelArrayList;
    }

    @Override
    public int getCount() {
        return channelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return channelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.item_grid, null);
            holder = new ViewHolder();
            holder.thumb_featured = (ImageView) view.findViewById(R.id.item_thumb);
            holder.title_channel = (TextView) view.findViewById(R.id.item_title_kanal);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Channel channel = channelArrayList.get(position);

        holder.title_channel.setText(channel.getChannel_title().toUpperCase());
        if (channel.getImage_url().length() > 0) {
            Picasso.with(context).load(channel.getImage_url())
                    .transform(new CropSquareTransformation()).into(holder.thumb_featured);
        } else {
            holder.thumb_featured.setImageResource(R.drawable.default_image);
        }

        return view;
    }

    private static class ViewHolder {
        public TextView title_channel;
        public ImageView thumb_featured;
    }

}
