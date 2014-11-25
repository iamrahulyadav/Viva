package id.co.viva.news.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter;

import java.util.ArrayList;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.R;
import id.co.viva.news.app.VivaApp;
import id.co.viva.news.app.activity.ActDetailFavorite;
import id.co.viva.news.app.adapter.FavoriteAdapter;
import id.co.viva.news.app.model.Favorites;

/**
 * Created by reza on 17/11/14.
 */
public class FavoritesFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ArrayList<Favorites> favoritesArrayList;
    private String favoriteList;
    private int favoriteListSize;
    private FavoriteAdapter favoriteAdapter;
    private ScaleInAnimationAdapter scaleInAnimationAdapter;
    private ListView listFavorite;
    private TextView textNoResult;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().
                getColor(R.color.header_headline_terbaru_new)));
        activity.getActionBar().setIcon(R.drawable.logo_viva_coid_second);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_favorites, container, false);

        textNoResult = (TextView) rootView.findViewById(R.id.text_no_result_detail_content_favorite);
        textNoResult.setVisibility(View.GONE);

        listFavorite = (ListView) rootView.findViewById(R.id.list_favorites);
        listFavorite.setOnItemClickListener(this);

        VivaApp.getInstance().getSharedPreferences(VivaApp.getInstance());
        VivaApp.getInstance().getDefaultEditor();

        favoriteList = VivaApp.getInstance().getSharedPreferences(VivaApp.getInstance())
                .getString(Constant.FAVORITES_LIST, "");
        favoriteListSize = VivaApp.getInstance().getSharedPreferences(VivaApp.getInstance())
                .getInt(Constant.FAVORITES_LIST_SIZE, 0);

        favoritesArrayList = VivaApp.getInstance().getInstanceGson().
                fromJson(favoriteList, VivaApp.getInstance().getType());

        favoriteAdapter = new FavoriteAdapter(VivaApp.getInstance(), favoritesArrayList);

        for(int i=0; i<favoriteListSize; i++) {
            Log.i(Constant.TAG, "TITLE FAVORITES : " + favoritesArrayList.get(i).getTitle());
        }
        VivaApp.getInstance().getDefaultEditor().commit();

        if(favoriteListSize > 0 || favoritesArrayList != null) {
            scaleInAnimationAdapter = new ScaleInAnimationAdapter(favoriteAdapter);
            scaleInAnimationAdapter.setAbsListView(listFavorite);
            listFavorite.setAdapter(scaleInAnimationAdapter);
            scaleInAnimationAdapter.notifyDataSetChanged();
        } else {
            textNoResult.setVisibility(View.VISIBLE);
        }

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if(favoriteListSize > 0) {
            Favorites favorites = favoritesArrayList.get(position);
            Bundle bundle = new Bundle();
            bundle.putString("title", favorites.getTitle());
            bundle.putString("image_url", favorites.getImage_url());
            bundle.putString("date_publish", favorites.getDate_publish());
            bundle.putString("content", favorites.getContent());
            bundle.putString("reporter_name", favorites.getReporter_name());
            Intent intent = new Intent(VivaApp.getInstance(), ActDetailFavorite.class);
            intent.putExtras(bundle);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
        }
    }

}
