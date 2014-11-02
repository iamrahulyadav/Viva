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
        View rootView = LayoutInflater.from(context)
                .inflate(R.layout.item_channel_bola_list, viewGroup, false);

        TextView title_item_channel_bola = (TextView) rootView.findViewById(R.id.title_item_channel_bola);
        TextView date_item_channel_bola = (TextView) rootView.findViewById(R.id.date_item_channel_bola);
        ImageView image_item_channel_bola = (ImageView) rootView.findViewById(R.id.image_item_channel_bola);

        ChannelBola channelBola = channelBolaArrayList.get(position);
        title_item_channel_bola.setText(channelBola.getTitle());

        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = (Date)formatter.parse(channelBola.getDate_publish());
            date_item_channel_bola.setText(Constant.getTimeAgo(date.getTime(), context));
        } catch (Exception e) {
            e.getMessage();
        }

        if(channelBola.getImage_url().length() > 0) {
            Picasso.with(context).load(channelBola.getImage_url()).into(image_item_channel_bola);
        }

        return rootView;
    }

}
