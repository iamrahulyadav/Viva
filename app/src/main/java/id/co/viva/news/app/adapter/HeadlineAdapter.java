package id.co.viva.news.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import id.co.viva.news.app.fragment.LatestHeadlineIndexFragment;
import id.co.viva.news.app.model.Headline;

/**
 * Created by rezarachman on 02/10/14.
 */
public class HeadlineAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Headline> headlineArrayList;

    public HeadlineAdapter(FragmentManager fragmentManager, ArrayList<Headline> headlineArrayList) {
        super(fragmentManager);
        this.headlineArrayList = headlineArrayList;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return LatestHeadlineIndexFragment.newInstance(
                        headlineArrayList.get(position).getId(),
                        headlineArrayList.get(position).getTitle(),
                        headlineArrayList.get(position).getImage_url(),
                        headlineArrayList.get(position).getUrl());
            case 1:
                return LatestHeadlineIndexFragment.newInstance(
                        headlineArrayList.get(position).getId(),
                        headlineArrayList.get(position).getTitle(),
                        headlineArrayList.get(position).getImage_url(),
                        headlineArrayList.get(position).getUrl());
            case 2:
                return LatestHeadlineIndexFragment.newInstance(
                        headlineArrayList.get(position).getId(),
                        headlineArrayList.get(position).getTitle(),
                        headlineArrayList.get(position).getImage_url(),
                        headlineArrayList.get(position).getUrl());
            case 3:
                return LatestHeadlineIndexFragment.newInstance(
                        headlineArrayList.get(position).getId(),
                        headlineArrayList.get(position).getTitle(),
                        headlineArrayList.get(position).getImage_url(),
                        headlineArrayList.get(position).getUrl());
            case 4:
                return LatestHeadlineIndexFragment.newInstance(
                        headlineArrayList.get(position).getId(),
                        headlineArrayList.get(position).getTitle(),
                        headlineArrayList.get(position).getImage_url(),
                        headlineArrayList.get(position).getUrl());
            default:
                return null;
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return headlineArrayList.get(position).getKanal().toUpperCase();
    }

    @Override
    public int getCount() {
        return headlineArrayList.size();
    }

}
