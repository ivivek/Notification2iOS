package com.linetra.notification2ios;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Activity context = this;
    private static Button saveButton;
    private static EditText editText_userid;
    private static EditText editText_token;
    private static String userID;
    private static String token;
    public static final String PREFS_NAME = "N2I_PREFS";
    public static final String PREFS_USERID_KEY = "N2I_USERID";
    public static final String PREFS_TOKEN_KEY = "N2I_TOKEN";


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

        saveButton = (Button) findViewById(R.id.save_button);
        editText_userid = (EditText) findViewById(R.id.editText1);
        editText_token = (EditText) findViewById(R.id.editText2);

        get_prefs(context);
        if (userID != null && !userID.isEmpty()) {
            editText_userid.setText(userID);
            editText_userid.setCursorVisible(false);

        }
        if (token != null && !token.isEmpty()) {
            editText_token.setText(token);
            editText_token.setCursorVisible(false);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText_userid.getText().toString() == null || editText_token.getText().toString() == null ||
                        editText_userid.getText().toString().isEmpty() || editText_token.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Invalid UserID/Token ",
                            Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Invalid UserID / Token");
                }
                else {
                    editText_token.setCursorVisible(false);
                    editText_userid.setCursorVisible(false);
                    userID = editText_userid.getText().toString();
                    token = editText_token.getText().toString();
                    Log.d(TAG, "UserID: " +userID);
                    Log.d(TAG, "Token: " +token);
                    save_prefs(context, userID, token);
                    Toast.makeText(MainActivity.this, "UserID & Token saved",
                            Toast.LENGTH_LONG).show();

                }
            }
        });

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

    }

    public void get_prefs(Context context) {
        SharedPreferences settings;
        String text;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        userID = settings.getString(PREFS_USERID_KEY, null);
        token = settings.getString(PREFS_USERID_KEY, null);
    }

    public void save_prefs(Context context, String userid, String token) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.putString(PREFS_USERID_KEY, userid);
        editor.putString(PREFS_TOKEN_KEY, token);
        editor.commit();
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
                String notification_title = intent.getStringExtra(N2INotificationListener.NOTIFICATION_TITLE);
                String notification_subtext = intent.getStringExtra(N2INotificationListener.NOTIFICATION_SUBTEXT);

                Log.d(TAG, "Package Name: " + package_name);
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

                Log.d(TAG, "Calling Async task");
                SendNotificationTask task = new SendNotificationTask(s.toString(), package_name, userID, token);
                task.execute();

            }
        }
    };
}