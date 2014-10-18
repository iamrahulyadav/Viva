package id.co.viva.news.app.model;

/**
 * Created by rezarachman on 01/10/14.
 */
public class News {

    private String id;
    private String title;
    private String slug;
    private String kanal;
    private String url;
    private String image_url;
    private String date_publish;

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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
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

    public News(String id, String title, String slug,
                String kanal, String url, String image_url, String date_publish) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.kanal = kanal;
        this.url = url;
        this.image_url = image_url;
        this.date_publish = date_publish;
    }

}
