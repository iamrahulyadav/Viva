package id.co.viva.news.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import id.co.viva.news.app.fragment.DetailIndexContent;
import id.co.viva.news.app.model.ChannelLife;

/**
 * Created by reza on 24/10/14.
 */
public class DetailContentAdapterLife extends FragmentStatePagerAdapter {

    private ArrayList<ChannelLife> channelLifeArrayList;

    public DetailContentAdapterLife(FragmentManager fragmentManager, ArrayList<ChannelLife> channelLifeArrayList) {
        super(fragmentManager);
        this.channelLifeArrayList = channelLifeArrayList;
    }

    @Override
    public Fragment getItem(int position) {
        return DetailIndexContent.newInstance(channelLifeArrayList.get(position).getId(), "vivalife");
    }

    @Override
    public int getCount() {
        return channelLifeArrayList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
