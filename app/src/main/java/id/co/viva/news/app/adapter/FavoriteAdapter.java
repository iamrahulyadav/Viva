package id.co.viva.news.app.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.UndoAdapter;

import java.util.ArrayList;

import id.co.viva.news.app.R;
import id.co.viva.news.app.model.Favorites;

/**
 * Created by reza on 22/11/14.
 */
public class FavoriteAdapter extends BaseAdapter implements UndoAdapter {

    private Context context;
    private ArrayList<Favorites> favoritesArrayList;

    public FavoriteAdapter(Context context, ArrayList<Favorites> favoritesArrayList) {
        this.favoritesArrayList = favoritesArrayList;
        this.context = context;
    }

    public Object removeItem(int position) {
        return favoritesArrayList.remove(position);
    }

    @Override
    public int getCount() {
        return favoritesArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return favoritesArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View rootView = LayoutInflater.from(context)
                .inflate(R.layout.item_favorites_list, viewGroup, false);

        TextView date_item_favorite = (TextView) rootView.findViewById(R.id.date_item_favorites);
        TextView title_item_favorite = (TextView) rootView.findViewById(R.id.title_item_favorites);
        ImageView image_item_favorite = (ImageView) rootView.findViewById(R.id.image_item_favorites);

        Favorites favorites = favoritesArrayList.get(position);
        title_item_favorite.setText(favorites.getTitle());
        date_item_favorite.setText(favorites.getDate_publish());
        if(favorites.getKanal().equalsIgnoreCase("bola")) {
            image_item_favorite.setImageResource(R.drawable.icon_viva_bola);
        } else if(favorites.getKanal().equalsIgnoreCase("vivalife")) {
            image_item_favorite.setImageResource(R.drawable.icon_viva_life);
        } else {
            image_item_favorite.setImageResource(R.drawable.icon_viva_news);
        }

        return rootView;
    }

    @NonNull
    @Override
    public View getUndoView(int i, @Nullable View convertView, @NonNull ViewGroup viewGroup) {
        View views = convertView;
        if (views == null) {
            views = LayoutInflater.from(context).inflate(R.layout.item_undo, viewGroup, false);
        }
        return views;
    }

    @NonNull
    @Override
    public View getUndoClickView(@NonNull View view) {
        return view.findViewById(R.id.undo_row_undobutton);
    }

}
