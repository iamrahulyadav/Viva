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
import id.co.viva.news.app.model.ChannelList;
import id.co.viva.news.app.model.SearchResult;

/**
 * Created by reza on 09/06/15.
 */
public class ChannelBigAdapter extends BaseAdapter {

    private Context context;
    private int typeList;
    private ArrayList<ChannelList> channelListArrayList;
    private ArrayList<SearchResult> searchResults;

    public final static String CHANNEL_BOLA = "bola";
    public final static String CHANNEL_LIFE = "vivalife";
    public final static String CHANNEL_AUTO = "otomotif";

    public ChannelBigAdapter(Context context, int typeList,
                             ArrayList<ChannelList> channelListArrayList,
                             ArrayList<SearchResult> searchResults) {
        this.context = context;
        this.typeList = typeList;
        this.channelListArrayList = channelListArrayList;
        this.searchResults = searchResults;
    }

    @Override
    public int getCount() {
        switch (typeList) {
            case Constant.BIG_CARD_CHANNEL_LIST:
                return channelListArrayList.size();
            case Constant.BIG_CARD_SEARCH_RESULT:
                return searchResults.size();
            default:
                break;
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        switch (typeList) {
            case Constant.BIG_CARD_CHANNEL_LIST:
                return channelListArrayList.get(position);
            case Constant.BIG_CARD_SEARCH_RESULT:
                return  searchResults.get(position);
            default:
                break;
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        //Checking view
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            //Checking list type
            view = inflater.inflate(R.layout.item_big_card, null);
            holder = new ViewHolder();
            holder.icon_item_channel = (ImageView) view.findViewById(R.id.image_item_news);
            holder.icon_item_viva_channel = (ImageView) view.findViewById(R.id.icon_headline_terbaru);
            holder.title_item_channel = (TextView) view.findViewById(R.id.title_item_latest);
            holder.date_item_channel = (TextView) view.findViewById(R.id.date_item_news);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        //Checking type of list
        switch (typeList) {
            case Constant.BIG_CARD_CHANNEL_LIST:
                //Get position each item
                ChannelList channelList = channelListArrayList.get(position);
                //Set image
                if (channelList.getImage_url().length() > 0) {
                    Picasso.with(context).load(channelList.getImage_url())
                            .transform(new CropSquareTransformation()).into(holder.icon_item_channel);
                    //Check is tablet or not
                    if (Constant.isTablet(context)) {
                        holder.icon_item_channel.getLayoutParams().height =
                                Constant.getDynamicImageSize(context, Constant.DYNAMIC_SIZE_LIST_TYPE);
                        holder.icon_item_channel.requestLayout();
                    }
                }
                //Set title
                holder.title_item_channel.setText(channelList.getTitle());
                //Set icon
                switch (channelList.getKanal()) {
                    case CHANNEL_BOLA:
                        holder.icon_item_viva_channel.setImageResource(R.drawable.icon_viva_bola);
                        break;
                    case CHANNEL_LIFE:
                        holder.icon_item_viva_channel.setImageResource(R.drawable.icon_viva_life);
                        break;
                    case CHANNEL_AUTO:
                        holder.icon_item_viva_channel.setImageResource(R.drawable.icon_viva_otomotif);
                        break;
                    default:
                        holder.icon_item_viva_channel.setImageResource(R.drawable.icon_viva_news);
                        break;
                }
                //Set time
                try {
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = formatter.parse(channelList.getDate_publish());
                    holder.date_item_channel.setText(Constant.getTimeAgo(date.getTime()));
                } catch (Exception e) {
                    e.getMessage();
                }
                break;
            case Constant.BIG_CARD_SEARCH_RESULT:
                SearchResult result = searchResults.get(position);
                //Image result
                if (result.getImage_url().length() > 0) {
                    Picasso.with(context).load(result.getImage_url())
                            .transform(new CropSquareTransformation()).into(holder.icon_item_channel);
                }
                //Set title result
                holder.title_item_channel.setText(result.getTitle());
                //Set date result
                holder.date_item_channel.setText(result.getDate_publish());
                //Check channel result
                switch (result.getKanal()) {
                    case CHANNEL_BOLA:
                        holder.icon_item_viva_channel.setImageResource(R.drawable.icon_viva_bola);
                        break;
                    case CHANNEL_LIFE:
                        holder.icon_item_viva_channel.setImageResource(R.drawable.icon_viva_life);
                        break;
                    case CHANNEL_AUTO:
                        holder.icon_item_viva_channel.setImageResource(R.drawable.icon_viva_otomotif);
                        break;
                    default:
                        holder.icon_item_viva_channel.setImageResource(R.drawable.icon_viva_news);
                        break;
                }
                break;
            default:
                break;
        }
        return view;
    }

    private static class ViewHolder {
        public TextView title_item_channel;
        public TextView date_item_channel;
        public ImageView icon_item_channel;
        public ImageView icon_item_viva_channel;
    }

}
