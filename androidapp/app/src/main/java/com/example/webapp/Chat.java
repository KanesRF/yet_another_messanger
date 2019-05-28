package com.example.webapp;

import android.app.Activity;
import android.app.Notification;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.webapp.App.CHANNEL_1_ID;
import static com.example.webapp.App.CHANNEL_2_ID;

public class Chat extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    /*
        private static WebsocketServer INSTANCE = new WebsocketServer();
    public static WebsocketServer getIstance() {
        return INSTANCE;
    }
     */
    private ArrayAdapter<String> arrayAdapter;
    private String uuid, token, chat_uuid, nickname = "";
    private ArrayList<String> all_msgs;

    private ArrayList<String> all_files_names;
    private ArrayList<String> all_files_uuids;

    private DrawerLayout drawLayout;
    private List<String> dataList = new ArrayList<String>();
    private NotificationManagerCompat notificationManager_c, notificationManager_m;
    private ArrayList<String> all_id_notificator_msg = new ArrayList<String>();
    private ArrayList<String> all_id_notificator_chat = new ArrayList<String>();
    private static final int READ_REQUEST_CODE = 42;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = null;
            message = intent.getStringExtra("msg");
            if (message == null || message.equals(""))
            {
                return;
            }
            JSONObject recievedData, params_json;
            String uuid_msg = null, text = null, chat_uuid_get = "", chat_name = null, method, body, name;
            String[] array = null;
            try {
                recievedData = new JSONObject(message);
                method = recievedData.getString("method");
                params_json = recievedData.getJSONObject("params");
                if (method.equals("file_broadcast_create"))
                {
                    name = params_json.getString("name");
                    body = params_json.getString("body");
                }
                else {
                    text = params_json.getString("text");
                    uuid_msg = params_json.getString("uuid");
                    chat_uuid_get = params_json.getString("chatUUID");
                    chat_name = params_json.getString("chatName");
                    //uuid_msg = params_json.getString("uuid");
                }



            }catch (JSONException e)
            {
                e.printStackTrace();
            }

            if (chat_uuid_get.equals(chat_uuid))
            {
                dataList.add(text);
                all_msgs.add(uuid_msg);
                arrayAdapter.notifyDataSetChanged();
            }
            else
            {
                for (String s : all_id_notificator_msg)
                {
                    if (s.equals(chat_name))
                    {
                        return;
                    }
                }
                all_id_notificator_msg.add(chat_name);
                sendOnChannel1("New message in " + chat_name, (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE));
            }
        }

    };

    // THIS IS FOR CHAT NOTIFICATION
    private BroadcastReceiver cMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = null;
            message = intent.getStringExtra("chat");
            if (message == null || message.equals(""))
            {
                return;
            }
            JSONObject recievedData, params_json;
            String uuid_msg, text, chat_uuid, chat_name;
            String[] array = null;
            try {
                recievedData = new JSONObject(message);
                params_json = recievedData.getJSONObject("params");
                text = params_json.getString("name");
                chat_uuid = params_json.getString("uuid");

            }catch (JSONException e)
            {
                e.printStackTrace();
                return;
            }
            for (String s : all_id_notificator_chat)
            {
                if (s.equals(text))
                {
                    return;
                }
            }
            all_id_notificator_chat.add(text);
            sendOnChanne22("You've been added to chat " + text, (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE));

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Bundle extras = getIntent().getExtras();
        uuid = extras.getString("UUID");
        token = extras.getString("TOKEN");
        chat_uuid = extras.getString("CHAT");
        nickname = extras.getString("LOGIN");
        SendJSON sender = new SendJSON(1000000, 100000);
        String result = null;

        notificationManager_m = NotificationManagerCompat.from(this);
        notificationManager_c = NotificationManagerCompat.from(this);
        //

        /*EditText edi = findViewById(R.id.chat_input);
        Point size = new Point();
        ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(size);
        LinearLayout.LayoutParams vi_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (size.y * 0.2));
        edi.setLayoutParams(vi_params);*/

        ListView msg_list = findViewById(R.id.all_msg);

        Point size = new Point();
        size = new Point();
        ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(size);
        LinearLayout.LayoutParams vi_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (size.y * 0.65));
        msg_list.setLayoutParams(vi_params);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, dataList);
        msg_list.setAdapter(arrayAdapter);



        String [] filenames;//{"[file 1]", "[garden]", "[meme1]", "[report lab 5]", "[file 33]"};
        String kostyl = null;
        //TODO get MY files
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

            this.all_files_names = new ArrayList<String>();
            this.all_files_uuids = new ArrayList<String>();
            for (int i = 0; i < uuids.length(); i++) {
                all_files_uuids.add(uuids.get(i).toString());
                all_files_names.add(names.get(i).toString());
            }

        }catch (JSONException e)
        {
            e.printStackTrace();
        }



        //uuids[], names[]
        //TODO get CHAT files

        String [] kostul = new String[all_files_names.size()];
        for (int i = 0;i < all_files_names.size(); i++)
        {
            kostul[i] = all_files_names.get(i);
        }

        AutoCompleteTextView SupaFiller  = findViewById(R.id.chat_input);
        ArrayAdapter<String> Filler_adapter = new  ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, kostul);
       // SupaFiller.setThreshold(1);
        SupaFiller.setAdapter(Filler_adapter);



        try{
            String IP = new Kostyl().IP;
            sender = new SendJSON(1000000, 100000);
            result = sender.execute(IP + "/chat", null, "GET", chat_uuid, token).get();
        }catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch(ExecutionException e)
        {
            e.printStackTrace();
        }

        //TODO name on top of list and check is result is only error code

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationView navigationView = findViewById(R.id.navigator_view);
        drawLayout = findViewById(R.id.draw_layout);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawLayout, toolbar,
                R.string.navigation_open, R.string.navigation_close);
        drawLayout.addDrawerListener(toggle);
        toggle.syncState();
//
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                mMessageReceiver, new IntentFilter("SUPA"));

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                cMessageReceiver, new IntentFilter("UBER"));

        String result2[] = result.split("\n"), name;
        JSONObject recievedData, params_json;
        JSONArray all_uuids;
        try {
            recievedData = new JSONObject(result2[0]);
            params_json = recievedData.getJSONObject("params");
            all_uuids = params_json.getJSONArray("messagesUUIDs");
            name = params_json.getString("name");
            this.all_msgs = new ArrayList<String>();
            for (int i = 0; i < all_uuids.length(); i++) {
                all_msgs.add(all_uuids.get(i).toString());
            }
        }catch (JSONException e)
        {
            e.printStackTrace();

        }
        //TODO error handle
        if(result.length() > 4)
        {
            draw_chats();
        }
        else
        {
            Intent intent1 = new Intent(this, MainMenu.class);
            intent1.putExtra("LOGIN", nickname);
            intent1.putExtra("UUID", uuid);
            intent1.putExtra("TOKEN", token);
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                    mMessageReceiver);
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                    cMessageReceiver);
            startActivity(intent1);
            finish();
            return;
        }
        Button send_button = findViewById(R.id.send);
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO send shit
                SendJSON sender = new SendJSON(1000000, 100000);
                JSONObject postData = new JSONObject();
                JSONObject params = new JSONObject();
                String result = null;

                //TODO delete filenames from message and send them separatly
                AutoCompleteTextView msg = findViewById(R.id.chat_input);
                String text = msg.getText().toString();
                for (String s : all_files_names)
                {
                    
                }
                try {
                    params.put("chatUUID", chat_uuid);
                    params.put("text", msg.getText().toString());
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
                    result = sender.execute(IP + "/message", postData.toString(), "POST", null, token).get();
                }catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch(ExecutionException e)
                {
                    e.printStackTrace();
                }

            }
        });
    }

    public void sendOnChannel1(String message, Integer id) {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_textsms_black_24dp)
                .setContentTitle("")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager_m.notify(id, notification);
    }

    public void sendOnChanne22(String message, Integer id) {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_forum_black_24dp)
                .setContentTitle("")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager_c.notify(id, notification);
    }


    private void make_clickable_text()
    {
        ListView msg_list = findViewById(R.id.all_msg);

        String mydata = "some string with 'the data i want' inside";
        Pattern pattern = Pattern.compile("/[*/]");
        Matcher matcher = pattern.matcher(mydata);
        if (matcher.find())
        {

        }
    }

    private void draw_chats() {
        ListView msg_list = findViewById(R.id.all_msg);
        //TODO for each uuid get text msg
        for(String s : all_msgs)
        {
            SendJSON sender = new SendJSON(1000000, 100000);
            JSONObject postData = new JSONObject();
            JSONObject params = new JSONObject();
            String result = null;
            try {
                params.put("uuid", s);
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
                result = sender.execute(IP + "/message", null, "GET", s, token).get();
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
            String cur_msg = null;
            try {
                recievedData = new JSONObject(result2[0]);
                params_json = recievedData.getJSONObject("params");
                cur_msg = params_json.getString("text");

            }catch (JSONException e)
            {
                e.printStackTrace();
            }
            if (cur_msg!=null)
            {
                dataList.add(cur_msg);
            }

        }

    }

    @Override
    public void onCreateNavigateUpTaskStack(TaskStackBuilder builder) {
        super.onCreateNavigateUpTaskStack(builder);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            /*TODO /chats */
            case R.id.chats_all:
                Intent intent1 = new Intent(this, MainMenu.class);
                intent1.putExtra("LOGIN", nickname);
                intent1.putExtra("UUID", uuid);
                intent1.putExtra("TOKEN", token);
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                        mMessageReceiver);
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                        cMessageReceiver);
                startActivity(intent1);
                finish();
                break;

            case R.id.friends:
                Intent intent = new Intent(this, Friendlist.class);
                intent.putExtra("LOGIN", nickname);
                intent.putExtra("UUID", uuid);
                intent.putExtra("TOKEN", token);
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                        mMessageReceiver);
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                        cMessageReceiver);
                startActivity(intent);
                finish();
                break;
            case R.id.logout:
                SendJSON sender = new SendJSON(1000000, 100000);
                String result;
                try{
                    String IP = new Kostyl().IP;
                    result = sender.execute(IP + "/auth", null, "DELETE", null, token).get();
                }catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch(ExecutionException e)
                {
                    e.printStackTrace();
                }
                Intent intent2 = new Intent(this, MainActivity.class);
                TockenMaster tockenMaster = new TockenMaster();
                tockenMaster.DeleteThoken();
                startActivity(intent2);
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                        mMessageReceiver);
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                        cMessageReceiver);
                finish();
                break;
            case R.id.upload:
                performFileSearch();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawLayout.isDrawerOpen(GravityCompat.START))
        {
            drawLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
        super.onBackPressed();
    }

    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("*/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    private String[] readTextFromUri(Uri uri) throws IOException {

        byte[] bytes = new byte[10485760], ans;
        int l = 0;
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

            FileInputStream is = new FileInputStream(fileDescriptor);
            l = is.read(bytes);
            is.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (l > 10485760) {
            Context context = getApplicationContext();
            CharSequence text = "Too large file!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return null;
        }
        ans = new byte[l];
        for (int i = 0; i < l; i++) {
            ans[i] = bytes[i];
        }
        String base64 = Base64.encodeToString(ans, Base64.DEFAULT);
        String[] paths = uri.toString().split("/");

        String[] answer = {base64, paths[paths.length - 1]};
        return answer;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                String [] file = null;
                try {
                    file = readTextFromUri(uri);
                }catch (IOException e)
                {

                }
                //TODO upload file
                //Log.i(TAG, "Uri: " + uri.toString());
                //showImage(uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
