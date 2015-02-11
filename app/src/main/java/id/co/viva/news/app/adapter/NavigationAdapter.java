package id.co.viva.news.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import id.co.viva.news.app.R;
import id.co.viva.news.app.interfaces.Item;
import id.co.viva.news.app.model.NavigationItem;
import id.co.viva.news.app.model.NavigationSectionItem;

/**
 * Created by rezarachman on 30/09/14.
 */
public class NavigationAdapter extends ArrayAdapter<Item> {

    private Context context;
    private ArrayList<Item> navItems;
    private LayoutInflater vi;

    public NavigationAdapter(Context context, ArrayList<Item> navItems) {
        super(context, 0, navItems);
        this.context = context;
        this.navItems = navItems;
        vi = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return navItems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View v = view;
        ViewHolder holder;
        Item item = navItems.get(position);
        if(item != null) {
            if(item.isSection()) {
                NavigationSectionItem navigationSectionItem = (NavigationSectionItem) item;
                if(v == null) {
                    v = vi.inflate(R.layout.item_navigation_section_list, null);
                    holder = new ViewHolder();
                    holder.sectionView = (TextView) v.findViewById(R.id.list_item_section_text);
                    v.setTag(holder);
                } else {
                    holder = (ViewHolder) v.getTag();
                }
                v.setOnClickListener(null);
                v.setOnLongClickListener(null);
                v.setLongClickable(false);
                holder.sectionView.setText(navigationSectionItem.getTitle());
            } else {
                NavigationItem ei = (NavigationItem) item;
                if(v == null) {
                    v = vi.inflate(R.layout.item_navigation_list, null);
                    holder = new ViewHolder();
                    holder.title = (TextView) v.findViewById(R.id.text_navigation_list);
                    holder.image = (ImageView) v.findViewById(R.id.list_item_entry_drawable);
                    v.setTag(holder);
                } else {
                    holder = (ViewHolder) v.getTag();
                }
                if(holder.title != null)
                    holder.title.setText(ei.getTitle());
                if(holder.image != null)
                    holder.image.setImageResource(ei.getIcon());
            }
        }
        return v;
    }

    private static class ViewHolder {
        public TextView sectionView;
        public TextView title;
        public ImageView image;
    }

}
