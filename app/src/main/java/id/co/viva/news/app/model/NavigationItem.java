package id.co.viva.news.app.model;

public class NavigationItem {

    private String name;
    private int type;
    private String parent;
    private String screen;
    private String hit_url;
    private String asset_url;
    private String color;
    private String index;
    private String layoutType;

    public String getLayoutType() {
        return layoutType;
    }

    public String getIndex() {
        return index;
    }

    public String getParent() {
        return parent;
    }

    public String getColor() {
        return color;
    }

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

    public NavigationItem(String name, int type, String parent, String screen,
                          String hit_url, String asset_url, String color, String index, String layoutType) {
        this.name = name;
        this.type = type;
        this.parent = parent;
        this.screen = screen;
        this.hit_url = hit_url;
        this.asset_url = asset_url;
        this.color = color;
        this.index = index;
        this.layoutType = layoutType;
    }

}
