package com.example.webapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    public static final String CHANNEL_1_ID = "msgs";
    public static final String CHANNEL_2_ID = "chats";

    @Override
    public void onCreate(){
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Messages",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("");

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Chats",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel2.setDescription("");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
        }
    }
}
