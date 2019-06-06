package com.example.webapp;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.webkit.CookieManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class TockenMaster
{
    public void writeToFile(String uuid, String token, String name)
    {
        CookieManager cookieMgr = CookieManager.getInstance();
        String IP = new Kostyl().IP;
        cookieMgr.setCookie("http://" + IP,token);
        boolean res;
        final File path =
                Environment.getExternalStoragePublicDirectory
                        (
                                //Environment.DIRECTORY_PICTURES
                                Environment.DIRECTORY_DCIM + "/msg/"
                        );

        // Make sure the path directory exists.
        if(!path.exists())
        {
            // Make it, if it doesn't exit
            path.mkdirs();
        }

        final File file = new File(path, "config.txt");
        try {
            if (file.exists())
                res = file.delete();
        }
        catch (Exception e)
        {

        }
        try {
            res = file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(uuid + "\n" + token + "\n" + name);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public String readFromFile()
    {

        final File path =
                Environment.getExternalStoragePublicDirectory
                        (
                                //Environment.DIRECTORY_PICTURES
                                Environment.DIRECTORY_DCIM + "/msg/"
                        );
        final File file = new File(path, "config.txt");
        try {
            FileInputStream fin = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
            StringBuilder sb = new StringBuilder();
            String uuid = null, token = null, name = null;
            uuid = reader.readLine();
            token = reader.readLine();
            name = reader.readLine();
            reader.close();
            CookieManager cookieMgr = CookieManager.getInstance();
            String IP = new Kostyl().IP;
            token = cookieMgr.getCookie("http://" + IP);
            return uuid + "\n" + token + "\n" + name;
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return null;
    }

    public void DeleteThoken()
    {
        final File path =
                Environment.getExternalStoragePublicDirectory
                        (
                                //Environment.DIRECTORY_PICTURES
                                Environment.DIRECTORY_DCIM + "/msg/"
                        );
        final File file = new File(path, "config.txt");
        if (file.exists())
        {
            file.delete();
        }
    }
}
