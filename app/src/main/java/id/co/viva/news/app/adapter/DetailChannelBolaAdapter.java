package id.co.viva.news.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import id.co.viva.news.app.fragment.DetailIndexChannelBola;
import id.co.viva.news.app.model.FeaturedBola;

/**
 * Created by reza on 23/10/14.
 */
public class DetailChannelBolaAdapter extends FragmentStatePagerAdapter {

    private ArrayList<FeaturedBola> featuredBolaArrayList;

    public DetailChannelBolaAdapter(FragmentManager fragmentManager, ArrayList<FeaturedBola> featuredBolaArrayList) {
        super(fragmentManager);
        this.featuredBolaArrayList = featuredBolaArrayList;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return DetailIndexChannelBola.newInstance(featuredBolaArrayList.get(position).getChannel_id());
            case 1:
                return DetailIndexChannelBola.newInstance(featuredBolaArrayList.get(position).getChannel_id());
            case 2:
                return DetailIndexChannelBola.newInstance(featuredBolaArrayList.get(position).getChannel_id());
            case 3:
                return DetailIndexChannelBola.newInstance(featuredBolaArrayList.get(position).getChannel_id());
            case 4:
                return DetailIndexChannelBola.newInstance(featuredBolaArrayList.get(position).getChannel_id());
            case 5:
                return DetailIndexChannelBola.newInstance(featuredBolaArrayList.get(position).getChannel_id());
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
        return featuredBolaArrayList.get(position).getChannel_title();
    }

    @Override
    public int getCount() {
        return featuredBolaArrayList.size();
    }

}
