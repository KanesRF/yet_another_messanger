package com.example.webapp;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;



public class Register extends AppCompatActivity {

    TockenMaster tockenMaster;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        tockenMaster = new TockenMaster();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button Cancel_button, OK_button;
        final EditText Name, Password1, Password2, Email, Login;
        Cancel_button = findViewById(R.id.Cancel_register);
        OK_button = findViewById(R.id.OK_register);
        //Name = findViewById(R.id.Sername_register);
        Password1 = findViewById(R.id.Password_register);
        Password2 = findViewById(R.id.Second_password_register);
        Email = findViewById(R.id.Email_register);
        Login = findViewById(R.id.Login_register);
        OK_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Password1.getText().toString().equals(Password2.getText().toString()))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                    builder.setTitle("Error");
                    builder.setMessage("You entered second time different password");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.show();
                }
                else {
                    String password, name, sername, login, email;
                    password = Password1.getText().toString();
                    //name = Name.getText().toString();
                    login = Login.getText().toString();
                    email = Email.getText().toString();
                    if (password.equals("") ||  login.equals("") || email.equals(""))
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                        builder.setTitle("Error");
                        builder.setMessage("Please, fill all fields");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        builder.show();
                        return;
                    }
                    JSONObject postData = new JSONObject();
                    JSONObject params = new JSONObject();
                    String result = null;
                    try {
                        postData.put("id", "1234");
                        postData.put("jsonrpc", "2.0");
                        postData.put("method", "creat_user");

                        params.put("name",login);
                        params.put("email",email);
                        params.put("password",password);

                        postData.put("params", params);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                    SendJSON sender = new SendJSON(1000000, 1000000);
                    try{
                        String IP = new Kostyl().IP;
                        result = sender.execute(IP + "/user", postData.toString(), "POST").get();
                    }catch (InterruptedException e)
                    {
                        e.printStackTrace();
                        return;
                    }
                    catch(ExecutionException e)
                    {
                        e.printStackTrace();
                        return;
                    }
                    String result2[] = result.split("\n");
                    result = result2[0];
                    if (result.length() > 4)//на случай, если там код, а не тело
                    {
                        JSONObject recievedData, params_json;
                        String uuid, token;
                        try {
                            recievedData = new JSONObject(result);
                            params_json = recievedData.getJSONObject("params");
                            uuid = params_json.getString("uuid");

                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                            return;
                        }
                        //String prov = tockenMaster.readFromFile(Register.this);
                        tockenMaster.writeToFile(uuid, result2[1]);

                        finish();
                    }

                }
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
