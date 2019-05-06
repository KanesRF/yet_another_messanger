package com.example.webapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 123;

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
        Button LoginButton, RegisterButton;
        CheckBox VisiblePassword;

        String is_logined = null;
        TockenMaster tockenMaster = new TockenMaster();
        tockenMaster.DeleteThoken();
        is_logined = tockenMaster.readFromFile();
        if (is_logined != null) {
            open_main_menu(is_logined);
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

                SupaLoginer loginer = new SupaLoginer(login, password, MainActivity.this);
                String uuid = loginer.TryToLogin();

                if (uuid != null) {
                    open_main_menu(uuid);
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

    private void open_main_menu(String uuid) {
        Intent intent = new Intent(this, MainMenu.class);
        //intent.putExtra("LOGIN", Login.getText().toString());
        intent.putExtra("UUID", uuid);
        startActivity(intent);
    }

    private void open_register_menu() {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }


    private boolean hasPermissions() {
        int res = 0;
        //string array of permissions,
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        for (String perms : permissions) {
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    private void requestPerms() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
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

                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) || shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "Storage Permissions denied.", Toast.LENGTH_SHORT).show();

                } else {
                    //showNoStoragePermissionSnackbar();
                }
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            main_activity();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}


