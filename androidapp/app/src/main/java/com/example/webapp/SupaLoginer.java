package com.example.webapp;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class SupaLoginer
{
    private String login, password;
    private Context context;

    public SupaLoginer(String login, String password,  Context context)
    {
        this.login = login;
        this.password = password;
        this.context = context;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String TryToLogin()
    {
        String uuid = null, token = null, name = null;
        JSONObject postData = new JSONObject();
        JSONObject params = new JSONObject();
        String result = null;
        try {
            postData.put("id", "1234");
            postData.put("jsonrpc", "2.0");
            postData.put("method", "creat_user");

            params.put("email",this.login);
            params.put("password",this.password);

            postData.put("params", params);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        SendJSON sender = new SendJSON(1000000, 1000000);
        try{
            String IP = new Kostyl().IP;
            result = sender.execute(IP + "/auth", postData.toString(), "POST").get();
        }catch (InterruptedException e)
        {
            e.printStackTrace();
            return null;
        }
        catch(ExecutionException e)
        {
            e.printStackTrace();
            return null;
        }

        if (result.length() > 4)//на случай, если там код, а не тело
        {
            JSONObject recievedData, params_json;
            String kostyl[] = result.split("\n");

            try {
                recievedData = new JSONObject(kostyl[0]);
                params_json = recievedData.getJSONObject("params");
                uuid = params_json.getString("uuid");
                token = kostyl[1];
                name = params_json.getString("name");

            }catch (JSONException e)
            {
                e.printStackTrace();
                return null;
            }
            TockenMaster tockenMaster = new TockenMaster();
            //String prov = tockenMaster.readFromFile(Register.this);
            tockenMaster.writeToFile(uuid, token, name);
        }
        return uuid + "\n" + token + "\n" + name;
    }
}
