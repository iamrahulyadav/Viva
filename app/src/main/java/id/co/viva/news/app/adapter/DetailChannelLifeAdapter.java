package id.co.viva.news.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import id.co.viva.news.app.fragment.DetailIndexChannelLife;
import id.co.viva.news.app.model.FeaturedLife;

/**
 * Created by reza on 23/10/14.
 */
public class DetailChannelLifeAdapter extends FragmentStatePagerAdapter {

    private ArrayList<FeaturedLife> featuredLifeArrayList;

    public DetailChannelLifeAdapter(FragmentManager fragmentManager, ArrayList<FeaturedLife> featuredLifeArrayList) {
        super(fragmentManager);
        this.featuredLifeArrayList = featuredLifeArrayList;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return DetailIndexChannelLife.newInstance(featuredLifeArrayList.get(position).getChannel_id());
            case 1:
                return DetailIndexChannelLife.newInstance(featuredLifeArrayList.get(position).getChannel_id());
            case 2:
                return DetailIndexChannelLife.newInstance(featuredLifeArrayList.get(position).getChannel_id());
            case 3:
                return DetailIndexChannelLife.newInstance(featuredLifeArrayList.get(position).getChannel_id());
            case 4:
                return DetailIndexChannelLife.newInstance(featuredLifeArrayList.get(position).getChannel_id());
            case 5:
                return DetailIndexChannelLife.newInstance(featuredLifeArrayList.get(position).getChannel_id());
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
        return featuredLifeArrayList.get(position).getChannel_title();
    }

    @Override
    public int getCount() {
        return featuredLifeArrayList.size();
    }

}
