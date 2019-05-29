package com.example.webapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

//TODO add notificator

public class AddFriend extends Activity {

    private String uuid = null, token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        Bundle b = getIntent().getExtras();
        String value = null;
        if(b != null) {
            uuid = b.getString("UUID");
            token = b.getString("TOKEN");
        }
        int width, heigth;
        width = dm.widthPixels;
        heigth = dm.heightPixels;

        getWindow().setLayout((int)(width * .8), (int)(heigth*.7));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);

        Button Cancel_button = findViewById(R.id.Cancel);
        Button Ok_button = findViewById(R.id.OK);

        final EditText EmailFriend = findViewById(R.id.email_friend);

        Ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = "";
                //TODO Send GET request with my uuid and email
                JSONObject postData = new JSONObject();
                JSONObject params = new JSONObject();
                try {
                    try {
                        postData.put("id", "1234");
                        postData.put("jsonrpc", "2.0");
                        postData.put("method", "creat_user");
                        params.put("email", EmailFriend.getText().toString());
                        postData.put("params", params);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                    SendJSON sender = new SendJSON(100000, 100000);
                    String IP = new Kostyl().IP + "/contact";
                    result = sender.execute(IP, postData.toString(), "POST", null, token).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                //intent.putExtra("name", result);
                //TODO remove plug, parse answer

                if (result.length() < 4)
                {
                    intent.putExtra("name", "");
                    setResult(RESULT_OK, intent);
                    finish();
                }
                intent.putExtra("name", result);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
