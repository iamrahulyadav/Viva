package id.co.viva.news.app.notification;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.R;
import id.co.viva.news.app.activity.ActNotification;

/**
 * Created by reza on 19/01/15.
 */
public class GcmIntentService extends IntentService {

    private NotificationManager mNotificationManager;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        if(!bundle.isEmpty()) {
            if(GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.e(Constant.TAG, "Send error: " + bundle.toString());
            } else if(GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.e(Constant.TAG, "Deleted messages on server: " + bundle.toString());
            } else if(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Log.i(Constant.TAG, "Received: " + bundle.toString());
                getContent(bundle);
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String id, String title, String kanal, int notification_id, String type) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent;
        if(type.equals("browse")) {
            Uri uri = Uri.parse(getResources().getString(R.string.url_google_play) + getPackageName());
            intent = new Intent(Intent.ACTION_VIEW, uri);
        } else {
            intent = new Intent(this, ActNotification.class);
            intent.putExtra("id", id);
            intent.putExtra("kanal", kanal);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        int requestID = (int) System.currentTimeMillis();
        PendingIntent contentIntent = PendingIntent.getActivity(this, requestID,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        if(kanal.equals("vivalife")) {
            builder.setSmallIcon(R.drawable.icon_viva_life);
        } else if(kanal.equals("bola")) {
            builder.setSmallIcon(R.drawable.icon_viva_bola);
        } else {
            builder.setSmallIcon(R.drawable.icon_viva_news);
        }
        builder.setContentTitle(title)
                .setAutoCancel(true)
                .setTicker(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                .setSound(sound)
                .setContentIntent(contentIntent);
        mNotificationManager.notify(notification_id, builder.build());
    }

    private void getContent(Bundle extras) {
        String id = extras.containsKey("id") ? extras.getString("id") : "";
        String title = extras.containsKey("title") ? extras.getString("title") : "";
        String kanal = extras.containsKey("cat") ? extras.getString("cat") : "";
        String type = extras.containsKey("act") ? extras.getString("act") : "";
        String nid = extras.containsKey("nid") ? extras.getString("nid") : "0";
        int notification_id = Integer.parseInt(nid);
        if(notification_id != 0) {
            sendNotification(id, title, kanal, notification_id, type);
        }
    }

}
