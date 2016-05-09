package com.linetra.notification2ios;

import android.app.Notification;

import android.service.notification.StatusBarNotification;
import android.util.Log;

public class N2INotificationListener extends android.service.notification.NotificationListenerService {
    private static final String TAG = N2INotificationListener.class.getSimpleName();

    @Override
    public void onCreate() {
        Log.d(TAG, TAG + " Created");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbnNew) {
        Log.d(TAG, " -------- onNotificationPosted ----- from : " + sbnNew.getPackageName()
                + " \n" + sbnNew.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT));

    }

    private void cancelNotificationCompat(final StatusBarNotification sbn) {
        Log.d(TAG, " -------- cancelNotificationCompat ----- ");
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.d(TAG, " -------- onNotificationRemoved ----- ");
    }



}
