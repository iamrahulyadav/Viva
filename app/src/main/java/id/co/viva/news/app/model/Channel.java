package id.co.viva.news.app.model;

/**
 * Created by reza on 03/07/15.
 */
public class Channel {

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

    public String getId() {
        return id;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public String getLevel() {
        return level;
    }

    public String getTitle() {
        return title;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getKanal() {
        return kanal;
    }

    public Channel(String channel_title, String id, String channel_id,
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
