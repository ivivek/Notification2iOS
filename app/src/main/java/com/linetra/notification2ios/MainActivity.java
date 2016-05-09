package com.linetra.notification2ios;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PackageManager pm = getPackageManager();
        ComponentName notiComponentName = new ComponentName(this, N2INotificationListener.class);

        if (pm.getComponentEnabledSetting(notiComponentName)
                != PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
            Log.i(TAG, "Enabling Component : " + notiComponentName.toString());
            pm.setComponentEnabledSetting(
                    notiComponentName,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }

        registerReceiver(N2IReceiver, makeIntentFilter());
    }

    private static IntentFilter makeIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(N2INotificationListener.ACTION_NEW_NOTIFICATION);
        return intentFilter;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private final BroadcastReceiver N2IReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "Entered BroadcastReceiver");
            if (N2INotificationListener.ACTION_NEW_NOTIFICATION.equals(action)) {
                Log.d(TAG, "ACTION_NEW_NOTIFICATION");
                String package_name = intent.getStringExtra(N2INotificationListener.PACKAGE_NAME);
                String notification_text = intent.getStringExtra(N2INotificationListener.NOTIFICATION_TEXT);
                Log.d(TAG, "Package Name: " +package_name);
                Log.d(TAG, "Notification: " +notification_text);

                StringBuilder s = new StringBuilder();
                s.append(package_name);
                s.append(": ");
                s.append(notification_text);

                Log.d(TAG, "Calling Async task");
                SendNotificationTask task = new SendNotificationTask(s.toString());
                task.execute();

            }
        }
    };
}