package id.co.viva.news.app.services;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.VivaApp;
import id.co.viva.news.app.interfaces.OnCompleteListener;

/**
 * Created by reza on 01/12/14.
 */
public class UserAccount {

    private OnCompleteListener mListener;

    private static final int STATUS_SUCCESS = 1;
    private static final int STATUS_FAILED = 0;
    private static final String EMAIL = "email";
    private static final String FULLNAME = "fullname";
    private static final String STATUS = "status";

    private String article_id;
    private String comment_text;
    private String app_id;
    private String mEmail;
    private String mPassword;
    private String mUsername;
    private String mAlamat;
    private String mKota;
    private String mGender;
    private String mBirthDate;
    private String mPhone;

    public UserAccount() {}

    public UserAccount(String article_id, String mEmail, String mUsername, String comment_text,
                       String app_id, OnCompleteListener mListener) {
        this.article_id = article_id;
        this.mEmail = mEmail;
        this.mUsername = mUsername;
        this.comment_text = comment_text;
        this.app_id = app_id;
        this.mListener = mListener;
    }

    public UserAccount(String mEmail, String mPassword, OnCompleteListener mListener) {
        this.mEmail = mEmail;
        this.mPassword = mPassword;
        this.mListener = mListener;
    }

    public UserAccount(String mUsername, String mEmail, String mPassword, String mAlamat,
                       String mKota, String mGender, String mBirthDate, String mPhone,
                       OnCompleteListener mListener) {
        this.mEmail = mEmail;
        this.mPassword = mPassword;
        this.mUsername = mUsername;
        this.mAlamat = mAlamat;
        this.mKota = mKota;
        this.mGender = mGender;
        this.mBirthDate = mBirthDate;
        this.mPhone = mPhone;
        this.mListener = mListener;
    }

    public void sendComment() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.NEW_COMMENTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.i(Constant.TAG, "Response Comment : " + s);
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            int status = jsonObject.getInt(STATUS);
                            if(status == STATUS_SUCCESS) {
                                mListener.onComplete();
                            } else if(status == STATUS_FAILED) {
                                mListener.onFailed();
                            }
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.getMessage();
                        mListener.onError();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("article_id", article_id);
                params.put("email", mEmail);
                params.put("username", mUsername);
                params.put("comment_text", comment_text);
                params.put("app_id", "Android");
                return params;
            }
        };
        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VivaApp.getInstance().addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
    }

    public void signIn() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.NEW_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.i(Constant.TAG, "Response Login : " + s);
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            int status = jsonObject.getInt(STATUS);
                            String email = jsonObject.getString(EMAIL);
                            String fullname = jsonObject.getString(FULLNAME);
                            if(status == STATUS_SUCCESS) {
                                mListener.onComplete();
                                saveLoginStates(email, fullname);
                            } else if(status == STATUS_FAILED) {
                                mListener.onFailed();
                            }
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.getMessage();
                        mListener.onError();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", mEmail);
                params.put("password", mPassword);
                Log.i(Constant.TAG, "Email : " + mEmail);
                Log.i(Constant.TAG, "Password : " + mPassword);
                return params;
            }
        };
        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VivaApp.getInstance().addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
    }

    public void signUp() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.NEW_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.i(Constant.TAG, "Response Register : " + s);
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            int status = jsonObject.getInt(STATUS);
                            if(status == STATUS_SUCCESS) {
                                mListener.onComplete();
                            } else if(status == STATUS_FAILED) {
                                mListener.onFailed();
                            }
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.getMessage();
                        mListener.onError();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", mEmail);
                params.put("password", mPassword);
                params.put("username", mUsername);
                params.put("address", mAlamat);
                params.put("city", mKota);
                params.put("gender", mGender);
                params.put("birthdate", mBirthDate);
                params.put("phone", mPhone);
                return params;
            }
        };
        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VivaApp.getInstance().addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
    }

    private void saveLoginStates(String emailState, String fullnameState) {
        VivaApp.getInstance().getDefaultEditor().putString(Constant.LOGIN_STATES_EMAIL, emailState);
        VivaApp.getInstance().getDefaultEditor().putString(Constant.LOGIN_STATES_FULLNAME, fullnameState);
        VivaApp.getInstance().getDefaultEditor().putBoolean(Constant.LOGIN_STATES_ISLOGIN, true);
        VivaApp.getInstance().getDefaultEditor().commit();
    }

    public void deleteLoginStates() {
        VivaApp.getInstance().getDefaultEditor().remove(Constant.LOGIN_STATES_EMAIL);
        VivaApp.getInstance().getDefaultEditor().remove(Constant.LOGIN_STATES_FULLNAME);
        VivaApp.getInstance().getDefaultEditor().putBoolean(Constant.LOGIN_STATES_ISLOGIN, false);
        VivaApp.getInstance().getDefaultEditor().commit();
    }

}
