package com.example.webapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class FaceGetter {
    private String uuid, token;
    public FaceGetter(String uuid, String tocken)
    {
        this.uuid = uuid;
        this.token = tocken;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    public Bitmap get_avu()
    {
        SendJSON sender = new SendJSON(1000000, 100000);
        String result = null;
        try{
            String IP = new Kostyl().IP;
            result = sender.execute(IP + "/user", null, "GET", uuid, token).get();
        }catch (InterruptedException e)
        {
            e.printStackTrace();
        }catch(ExecutionException e)
        {
            e.printStackTrace();
        }
        if (result == null)
        {
            return null;
        }
        String result2[] = result.split("\n");
        JSONObject recievedData, params_json;
        String cur_msg = null;
        try {
            recievedData = new JSONObject(result2[0]);
            params_json = recievedData.getJSONObject("params");
            cur_msg = params_json.getString("avatarUUID");

        }catch (JSONException e)
        {
            e.printStackTrace();
        }
        if (cur_msg == null || cur_msg == "")
        {
            return null;
        }

        sender = new SendJSON(1000000, 100000);
        result = null;
        try{
            String IP = new Kostyl().IP;
            result = sender.execute(IP + "/file", null, "GET", cur_msg, token).get();
        }catch (InterruptedException e)
        {
            e.printStackTrace();
        }catch(ExecutionException e)
        {
            e.printStackTrace();
        }
        if (result == null)
        {
            return null;
        }
        String result2_2[] = result.split("\n");
        //JSONObject recievedData, params_json;
        cur_msg = null;
        try {
            recievedData = new JSONObject(result2_2[0]);
            params_json = recievedData.getJSONObject("params");
            cur_msg = params_json.getString("body");

        }catch (JSONException e)
        {
            e.printStackTrace();
        }
        if (cur_msg == null || cur_msg == "")
        {
            return null;
        }
        byte[] bytes = Base64.decode(cur_msg, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bmp;
    }
}
