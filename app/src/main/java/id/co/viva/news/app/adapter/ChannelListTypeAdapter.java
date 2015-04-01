package id.co.viva.news.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.R;
import id.co.viva.news.app.component.CropSquareTransformation;
import id.co.viva.news.app.model.FeaturedBola;
import id.co.viva.news.app.model.FeaturedLife;
import id.co.viva.news.app.model.FeaturedNews;

/**
 * Created by reza on 16/02/15.
 */
public class ChannelListTypeAdapter extends BaseAdapter {

    private Context context;
    private String mType;
    private ArrayList<FeaturedBola> featuredBolaArrayList;
    private ArrayList<FeaturedNews> featuredNewsArrayList;
    private ArrayList<FeaturedLife> featuredLifeArrayList;

    public ChannelListTypeAdapter(Context context, ArrayList<FeaturedBola> featuredBolaArrayList,
                                  ArrayList<FeaturedLife> featuredLifeArrayList, ArrayList<FeaturedNews> featuredNewsArrayList,
                                  String mType) {
        this.context = context;
        this.mType = mType;
        this.featuredBolaArrayList = featuredBolaArrayList;
        this.featuredLifeArrayList = featuredLifeArrayList;
        this.featuredNewsArrayList = featuredNewsArrayList;
    }

    @Override
    public int getCount() {
        if(mType.equalsIgnoreCase(Constant.ADAPTER_CHANNEL_BOLA)) {
            return featuredBolaArrayList.size();
        } else if(mType.equalsIgnoreCase(Constant.ADAPTER_CHANNEL_LIFE)) {
            return featuredLifeArrayList.size();
        } else if(mType.equalsIgnoreCase(Constant.ADAPTER_CHANNEL_NEWS)) {
            return featuredNewsArrayList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if(mType.equalsIgnoreCase(Constant.ADAPTER_CHANNEL_BOLA)) {
            return featuredBolaArrayList.get(position);
        } else if(mType.equalsIgnoreCase(Constant.ADAPTER_CHANNEL_LIFE)) {
            return featuredLifeArrayList.get(position);
        } else if(mType.equalsIgnoreCase(Constant.ADAPTER_CHANNEL_NEWS)) {
            return featuredNewsArrayList.get(position);
        } else {
            return null;
        }
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
            view = inflater.inflate(R.layout.item_channel_type_list, null);
            holder = new ViewHolder();
            holder.image_item_channel = (ImageView) view.findViewById(R.id.item_image_channel_list_type);
            holder.title_item_channel = (TextView) view.findViewById(R.id.item_title_channel_list_type);
            holder.transparent_layout = (RelativeLayout) view.findViewById(R.id.item_image_channel_list_type_transparent);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (mType.equalsIgnoreCase(Constant.ADAPTER_CHANNEL_BOLA)) {
            FeaturedBola featuredBola = featuredBolaArrayList.get(position);
            if(holder.title_item_channel != null) {
                if(featuredBola.getChannel_title().length() > 0) {
                    holder.title_item_channel.setText(featuredBola.getChannel_title().toUpperCase());
                }
            }
            if (holder.image_item_channel != null) {
                if(featuredBola.getImage_url().length() > 0) {
                    Picasso.with(context).load(featuredBola.getImage_url())
                            .transform(new CropSquareTransformation()).into(holder.image_item_channel);
                    if (Constant.isTablet(context)) {
                        holder.image_item_channel.getLayoutParams().height = Constant.getDynamicImageSize(context, Constant.DYNAMIC_SIZE_LIST_TYPE);
                        holder.transparent_layout.getLayoutParams().height = Constant.getDynamicImageSize(context, Constant.DYNAMIC_SIZE_LIST_TYPE);
                        holder.image_item_channel.requestLayout();
                        holder.transparent_layout.requestLayout();
                    }
                } else {
                    holder.image_item_channel.setImageResource(R.drawable.default_image);
                }
            }
        } else if (mType.equalsIgnoreCase(Constant.ADAPTER_CHANNEL_LIFE)) {
            FeaturedLife featuredLife = featuredLifeArrayList.get(position);
            if (holder.title_item_channel != null) {
                if (featuredLife.getChannel_title().length() > 0) {
                    holder.title_item_channel.setText(featuredLife.getChannel_title().toUpperCase());
                }
            }
            if (holder.image_item_channel != null) {
                if (featuredLife.getImage_url().length() > 0) {
                    Picasso.with(context).load(featuredLife.getImage_url())
                            .transform(new CropSquareTransformation()).into(holder.image_item_channel);
                    if (Constant.isTablet(context)) {
                        holder.image_item_channel.getLayoutParams().height = Constant.getDynamicImageSize(context, Constant.DYNAMIC_SIZE_LIST_TYPE);
                        holder.transparent_layout.getLayoutParams().height = Constant.getDynamicImageSize(context, Constant.DYNAMIC_SIZE_LIST_TYPE);
                        holder.image_item_channel.requestLayout();
                        holder.transparent_layout.requestLayout();
                    }
                } else {
                    holder.image_item_channel.setImageResource(R.drawable.default_image);
                }
            }
        } else if (mType.equalsIgnoreCase(Constant.ADAPTER_CHANNEL_NEWS)) {
            FeaturedNews featuredNews = featuredNewsArrayList.get(position);
            if (holder.title_item_channel != null) {
                if (featuredNews.getChannel_title().length() > 0) {
                    holder.title_item_channel.setText(featuredNews.getChannel_title().toUpperCase());
                }
            }
            if (holder.image_item_channel != null) {
                if (featuredNews.getImage_url().length() > 0) {
                    Picasso.with(context).load(featuredNews.getImage_url())
                            .transform(new CropSquareTransformation()).into(holder.image_item_channel);
                    if (Constant.isTablet(context)) {
                        holder.image_item_channel.getLayoutParams().height = Constant.getDynamicImageSize(context, Constant.DYNAMIC_SIZE_LIST_TYPE);
                        holder.transparent_layout.getLayoutParams().height = Constant.getDynamicImageSize(context, Constant.DYNAMIC_SIZE_LIST_TYPE);
                        holder.image_item_channel.requestLayout();
                        holder.transparent_layout.requestLayout();
                    }
                } else {
                    holder.image_item_channel.setImageResource(R.drawable.default_image);
                }
            }
        }

        return view;
    }

    private static class ViewHolder {
        public ImageView image_item_channel;
        public TextView title_item_channel;
        public RelativeLayout transparent_layout;
    }

}
