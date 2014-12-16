package id.co.viva.news.app.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.doomonafireball.betterpickers.datepicker.DatePickerBuilder;
import com.doomonafireball.betterpickers.datepicker.DatePickerDialogFragment;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;

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
        OnProgressDoneListener, AdapterView.OnItemSelectedListener,
        DatePickerDialogFragment.DatePickerDialogHandler {

    private static final String TYPE_LOGOUT = "logout";
    private static final String TYPE_SAVE = "save";

    private CircularProgressButton btnLogout;
    private CircularProgressButton btnSave;
    private RelativeLayout backgroundLayout;
    private TextView mProfileName;
    private TextView mProfileEmail;
    private ImageView mprofileThumb;
    private EditText etPhone;
    private EditText etCity;
    private EditText etBirth;
    private UserAccount userAccount;
    private Spinner spinnerGender;
    private String genderSelected;
    private String fullname;
    private String email;
    private String photourl;
    private String phone;
    private String gender;
    private String city;
    private String birthdate;
    private ProgressGenerator progressGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_profile);
        getHeaderActionBar();
        defineView();
        getProfile();
        //Showing Data
        if(fullname.length() > 0) {
            mProfileName.setText(fullname);
        }
        if(email.length() > 0) {
            mProfileEmail.setText(email);
        }
        if(photourl.length() > 0) {
            Log.i(Constant.TAG, "Url Photo : " + photourl);
            try {
                setBackgroundBlurred(photourl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(phone.length() > 0) {
            etPhone.setText(phone);
        }
        if(city.length() > 0) {
            etCity.setText(city);
        }
        if(birthdate.length() > 0) {
            etBirth.setText(birthdate);
        }
        populateDataGender();
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
            progressGenerator = new ProgressGenerator(this, TYPE_LOGOUT);
            progressGenerator.start(btnLogout);
            userAccount = new UserAccount(this);
            userAccount.deleteLoginStates();
        } else if(view.getId() == R.id.btn_change_data_user) {
            String phone = etPhone.getText().toString();
            String city = etCity.getText().toString();
            String birth = etBirth.getText().toString();
            progressGenerator = new ProgressGenerator(this, TYPE_SAVE);
            progressGenerator.start(btnSave);
            userAccount = new UserAccount(this);
            userAccount.saveAttributesUserProfile(phone, genderSelected, birth, city);
        } else if(view.getId() == R.id.form_regist_birthdate) {
            DatePickerBuilder dpb = new DatePickerBuilder()
                    .setFragmentManager(getSupportFragmentManager())
                    .setStyleResId(R.style.BetterPickersDialogFragment);
            dpb.show();
        }
    }

    private void disableViews() {
        etBirth.setEnabled(false);
        etCity.setEnabled(false);
        etPhone.setEnabled(false);
        spinnerGender.setEnabled(false);
        btnSave.setOnClickListener(null);
        btnLogout.setOnClickListener(null);
    }

    private void enableViews() {
        etBirth.setEnabled(true);
        etCity.setEnabled(true);
        etPhone.setEnabled(true);
        spinnerGender.setEnabled(true);
        btnSave.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
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
    public void onProgressDone(String type) {
        if(type.equalsIgnoreCase(TYPE_LOGOUT)) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    enableViews();
                    refreshContent();
                }
            };
            Handler h = new Handler();
            h.postDelayed(r, 1000);
        } else {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    btnSave.setProgress(0);
                    enableViews();
                }
            };
            Handler h = new Handler();
            h.postDelayed(r, 1000);
        }
    }

    @Override
    public void onProgressProcess() {
        disableViews();
    }

    private void getProfile() {
        Global.getInstance(this).getDefaultEditor();
        fullname = Global.getInstance(this).getSharedPreferences(this)
                .getString(Constant.LOGIN_STATES_FULLNAME, "");
        email = Global.getInstance(this).getSharedPreferences(this)
                .getString(Constant.LOGIN_STATES_EMAIL, "");
        photourl = Global.getInstance(this).getSharedPreferences(this)
                .getString(Constant.LOGIN_STATES_URL_PHOTO, "");
        phone = Global.getInstance(this).getSharedPreferences(this)
                .getString(Constant.LOGIN_STATES_PHONE, "");
        city = Global.getInstance(this).getSharedPreferences(this)
                .getString(Constant.LOGIN_STATES_CITY, "");
        gender = Global.getInstance(this).getSharedPreferences(this)
                .getString(Constant.LOGIN_STATES_GENDER, "");
        birthdate = Global.getInstance(this).getSharedPreferences(this)
                .getString(Constant.LOGIN_STATES_BIRTHDATE, "");
    }

    private void defineView() {
        backgroundLayout = (RelativeLayout) findViewById(R.id.layout_background_profile_photo);
        spinnerGender = (Spinner) findViewById(R.id.spin_regist_gender);
        etPhone = (EditText) findViewById(R.id.form_regist_phone);
        etCity = (EditText) findViewById(R.id.form_regist_city);
        etBirth = (EditText) findViewById(R.id.form_regist_birthdate);
        mProfileName = (TextView) findViewById(R.id.tv_profile_name);
        mProfileEmail = (TextView) findViewById(R.id.tv_profile_email);
        mprofileThumb = (ImageView) findViewById(R.id.img_thumb_profile);
        btnLogout = (CircularProgressButton) findViewById(R.id.btn_logout);
        btnSave = (CircularProgressButton) findViewById(R.id.btn_change_data_user);
        btnLogout.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        etBirth.setOnClickListener(this);
        spinnerGender.setOnItemSelectedListener(this);
    }

    private void refreshContent() {
        finish();
        Intent intent = new Intent(this, ActLanding.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void populateDataGender() {
        ArrayList<String> genderList = new ArrayList<String>();
        genderList.add(getResources().getString(R.string.label_gender_male));
        genderList.add(getResources().getString(R.string.label_gender_female));
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, genderList);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);
        if(gender.length() > 0) {
            if(gender.equalsIgnoreCase(getResources().getString(R.string.label_gender_male))) {
                spinnerGender.setSelection(0);
            } else {
                spinnerGender.setSelection(1);
            }
        }
    }

    @Override
    public void onDialogDateSet(int i, int year, int month, int date) {
        etBirth.setText(String.valueOf(date) + "-" + String.valueOf(month+1) + "-" + String.valueOf(year));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        genderSelected = adapterView.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void setBackgroundBlurred(String mUrl) throws IOException {
        Picasso.with(this).load(mUrl).into(target);
    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            BitmapDrawable bitmapDrawable = new BitmapDrawable(Constant.blur(ActUserProfile.this, bitmap));
            backgroundLayout.setBackgroundDrawable(bitmapDrawable);
            Picasso.with(ActUserProfile.this).load(photourl).into(mprofileThumb);
        }
        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            backgroundLayout.setBackgroundDrawable(errorDrawable);
        }
        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            backgroundLayout.setBackgroundDrawable(placeHolderDrawable);
        }
    };

}
