package com.linetra.notification2ios;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by vivek on 9/5/16.
 */
public class SendNotificationTask extends AsyncTask {
    private static final String TAG = "SendNotificationTask";
    HttpsURLConnection conn;
    private String notification;
    private String package_name;
    private String token;
    private String userid;

    public SendNotificationTask(String notification, String package_name, String userid, String token) {
        this.notification = notification;
        this.package_name = package_name;
        this.userid = userid;
        this.token = token;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        Log.d("SendNotificationTask", "doInBackground");

        try {

            if (token == null || userid == null) {
                Log.e(TAG, "UserID/Token is not valid");
                return null;
            }

            URL url = new URL("https://api.pushover.net/1/messages.json");
            conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            List<NameValuePair> httpparams = new ArrayList<NameValuePair>();
            httpparams.add(new BasicNameValuePair("token", token));
            httpparams.add(new BasicNameValuePair("user", userid));
            httpparams.add(new BasicNameValuePair("message", notification));
            httpparams.add(new BasicNameValuePair("title", package_name));

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(httpparams));
            writer.flush();
            writer.close();
            os.close();
            conn.connect();

            // Get Response
            InputStream is = conn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            String responseStr = response.toString();
            Log.d("Server response",responseStr);

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        Log.d("SendNotificationTask", "onPreExecute");
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object o) {
        Log.d("SendNotificationTask", "onPostExecute");
        super.onPostExecute(o);
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        Log.d("SendNotificationTask", "onProgressUpdate");
        super.onProgressUpdate(values);
    }

    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
