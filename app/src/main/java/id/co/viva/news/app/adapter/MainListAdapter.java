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
import id.co.viva.news.app.model.Headline;
import id.co.viva.news.app.model.News;

/**
 * Created by rezarachman on 01/10/14.
 */
public class MainListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Headline> headlineArrayList;
    private ArrayList<News> newsArrayList;
    private int typeList;

    public MainListAdapter(Context context, int typeList,
                           ArrayList<Headline> headlineArrayList, ArrayList<News> newsArrayList) {
        this.context = context;
        this.typeList = typeList;
        this.headlineArrayList = headlineArrayList;
        this.newsArrayList = newsArrayList;
    }

    @Override
    public int getCount() {
        switch (typeList) {
            case Constant.HEADLINES_LIST:
                return headlineArrayList.size();
            case Constant.NEWS_LIST:
                return newsArrayList.size();
            default:
                break;
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        switch (typeList) {
            case Constant.HEADLINES_LIST:
                return headlineArrayList.get(position);
            case Constant.NEWS_LIST:
                return newsArrayList.get(position);
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
        //Checking type of list
        switch (typeList) {
            case Constant.HEADLINES_LIST:
                //Get position each item
                Headline headline = headlineArrayList.get(position);
                //Set image
                if (headline.getImage_url().length() > 0) {
                    Picasso.with(context).load(headline.getImage_url())
                            .transform(new CropSquareTransformation()).into(holderSmall.icon_item_news);
                    //Check is tablet or not
                    if (Constant.isTablet(context)) {
                        holderSmall.icon_item_news.getLayoutParams().height =
                                Constant.getDynamicImageSize(context, Constant.DYNAMIC_SIZE_LIST_TYPE);
                        holderSmall.icon_item_news.requestLayout();
                    }
                }
                //Set title
                holderSmall.title_item_news.setText(headline.getTitle());
                //Set time
                try {
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = formatter.parse(headline.getDate_publish());
                    holderSmall.date_item_news.setText(Constant.getTimeAgo(date.getTime()));
                } catch (Exception e) {
                    e.getMessage();
                }
                break;
            case Constant.NEWS_LIST:
                //Get position each item
                News news = newsArrayList.get(position);
                //Set image
                if (news.getImage_url().length() > 0) {
                    Picasso.with(context).load(news.getImage_url())
                            .transform(new CropSquareTransformation()).into(holderSmall.icon_item_news);
                    //Check is tablet or not
                    if (Constant.isTablet(context)) {
                        holderSmall.icon_item_news.getLayoutParams().height =
                                Constant.getDynamicImageSize(context, Constant.DYNAMIC_SIZE_LIST_TYPE);
                        holderSmall.icon_item_news.requestLayout();
                    }
                }
                //Set title
                holderSmall.title_item_news.setText(news.getTitle());
                //Set time
                try {
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = formatter.parse(news.getDate_publish());
                    holderSmall.date_item_news.setText(Constant.getTimeAgo(date.getTime()));
                } catch (Exception e) {
                    e.getMessage();
                }
                break;
            default:
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
