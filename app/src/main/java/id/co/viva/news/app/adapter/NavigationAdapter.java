package id.co.viva.news.app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.R;
import id.co.viva.news.app.interfaces.Item;
import id.co.viva.news.app.model.NavigationItem;
import id.co.viva.news.app.model.NavigationProfileItem;
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
        Item item = navItems.get(position);
        if(item != null) {
            if(item.isSection()) {
                NavigationSectionItem navigationSectionItem = (NavigationSectionItem) item;
                v = vi.inflate(R.layout.item_navigation_section_list, null);
                v.setOnClickListener(null);
                v.setOnLongClickListener(null);
                v.setLongClickable(false);
                TextView sectionView = (TextView) v.findViewById(R.id.list_item_section_text);
                sectionView.setText(navigationSectionItem.getTitle());
            } else if(item.isUserProfile()) {
                NavigationProfileItem navigationProfileItem = (NavigationProfileItem) item;
                v = vi.inflate(R.layout.item_navigation_profile_list, null);
                v.setOnLongClickListener(null);
                v.setLongClickable(false);
                TextView username = (TextView) v.findViewById(R.id.tv_username);
                TextView email = (TextView) v.findViewById(R.id.tv_user_email);
                ImageView thumb = (ImageView) v.findViewById(R.id.img_profile);
                username.setText(navigationProfileItem.getUsername());
                email.setText(navigationProfileItem.getEmail());
                if(navigationProfileItem.getImgProfile() != null) {
                    if(navigationProfileItem.getImgProfile().length() > 0) {
                        Picasso.with(context).load(navigationProfileItem.getImgProfile()).into(thumb);
                    }
                }
            } else {
                NavigationItem ei = (NavigationItem) item;
                v = vi.inflate(R.layout.item_navigation_list, null);
                TextView title = (TextView) v.findViewById(R.id.text_navigation_list);
                ImageView image = (ImageView) v.findViewById(R.id.list_item_entry_drawable);
                if(title != null)
                    title.setText(ei.getTitle());
                if(image != null)
                    image.setImageResource(ei.getIcon());
            }
        }
        return v;
    }

}
