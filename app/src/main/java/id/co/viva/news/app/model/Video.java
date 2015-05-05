package id.co.viva.news.app.model;

/**
 * Created by reza on 12/01/15.
 */
public class Video {

    private String https;
    private String width;
    private String height;

    public String getHeight() {
        return height;
    }

    public Video(String https, String width, String height) {
        this.https = https;
        this.width = width;
        this.height = height;
    }

}
