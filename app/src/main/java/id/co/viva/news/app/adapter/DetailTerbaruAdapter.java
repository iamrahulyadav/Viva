package id.co.viva.news.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import id.co.viva.news.app.fragment.DetailTerbaruIndexFragment;
import id.co.viva.news.app.model.News;

/**
 * Created by reza on 15/10/14.
 */
public class DetailTerbaruAdapter extends FragmentStatePagerAdapter {

    private ArrayList<News> newsArrayList;

    public DetailTerbaruAdapter(FragmentManager fragmentManager, ArrayList<News> newsArrayList) {
        super(fragmentManager);
        this.newsArrayList = newsArrayList;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return DetailTerbaruIndexFragment.newInstance(newsArrayList.get(position).getId());
            case 1:
                return DetailTerbaruIndexFragment.newInstance(newsArrayList.get(position).getId());
            case 2:
                return DetailTerbaruIndexFragment.newInstance(newsArrayList.get(position).getId());
            case 3:
                return DetailTerbaruIndexFragment.newInstance(newsArrayList.get(position).getId());
            case 4:
                return DetailTerbaruIndexFragment.newInstance(newsArrayList.get(position).getId());
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return newsArrayList.size();
    }

}
