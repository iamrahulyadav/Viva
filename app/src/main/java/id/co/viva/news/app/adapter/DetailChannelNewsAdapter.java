package id.co.viva.news.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import id.co.viva.news.app.fragment.DetailIndexChannelNews;
import id.co.viva.news.app.model.FeaturedNews;

/**
 * Created by reza on 23/10/14.
 */
public class DetailChannelNewsAdapter extends FragmentStatePagerAdapter {

    private ArrayList<FeaturedNews> featuredNewsArrayList;

    public DetailChannelNewsAdapter(FragmentManager fragmentManager, ArrayList<FeaturedNews> featuredNewsArrayList) {
        super(fragmentManager);
        this.featuredNewsArrayList = featuredNewsArrayList;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return DetailIndexChannelNews.newInstance(featuredNewsArrayList.get(position).getChannel_id());
            case 1:
                return DetailIndexChannelNews.newInstance(featuredNewsArrayList.get(position).getChannel_id());
            case 2:
                return DetailIndexChannelNews.newInstance(featuredNewsArrayList.get(position).getChannel_id());
            case 3:
                return DetailIndexChannelNews.newInstance(featuredNewsArrayList.get(position).getChannel_id());
            case 4:
                return DetailIndexChannelNews.newInstance(featuredNewsArrayList.get(position).getChannel_id());
            case 5:
                return DetailIndexChannelNews.newInstance(featuredNewsArrayList.get(position).getChannel_id());
            case 6:
                return DetailIndexChannelNews.newInstance(featuredNewsArrayList.get(position).getChannel_id());
            case 7:
                return DetailIndexChannelNews.newInstance(featuredNewsArrayList.get(position).getChannel_id());
            case 8:
                return DetailIndexChannelNews.newInstance(featuredNewsArrayList.get(position).getChannel_id());
            case 9:
                return DetailIndexChannelNews.newInstance(featuredNewsArrayList.get(position).getChannel_id());
            case 10:
                return DetailIndexChannelNews.newInstance(featuredNewsArrayList.get(position).getChannel_id());
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
        return featuredNewsArrayList.get(position).getChannel_title();
    }

    @Override
    public int getCount() {
        return featuredNewsArrayList.size();
    }

}
