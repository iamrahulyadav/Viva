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
        View rootView = LayoutInflater.from(context)
                .inflate(R.layout.item_search_result, viewGroup, false);

        ImageView icon_item_search_result = (ImageView) rootView.findViewById(R.id.image_item_search_result);
        TextView title_item_search_result = (TextView) rootView.findViewById(R.id.title_item_search_result);
        TextView date_item_search_result = (TextView) rootView.findViewById(R.id.date_item_search_result);
        TextView subkanal_item_search_result = (TextView) rootView.findViewById(R.id.subkanal_item_search_result);
        RelativeLayout subkanal_item_search_result_layout = (RelativeLayout) rootView.findViewById(R.id.subkanal_item_search_result_layout);

        SearchResult result = searchResultArrayList.get(position);
        Picasso.with(context).load(result.getImage_url()).into(icon_item_search_result);
        title_item_search_result.setText(result.getTitle());
        date_item_search_result.setText(result.getDate_publish());
        subkanal_item_search_result.setText(result.getKanal());

        if(result.getKanal().equalsIgnoreCase("bola")) {
            subkanal_item_search_result_layout.setBackgroundResource(R.color.color_bola);
        } else if(result.getKanal().equalsIgnoreCase("vivalife")) {
            subkanal_item_search_result_layout.setBackgroundResource(R.color.color_life);
        } else  {
            subkanal_item_search_result_layout.setBackgroundResource(R.color.color_news);
        }

        return rootView;
    }

}
