package id.co.viva.news.app.model;

import id.co.viva.news.app.interfaces.Item;

/**
 * Created by reza on 28/10/14.
 */
public class NavigationSectionItem implements Item {

    private String title;

    public NavigationSectionItem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean isSection() {
        return true;
    }

    @Override
    public boolean isUserProfile() {
        return false;
    }

}
