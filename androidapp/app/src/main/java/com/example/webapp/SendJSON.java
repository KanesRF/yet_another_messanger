package com.example.webapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;


import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import com.sangupta.murmur.Murmur2;
import com.sangupta.murmur.Murmur3;

public class SendJSON extends AsyncTask<String, Void, String> {

    private int readTimeOut, connectTimeOut;
    private Context context;
    public SendJSON(int ReadTimeOut, int ConnectTimeOut, Context c)
    {
        this.readTimeOut = ReadTimeOut;
        this.connectTimeOut = ConnectTimeOut;
        this.context = c;
    }

    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected String doInBackground(String... params) {


        String data = "";
        String tocken = "";
        int response_code = 0;

        HttpURLConnection httpURLConnection = null;
        try {

            httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();
                httpURLConnection.setRequestMethod(params[2]);
            httpURLConnection.setReadTimeout(this.readTimeOut);
            httpURLConnection.setConnectTimeout(this.connectTimeOut);

           /* JSONObject fingering = new JSONObject();
            try {
                fingering.put("region", Locale.getDefault().getCountry());
                fingering.put("lang", Locale.getDefault().getLanguage());
                fingering.put("os", Build.VERSION.INCREMENTAL);

                int screen_height=0, screen_width=0;
                WindowManager wm;
                DisplayMetrics displaymetrics;
                wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                displaymetrics = new DisplayMetrics();
                wm.getDefaultDisplay().getMetrics(displaymetrics);
                screen_height = displaymetrics.heightPixels;
                screen_width = displaymetrics.widthPixels;
                fingering.put("screen_height",Integer.toString(screen_height));
                fingering.put("screen_width",Integer.toString(screen_width));
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }*/
            int screen_height=0, screen_width=0;
            WindowManager wm;
            DisplayMetrics displaymetrics;
            wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            displaymetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(displaymetrics);
            screen_height = displaymetrics.heightPixels;
            screen_width = displaymetrics.widthPixels;

            //int[] firstHash = lsh.hash(vector1);
            Murmur2 a = new Murmur2();
            byte [] all = (Locale.getDefault().getCountry() + Locale.getDefault().getLanguage() + Build.VERSION.INCREMENTAL + Integer.toString(screen_height) + Integer.toString(screen_width)).getBytes();
            String finger = Long.toString(Murmur2.hash64(all, all.length,0));
            httpURLConnection.setRequestProperty("fingerprint", finger);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            if(params.length > 3)
            {
                if (params[3] != null) {
                    httpURLConnection.setRequestProperty("uuid", params[3]);
                }
                if (params[4]!=null) {
                    httpURLConnection.setRequestProperty("Cookie", params[4]);
                }
            }
            if(params[1] != null)
            {
                httpURLConnection.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                //httpURLConnection.setRequestMethod(params[2]);
                wr.writeBytes( params[1]);
                wr.flush();
                wr.close();
            }
            else
            {
                httpURLConnection.setDoOutput(false);
            }

            response_code = httpURLConnection.getResponseCode();
            InputStream in = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in);

            //int inputStreamData = inputStreamReader.read();
            /*while (inputStreamData != -1) {
                char current = (char) inputStreamData;
                inputStreamData = inputStreamReader.read();
                data += current;
            }*/
            data = IOUtils.toString(inputStreamReader);
            tocken = httpURLConnection.getHeaderField("Set-Cookie");


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        if (response_code != 200)
        {
            String aaa =  Integer.toString(response_code);
            return aaa;
        }
        return data + "\n" +tocken + "\n" + response_code;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.e("TAG", result);
        //return result;
    }

}
