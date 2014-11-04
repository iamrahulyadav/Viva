package id.co.viva.news.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import id.co.viva.news.app.fragment.DetailIndexContent;
import id.co.viva.news.app.model.ChannelBola;

/**
 * Created by reza on 24/10/14.
 */
public class DetailContentAdapterBola extends FragmentStatePagerAdapter {

    private ArrayList<ChannelBola> channelBolaArrayList;

    public DetailContentAdapterBola(FragmentManager fragmentManager, ArrayList<ChannelBola> channelBolaArrayList) {
        super(fragmentManager);
        this.channelBolaArrayList = channelBolaArrayList;
    }

    @Override
    public Fragment getItem(int position) {
        return DetailIndexContent.newInstance(channelBolaArrayList.get(position).getId(),
                "bola");
    }

    @Override
    public int getCount() {
        return channelBolaArrayList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
