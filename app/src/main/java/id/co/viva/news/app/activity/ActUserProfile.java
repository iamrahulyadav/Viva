package id.co.viva.news.app.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.dd.processbutton.iml.ActionProcessButton;
import com.doomonafireball.betterpickers.datepicker.DatePickerBuilder;
import com.doomonafireball.betterpickers.datepicker.DatePickerDialogFragment;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.adapter.ProCityAdapter;
import id.co.viva.news.app.coachmark.CoachmarkBuilder;
import id.co.viva.news.app.coachmark.CoachmarkView;
import id.co.viva.news.app.component.ProgressGenerator;
import id.co.viva.news.app.component.ZoomFlip;
import id.co.viva.news.app.fragment.CardBackFragment;
import id.co.viva.news.app.fragment.CardFrontFragment;
import id.co.viva.news.app.interfaces.OnCompleteListener;
import id.co.viva.news.app.interfaces.OnProgressDoneListener;
import id.co.viva.news.app.interfaces.OnSpinnerListener;
import id.co.viva.news.app.interfaces.ShowingBackListener;
import id.co.viva.news.app.model.City;
import id.co.viva.news.app.model.Province;
import id.co.viva.news.app.services.GetDataUtils;
import id.co.viva.news.app.services.UserAccount;

/**
 * Created by reza on 03/12/14.
 */
public class ActUserProfile extends FragmentActivity implements View.OnClickListener, OnCompleteListener,
        OnProgressDoneListener, AdapterView.OnItemSelectedListener, ShowingBackListener, OnSpinnerListener,
        DatePickerDialogFragment.DatePickerDialogHandler, android.app.FragmentManager.OnBackStackChangedListener {

    private boolean isInternetPresent = false;
    private CircularProgressButton btnLogout;
    private ActionProcessButton btnSave;
    private RelativeLayout backgroundLayout;
    private TextView mProfileName;
    private TextView mProfileEmail;
    private ImageView mprofileThumb;
    private EditText etBirth;
    private EditText etState;
    private UserAccount userAccount;
    private Spinner spinProvince;
    private Spinner spinCity;
    private Spinner spinnerGender;

    private String genderSelected;
    private String provinceIdSelected;
    private String citySelected;
    private String fullname;
    private String email;
    private String photourl;
    private String gender;
    private String country;
    private String province;
    private String city;
    private String birthdate;

    private ArrayList<Province> provinceArrayList;
    private ArrayList<City> cityArrayList;
    private ProCityAdapter proCityAdapter;

    private ProgressGenerator progressGenerator;

    private ZoomFlip zoomFlip;
    private FrameLayout mParentLayout;
    private RelativeLayout mMainContainer;
    private RelativeLayout mOverlayLayout;
    private CardBackFragment mBackFragment;
    private CardFrontFragment mFrontFragment;
    private boolean mShowingBack = false;
    private GetDataUtils getDataUtils;

    private View coachmarkView;
    private CoachmarkView showtips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_profile);
        isInternetPresent = Global.getInstance(this)
                .getConnectionStatus()
                .isConnectingToInternet();

        getHeaderActionBar();
        getProfile();
        defineView();

        if(fullname.length() > 0) {
            mProfileName.setText(fullname);
        }
        if(email.length() > 0) {
            mProfileEmail.setText(email);
        }
        if(photourl.length() > 0) {
            Picasso.with(ActUserProfile.this)
                    .load(photourl)
                    .into(mprofileThumb);
        } else {
            mprofileThumb.setImageResource(R.drawable.ic_profile);
        }
        if(birthdate.length() > 0) {
            etBirth.setText(birthdate);
        }
        if(country.length() > 0) {
            etState.setText(country);
        }

        populateDataGender();

        if(isInternetPresent) {
            getDataUtils = new GetDataUtils(this, ActUserProfile.this);
            getDataUtils.getDataProvince();
        } else {
            Toast.makeText(this, R.string.title_no_connection, Toast.LENGTH_SHORT).show();
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
            progressGenerator = new ProgressGenerator(this);
            progressGenerator.start(btnLogout);
            userAccount = new UserAccount(this);
            userAccount.deleteLoginStates();
        } else if(view.getId() == R.id.btn_change_data_user) {
            String birth = etBirth.getText().toString();
            btnSave.setProgress(1);
            userAccount = new UserAccount(this);
            userAccount.saveAttributesUserProfile(genderSelected, birth, country, province, city);
            userAccount.editProfile(fullname, genderSelected, citySelected, birth, this);
        } else if(view.getId() == R.id.form_regist_birthdate) {
            DatePickerBuilder dpb = new DatePickerBuilder()
                    .setFragmentManager(getSupportFragmentManager())
                    .setStyleResId(R.style.BetterPickersDialogFragment);
            dpb.show();
        } else if(view.getId() == R.id.img_thumb_profile) {
            if(photourl.length() > 0) {
                flip();
                zoomFlip.zoomImageFromThumb(mprofileThumb);
            }
        }
    }

    private void disableViews() {
        mprofileThumb.setEnabled(false);
        spinnerGender.setEnabled(false);
        etBirth.setEnabled(false);
        etState.setEnabled(false);
        spinProvince.setEnabled(false);
        spinCity.setEnabled(false);
        btnSave.setOnClickListener(null);
        btnLogout.setOnClickListener(null);
    }

    private void enableViews() {
        mprofileThumb.setEnabled(true);
        spinnerGender.setEnabled(true);
        etBirth.setEnabled(true);
        etState.setEnabled(true);
        spinProvince.setEnabled(true);
        spinCity.setEnabled(true);
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
    public void onProgressDone() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                enableViews();
                refreshContent();
            }
        };
        Handler h = new Handler();
        h.postDelayed(r, 1000);
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
        gender = Global.getInstance(this).getSharedPreferences(this)
                .getString(Constant.LOGIN_STATES_GENDER, "");
        birthdate = Global.getInstance(this).getSharedPreferences(this)
                .getString(Constant.LOGIN_STATES_BIRTHDATE, "");
        country = Global.getInstance(this).getSharedPreferences(this)
                .getString(Constant.LOGIN_STATES_COUNTRY, "");
        province = Global.getInstance(this).getSharedPreferences(this)
                .getString(Constant.LOGIN_STATES_PROVINCE, "");
        city = Global.getInstance(this).getSharedPreferences(this)
                .getString(Constant.LOGIN_STATES_CITY, "");
    }

    private void defineView() {
        //Initiate Views
        cityArrayList = new ArrayList<City>();
        provinceArrayList = new ArrayList<Province>();
        coachmarkView = findViewById(R.id.coachmark_img_profile);
        backgroundLayout = (RelativeLayout) findViewById(R.id.layout_background_profile_photo);
        spinnerGender = (Spinner) findViewById(R.id.spin_regist_gender);
        spinCity = (Spinner) findViewById(R.id.spin_regist_city);
        spinProvince = (Spinner) findViewById(R.id.spin_regist_province);
        etBirth = (EditText) findViewById(R.id.form_regist_birthdate);
        etState = (EditText) findViewById(R.id.form_regist_country);
        etState.addTextChangedListener(mTextEditorWatcher);
        mProfileName = (TextView) findViewById(R.id.tv_profile_name);
        mProfileEmail = (TextView) findViewById(R.id.tv_profile_email);
        mprofileThumb = (ImageView) findViewById(R.id.img_thumb_profile);
        btnLogout = (CircularProgressButton) findViewById(R.id.btn_logout);
        btnSave = (ActionProcessButton) findViewById(R.id.btn_change_data_user);
        btnSave.setMode(ActionProcessButton.Mode.ENDLESS);
        mBackFragment = new CardBackFragment(fullname, photourl, this);
        mFrontFragment = new CardFrontFragment(photourl, this);
        getFragmentManager().beginTransaction()
                .add(R.id.main, mFrontFragment).commit();
        mParentLayout = (FrameLayout) findViewById(R.id.container);
        mMainContainer = (RelativeLayout) findViewById(R.id.main);
        mMainContainer.setVisibility(View.GONE);
        mOverlayLayout = (RelativeLayout) findViewById(R.id.overlay);
        zoomFlip = new ZoomFlip(mParentLayout, mMainContainer, mOverlayLayout);
        //Add Listener
        zoomFlip.setShowingBackListener(this);
        btnLogout.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        etBirth.setOnClickListener(this);
        mprofileThumb.setOnClickListener(this);
        spinnerGender.setOnItemSelectedListener(this);
        spinProvince.setOnItemSelectedListener(this);
        spinCity.setOnItemSelectedListener(this);
        getFragmentManager().addOnBackStackChangedListener(this);
        //Show Coach-Mark
        showCoachMark();
    }

    private void showCoachMark() {
        if(Global.getInstance(this).getSharedPreferences(this).getBoolean(Constant.FIRST_INSTALL_PROFILE, true)) {
            RelativeLayout relativeLayout = new RelativeLayout(this);
            relativeLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            ((RelativeLayout) coachmarkView).addView(relativeLayout);
            showtips = new CoachmarkBuilder(this)
                    .setTarget(mprofileThumb)
                    .setTitle(getResources().getString(R.string.label_image_profile))
                    .setDescription(getResources().getString(R.string.label_image_profile_desc))
                    .setDelay(1000)
                    .build();
            showtips.show(this);
            Global.getInstance(this).getSharedPreferences(this).
                    edit().putBoolean(Constant.FIRST_INSTALL_PROFILE, false).commit();
        }
    }

    private final TextWatcher  mTextEditorWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            String text = charSequence.toString();
            if(!text.equalsIgnoreCase(getResources().getString(R.string.label_registrasi_default_country))) {
                spinProvince.setEnabled(false);
                spinCity.setEnabled(false);
            } else {
                spinProvince.setEnabled(true);
                spinCity.setEnabled(true);
            }
        }
        @Override
        public void afterTextChanged(Editable editable) {}
    };

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
        Spinner spinner = (Spinner) adapterView;
        if(spinner.getId() == R.id.spin_regist_gender) {
            genderSelected = adapterView.getItemAtPosition(position).toString();
        } else if(spinner.getId() == R.id.spin_regist_province) {
            Province mProvince = provinceArrayList.get(position);
            provinceIdSelected = mProvince.getId_propinsi();
            if(isInternetPresent) {
                getDataUtils = new GetDataUtils(this, ActUserProfile.this);
                getDataUtils.getDataCity(provinceIdSelected);
            } else {
                Toast.makeText(this, R.string.title_no_connection, Toast.LENGTH_SHORT).show();
            }
        } else if(spinner.getId() == R.id.spin_regist_city) {
            City mCity = cityArrayList.get(position);
            citySelected = mCity.getNama();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onBackPressed() {
        if (mShowingBack) {
            flip();
            zoomFlip.moveToThumb();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit);
        }
    }

    @Override
    public void onShowingBackClick() {
        flip();
        zoomFlip.moveToThumb();
    }

    private void flip() {
        if (mShowingBack) {
            getFragmentManager().popBackStack();
            return;
        }
        mShowingBack = true;
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.card_flip_right_in,
                        R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in,
                        R.animator.card_flip_left_out)
                .replace(R.id.main, mBackFragment)
                .addToBackStack(null).commit();
    }

    @Override
    public void onBackStackChanged() {
        mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);
        invalidateOptionsMenu();
    }

    @Override
    public void onSuccessLoadDataSpinner(String response, String type) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArrayResponses = jsonObject.getJSONArray(Constant.response);
            if(cityArrayList.size() > 0) {
                cityArrayList.clear();
            }
            if (jsonArrayResponses != null) {
                for (int i=0; i<jsonArrayResponses.length(); i++) {
                    JSONObject jsonHeadline = jsonArrayResponses.getJSONObject(i);
                    String nama = jsonHeadline.getString(Constant.TAG_LOCATION_NAME);
                    String id_propinsi = jsonHeadline.getString(Constant.TAG_LOCATION_PROVINCE_ID);
                    String id_kabupaten = jsonHeadline.getString(Constant.TAG_LOCATION_KABUPATEN_ID);
                    if(type.equals(Constant.ADAPTER_PROVINCE)) {
                        provinceArrayList.add(new Province(nama,id_propinsi, id_kabupaten));
                    } else {
                        cityArrayList.add(new City(nama, id_propinsi, id_kabupaten));
                    }
                }
            }
            if(type.equals(Constant.ADAPTER_PROVINCE)) {
                if(provinceArrayList.size() > 0) {
                    proCityAdapter = new ProCityAdapter(provinceArrayList, null,
                            this, Constant.ADAPTER_PROVINCE);
                    spinProvince.setAdapter(proCityAdapter);
                }
            } else if(type.equals(Constant.ADAPTER_CITY)) {
                if(cityArrayList.size() > 0) {
                    proCityAdapter = new ProCityAdapter(null, cityArrayList,
                            this, Constant.ADAPTER_CITY);
                    spinCity.setAdapter(proCityAdapter);
                }
            }
            proCityAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public void onErrorLoadDataSpinner(String error, String type) {
        if(type.equals(Constant.ADAPTER_PROVINCE)) {
//            Toast.makeText(this, R.string.label_failed_get_province, Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(this, R.string.label_failed_get_city, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onComplete(String message) {
        btnSave.setProgress(100);
        Runnable r = new Runnable() {
            @Override
            public void run() {
                enableViews();
                btnSave.setProgress(0);
            }
        };
        Handler h = new Handler();
        h.postDelayed(r, 2000);
    }

    @Override
    public void onFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        btnSave.setProgress(0);
        enableViews();
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        btnSave.setProgress(0);
        enableViews();
    }

    @Override
    public void onDelay(String message) {}

}
