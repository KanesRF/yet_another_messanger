package com.example.webapp;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 123;
    private static final int READ_REQUEST_CODE = 42;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (hasPermissions() == false) {
            requestPerms();
        } else {
            main_activity();
        }
    }




    public void main_activity() {
        setContentView(R.layout.activity_main);
       // open_main_menu("aa", "aa", "bb");
       // UberWebSocket aa = new UberWebSocket();
       // aa.connectWebSocket();


      //  performFileSearch();


        Button LoginButton, RegisterButton;
        CheckBox VisiblePassword;
        String is_logined = null;
        TockenMaster tockenMaster = new TockenMaster();
        //tockenMaster.DeleteThoken();
        is_logined = tockenMaster.readFromFile();
        if (is_logined != null) {
            String [] separated = is_logined.split("\n");
            open_main_menu(separated[0], separated[1], separated[2]);
            //open_main_menu(separated[0], separated[1], null);
        }


        LoginButton = findViewById(R.id.loginButton);
        RegisterButton = findViewById(R.id.register_button);
        VisiblePassword = findViewById(R.id.show_password);
        final EditText Password;
        Password = findViewById(R.id.password);

        VisiblePassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Password.setTransformationMethod(null);
                } else {
                    Password.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText Login, Password;
                String login, password;

                Login = findViewById(R.id.Login_register);
                Password = findViewById(R.id.password);

                login = Login.getText().toString();
                password = Password.getText().toString();

                //authorize request
                // open_main_menu("123uuid123\nKaneS");
                // finish();
                SupaLoginer loginer = new SupaLoginer(login, password, MainActivity.this);
                String all = loginer.TryToLogin();
                String[] separated = all.split("\n");


                if (!separated[0].equals("null") && !separated[1].equals("null") && !separated[2].equals("null")) {
                    open_main_menu(separated[0], separated[1], separated[2]);
                }
            }
        });
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_register_menu();
            }
        });
    }

    private void open_main_menu(String uuid, String token, String login) {
        Intent supa_service = new Intent(getApplicationContext(), UberWebSocket.class);
        supa_service.putExtra("TOKEN", token);
        //startService(new Intent(getApplicationContext(), UberWebSocket.class));
        startService(supa_service);
        Intent intent = new Intent(this, MainMenu.class);
        //String[] separated = uuid.split("\n");
        //TODO запрос на логин от юзера
        intent.putExtra("LOGIN", login);
        intent.putExtra("UUID", uuid);
        intent.putExtra("TOKEN", token);
        startActivity(intent);
        finish();
    }

    private void open_register_menu() {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }


    private boolean hasPermissions() {
        int res = 0;
        //string array of permissions,
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS};

        for (String perms : permissions) {
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    private void requestPerms() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allowed = true;

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:

                for (int res : grantResults) {
                    // if user granted all permissions.
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }

                break;
            default:
                // if user not granted permissions.
                allowed = false;
                break;
        }

        if (allowed) {
            //user granted all permissions we can perform our task.
            main_activity();
        } else {
            // we will give warning to user that they haven't granted permissions.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) || shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                || shouldShowRequestPermissionRationale( Manifest.permission.CAMERA) ||shouldShowRequestPermissionRationale( Manifest.permission.WRITE_CONTACTS) || shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS )){
                    Toast.makeText(this, "Storage Permissions denied.", Toast.LENGTH_SHORT).show();

                } else {
                    //showNoStoragePermissionSnackbar();
                }
            }
        }

    }


    private String readTextFromUri(Uri uri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            main_activity();
            return;
        }
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                String file = null;
                try {
                    file = readTextFromUri(uri);
                }catch (IOException e)
                {

                }
                //Log.i(TAG, "Uri: " + uri.toString());
                //showImage(uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}


