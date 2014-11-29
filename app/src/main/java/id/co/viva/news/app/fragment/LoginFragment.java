package id.co.viva.news.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;

import id.co.viva.news.app.R;
import id.co.viva.news.app.VivaApp;
import id.co.viva.news.app.component.FloatingLabelView;
import id.co.viva.news.app.component.ProgressGenerator;
import id.co.viva.news.app.interfaces.OnCompleteListener;

/**
 * Created by reza on 27/11/14.
 */
public class LoginFragment extends Fragment implements OnCompleteListener, View.OnClickListener {

    private ActionProcessButton btnRegister;
    private ActionProcessButton btnSign;
    private FloatingLabelView mEmail;
    private FloatingLabelView mPassword;
    private ProgressGenerator progressGenerator;
    private TextView tvForgotPassword;
    private ImageView iconFb;
    private ImageView iconPath;
    private ImageView iconGPlus;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_login, container, false);

        progressGenerator = new ProgressGenerator(this);
        mEmail = (FloatingLabelView) rootView.findViewById(R.id.form_email);
        mPassword = (FloatingLabelView) rootView.findViewById(R.id.form_password);
        iconFb = (ImageView) rootView.findViewById(R.id.img_fb);
        iconGPlus = (ImageView) rootView.findViewById(R.id.img_gplus);
        iconPath = (ImageView) rootView.findViewById(R.id.img_path);
        btnSign = (ActionProcessButton) rootView.findViewById(R.id.btn_log_in);
        btnRegister = (ActionProcessButton) rootView.findViewById(R.id.btn_register);
        tvForgotPassword = (TextView) rootView.findViewById(R.id.tv_forgot_password);
        btnSign.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        iconFb.setOnClickListener(this);
        iconGPlus.setOnClickListener(this);
        iconPath.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);
        btnSign.setMode(ActionProcessButton.Mode.ENDLESS);
        btnRegister.setMode(ActionProcessButton.Mode.ENDLESS);

        return rootView;
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_log_in) {
            progressGenerator.start(btnSign);
            btnSign.setEnabled(false);
            btnRegister.setEnabled(false);
            iconPath.setEnabled(false);
            iconGPlus.setEnabled(false);
            iconFb.setEnabled(false);
            tvForgotPassword.setEnabled(false);
            disableParentView(mEmail);
            disableParentView(mPassword);
        } else if(view.getId() == R.id.btn_register) {

        } else if(view.getId() == R.id.img_path) {
            Toast.makeText(VivaApp.getInstance(), "PATH", Toast.LENGTH_SHORT).show();
        } else if(view.getId() == R.id.img_gplus) {
            Toast.makeText(VivaApp.getInstance(), "GPLUS", Toast.LENGTH_SHORT).show();
        } else if(view.getId() == R.id.img_fb) {
            Toast.makeText(VivaApp.getInstance(), "FB", Toast.LENGTH_SHORT).show();
        } else if(view.getId() == R.id.tv_forgot_password) {
            Toast.makeText(VivaApp.getInstance(), "FORGOT PASSWORD", Toast.LENGTH_SHORT).show();
        }
    }

    private void disableParentView(LinearLayout parentLayout) {
        if(parentLayout != null) {
            for (int i=0; i<parentLayout.getChildCount(); i++) {
                View views = parentLayout.getChildAt(i);
                views.setEnabled(false);
            }
        }
    }

}
