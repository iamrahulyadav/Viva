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

import id.co.viva.news.app.R;
import id.co.viva.news.app.component.CropSquareTransformation;
import id.co.viva.news.app.model.SearchResult;

/**
 * Created by reza on 16/10/14.
 */
public class SearchResultAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<SearchResult> searchResultArrayList;

    public SearchResultAdapter(Context context, ArrayList<SearchResult> searchResultArrayList) {
        this.context = context;
        this.searchResultArrayList = searchResultArrayList;
    }

    @Override
    public int getCount() {
        return searchResultArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return searchResultArrayList.get(position);
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
            view = inflater.inflate(R.layout.item_search_result, null);
            holder = new ViewHolder();
            holder.title_item_search_result = (TextView) view.findViewById(R.id.title_item_search_result);
            holder.date_item_search_result = (TextView) view.findViewById(R.id.date_item_search_result);
            holder.subkanal_item_search_result = (TextView) view.findViewById(R.id.subkanal_item_search_result);
            holder.subkanal_item_search_result_layout = (RelativeLayout) view.findViewById(R.id.subkanal_item_search_result_layout);
            holder.icon_item_search_result = (ImageView) view.findViewById(R.id.image_item_search_result);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        SearchResult result = searchResultArrayList.get(position);
        if(result.getImage_url().length() > 0) {
            Picasso.with(context).load(result.getImage_url()).transform(new CropSquareTransformation()).into(holder.icon_item_search_result);
        }
        holder.title_item_search_result.setText(result.getTitle());
        holder.date_item_search_result.setText(result.getDate_publish());
        holder.subkanal_item_search_result.setText(result.getKanal());

        if(result.getKanal().equalsIgnoreCase("bola")) {
            holder.subkanal_item_search_result_layout.setBackgroundResource(R.color.color_bola);
        } else if(result.getKanal().equalsIgnoreCase("vivalife")) {
            holder.subkanal_item_search_result_layout.setBackgroundResource(R.color.color_life);
        } else  {
            holder.subkanal_item_search_result_layout.setBackgroundResource(R.color.color_news);
        }

        return view;
    }

    private static class ViewHolder {
        public TextView title_item_search_result;
        public TextView date_item_search_result;
        public TextView subkanal_item_search_result;
        public RelativeLayout subkanal_item_search_result_layout;
        public ImageView icon_item_search_result;
    }

}
