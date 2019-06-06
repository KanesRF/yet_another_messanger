package com.example.webapp;

import android.app.Activity;
import android.app.Notification;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.webapp.App.CHANNEL_1_ID;
import static com.example.webapp.App.CHANNEL_2_ID;
import static com.example.webapp.MainMenu.ADD_CHAT;

public class Friendlist extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawLayout;
    private String nickname, uuid, tocken;
    private ArrayList<String> all_friends;
    static final int ADD_FRIEND = 2;
    private ArrayAdapter<String> arrayAdapter;
    private NotificationManagerCompat notificationManager_c, notificationManager_m;
    private ArrayList<String> all_id_notificator_msg = new ArrayList<String>();
    private ArrayList<String> all_id_notificator_chat = new ArrayList<String>();
    private static final int READ_REQUEST_CODE = 42, UPLOAD_AVA = 24;
    private List<String> dataList = new ArrayList<String>();

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = null;
            message = intent.getStringExtra("msg");
            if (message == null || message.equals("")) {
                return;
            }
            JSONObject recievedData, params_json;
            String uuid_msg = null, text = null, chat_uuid_get = null, chat_name = null;
            String[] array = null;
            try {
                recievedData = new JSONObject(message);
                params_json = recievedData.getJSONObject("params");
                text = params_json.getString("text");
                uuid_msg = params_json.getString("uuid");
                chat_uuid_get = params_json.getString("chatUUID");
                chat_name = params_json.getString("chatName");
                //uuid_msg = params_json.getString("uuid");

            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            for (String s : all_id_notificator_chat) {
                if (s.equals(chat_name)) {
                    return;
                }
            }

            all_id_notificator_msg.add(chat_name);
            sendOnChannel1("New message in " + chat_name, (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE));

        }

    };

    // THIS IS FOR CHAT NOTIFICATION
    private BroadcastReceiver cMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = null;
            message = intent.getStringExtra("chat");
            if (message == null || message.equals("")) {
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

            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            for (String s : all_id_notificator_chat) {
                if (s.equals(text)) {
                    return;
                }
            }
            all_id_notificator_chat.add(text);
            sendOnChanne22("You've been added to chat " + text, (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE));

        }

    };

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendlist);
        Bundle extras = getIntent().getExtras();
        Toolbar toolbar = findViewById(R.id.toolbar);

        drawLayout = findViewById(R.id.draw_layout);
        nickname = extras.getString("LOGIN");
        uuid = extras.getString("UUID");
        tocken = extras.getString("TOKEN");
        ListView friend_list = findViewById(R.id.Chats);
        Point size = new Point();
        ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(size);
        LinearLayout.LayoutParams vi_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (size.y * 0.70));
        friend_list.setLayoutParams(vi_params);

        notificationManager_m = NotificationManagerCompat.from(this);
        notificationManager_c = NotificationManagerCompat.from(this);

        setSupportActionBar(toolbar);
        NavigationView navigationView = findViewById(R.id.navigator_view);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Friendlist.this, AddFriend.class);
                intent.putExtra("LOGIN", nickname);
                intent.putExtra("UUID", uuid);
                intent.putExtra("TOKEN", tocken);
                startActivityForResult(intent, ADD_FRIEND);
            }
        });


        View headerView = navigationView.getHeaderView(0);
        TextView nickname_text = headerView.findViewById(R.id.nickname_in_menu);
        nickname_text.setText(nickname);

        FaceGetter fg = new FaceGetter(uuid, tocken, this);
        Bitmap ava = fg.get_avu();
        ImageView avatar = headerView.findViewById(R.id.face);
        if (ava != null) {
            avatar.setImageBitmap(ava);
        }

        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawLayout, toolbar,
                R.string.navigation_open, R.string.navigation_close);
        drawLayout.addDrawerListener(toggle);
        toggle.syncState();
        //String [] all_chats2 = {"Vasya\nuuid1", "Petya\nuuid2"};

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                mMessageReceiver, new IntentFilter("SUPA"));

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                cMessageReceiver, new IntentFilter("UBER"));

        SendJSON sender = new SendJSON(1000000, 100000, this);
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
        try {
            String IP = new Kostyl().IP;
            result = sender.execute(IP + "/contacts", null, "GET", null, tocken).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        String result2[] = result.split("\n");
        JSONObject recievedData, params_json;
        JSONArray all_uuids, all_names;
        String[] array = null;
        try {
            recievedData = new JSONObject(result2[0]);
            params_json = recievedData.getJSONObject("params");
            all_uuids = params_json.getJSONArray("uuids");
            all_names = params_json.getJSONArray("names");
            array = new String[all_uuids.length()];
            for (int i = 0; i < all_uuids.length(); i++) {
                array[i] = all_names.get(i) + "\n" + all_uuids.get(i);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (array != null) {
            this.all_friends = new ArrayList<String>(Arrays.asList(array));
        } else {
            this.all_friends = new ArrayList<String>();
        }
        if (all_friends != null) {
            draw_friends();
        }
    }

    private void draw_friends() {
        ListView friend_list = findViewById(R.id.Chats);

        for (String s : all_friends) {
            String[] cur = s.split("\n");
            dataList.add(cur[0]);

        }
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        friend_list.setAdapter(arrayAdapter);

        friend_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int index, long l)
            {
                PopupMenu menu = new PopupMenu(Friendlist.this, view);
                menu.getMenuInflater().inflate(R.menu.generic_menu, menu.getMenu());
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        switch (item.getItemId())
                        {
                            case R.id.delete:
                                String [] chosen = all_friends.get(index).split("\n");
                                SupaDeleter del = new SupaDeleter(tocken, Friendlist.this);
                                if(del.delete_by_uuid(chosen[1], "contact"))
                                {
                                    all_friends.remove(index);
                                    dataList.remove(index);
                                    arrayAdapter.notifyDataSetChanged();
                                    return true;
                                }

                            default:
                                return true;
                        }
                    }
                });
                menu.show();
                return true;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        if ((requestCode == READ_REQUEST_CODE || requestCode == UPLOAD_AVA) && resultCode == Activity.RESULT_OK) {
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
                SendJSON sender = new SendJSON(100000, 100000, this);
                String result = "";
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
                    result = sender.execute(IP + "/file", postData.toString(), "POST", null, tocken).get();

                    if ( requestCode == UPLOAD_AVA)
                    {
                        String [] kostyl = result.split("\n");
                        JSONObject recievedData, params_json;
                        String  ava_uuid = null;
                        String[] array = null;
                        try {
                            recievedData = new JSONObject(kostyl[0]);
                            params_json = recievedData.getJSONObject("params");
                            ava_uuid = params_json.getString("uuid");

                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                            return;
                        }

                        postData = new JSONObject();
                        params = new JSONObject();
                        try {
                            params.put("avatarUUID", ava_uuid);
                            postData.put("id", "1234");
                            postData.put("jsonrpc", "2.0");
                            postData.put("method", "creat_user");
                            postData.put("params", params);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }
                        sender = new SendJSON(100000, 100000, this);
                        result = sender.execute(IP + "/user", postData.toString(), "PUT", null, tocken).get();
                    }
                }catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch(ExecutionException e)
                {
                    e.printStackTrace();
                }
                if (result.length() < 6)
                {
                    return;
                }
                if (requestCode == UPLOAD_AVA)
                {
                    byte[] bytes = Base64.decode(file[0], Base64.DEFAULT);
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    Toolbar toolbar = findViewById(R.id.toolbar);
                    setSupportActionBar(toolbar);
                    NavigationView navigationView = findViewById(R.id.navigator_view);

                    View headerView = navigationView.getHeaderView(0);
                    navigationView.setNavigationItemSelectedListener(this);
                    ImageView avatar = headerView.findViewById(R.id.face);

                    avatar.setImageBitmap(bmp);

                }
                //TODO upload file
                //Log.i(TAG, "Uri: " + uri.toString());
                //showImage(uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreateNavigateUpTaskStack(TaskStackBuilder builder) {
        super.onCreateNavigateUpTaskStack(builder);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            /*TODO /chats */
            case R.id.chats_all:
                Intent intent = new Intent(this, MainMenu.class);
                intent.putExtra("LOGIN", nickname);
                intent.putExtra("UUID", uuid);
                intent.putExtra("TOKEN", tocken);
                startActivity(intent);
                finish();
                break;

            case R.id.friends:
                Intent intent2 = new Intent(this, Friendlist.class);
                intent2.putExtra("LOGIN", nickname);
                intent2.putExtra("UUID", uuid);
                intent2.putExtra("TOKEN", tocken);
                startActivity(intent2);
                finish();
                break;
            case R.id.logout:
                SendJSON sender = new SendJSON(1000000, 100000, this);
                String result;
                try {
                    String IP = new Kostyl().IP;
                    result = sender.execute(IP + "/auth", null, "DELETE", null, tocken).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                Intent intent3 = new Intent(this, MainActivity.class);
                TockenMaster tockenMaster = new TockenMaster();
                tockenMaster.DeleteThoken();
                startActivity(intent3);
                finish();
                break;
            case R.id.upload:
                performFileSearch();
                break;
            case R.id.ava:
                upload_ava();
                break;
            case R.id.all_files:
                Intent intentf = new Intent(this, Show_all_files.class);
                intentf.putExtra("LOGIN", nickname);
                intentf.putExtra("UUID", uuid);
                intentf.putExtra("TOKEN", tocken);
                startActivity(intentf);
                break;
        }

        return true;
    }

    private void upload_ava()
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, UPLOAD_AVA);
    }

    @Override
    public void onBackPressed() {
        if (drawLayout.isDrawerOpen(GravityCompat.START)) {
            drawLayout.closeDrawer(GravityCompat.START);
        } else {
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
}
