package id.co.viva.news.app.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.doomonafireball.betterpickers.datepicker.DatePickerBuilder;
import com.doomonafireball.betterpickers.datepicker.DatePickerDialogFragment;

import java.util.ArrayList;

import id.co.viva.news.app.R;
import id.co.viva.news.app.VivaApp;
import id.co.viva.news.app.interfaces.OnCompleteListener;
import id.co.viva.news.app.services.UserAccount;
import id.co.viva.news.app.services.Validation;

/**
 * Created by reza on 02/12/14.
 */
public class ActRegistration extends FragmentActivity
        implements View.OnClickListener, AdapterView.OnItemSelectedListener,
        DatePickerDialogFragment.DatePickerDialogHandler, OnCompleteListener {

    private EditText etUsername;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etAddress;
    private EditText etCity;
    private Spinner etGender;
    private EditText etBirth;
    private EditText etTlp;
    private Button btnRegist;

    private String genderSelected;

    private Validation validation;
    private UserAccount userAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_register);

        validation = new Validation();

        getPrefActionBar();

        etUsername = (EditText) findViewById(R.id.form_regist_username);
        etEmail = (EditText) findViewById(R.id.form_regist_email);
        etPassword = (EditText) findViewById(R.id.form_regist_password);
        etCity = (EditText) findViewById(R.id.form_regist_city);
        etAddress = (EditText) findViewById(R.id.form_regist_address);
        etGender = (Spinner) findViewById(R.id.form_regist_gender);
        etBirth = (EditText) findViewById(R.id.form_regist_birthdate);
        etTlp = (EditText) findViewById(R.id.form_regist_phone);
        btnRegist = (Button) findViewById(R.id.btn_daftar);

        etGender.setOnItemSelectedListener(this);
        etBirth.setOnClickListener(this);
        btnRegist.setOnClickListener(this);

        populateDataGender();
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

    private void getPrefActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setTitle("Registrasi");
    }

    private void populateDataGender() {
        ArrayList<String> genderList = new ArrayList<String>();
        genderList.add(getResources().getString(R.string.label_gender_male));
        genderList.add(getResources().getString(R.string.label_gender_female));
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, genderList);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etGender.setAdapter(genderAdapter);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.form_regist_birthdate) {
            DatePickerBuilder dpb = new DatePickerBuilder()
                    .setFragmentManager(getSupportFragmentManager())
                    .setStyleResId(R.style.BetterPickersDialogFragment);
            dpb.show();
        } else if(view.getId() == R.id.btn_daftar) {
            String username = etUsername.getText().toString();
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String address = etAddress.getText().toString();
            String birthdate = etBirth.getText().toString();
            String phone = etTlp.getText().toString();
            String city = etCity.getText().toString();
            //Some Validation
            if(!validation.isLengthValid(username)) {
                etUsername.setError(getResources().getString(R.string.label_registrasi_username_character_length));
            } else if(!validation.isEmailValid(email)) {
                etEmail.setError(getResources().getString(R.string.label_validation_email));
            } else if(!validation.isLengthValid(password)) {
                etPassword.setError(getResources().getString(R.string.label_validation_password_length));
            } else if(!validation.isLengthValid(address)) {
                etAddress.setError(getResources().getString(R.string.label_registrasi_address_character_length));
            } else if(birthdate.length() < 1) {
                etBirth.setError(getResources().getString(R.string.label_registrasi_birt_validation));
            } else if(!validation.isLengthValid(phone)) {
                etTlp.setError(getResources().getString(R.string.label_registrasi_phone_validation));
            } else if(city.length() < 3) {
                etCity.setError(getResources().getString(R.string.label_registrasi_city_validation));
            } else {
                userAccount = new UserAccount(email, password, username, address,
                        city, genderSelected, birthdate, phone, this);
                userAccount.signUp();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        genderSelected = adapterView.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onDialogDateSet(int i, int year, int month, int date) {
        etBirth.setText(String.valueOf(date) + "-" + String.valueOf(month+1) + "-" + String.valueOf(year));
    }

    @Override
    public void onComplete() {
        Toast.makeText(VivaApp.getInstance(),
                R.string.label_validation_success_register, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onFailed() {
        Toast.makeText(VivaApp.getInstance(),
                R.string.label_validation_failed_register, Toast.LENGTH_SHORT).show();
    }

}
