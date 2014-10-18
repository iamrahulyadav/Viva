package id.co.viva.news.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import id.co.viva.news.app.R;
import id.co.viva.news.app.VivaApp;

/**
 * Created by reza on 15/10/14.
 */
public class NewsFragment extends Fragment {

    private boolean isInternetPresent = false;
    private GridView gridNews;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isInternetPresent = VivaApp.getInstance().getConnectionStatus().isConnectingToInternet();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_news, container, false);
            gridNews = (GridView) rootView.findViewById(R.id.grid_news);
        return rootView;
    }

}
