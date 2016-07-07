package com.linetra.notification2ios;

import android.app.Notification;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.service.notification.StatusBarNotification;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class N2INotificationListener extends android.service.notification.NotificationListenerService {
    private static final String TAG = N2INotificationListener.class.getSimpleName();
    public final static String ACTION_NEW_NOTIFICATION =
            "com.linetra.notification2ios.ACTION_NEW_NOTIFICATION";
    public final static String PACKAGE_NAME =
            "com.linetra.notification2ios.EXTRA_DATA";
    public final static String NOTIFICATION_TEXT =
            "com.linetra.notification2ios.NOTIFICATION_TEXT";
    public final static String NOTIFICATION_TITLE =
            "com.linetra.notification2ios.NOTIFICATION_TITLE";
    public final static String NOTIFICATION_SUBTEXT =
            "com.linetra.notification2ios.NOTIFICATION_SUBTEXT";
    String appName;

    private static final String PUSHOVER_REQUEST_URL = "https://api.pushover.net/1/messages.json";

    private static String userID;
    private static String token;
    public static int notificationID = 10001;

    public static final String PREFS_NAME = "N2I_PREFS";
    public static final String PREFS_USERID_KEY = "N2I_USERID";
    public static final String PREFS_TOKEN_KEY = "N2I_TOKEN";


    @Override
    public void onCreate() {
        Log.d(TAG, TAG + " Created");
        send_notification("Sending all notifications to Pushover");
        SharedPreferences settings;
        settings = getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        userID = settings.getString(PREFS_USERID_KEY, null);
        token = settings.getString(PREFS_TOKEN_KEY, null);

    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbnNew) {
        final Intent intent = new Intent(ACTION_NEW_NOTIFICATION);

        intent.putExtra(NOTIFICATION_TITLE, sbnNew.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE));
        intent.putExtra(NOTIFICATION_TEXT, sbnNew.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT));
        intent.putExtra(NOTIFICATION_SUBTEXT, sbnNew.getNotification().extras.getCharSequence(Notification.EXTRA_SUB_TEXT));

        PackageManager packageManager= getApplicationContext().getPackageManager();
        try {
            appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(sbnNew.getPackageName(), PackageManager.GET_META_DATA));
        } catch (final PackageManager.NameNotFoundException e) {
            appName = null;
        }

        if (appName != null) {
            intent.putExtra(PACKAGE_NAME, appName);
        } else {
            intent.putExtra(PACKAGE_NAME, sbnNew.getPackageName());
        }

        if (is_app_allowed(appName)) {
            Log.d(TAG, "onNotificationPosted: Application Name: " + appName);
            Log.d(TAG, "onNotificationPosted: Title: " + sbnNew.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE));
            Log.d(TAG, "onNotificationPosted: " + sbnNew.getPackageName()
                    + ": " + sbnNew.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT));
            Log.d(TAG, "onNotificationPosted:  Sub text: " + sbnNew.getNotification().extras.getCharSequence(Notification.EXTRA_SUB_TEXT));


            CharSequence notification_text = sbnNew.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT).toString();
            CharSequence notification_title = sbnNew.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE).toString();
            CharSequence notification_subtext = sbnNew.getNotification().extras.getCharSequence(Notification.EXTRA_SUB_TEXT);

            Log.d(TAG, "Notification: " + notification_text);

            StringBuilder s = new StringBuilder();
            if (notification_title != null) {
                s.append(notification_title);
            }
            if (notification_text != null) {
                s.append(" - ");
                s.append(notification_text);
            }
            if (notification_subtext != null) {
                s.append(" - ");
                s.append(notification_subtext);
            }
            final String notification_full = s.toString();
            send_notification_to_server(notification_full);

        }

    }

    void send_notification_to_server(final String notification) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PUSHOVER_REQUEST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "HTTP POST Response: " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, "HTTP POST Error: " + error.toString());
                        send_notification("Error sending notifications");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", token);
                params.put("user", userID);
                params.put("message", notification);
                params.put("title", appName);

                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                Log.d(TAG, "Response code: " + response.statusCode);
                if (response.statusCode == HttpStatus.SC_OK) {
                    send_notification("Sending all notifications to Pushover");
                    Log.i(TAG, "Sending all notifications to Pushover");
                }
                return super.parseNetworkResponse(response);
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        Log.d(TAG, "Sending to pushover");
        requestQueue.add(stringRequest);
    }
    boolean is_app_allowed(String app_name) {
        if (app_name.equals("Notification2iOS"))
            return false;
        else
            return true;
    }

    private void send_notification(String notification) {
        Notification notif = new NotificationCompat.Builder(this)
                .setContentTitle("Notification2iOS")
                .setContentText(notification)
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
        notif.flags |= Notification.FLAG_NO_CLEAR;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationID, notif);

    }


    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.d(TAG,"onNotificationRemoved");
    }
}