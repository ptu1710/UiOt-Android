package com.ixxc.uiot.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ixxc.uiot.R;

import java.util.Map;
import java.util.Objects;

public class MyFCMService extends FirebaseMessagingService {

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        String channelName = "Rule Notification";
        NotificationChannel channel = new NotificationChannel("OR", channelName, NotificationManager.IMPORTANCE_HIGH);
        Uri defaultSoundUri =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        channel.setShowBadge(false);
        channel.setSound(defaultSoundUri, null);
        channel.enableVibration(true);
        NotificationManager service = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        service.createNotificationChannel(channel);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {

        if (!message.getData().isEmpty()) {
            Map<String, String> messageData = message.getData();

            boolean isSilent = !messageData.containsKey("or-title");

            if (!isSilent) {

                String title = messageData.get("or-title");
                String body = messageData.get("or-body");

                long notificationId = Long.parseLong(Objects.requireNonNull(messageData.get("notification-id")));

                handleNotification(notificationId, title, body);
            }
        }
    }

    private void handleNotification(Long notificationId, String title, String body) {

        PackageManager pm = getPackageManager();
        Intent notificationIntent = pm.getLaunchIntentForPackage(getPackageName());
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "OR")
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_vn)
                .setContentIntent(pendingIntent);

        Log.d("API LOG", "Showing notification id=" + notificationId + ", title=" + title + ", body=" + body);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(notificationId.hashCode(), notificationBuilder.build());
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        // sendRegistrationToServer(token);
        super.onNewToken(token);
    }
}
