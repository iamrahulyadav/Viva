package id.co.viva.news.app.model;

/**
 * Created by reza on 22/10/14.
 */
public class FeaturedBola {

    private String channel_title;
    private String id;
    private String channel_id;
    private String level;
    private String title;
    private String image_url;
    private String kanal;

    public String getChannel_title() {
        return channel_title;
    }

    public void setChannel_title(String channel_title) {
        this.channel_title = channel_title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKanal() {
        return kanal;
    }

    public void setKanal(String kanal) {
        this.kanal = kanal;
    }

    public FeaturedBola(String channel_title, String id, String channel_id,
                        String level, String title, String kanal, String image_url) {
        this.id = id;
        this.image_url = image_url;
        this.kanal = kanal;
        this.channel_id = channel_id;
        this.level = level;
        this.channel_title = channel_title;
        this.title = title;
    }

}
