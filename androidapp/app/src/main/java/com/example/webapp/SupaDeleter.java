package com.example.webapp;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class SupaDeleter {

    private String tocken;
    private Context context;

    public SupaDeleter(String tocken, Context context)
    {
        this.tocken = tocken;
        this.context = context;
    }

    public boolean delete_by_uuid(String uuid, String path)
    {
        String result = null;
        SendJSON sender = new SendJSON(1000000, 1000000, context);
        try{
            String IP = new Kostyl().IP;
            result = sender.execute(IP + "/" + path, null, "DELETE",uuid, tocken).get();
        }catch (InterruptedException e)
        {
            e.printStackTrace();
            return false;
        }
        catch(ExecutionException e)
        {
            e.printStackTrace();
            return false;
        }
        String [] kostul = result.split("\n");
        if (kostul[2].equals("200"))
        {
            return true;
        }
        return false;
    }
}
