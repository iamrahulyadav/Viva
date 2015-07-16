package id.co.viva.news.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import id.co.viva.news.app.fragment.DetailPagingFragment;

/**
 * Created by reza on 15/07/15.
 */
public class PagingDetailArticleAdapter extends FragmentStatePagerAdapter {

    private ArrayList<String> contents;

    public PagingDetailArticleAdapter(FragmentManager fragmentManager, ArrayList<String> contents) {
        super(fragmentManager);
        this.contents = contents;
    }

    @Override
    public Fragment getItem(int position) {
        return DetailPagingFragment.newInstance(contents.get(position));
    }

    @Override
    public int getCount() {
        return contents.size();
    }

}
