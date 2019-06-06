package com.example.webapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AddChat extends Activity {

    //TODO add notificator

    List<String> all_friends = null;
    String tocken, uuid, chat_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chat);
        //String chat_name = "";
        final ListView friend_list = findViewById(R.id.Chats);
        Point size = new Point();
        ((WindowManager)this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(size);
        LinearLayout.LayoutParams vi_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int)(size.y*0.55));
        friend_list.setLayoutParams(vi_params);

        Bundle extras = getIntent().getExtras();
        tocken = extras.getString("TOKEN");
        uuid = extras.getString("UUID");

        SendJSON sender = new SendJSON(1000000, 1000000, this);
        JSONObject postData = new JSONObject();
        JSONObject params = new JSONObject();
        String result = "";
        try {
            postData.put("id", "1234");
            postData.put("jsonrpc", "2.0");
            postData.put("method", "creat_user");
            postData.put("params", params);

        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        try{
            String IP = new Kostyl().IP;
            result = sender.execute(IP + "/contacts", null, "GET", null, tocken).get();
        }catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch(ExecutionException e)
        {
            e.printStackTrace();
        }
        String result2[] = result.split("\n");
        JSONObject recievedData, params_json;
        JSONArray all_uuids, all_names;
        String[] array = null;
        ArrayList<String> names = new ArrayList<String>();
        try {
            recievedData = new JSONObject(result2[0]);
            params_json = recievedData.getJSONObject("params");
            all_uuids = params_json.getJSONArray("uuids");
            all_names = params_json.getJSONArray("names");
            array = new String[all_uuids.length()];
            for (int i = 0; i < all_uuids.length(); i++) {
                array[i] = all_names.get(i) + "\n" + all_uuids.get(i);
                names.add((String)all_names.get(i));
            }

        }catch (JSONException e)
        {
            e.printStackTrace();
        }

        //TODO Parse friendlist from this answer, but for now this plug
       /* String [] all_friends = {"Vasya\nuuid1", "Petya\nuuid2"};
        List<String> dataList = new ArrayList<String>();
        for (String s : all_friends) {
            String[] cur = s.split("\n");
            dataList.add(cur[0]);
        }*/

        if(array != null) {
            this.all_friends = new ArrayList<String>(Arrays.asList(array));
        }
        else
        {
            this.all_friends = new ArrayList<String>();
        }



        friend_list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, names);
        friend_list.setAdapter(arrayAdapter);


        Button Cancel_button = findViewById(R.id.Cancel);
        Button Ok_button = findViewById(R.id.OK);

        Ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = "";
                ArrayList<String> Chosen = new ArrayList<String>();
                try{
                    SendJSON sender = new SendJSON(100000, 100000, AddChat.this);
                    //result = sender.execute("http://192.168.43.15:9090/chats", null, "GET", uuid, token).get();
                    // test
                    JSONObject postData = new JSONObject();
                    JSONObject params = new JSONObject();
                    JSONArray array;
                    EditText name = findViewById(R.id.chat_name);
                    chat_name = name.getText().toString();
                    if (chat_name.equals(""))
                    {
                       // Intent intent = new Intent();
                       // intent.putExtra("name", "");
                       // setResult(RESULT_OK, intent);
                        finish();
                    }
                    try {
                        postData.put("id", "1234");
                        postData.put("jsonrpc", "2.0");
                        postData.put("method", "creat_user");

                        final SparseBooleanArray sbArray = friend_list.getCheckedItemPositions();

                        for (int i = 0; i < sbArray.size(); i++) {
                            int key = sbArray.keyAt(i);
                            if (sbArray.get(key)) {
                                String cur = all_friends.get(i);
                                String cur2[] = cur.split("\n");
                                Chosen.add(cur2[1]);
                            }
                        }

                        array = new JSONArray(Chosen.toArray());
                        params.put("participantsUUIDs",array);
                        params.put("name",chat_name);

                        postData.put("params", params);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //
                    String IP = new Kostyl().IP + "/chat";
                    result = sender.execute(IP, postData.toString(), "POST", null, tocken).get();
                }catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
        catch(ExecutionException e)
                {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                //intent.putExtra("name", result);
                //TODO remove plug, parse answer
                if (result.length() < 4)
                {
                   // intent.putExtra("name", "");
                  //  setResult(RESULT_OK, intent);
                    finish();
                }
             //   intent.putExtra("name", chat_name + "\n" + result);
              //  setResult(RESULT_OK, intent);
                finish();
            }
        });

        Cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent intent = new Intent();
                //intent.putExtra("name", "");
               // setResult(RESULT_OK, intent);
               // finish();
                finish();
            }
        });
    }

}
