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
import id.co.viva.news.app.model.SearchResult;

/**
 * Created by reza on 16/10/14.
 */
public class SearchResultAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<SearchResult> searchResultArrayList;

    private static class ViewHolder {
        private ImageView icon_item_search_result;
        private TextView title_item_search_result;
        private TextView date_item_search_result;
        private TextView subkanal_item_search_result;
        private RelativeLayout subkanal_item_search_result_layout;
    }

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
            holder.icon_item_search_result = (ImageView) view.findViewById(R.id.image_item_search_result);
            holder.title_item_search_result = (TextView) view.findViewById(R.id.title_item_search_result);
            holder.date_item_search_result = (TextView) view.findViewById(R.id.date_item_search_result);
            holder.subkanal_item_search_result = (TextView) view.findViewById(R.id.subkanal_item_search_result);
            holder.subkanal_item_search_result_layout = (RelativeLayout) view.findViewById(R.id.subkanal_item_search_result_layout);

            SearchResult result = searchResultArrayList.get(position);
            Picasso.with(context).load(result.getImage_url()).resize(89, 89).centerCrop().into(holder.icon_item_search_result);
            holder.title_item_search_result.setText(result.getTitle());
            holder.date_item_search_result.setText(result.getDate_publish());
            holder.subkanal_item_search_result.setText(result.getKanal());

            if(result.getKanal().equalsIgnoreCase("bisnis")) {
                holder.subkanal_item_search_result_layout.setBackgroundResource(R.color.color_news);
            } else if(result.getKanal().equalsIgnoreCase("nasional")) {
                holder.subkanal_item_search_result_layout.setBackgroundResource(R.color.color_news);
            } else if(result.getKanal().equalsIgnoreCase("metro")) {
                holder.subkanal_item_search_result_layout.setBackgroundResource(R.color.color_jualbeli);
            } else if(result.getKanal().equalsIgnoreCase("politik")) {
                holder.subkanal_item_search_result_layout.setBackgroundResource(R.color.color_jualbeli);
            } else if(result.getKanal().equalsIgnoreCase("dunia")) {
                holder.subkanal_item_search_result_layout.setBackgroundResource(R.color.color_log);
            } else if(result.getKanal().equalsIgnoreCase("sainstek")) {
                holder.subkanal_item_search_result_layout.setBackgroundResource(R.color.color_log);
            } else if(result.getKanal().equalsIgnoreCase("sorot")) {
                holder.subkanal_item_search_result_layout.setBackgroundResource(R.color.color_forum);
            } else if(result.getKanal().equalsIgnoreCase("wawancara")) {
                holder.subkanal_item_search_result_layout.setBackgroundResource(R.color.color_forum);
            } else if(result.getKanal().equalsIgnoreCase("fokus")) {
                holder.subkanal_item_search_result_layout.setBackgroundResource(R.color.color_auto);
            } else if(result.getKanal().equalsIgnoreCase("bola")) {
                holder.subkanal_item_search_result_layout.setBackgroundResource(R.color.color_bola);
            } else if(result.getKanal().equalsIgnoreCase("vlog")) {
                holder.subkanal_item_search_result_layout.setBackgroundResource(R.color.color_life);
            } else if(result.getKanal().equalsIgnoreCase("lifestyle")) {
                holder.subkanal_item_search_result_layout.setBackgroundResource(R.color.color_log);
            } else if(result.getKanal().equalsIgnoreCase("otomotif")) {
                holder.subkanal_item_search_result_layout.setBackgroundResource(R.color.color_news);
            }
            view.setTag(holder);
        }
        return view;
    }

}
