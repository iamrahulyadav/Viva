package id.co.viva.news.app.model;

import id.co.viva.news.app.interfaces.Item;

/**
 * Created by reza on 20/11/14.
 */
public class NavigationProfileItem implements Item {

    private String imgProfile;
    private String username;
    private String email;

    public NavigationProfileItem(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public NavigationProfileItem(String username, String email, String imgProfile) {
        this.username = username;
        this.email = email;
        this.imgProfile = imgProfile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImgProfile() {
        return imgProfile;
    }

    public void setImgProfile(String imgProfile) {
        this.imgProfile = imgProfile;
    }

    @Override
    public boolean isSection() {
        return false;
    }

    @Override
    public boolean isUserProfile() {
        return true;
    }

}
