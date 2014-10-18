package id.co.viva.news.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import id.co.viva.news.app.R;
import id.co.viva.news.app.model.NavigationItem;

/**
 * Created by rezarachman on 30/09/14.
 */
public class NavigationAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<NavigationItem> navItems;

    public NavigationAdapter(Context context, ArrayList<NavigationItem> navItems){
        this.context = context;
        this.navItems = navItems;
    }

    @Override
    public int getCount() {
        return navItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navItems.get(position);
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
            view = inflater.inflate(R.layout.item_navigation_list, null);
            holder = new ViewHolder();
            holder.image = (ImageView) view.findViewById(R.id.icon_navigation_list);
            holder.title = (TextView) view.findViewById(R.id.text_navigation_list);

            if(String.valueOf(navItems.get(position).getIcon()) != null ||
                    String.valueOf(navItems.get(position).getIcon()).isEmpty()) {
                holder.image.setImageResource(navItems.get(position).getIcon());
            }
            holder.title.setText(navItems.get(position).getTitle());

            view.setTag(holder);
        }
        return view;
    }

    private static class ViewHolder {
        public ImageView image;
        public TextView title;
    }

}
