package id.co.viva.news.app.model;

/**
 * Created by rezarachman on 02/10/14.
 */
public class Headline {

    private String id;
    private String title;
    private String slug;
    private String kanal;
    private String timestamp;
    private String image_url;
    private String date_publish;
    private String source;
    private String url;

    public String getTimestamp() {
        return timestamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKanal() {
        return kanal;
    }

    public void setKanal(String kanal) {
        this.kanal = kanal;
    }

    public String getDate_publish() {
        return date_publish;
    }

    public void setDate_publish(String date_publish) {
        this.date_publish = date_publish;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Headline(String id, String title, String slug,
                String kanal, String image_url, String date_publish,
                String source, String url, String timestamp) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.kanal = kanal;
        this.image_url = image_url;
        this.date_publish = date_publish;
        this.source = source;
        this.url = url;
        this.timestamp = timestamp;
    }

}
