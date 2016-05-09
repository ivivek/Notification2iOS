package com.linetra.notification2ios;

import android.app.Notification;

import android.content.Intent;
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


    @Override
    public void onCreate() {
        Log.d(TAG, TAG + " Created");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbnNew) {
        final Intent intent = new Intent(ACTION_NEW_NOTIFICATION);
        intent.putExtra(PACKAGE_NAME, sbnNew.getPackageName());
        intent.putExtra(NOTIFICATION_TEXT, sbnNew.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT));
        sendBroadcast(intent);
        Log.d(TAG, "onNotificationPosted: " + sbnNew.getPackageName()
                + ": " + sbnNew.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT));
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.d(TAG, " -------- onNotificationRemoved ----- ");
    }



}
