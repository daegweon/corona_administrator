package com.example.corona_administrator.messaging;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PushMessagingService extends FirebaseMessagingService {
    public static String ACTION_MSG_FROM_SERVER = "msg_from_server";

    @Override
    public void onNewToken(@NonNull String token) {
        JSONObject uploadJSON = new JSONObject();

        try {
            uploadJSON.put("code", "TEMP_CODE");
            uploadJSON.put("token", token);
        }catch (JSONException e){
            e.printStackTrace();
        }

        HttpURLConnection conn = null;
        try{
            conn = (HttpURLConnection)new URL("http://143.248.53.196:8000/api/register_manager").openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(500);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (conn != null) {
            try {
                // 서버로 전송
                conn.connect();

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8));
                writer.write(uploadJSON.toString());
                writer.flush();
                writer.close();

                conn.getOutputStream().close();
                // 정상적으로 리턴이 돌아오면
                if (conn.getResponseCode() == 201) {
                    Log.d("SendAsync", "Sending data was successful");
                }
            } catch (Exception e){
                e.printStackTrace();
            } finally{
                conn.disconnect();
            }
        }

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
