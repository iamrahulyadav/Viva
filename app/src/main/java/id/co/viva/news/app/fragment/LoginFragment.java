package id.co.viva.news.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.ColorDrawable;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.activity.ActRegistration;
import id.co.viva.news.app.component.FloatingLabelView;
import id.co.viva.news.app.interfaces.OnCompleteListener;
import id.co.viva.news.app.services.UserAccount;
import id.co.viva.news.app.services.Validation;

/**
 * Created by reza on 27/11/14.
 */
public class LoginFragment extends Fragment implements OnCompleteListener,
        View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //Google Sign In Variables
    public static GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 0;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;

    private ActionProcessButton btnRegister;
    private ActionProcessButton btnSign;
    private FloatingLabelView mEmail;
    private FloatingLabelView mPassword;
    private TextView tvForgotPassword;
    private ImageView iconFb;
    private ImageView iconPath;
    private ImageView iconGPlus;
    private Validation validation;
    private UserAccount userAccount;
    private Boolean isInternetPresent = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isInternetPresent = Global.getInstance(getActivity()).getConnectionStatus().isConnectingToInternet();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().
                getColor(R.color.header_headline_terbaru_new)));
        activity.getActionBar().setIcon(R.drawable.logo_viva_coid_second);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_login, container, false);

        validation = new Validation();
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

        getGoogleClient();

        return rootView;
    }

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(getActivity(), RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        } else {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onComplete() {
        btnSign.setProgress(100);
        Toast.makeText(getActivity(),
                R.string.label_validation_success_login, Toast.LENGTH_SHORT).show();
        getActivity().finish();
        startActivity(getActivity().getIntent());
    }

    @Override
    public void onFailed() {
        btnSign.setProgress(0);
        enableWhenPressed();
        Toast.makeText(getActivity(),
                R.string.label_validation_failed_login,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError() {
        btnSign.setProgress(0);
        enableWhenPressed();
        Toast.makeText(getActivity(),
                R.string.label_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_log_in) {
            String email = mEmail.getText();
            String password = mPassword.getText();
            if(isInternetPresent) {
                if(validation.isEmailValid(email) && validation.isLengthValid(password)) {
                    userAccount = new UserAccount(email, password, this, getActivity());
                    disableWhenPressed();
                    btnSign.setProgress(1);
                    userAccount.signIn();
                } else if(!validation.isEmailValid(email) && validation.isLengthValid(password)) {
                    Toast.makeText(getActivity(), R.string.label_validation_email, Toast.LENGTH_SHORT).show();
                } else if(validation.isEmailValid(email) && !validation.isLengthValid(password)) {
                    Toast.makeText(getActivity(), R.string.label_validation_password_length, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), R.string.label_validation_both, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), R.string.title_no_connection, Toast.LENGTH_SHORT).show();
            }
        } else if(view.getId() == R.id.btn_register) {
            Intent intent = new Intent(getActivity(), ActRegistration.class);
            startActivity(intent);
        } else if(view.getId() == R.id.img_path) {
            Toast.makeText(getActivity(), "PATH", Toast.LENGTH_SHORT).show();
        } else if(view.getId() == R.id.img_gplus) {
            disableWhenPressed();
            signInWithGplus();
        } else if(view.getId() == R.id.img_fb) {
            Toast.makeText(getActivity(), "FB", Toast.LENGTH_SHORT).show();
        } else if(view.getId() == R.id.tv_forgot_password) {
            Toast.makeText(getActivity(), "FORGOT PASSWORD", Toast.LENGTH_SHORT).show();
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

    private void enableParentView(LinearLayout parentLayout) {
        if(parentLayout != null) {
            for (int i=0; i<parentLayout.getChildCount(); i++) {
                View views = parentLayout.getChildAt(i);
                views.setEnabled(true);
            }
        }
    }

    private void disableWhenPressed() {
        btnSign.setEnabled(false);
        btnRegister.setEnabled(false);
        iconPath.setEnabled(false);
        iconGPlus.setEnabled(false);
        iconFb.setEnabled(false);
        tvForgotPassword.setEnabled(false);
        disableParentView(mEmail);
        disableParentView(mPassword);
    }

    private void enableWhenPressed() {
        btnSign.setEnabled(true);
        btnRegister.setEnabled(true);
        iconPath.setEnabled(true);
        iconGPlus.setEnabled(true);
        iconFb.setEnabled(true);
        tvForgotPassword.setEnabled(true);
        enableParentView(mEmail);
        enableParentView(mPassword);
    }

    private void getGoogleClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;
        getProfileInfoFromGPlus();
        Toast.makeText(getActivity(), R.string.label_successful_gplus_connected, Toast.LENGTH_LONG).show();
        getActivity().finish();
        startActivity(getActivity().getIntent());
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != getActivity().RESULT_OK) {
                mSignInClicked = false;
            }
            mIntentInProgress = false;
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), getActivity(), 0).show();
            return;
        }
        if (!mIntentInProgress) {
            mConnectionResult = result;
            if (mSignInClicked) {
                resolveSignInError();
            }
        }
    }

    private void getProfileInfoFromGPlus() {
        try {
            if(Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                userAccount = new UserAccount(getActivity());
                userAccount.saveLoginStatesGPlus(email, personName, personPhotoUrl);
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

}
