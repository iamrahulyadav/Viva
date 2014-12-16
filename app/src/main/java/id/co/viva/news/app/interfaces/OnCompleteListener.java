package id.co.viva.news.app.interfaces;

/**
 * Created by reza on 28/11/14.
 */
public interface OnCompleteListener {
    public void onComplete(String message);
    public void onFailed(String message);
    public void onError(String message);
    public void onDelay(String message);
}
