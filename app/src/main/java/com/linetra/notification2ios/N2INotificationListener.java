package com.linetra.notification2ios;

import android.app.Notification;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.service.notification.StatusBarNotification;
import android.util.Log;

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



    @Override
    public void onCreate() {
        Log.d(TAG, TAG + " Created");
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

        // Skipping notification from this app
        if (!appName.equals("Notification2iOS"))
            sendBroadcast(intent);

        Log.d(TAG, "onNotificationPosted: Application Name: " +appName);
        Log.d(TAG, "onNotificationPosted: Title: " +sbnNew.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE));
        Log.d(TAG, "onNotificationPosted: " + sbnNew.getPackageName()
                + ": " + sbnNew.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT));
        Log.d(TAG, "onNotificationPosted:  Sub text: " +sbnNew.getNotification().extras.getCharSequence(Notification.EXTRA_SUB_TEXT));
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.d(TAG,"onNotificationRemoved");
    }
}