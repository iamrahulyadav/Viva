package id.co.viva.news.app.model;

/**
 * Created by reza on 10/04/15.
 */
public class Places {

    private String mCity;
    private String mSubLocality;
    private String mSubAdminArea;

    public Places(String mCity, String mSubLocality, String mSubAdminArea) {
        this.mCity = mCity;
        this.mSubLocality = mSubLocality;
        this.mSubAdminArea = mSubAdminArea;
    }

    public String getmCity() {
        return mCity;
    }

    public String getmSubLocality() {
        return mSubLocality;
    }

    public String getmSubAdminArea() {
        return mSubAdminArea;
    }

}
