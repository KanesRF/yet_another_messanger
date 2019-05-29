package com.example.webapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



public class SendJSON extends AsyncTask<String, Void, String> {

    private int readTimeOut, connectTimeOut;
    public SendJSON(int ReadTimeOut, int ConnectTimeOut)
    {
        this.readTimeOut = ReadTimeOut;
        this.connectTimeOut = ConnectTimeOut;
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
            if(params.length > 3)
            {
                if (params[3] != null) {
                    httpURLConnection.setRequestProperty("uuid", params[3]);
                }
                httpURLConnection.setRequestProperty("token", params[4]);
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
            tocken = httpURLConnection.getHeaderField("token");
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
