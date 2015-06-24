package id.co.viva.news.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import id.co.viva.news.app.R;
import id.co.viva.news.app.component.ProgressWheel;
import id.co.viva.news.app.model.NavigationItem;

/**
 * Created by rezarachman on 30/09/14.
 */
public class NavigationAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<NavigationItem> navItems;
    private LayoutInflater viewInflater;
    private final static int MENU_LIST_ITEM = 0;
    private final static int MENU_LIST_SECTION = 1;
    private final static int NUMBER_OF_TYPE = 2;

    public NavigationAdapter(Context context, ArrayList<NavigationItem> navItems) {
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
    public int getViewTypeCount() {
        return NUMBER_OF_TYPE;
    }

    @Override
    public int getItemViewType(int position) {
        if (navItems.get(position).getType() == MENU_LIST_ITEM) {
            return MENU_LIST_ITEM;
        } else {
            return MENU_LIST_SECTION;
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        //Holder for component
        final ViewHolder holder;
        //Get each item
        NavigationItem navigationItem = navItems.get(position);
        //Identify kind of view
        int type = getItemViewType(position);
        //Checking view process
        if (view == null) {
            viewInflater = LayoutInflater.from(context);
            holder = new ViewHolder();
            switch (type) {
                case MENU_LIST_ITEM:
                    view = viewInflater.inflate(R.layout.item_navigation_list, null);
                    holder.title = (TextView) view.findViewById(R.id.text_navigation_list);
                    holder.image = (ImageView) view.findViewById(R.id.list_item_entry_drawable);
                    holder.progress = (ProgressWheel) view.findViewById(R.id.progress_wheel_item_list);
                    break;
                case MENU_LIST_SECTION:
                    view = viewInflater.inflate(R.layout.item_navigation_section_list, null);
                    holder.title = (TextView) view.findViewById(R.id.list_item_section_text);
                    view.setOnClickListener(null);
                    view.setOnLongClickListener(null);
                    view.setLongClickable(false);
                    view.setTag(holder);
                    break;
            }
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        //Put it into component
        if (holder.title != null) {
            if (navigationItem.getName().length() > 0) {
                holder.title.setText(navigationItem.getName());
            }
        }
        if (holder.image != null) {
            if (navigationItem.getAsset_url().length() > 0) {
                Picasso.with(context).load(navigationItem.getAsset_url()).into(holder.image, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progress.setVisibility(View.GONE);
                    }
                    @Override
                    public void onError() {}
                });
            }
        }
        return view;
    }

    private static class ViewHolder {
        public TextView title;
        public ImageView image;
        public ProgressWheel progress;
    }

}
