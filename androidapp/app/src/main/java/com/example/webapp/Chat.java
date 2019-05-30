package com.example.webapp;

import android.app.Activity;
import android.app.Notification;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static com.example.webapp.App.CHANNEL_1_ID;
import static com.example.webapp.App.CHANNEL_2_ID;

public class Chat extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ArrayAdapter<SpannableString> arrayAdapter;
    private String uuid, token, chat_uuid, nickname = "";
    private ArrayList<msg> all_msgs;
    private ArrayList<String> all_files_names;
    private ArrayList<String> all_files_uuids;
    private DrawerLayout drawLayout;
    //private ArrayList<msg> dataList = new ArrayList<msg>();
    private NotificationManagerCompat notificationManager_c, notificationManager_m;
    private ArrayList<String> all_id_notificator_msg = new ArrayList<String>();
    private ArrayList<String> all_id_notificator_chat = new ArrayList<String>();
    private SpannableString ss;
    private ArrayList<ClickableSpan> clickableSpan = new ArrayList<ClickableSpan>();
    private static final int READ_REQUEST_CODE = 42;
    private RecyclerView msg_list;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public class msg
    {
        public String uuid;
        public long time;
        public String text;
        public String creatorUUID;
        public String creatorName;
        public String date;
        public SpannableString span_text;
    }

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
            String uuid_msg = null, text = null, chat_uuid_get = "", chat_name = null, method, body, name, timestamp = "", creator_uuid = "", creator_name = "";
            String[] array = null;
            try {
                recievedData = new JSONObject(message);
                method = recievedData.getString("method");
                params_json = recievedData.getJSONObject("params");
                if (method.equals("file_broadcast_create"))
                {
                    name = params_json.getString("name");
                    body = params_json.getString("body");
                    timestamp = params_json.getString("createdAt");
                    creator_uuid = params_json.getString("creatorUUID");
                    creator_name = params_json.getString("creatorName");
                }
                else {
                    text = params_json.getString("text");
                    uuid_msg = params_json.getString("uuid");
                    chat_uuid_get = params_json.getString("chatUUID");
                    chat_name = params_json.getString("chatName");
                    timestamp = params_json.getString("createdAt");
                    creator_uuid = params_json.getString("creatorUUID");
                    creator_name = params_json.getString("creatorName");
                    //uuid_msg = params_json.getString("uuid");
                }

            }catch (JSONException e)
            {
                e.printStackTrace();
            }

            if (chat_uuid_get.equals(chat_uuid))
            {
                //dataList.add(make_clickable_text(text));
                msg cur_msg = new msg();
                cur_msg.uuid = uuid_msg;
                cur_msg.time = Long.parseLong(timestamp);
                cur_msg.text = text;
                cur_msg.creatorUUID = creator_uuid;
                cur_msg.span_text = make_clickable_text(text);
                cur_msg.creatorName = creator_name;
                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(cur_msg.time);
                cur_msg.date = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();
                all_msgs.add(cur_msg);
                //make_clickable_text(text);
                mAdapter.notifyDataSetChanged();
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
                text = params_json.getString("chatName");
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


        NavigationView navigationView = findViewById(R.id.navigator_view);
        View headerView = navigationView.getHeaderView(0);
        TextView nickname_text = headerView.findViewById(R.id.nickname_in_menu);
        nickname_text.setText(nickname);


        notificationManager_m = NotificationManagerCompat.from(this);
        notificationManager_c = NotificationManagerCompat.from(this);

        this.msg_list = findViewById(R.id.all_msg);

        mLayoutManager = new LinearLayoutManager(this);
        this.all_msgs = new ArrayList<msg>();
        mAdapter = new SupaAdapter(all_msgs);

        this.msg_list.setLayoutManager(mLayoutManager);
        this.msg_list.setAdapter(mAdapter);


        Point size = new Point();
        ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(size);
        LinearLayout.LayoutParams vi_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (size.y * 0.65));
        msg_list.setLayoutParams(vi_params);


        String kostyl = null;
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
            if (this.all_files_names == null)
            {
                this.all_files_names = new ArrayList<String>();
                this.all_files_uuids = new ArrayList<String>();
            }
            e.printStackTrace();
        }

        String [] kostul = new String[all_files_names.size()];
        for (int i = 0;i < all_files_names.size(); i++)
        {
            kostul[i] = all_files_names.get(i);
        }

        MultiAutoCompleteTextView SupaFiller  = findViewById(R.id.chat_input);
        ArrayAdapter<String> Filler_adapter = new  ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, kostul);
        SupaFiller.setAdapter(Filler_adapter);
        SupaFiller.setTokenizer(new SupaTokenizer());


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

            for (int i = 0; i < all_uuids.length(); i++) {
                msg cur = new msg();
                cur.uuid = all_uuids.get(i).toString();
                all_msgs.add(cur);
                //all_msgs.add(all_uuids.get(i).toString());
            }
        }catch (JSONException e)
        {
            e.printStackTrace();

        }

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
                SendJSON sender = new SendJSON(1000000, 100000);
                JSONObject postData = new JSONObject();
                JSONObject params = new JSONObject();
                String result = null;
                MultiAutoCompleteTextView msg = findViewById(R.id.chat_input);
                String text = msg.getText().toString();
                for (String s : all_files_names)
                {

                }
                String supa_kost = null;
                try{
                    supa_kost = new String(msg.getText().toString().getBytes("UTF-8"), "ISO-8859-1");
                }catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
                try {
                    params.put("chatUUID", chat_uuid);

                    params.put("text", supa_kost);
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

    private SpannableString make_clickable_text(String msg)
    {
            Pattern pattern = Pattern.compile("\\[.+\\]");
            Matcher matcher = pattern.matcher(msg);
            SpannableString ss_ = new SpannableString(msg);
            while (matcher.find())
            {
                int first = matcher.start();
                int second = matcher.end();
                //clickableSpan
                ClickableSpan clickableSpan1 = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        TextView tv = (TextView) widget;
                        Spanned s = (Spanned) tv.getText();
                        int start = s.getSpanStart(this);
                        int end = s.getSpanEnd(this);
                        String filename = s.subSequence(start, end).toString();
                        int index = 0;
                        for (String cur: all_files_names)
                        {
                            if (cur.equals(filename))
                            {
                                index = all_files_names.indexOf(filename);
                                break;
                            }
                        }

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
                            result = sender.execute(IP + "/file", null, "GET", all_files_uuids.get(index), token).get();
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
                        filename = filename.replaceAll("^\\[|\\]$","");

                        save_file("saved_files",filename, cur_msg);


                    }
                };
                clickableSpan.add(clickableSpan1);
                ss_.setSpan(clickableSpan1, first, second, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss_.setSpan( new ForegroundColorSpan(Color.BLUE), first, second, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            }
            return ss_;
    }

    private void draw_chats() {
       // RecyclerView  msg_list = findViewById(R.id.all_msg);
        //TODO for each uuid get text msg
        for(msg s : all_msgs)
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
                result = sender.execute(IP + "/message", null, "GET", s.uuid, token).get();
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
            String cur_msg = null, timestamp = null, creator_uuid = null, creator_name = null;
            try {
                recievedData = new JSONObject(result2[0]);
                params_json = recievedData.getJSONObject("params");
                cur_msg = params_json.getString("text");
                timestamp = params_json.getString("createdAt");
                creator_uuid = params_json.getString("creatorUUID");
                creator_name = params_json.getString("creatorName");
            }catch (JSONException e)
            {
                e.printStackTrace();
            }
            if (cur_msg!=null)
            {
                s.time = Long.parseLong(timestamp);
                s.text = cur_msg;
                s.creatorUUID = creator_uuid;
                s.creatorName = creator_name;
                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(s.time);
                s.date = DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString();
                s.span_text = make_clickable_text(s.text);
               // make_clickable_text(cur_msg);
            }

        }
        Collections.sort(all_msgs, new Comparator< msg >() {
            @Override public int compare(msg p1, msg p2) {
                return (int)(p1.time- p2.time);
            }
        });
     /*   for (msg s: all_msgs)
        {
            dataList.add(make_clickable_text(s.text));
        }*/
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

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    private String[] readTextFromUri(Uri uri) throws IOException {

        byte[] bytes = new byte [10485760], ans;
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
        if (l > 10485760)
        {
            Context context = getApplicationContext();
            CharSequence text = "Too large file!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return null;
        }
        ans = new byte[l];
        for (int i = 0; i < l ; i++)
        {
            ans[i] = bytes[i];
        }
        String base64 = Base64.encodeToString(ans, Base64.DEFAULT);

        String name = uri2filename(uri);

        String [] answer = {base64, name};
        return answer;

    }

    private String uri2filename(Uri uri) {

        String ret = null;
        String scheme = uri.getScheme();

        if (scheme.equals("file")) {
            ret = uri.getLastPathSegment();
        }
        else if (scheme.equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                ret = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }
        }
        return ret;
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
                    return;
                }
                if (file == null)
                {
                    return ;
                }
                SendJSON sender = new SendJSON(100000, 100000);
                String result;
                JSONObject postData = new JSONObject();
                JSONObject params = new JSONObject();
                try {
                    params.put("name", "[" + file[1] + "]");
                    params.put("body", file[0]);
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
                    result = sender.execute(IP + "/file", postData.toString(), "POST", null, token).get();
                }catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch(ExecutionException e)
                {
                    e.printStackTrace();
                }
                //TODO upload file
                //Log.i(TAG, "Uri: " + uri.toString());
                //showImage(uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
