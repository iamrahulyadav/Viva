package id.co.viva.news.app.interfaces;

import com.facebook.Response;
import com.facebook.model.GraphUser;

/**
 * Created by reza on 11/12/14.
 */
public interface OnFacebookInfoListener {
    public void onCompleteGetInfo(GraphUser graphUser, Response response);
    public void onSuccessGetInfo();
}
