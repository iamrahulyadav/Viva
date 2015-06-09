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
import id.co.viva.news.app.model.ChannelBola;
import id.co.viva.news.app.model.ChannelLife;
import id.co.viva.news.app.model.ChannelNews;
import id.co.viva.news.app.model.SearchResult;

/**
 * Created by reza on 09/06/15.
 */
public class ChannelBigAdapter extends BaseAdapter {

    private Context context;
    private int typeList;
    private ArrayList<ChannelBola> channelBolaArrayList;
    private ArrayList<ChannelNews> channelNewsArrayList;
    private ArrayList<ChannelLife> channelLifeArrayList;
    private ArrayList<SearchResult> searchResults;

    public ChannelBigAdapter(Context context, int typeList,
                             ArrayList<ChannelBola> channelBolaArrayList,
                             ArrayList<ChannelNews> channelNewsArrayList,
                             ArrayList<ChannelLife> channelLifeArrayList,
                             ArrayList<SearchResult> searchResults) {
        this.context = context;
        this.typeList = typeList;
        this.channelBolaArrayList = channelBolaArrayList;
        this.channelNewsArrayList = channelNewsArrayList;
        this.channelLifeArrayList = channelLifeArrayList;
        this.searchResults = searchResults;
    }

    @Override
    public int getCount() {
        switch (typeList) {
            case Constant.BIG_CARD_CHANNEL_BOLA_LIST:
                return channelBolaArrayList.size();
            case Constant.BIG_CARD_CHANNEL_NEWS_LIST:
                return channelNewsArrayList.size();
            case Constant.BIG_CARD_CHANNEL_LIFE_LIST:
                return channelLifeArrayList.size();
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
            case Constant.BIG_CARD_CHANNEL_BOLA_LIST:
                return channelBolaArrayList.get(position);
            case Constant.BIG_CARD_CHANNEL_NEWS_LIST:
                return channelNewsArrayList.get(position);
            case Constant.BIG_CARD_CHANNEL_LIFE_LIST:
                return channelLifeArrayList.get(position);
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
            case Constant.BIG_CARD_CHANNEL_BOLA_LIST:
                //Get position each item
                ChannelBola channelBola = channelBolaArrayList.get(position);
                //Set image
                if (channelBola.getImage_url().length() > 0) {
                    Picasso.with(context).load(channelBola.getImage_url())
                            .transform(new CropSquareTransformation()).into(holder.icon_item_channel);
                    //Check is tablet or not
                    if (Constant.isTablet(context)) {
                        holder.icon_item_channel.getLayoutParams().height =
                                Constant.getDynamicImageSize(context, Constant.DYNAMIC_SIZE_LIST_TYPE);
                        holder.icon_item_channel.requestLayout();
                    }
                }
                //Set title
                holder.title_item_channel.setText(channelBola.getTitle());
                //Set icon
                holder.icon_item_viva_channel.setImageResource(R.drawable.icon_viva_bola);
                //Set time
                try {
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = formatter.parse(channelBola.getDate_publish());
                    holder.date_item_channel.setText(Constant.getTimeAgo(date.getTime()));
                } catch (Exception e) {
                    e.getMessage();
                }
                break;
            case Constant.BIG_CARD_CHANNEL_NEWS_LIST:
                //Get position each item
                ChannelNews channelNews = channelNewsArrayList.get(position);
                //Set image
                if (channelNews.getImage_url().length() > 0) {
                    Picasso.with(context).load(channelNews.getImage_url())
                            .transform(new CropSquareTransformation()).into(holder.icon_item_channel);
                    //Check is tablet or not
                    if (Constant.isTablet(context)) {
                        holder.icon_item_channel.getLayoutParams().height =
                                Constant.getDynamicImageSize(context, Constant.DYNAMIC_SIZE_LIST_TYPE);
                        holder.icon_item_channel.requestLayout();
                    }
                }
                //Set title
                holder.title_item_channel.setText(channelNews.getTitle());
                //Set icon
                holder.icon_item_viva_channel.setImageResource(R.drawable.icon_viva_news);
                //Set time
                try {
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = formatter.parse(channelNews.getDate_publish());
                    holder.date_item_channel.setText(Constant.getTimeAgo(date.getTime()));
                } catch (Exception e) {
                    e.getMessage();
                }
                break;
            case Constant.BIG_CARD_CHANNEL_LIFE_LIST:
                //Get position each item
                ChannelLife channelLife = channelLifeArrayList.get(position);
                //Set image
                if (channelLife.getImage_url().length() > 0) {
                    Picasso.with(context).load(channelLife.getImage_url())
                            .transform(new CropSquareTransformation()).into(holder.icon_item_channel);
                    //Check is tablet or not
                    if (Constant.isTablet(context)) {
                        holder.icon_item_channel.getLayoutParams().height =
                                Constant.getDynamicImageSize(context, Constant.DYNAMIC_SIZE_LIST_TYPE);
                        holder.icon_item_channel.requestLayout();
                    }
                }
                //Set title
                holder.title_item_channel.setText(channelLife.getTitle());
                //Set icon
                holder.icon_item_viva_channel.setImageResource(R.drawable.icon_viva_life);
                //Set time
                try {
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = formatter.parse(channelLife.getDate_publish());
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
                if (result.getKanal().equalsIgnoreCase("bola")) {
                    holder.icon_item_viva_channel.setImageResource(R.drawable.icon_viva_bola);
                } else if (result.getKanal().equalsIgnoreCase("vivalife")) {
                    holder.icon_item_viva_channel.setImageResource(R.drawable.icon_viva_life);
                } else {
                    holder.icon_item_viva_channel.setImageResource(R.drawable.icon_viva_news);
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
