package com.example.webapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.util.Base64;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Show_all_files extends Activity {

    private String uuid = null, token;
    private ArrayList<Filees> all_files = new ArrayList<Filees>();
    private RecyclerView mRecyclerView;
    private UnterAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public class Filees
    {
        Bitmap bm;
        String name;
        String uuid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_files);
        Bundle b = getIntent().getExtras();
        String value = null;
        if(b != null) {
            uuid = b.getString("UUID");
            token = b.getString("TOKEN");
        }
        String kostyl = null;
        SendJSON sender = new SendJSON(1000000, 100000);
        try{
            String IP = new Kostyl().IP;
            kostyl = sender.execute(IP + "/files", null, "GET", null, token).get();
        }catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch(ExecutionException e)
        {
            e.printStackTrace();
        }
        String kostyl2[] = kostyl.split("\n");
        JSONObject recievedData1, params_json1;
        JSONArray uuids, names;
        String cur_msg = null;
        try {
            recievedData1 = new JSONObject(kostyl2[0]);
            params_json1 = recievedData1.getJSONObject("params");
            uuids = params_json1.getJSONArray("uuids");
            names = params_json1.getJSONArray("names");
            for (int i = 0; i < uuids.length(); i++) {
                Filees cur = new Filees();
                cur.name = names.get(i).toString();
                cur.uuid = uuids.get(i).toString();
                all_files.add(cur);
            }
        }catch (JSONException e)
        {
            e.printStackTrace();
        }

        for(Filees cur: all_files) {

            Pattern pattern = Pattern.compile("\\[.+?\\.jpg\\]");
            Matcher matcher = pattern.matcher(cur.name);
            SpannableString ss_ = new SpannableString(cur.name);
            if (matcher.find()) {


                sender = new SendJSON(1000000, 100000);
                String result = null;
                try {
                    String IP = new Kostyl().IP;
                    result = sender.execute(IP + "/file", null, "GET", cur.uuid, token).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                if (result == null) {
                    return;
                }
                String result2[] = result.split("\n");
                JSONObject recievedData, params_json;
                cur_msg = null;
                try {
                    recievedData = new JSONObject(result2[0]);
                    params_json = recievedData.getJSONObject("params");
                    cur_msg = params_json.getString("body");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (cur_msg == null) {
                    return;
                }
                byte[] bytes = Base64.decode(cur_msg, Base64.DEFAULT);
               cur.bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
            else
            {
                cur.bm = null;
            }
        }


        mRecyclerView = findViewById(R.id.all_files);
        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new UnterAdapter(all_files);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new UnterAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String uuid = all_files.get(position).uuid;
                SendJSON sender = new SendJSON(1000000, 100000);
                String result = null;
                try{
                    String IP = new Kostyl().IP;
                    result = sender.execute(IP + "/file", null, "GET", uuid, token).get();
                }catch (InterruptedException e)
                {
                    e.printStackTrace();
                }catch(ExecutionException e)
                {
                    e.printStackTrace();
                }
                if (result == null)
                {
                    return;
                }
                String result2[] = result.split("\n");
                JSONObject recievedData, params_json;
                String cur_msg = null;
                try {
                    recievedData = new JSONObject(result2[0]);
                    params_json = recievedData.getJSONObject("params");
                    cur_msg = params_json.getString("body");

                }catch (JSONException e)
                {
                    e.printStackTrace();
                }
                if (cur_msg == null)
                {
                    return;
                }
                String filename = all_files.get(position).name.replaceAll("^\\[|\\]$","");

                save_file("saved_files",filename, cur_msg);
                Context context = getApplicationContext();
                CharSequence text = "Saved file " + filename;
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });

    }
    public void save_file(String albumName, String filename, String body) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), albumName);

        if (!file.mkdirs())
        {
            //Log.e(LOG_TAG, "Directory not created");
        }
        File f = new File(file, filename);
        if (f.exists()) {
            f.delete();
        }
        try {
            f.createNewFile();

            FileOutputStream out = new FileOutputStream(f);
            byte[] bytes = Base64.decode(body, Base64.DEFAULT);
            out.write(bytes);
            out.flush();
            out.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
