package id.co.viva.news.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import id.co.viva.news.app.fragment.DetailHeadlineIndexFragment;
import id.co.viva.news.app.model.Headline;

/**
 * Created by root on 07/10/14.
 */
public class DetailHeadlineAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Headline> headlineList;

    public DetailHeadlineAdapter(FragmentManager fragmentManager, ArrayList<Headline> headlineList) {
        super(fragmentManager);
        this.headlineList = headlineList;
    }

    @Override
    public Fragment getItem(int position) {
        return DetailHeadlineIndexFragment.newInstance(headlineList.get(position).getId());
    }

    @Override
    public int getCount() {
        return headlineList.size();
    }

}
