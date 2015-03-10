package id.co.viva.news.app.model;

import id.co.viva.news.app.interfaces.Item;

/**
 * Created by reza on 28/10/14.
 */
public class NavigationSectionItem implements Item {

    private String titleSection;

    public NavigationSectionItem(String titleSection) {
        this.titleSection = titleSection;
    }

    public String getTitleSection() {
        return this.titleSection;
    }

    @Override
    public boolean isSection() {
        return true;
    }

}
