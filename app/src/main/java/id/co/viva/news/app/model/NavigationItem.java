package id.co.viva.news.app.model;

public class NavigationItem {

    private String name;
    private int type;
    private String screen;
    private String hit_url;
    private String asset_url;

    public String getScreen() {
        return screen;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public String getHit_url() {
        return hit_url;
    }

    public String getAsset_url() {
        return asset_url;
    }

    public NavigationItem(String name, int type,
                          String screen, String hit_url, String asset_url) {
        this.name = name;
        this.type = type;
        this.screen = screen;
        this.hit_url = hit_url;
        this.asset_url = asset_url;
    }

}
