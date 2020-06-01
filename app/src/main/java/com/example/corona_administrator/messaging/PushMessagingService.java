package com.example.corona_administrator.messaging;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushMessagingService extends FirebaseMessagingService {
    public static String ACTION_MSG_FROM_SERVER = "msg_from_server";

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("PushMessagingService", "onNewToken - " + token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            Log.d("PushMessagingService", "onMessageReceived - " + remoteMessage);
            String messageTitle = remoteMessage.getNotification().getTitle();
            String messageBody = remoteMessage.getNotification().getBody();

            Intent intent = new Intent(ACTION_MSG_FROM_SERVER);
            intent.putExtra("TITLE", messageTitle);
            intent.putExtra("BODY", messageBody);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }
}
