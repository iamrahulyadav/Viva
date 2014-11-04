package id.co.viva.news.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import id.co.viva.news.app.fragment.DetailIndexContent;
import id.co.viva.news.app.model.ChannelNews;

/**
 * Created by reza on 24/10/14.
 */
public class DetailContentAdapterNews extends FragmentStatePagerAdapter {

    private ArrayList<ChannelNews> channelNewsArrayList;

    public DetailContentAdapterNews(FragmentManager fragmentManager, ArrayList<ChannelNews> channelNewsArrayList) {
        super(fragmentManager);
        this.channelNewsArrayList = channelNewsArrayList;
    }

    @Override
    public Fragment getItem(int position) {
        return DetailIndexContent.newInstance(channelNewsArrayList.get(position).getId(), "news");
    }

    @Override
    public int getCount() {
        return channelNewsArrayList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
