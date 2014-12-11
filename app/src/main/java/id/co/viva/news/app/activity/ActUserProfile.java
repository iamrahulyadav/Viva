package id.co.viva.news.app.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.squareup.picasso.Picasso;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.component.ProgressGenerator;
import id.co.viva.news.app.interfaces.OnProgressDoneListener;
import id.co.viva.news.app.services.UserAccount;

/**
 * Created by reza on 03/12/14.
 */
public class ActUserProfile extends FragmentActivity implements View.OnClickListener,
        OnProgressDoneListener {

    private CircularProgressButton btnLogout;
    private TextView mProfileName;
    private TextView mProfileEmail;
    private ImageView mprofileThumb;
    private UserAccount userAccount;
    private String fullname;
    private String email;
    private String photourl;
    private ProgressGenerator progressGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_profile);

        getHeaderActionBar();
        defineView();
        getProfile();

        if(fullname.length() > 0) {
            mProfileName.setText(fullname);
        }
        if(email.length() > 0) {
            mProfileEmail.setText(email);
        }
        if(photourl.length() > 0) {
            Picasso.with(this).load(photourl).into(mprofileThumb);
        }
    }

    private void getHeaderActionBar() {
        getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().
                getColor(R.color.header_headline_terbaru_new)));
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setIcon(R.drawable.logo_viva_coid_second);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_logout) {
            progressGenerator.start(btnLogout);
            userAccount = new UserAccount(this);
            userAccount.deleteLoginStates();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProgressDone() {
        refreshContent();
    }

    private void getProfile() {
        Global.getInstance(this).getDefaultEditor();
        fullname = Global.getInstance(this).getSharedPreferences(this)
                .getString(Constant.LOGIN_STATES_FULLNAME, "");
        email = Global.getInstance(this).getSharedPreferences(this)
                .getString(Constant.LOGIN_STATES_EMAIL, "");
        photourl = Global.getInstance(this).getSharedPreferences(this)
                .getString(Constant.LOGIN_STATES_URL_PHOTO, "");
    }

    private void defineView() {
        mProfileName = (TextView) findViewById(R.id.tv_profile_name);
        mProfileEmail = (TextView) findViewById(R.id.tv_profile_email);
        mprofileThumb = (ImageView) findViewById(R.id.img_thumb_profile);
        btnLogout = (CircularProgressButton) findViewById(R.id.btn_logout);
        progressGenerator = new ProgressGenerator(this);
        btnLogout.setOnClickListener(this);
    }

    private void refreshContent() {
        finish();
        Intent intent = new Intent(this, ActLanding.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
