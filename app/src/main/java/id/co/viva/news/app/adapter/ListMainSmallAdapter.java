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
import id.co.viva.news.app.model.BeritaSekitar;
import id.co.viva.news.app.model.EntityMain;

/**
 * Created by rezarachman on 01/10/14.
 */
public class ListMainSmallAdapter extends BaseAdapter {

    private Context context;
    private int type;
    private ArrayList<BeritaSekitar> beritaSekitarArrayList;
    private ArrayList<EntityMain> entityMains;

    public ListMainSmallAdapter(Context context, int type,
                                ArrayList<BeritaSekitar> beritaSekitarArrayList,
                                ArrayList<EntityMain> entityMains) {
        this.context = context;
        this.type = type;
        this.beritaSekitarArrayList = beritaSekitarArrayList;
        this.entityMains = entityMains;
    }

    @Override
    public int getCount() {
        if (type == Constant.NEWS_AROUND_LIST) {
            return beritaSekitarArrayList.size();
        } else {
            return entityMains.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (type == Constant.NEWS_AROUND_LIST) {
            return beritaSekitarArrayList.get(position);
        } else {
            return entityMains.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolderSmallCard holderSmall;
        //Checking view
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            //Checking list type
            view = inflater.inflate(R.layout.item_channel_list, null);
            holderSmall = new ViewHolderSmallCard();
            holderSmall.icon_item_news = (ImageView) view.findViewById(R.id.image_item_channel);
            holderSmall.title_item_news = (TextView) view.findViewById(R.id.title_item_channel);
            holderSmall.date_item_news = (TextView) view.findViewById(R.id.date_item_channel);
            view.setTag(holderSmall);
        } else {
            holderSmall = (ViewHolderSmallCard) view.getTag();
        }
        //Choose type
        switch (type) {
            case Constant.NEWS_AROUND_LIST:
                //Get position each item
                BeritaSekitar beritaSekitar = beritaSekitarArrayList.get(position);
                //Set image
                if (beritaSekitar.getImage_url().length() > 0) {
                    Picasso.with(context).load(beritaSekitar.getImage_url())
                            .transform(new CropSquareTransformation()).into(holderSmall.icon_item_news);
                    //Check is tablet or not
                    if (Constant.isTablet(context)) {
                        holderSmall.icon_item_news.getLayoutParams().height =
                                Constant.getDynamicImageSize(context, Constant.DYNAMIC_SIZE_LIST_TYPE);
                        holderSmall.icon_item_news.requestLayout();
                    }
                }
                //Set title
                holderSmall.title_item_news.setText(beritaSekitar.getTitle());
                //Set time
                holderSmall.date_item_news.setText(beritaSekitar.getDate_publish());
                break;
            default:
                //Get position each item
                EntityMain entity = entityMains.get(position);
                //Set image
                if (entity.getImage_url().length() > 0) {
                    Picasso.with(context).load(entity.getImage_url())
                            .transform(new CropSquareTransformation()).into(holderSmall.icon_item_news);
                    //Check is tablet or not
                    if (Constant.isTablet(context)) {
                        holderSmall.icon_item_news.getLayoutParams().height =
                                Constant.getDynamicImageSize(context, Constant.DYNAMIC_SIZE_LIST_TYPE);
                        holderSmall.icon_item_news.requestLayout();
                    }
                }
                //Set title
                holderSmall.title_item_news.setText(entity.getTitle());
                //Set time
                try {
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = formatter.parse(entity.getDate_publish());
                    holderSmall.date_item_news.setText(Constant.getTimeAgo(date.getTime()));
                } catch (Exception e) {
                    e.getMessage();
                }
                break;
        }
        return view;
    }

    private static class ViewHolderSmallCard {
        private TextView title_item_news;
        private TextView date_item_news;
        private ImageView icon_item_news;
    }

}
