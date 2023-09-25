package com.example.a79373.grifchat;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MyService2 extends Service {
    private static final String TAG = "MyService2";
    MediaPlayer player2;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        player2 = MediaPlayer.create(this, R.raw.menu);
        player2.setLooping(true); // зацикливаем
    }

    @Override
    public void onDestroy() {

        player2.stop();
    }

    @Override
    public void onStart(Intent intent, int startid) {
        player2.start();
    }
}