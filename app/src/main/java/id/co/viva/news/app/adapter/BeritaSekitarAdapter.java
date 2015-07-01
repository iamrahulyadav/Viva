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

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.R;
import id.co.viva.news.app.component.CropSquareTransformation;
import id.co.viva.news.app.model.BeritaSekitar;

/**
 * Created by reza on 23/02/15.
 */
public class BeritaSekitarAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<BeritaSekitar> beritaSekitarArrayList;

    public BeritaSekitarAdapter(Context context, ArrayList<BeritaSekitar> beritaSekitarArrayList) {
        this.context = context;
        this.beritaSekitarArrayList = beritaSekitarArrayList;
    }

    @Override
    public int getCount() {
        return beritaSekitarArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return beritaSekitarArrayList.get(position);
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
            view = inflater.inflate(R.layout.item_big_card, null);
            holder = new ViewHolder();
            holder.icon_item_news = (ImageView) view.findViewById(R.id.image_item_news);
            holder.icon_item_viva_news = (ImageView) view.findViewById(R.id.icon_headline_terbaru);
            holder.title_item_news = (TextView) view.findViewById(R.id.title_item_latest);
            holder.date_item_news = (TextView) view.findViewById(R.id.date_item_news);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        BeritaSekitar beritaSekitar = beritaSekitarArrayList.get(position);

        if (beritaSekitar.getImage_url().length() > 0) {
            Picasso.with(context).load(beritaSekitar.getImage_url())
                    .transform(new CropSquareTransformation()).into(holder.icon_item_news);
            if (Constant.isTablet(context)) {
                holder.icon_item_news.getLayoutParams().height = Constant.getDynamicImageSize(context, Constant.DYNAMIC_SIZE_LIST_TYPE);
                holder.icon_item_news.requestLayout();
            }
        }

        holder.title_item_news.setText(beritaSekitar.getTitle());
        holder.date_item_news.setText(beritaSekitar.getDate_publish());

        if (beritaSekitar.getKanal().equalsIgnoreCase("bola")) {
            holder.icon_item_viva_news.setImageResource(R.drawable.icon_viva_bola);
        } else if (beritaSekitar.getKanal().equalsIgnoreCase("vivalife")) {
            holder.icon_item_viva_news.setImageResource(R.drawable.icon_viva_life);
        } else {
            holder.icon_item_viva_news.setImageResource(R.drawable.icon_viva_news);
        }

        return view;
    }

    private static class ViewHolder {
        public TextView title_item_news;
        public TextView date_item_news;
        public ImageView icon_item_news;
        public ImageView icon_item_viva_news;
    }

}
