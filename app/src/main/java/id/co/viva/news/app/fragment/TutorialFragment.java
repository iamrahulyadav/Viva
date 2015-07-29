package id.co.viva.news.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.activity.ActLanding;
import id.co.viva.news.app.component.ProgressWheel;

/**
 * Created by reza on 10/03/15.
 */
public class TutorialFragment extends Fragment {

    private String mUrl;
    private boolean isInternetPresent = false;
    private Activity mActivity;
    private ProgressWheel progressWheel;
    private TextView labelText;

    public static TutorialFragment newInstance(String url) {
        TutorialFragment tutorialFragment = new TutorialFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        tutorialFragment.setArguments(bundle);
        return tutorialFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isInternetPresent = Global.getInstance(getActivity())
                .getConnectionStatus().isConnectingToInternet();
        mUrl = getArguments().getString("url");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_coachmark, container, false);
        defineViews(view);
        return view;
    }

    private void defineViews(View mView) {
        ImageView imageView = (ImageView) mView.findViewById(R.id.tutorial_screen_image);
        labelText = (TextView) mView.findViewById(R.id.text_on_tutorial_page);
        labelText.setText(getResources().getString(R.string.label_process_get_image_tutorial));
        progressWheel = (ProgressWheel) mView.findViewById(R.id.progress_wheel);
        if (isInternetPresent) {
            if (getActivity() != null) {
                Picasso.with(getActivity()).load(mUrl).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressWheel.setVisibility(View.GONE);
                        labelText.setVisibility(View.GONE);
                    }
                    @Override
                    public void onError() {
                        progressWheel.setVisibility(View.INVISIBLE);
                        if (isAdded()) {
                            labelText.setText(getResources().getString(R.string.label_fail_get_image_tutorial));
                        }
                    }
                });
            } else {
                Picasso.with(mActivity).load(mUrl).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressWheel.setVisibility(View.GONE);
                        labelText.setVisibility(View.GONE);
                    }
                    @Override
                    public void onError() {
                        progressWheel.setVisibility(View.INVISIBLE);
                        labelText.setText(getResources().getString(R.string.label_fail_get_image_tutorial));
                    }
                });
            }
        } else {
            moveToApplication();
        }
    }

    private void moveToApplication() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), ActLanding.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
            getActivity().finish();
        } else {
            Intent intent = new Intent(mActivity, ActLanding.class);
            startActivity(intent);
            mActivity.overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
            mActivity.finish();
        }
    }

}
