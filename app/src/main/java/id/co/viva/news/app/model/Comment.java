package id.co.viva.news.app.model;

/**
 * Created by reza on 03/12/14.
 */
public class Comment {

    private String article_id;
    private String email;
    private String username;
    private String comment_text;
    private String app_id;

    public String getArticle_id() {
        return article_id;
    }

    public void setArticle_id(String article_id) {
        this.article_id = article_id;
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

    public String getComment_text() {
        return comment_text;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public Comment(String article_id, String email, String username,
                   String comment_text, String app_id) {
        this.article_id = article_id;
        this.email = email;
        this.username = username;
        this.comment_text = comment_text;
        this.app_id = app_id;
    }

}
