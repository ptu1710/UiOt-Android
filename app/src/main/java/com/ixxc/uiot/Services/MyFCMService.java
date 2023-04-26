package com.ixxc.uiot.Services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFCMService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        Log.d("API LOG", "From: " + message.getFrom());

        super.onMessageReceived(message);
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
