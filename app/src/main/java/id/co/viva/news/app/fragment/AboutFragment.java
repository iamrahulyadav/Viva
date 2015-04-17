package id.co.viva.news.app.fragment;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import id.co.viva.news.app.R;
import id.co.viva.news.app.model.DeviceInfo;

/**
 * Created by reza on 03/11/14.
 */
public class AboutFragment extends Fragment {

    private TextView textAppVersion;
    private ActionBarActivity mActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (ActionBarActivity) activity;
        mActivity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.new_base_color)));
        mActivity.getSupportActionBar().setIcon(R.drawable.logo_viva_coid_second);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_about, container, false);
        textAppVersion = (TextView) rootView.findViewById(R.id.text_app_version);
        DeviceInfo deviceInfo = new DeviceInfo(getActivity());
        textAppVersion.setText("Version " + deviceInfo.getAppVersionName());
        return rootView;
    }

}
