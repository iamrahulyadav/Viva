package id.co.viva.news.app.fragment;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dd.CircularProgressButton;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.R;
import id.co.viva.news.app.VivaApp;
import id.co.viva.news.app.component.ProgressGenerator;
import id.co.viva.news.app.interfaces.OnProgressDoneListener;
import id.co.viva.news.app.services.UserAccount;

/**
 * Created by reza on 03/12/14.
 */
public class UserProfileFragment extends Fragment implements View.OnClickListener, OnProgressDoneListener {

    private CircularProgressButton btnLogout;
    private TextView mProfileName;
    private TextView mProfileEmail;
    private UserAccount userAccount;
    private String fullname;
    private String email;
    private ProgressGenerator progressGenerator;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().
                getColor(R.color.header_headline_terbaru_new)));
        activity.getActionBar().setIcon(R.drawable.logo_viva_coid_second);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_profile, container, false);

        mProfileName = (TextView) rootView.findViewById(R.id.tv_profile_name);
        mProfileEmail = (TextView) rootView.findViewById(R.id.tv_profile_email);
        btnLogout = (CircularProgressButton) rootView.findViewById(R.id.btn_logout);
        progressGenerator = new ProgressGenerator(this);

        VivaApp.getInstance().getDefaultEditor();
        fullname = VivaApp.getInstance().getSharedPreferences(VivaApp.getInstance())
                .getString(Constant.LOGIN_STATES_FULLNAME, "");
        email = VivaApp.getInstance().getSharedPreferences(VivaApp.getInstance())
                .getString(Constant.LOGIN_STATES_EMAIL, "");

        if(fullname.length() > 0) {
            mProfileName.setText(fullname);
        }
        if(email.length() > 0) {
            mProfileEmail.setText(email);
        }
        btnLogout.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_logout) {
            progressGenerator.start(btnLogout);
            userAccount = new UserAccount();
            userAccount.deleteLoginStates();
        }
    }

    @Override
    public void onProgressDone() {
        getActivity().finish();
        startActivity(getActivity().getIntent());
    }

}
