package com.example.webapp;

import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.storage.StorageManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.provider.DocumentFile;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import android.app.Notification;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import static com.example.webapp.App.CHANNEL_1_ID;
import static com.example.webapp.App.CHANNEL_2_ID;

public class MainMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NotificationManagerCompat notificationManager_c, notificationManager_m;
    private DrawerLayout drawLayout;
    private String nickname, uuid, token;
    private ArrayList<String> all_chats;
    private ArrayList<String> all_id_notificator = new ArrayList<String>();
    static final int ADD_CHAT = 1;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> dataList = new ArrayList<String>();
    private static final int READ_REQUEST_CODE = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Bundle extras = getIntent().getExtras();
        Toolbar toolbar = findViewById(R.id.toolbar);


        //notificationManager_c = NotificationManagerCompat.from(this);
        notificationManager_m = NotificationManagerCompat.from(this);

        drawLayout = findViewById(R.id.draw_layout);
        nickname = extras.getString("LOGIN");
        uuid = extras.getString("UUID");
        token = extras.getString("TOKEN");

        setSupportActionBar(toolbar);
        NavigationView navigationView = findViewById(R.id.navigator_view);

        View headerView = navigationView.getHeaderView(0);
        TextView nickname_text = headerView.findViewById(R.id.nickname_in_menu);
        nickname_text.setText(nickname);

        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawLayout, toolbar,
                R.string.navigation_open, R.string.navigation_close);
        drawLayout.addDrawerListener(toggle);
        toggle.syncState();

        SendJSON sender = new SendJSON(100000, 100000);
        String result = null;
        //TODO remove this plug and parse real JSON
        //String [] all_chats2 = {"Chat one\nuuid1", "Chat 2\nuuid2"};
        JSONObject postData = new JSONObject();
        JSONObject params = new JSONObject();
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
            result = sender.execute(IP + "/chats", null, "GET", null, token).get();
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
        try {
            recievedData = new JSONObject(result2[0]);
            params_json = recievedData.getJSONObject("params");
            all_uuids = params_json.getJSONArray("uuids");
            all_names = params_json.getJSONArray("names");
            array = new String[all_uuids.length()];
            for (int i = 0; i < all_uuids.length(); i++) {
                array[i] = all_names.get(i) + "\n" + all_uuids.get(i);
            }

        }catch (JSONException e)
        {
            e.printStackTrace();
        }
        if(array != null) {
            this.all_chats = new ArrayList<String>(Arrays.asList(array));
        }
        else
        {
            this.all_chats = new ArrayList<String>();
        }
        //this.all_chats = all_chats2;
        //Request /chats params:{name: "", uuid: ""}
      //  String [] chats = geChats();
        if (all_chats != null)
        {
            draw_chats();
        }
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                mMessageReceiver, new IntentFilter("SUPA"));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                cMessageReceiver, new IntentFilter("UBER"));
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
            String uuid_msg, text, chat_uuid, chat_name, method;
            String[] array = null;
            try {
                recievedData = new JSONObject(message);
                params_json = recievedData.getJSONObject("params");
                text = params_json.getString("name");
                uuid_msg = params_json.getString("uuid");
                method = recievedData.getString("method");
                if (method.equals("chat_broadcast_delete"))
                {
                    Integer i = 0;
                    for (String s:dataList)
                    {
                        if (s.equals(text))
                        {
                            break;
                        }
                        i = i + 1;
                    }
                    all_chats.remove(i);
                    dataList.remove(i);
                    arrayAdapter.notifyDataSetChanged();
                }
                else
                {
                    Integer i = 0;
                    for (String s:dataList)
                    {
                        if (s.equals(text))
                        {
                            return;
                        }
                        i = i + 1;
                    }
                    all_chats.add(text + "\n" + uuid_msg);
                    dataList.add(text);
                    arrayAdapter.notifyDataSetChanged();
                }


            }catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

    };

    // THIS IS FOR MSG NOTIFICATION
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
            String uuid_msg, text, chat_uuid = null, chat_name = null;
            String[] array = null;
            try {
                recievedData = new JSONObject(message);
                params_json = recievedData.getJSONObject("params");
                chat_name = params_json.getString("chatName");
                //uuid_msg = params_json.getString("uuid");
                chat_uuid = params_json.getString("chatUUID");

            }catch (JSONException e)
            {
                e.printStackTrace();
                return;
            }
            /*for (String s : all_chats)
            {
                String [] separated = s.split("\n");
                if (separated[1].equals(chat_uuid))
                {
                    chat_name = separated[0];
                    break;
                }
            }*/
            if(chat_name != null) {

                for (String s : all_id_notificator)
                {
                    if (s.equals(chat_name))
                    {
                        return;
                    }
                }
                all_id_notificator.add(chat_name);
                sendOnChannel1("New message in " + chat_name, (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE));
            }
        }
    };

    private void draw_chats()
    {
        ListView chats_list = findViewById(R.id.Chats);
        if(all_chats != null) {
            for (String s : all_chats) {
                String[] cur = s.split("\n");
                dataList.add(cur[0]);
            }
        }
        Point size = new Point();
        ((WindowManager)this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getSize(size);
        LinearLayout.LayoutParams vi_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int)(size.y*0.70));
        chats_list.setLayoutParams(vi_params);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, dataList);
        chats_list.setAdapter(arrayAdapter);

        chats_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {

                String[] chosen = all_chats.get(index).split("\n");
                Intent intent = new Intent(MainMenu.this, Chat.class);
                intent.putExtra("UUID", uuid);
                intent.putExtra("TOKEN", token);
                intent.putExtra("CHAT", chosen[1]);
                startActivity(intent);
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mMessageReceiver);
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(cMessageReceiver);
                finish();
            }
        });

        chats_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                String [] separate = all_chats.get(pos).split("\n");
                all_chats.remove(pos);
                dataList.remove(pos);
                arrayAdapter.notifyDataSetChanged();
                SendJSON sender = new SendJSON(100000, 1000);
                String result;
                try{
                    String IP = new Kostyl().IP;
                    result = sender.execute(IP + "/chat", null, "DELETE", separate[1], token).get();
                }catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch(ExecutionException e)
                {
                    e.printStackTrace();
                }

                return true;
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenu.this, AddChat.class);
                intent.putExtra("LOGIN", nickname);
                intent.putExtra("UUID", uuid);
                intent.putExtra("TOKEN", token);
                startActivity(intent );

            }
        });
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
                startActivity(intent1);
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mMessageReceiver);
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(cMessageReceiver);
                finish();
                break;

            case R.id.friends:
                Intent intent = new Intent(this, Friendlist.class);
                intent.putExtra("LOGIN", nickname);
                intent.putExtra("UUID", uuid);
                intent.putExtra("TOKEN", token);
                startActivity(intent);
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mMessageReceiver);
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(cMessageReceiver);
                finish();
                break;
            case R.id.logout:
                SendJSON sender = new SendJSON(100000, 100000);
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
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mMessageReceiver);
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
        String [] paths = uri.toString().split("/");

        String [] answer = {base64, paths[paths.length - 1]};
        return answer;


      /*  //String aaaaaaaaaaa = getRealPathFromURI(uri);
        //File file = new File(uri.getPath());
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String currentline;
        while ((currentline = reader.readLine()) != null) {
            stringBuilder.append(currentline + "\n");
        }

        final String path = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS + "/";
        file = new File(path, "haha.jpg");
        boolean a = file.createNewFile();
        FileOutputStream fOut = new FileOutputStream(file);
        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
        myOutWriter.append(stringBuilder.toString());

        myOutWriter.close();

        fOut.flush();
        fOut.close();*/


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
