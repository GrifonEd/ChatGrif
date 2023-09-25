package com.example.a79373.grifchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class StartGameActivity extends AppCompatActivity {
    public ImageView img;
    public Button btn;
    public EditText zapros;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startgame);
        worldya();
        startService(new Intent  (this, MyService2.class));
        Button button_restart  = (Button)findViewById(R.id.button_restart);
        button_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                try{
                    stopMusic2();
                    Intent intent = new Intent(StartGameActivity.this, Game_activity.class);
                    startActivity(intent);finish();
                }catch (Exception e){

                }
            }
        });
        Button buttonhome  = (Button)findViewById(R.id.button_home);
        buttonhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                try{
                    Intent GameBackActivityIntent = new Intent(StartGameActivity.this, ChatActivity.class);
                    startActivity(GameBackActivityIntent);
                    finish();
                    stopMusic2();
                }catch (Exception e){

                }
            }
        });
        Button button_happy  = (Button)findViewById(R.id.happy);
        button_happy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                try{
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Любовь — это все что у нас есть, и только любовью мы можем помочь друг другу.\nСпасибо тебе за каждое мгновение.\nМы будем вместе всегда!" , Toast.LENGTH_SHORT);




                    toast.show();

                }catch (Exception e){

                }
            }

        });
    }
    @Override
    protected void onPause (){
        super.onPause();
        stopMusic2();

    }
    @Override
    protected void onResume (){
        super.onResume();
        startService(new Intent  (this, MyService2.class));
    }
    public void worldya (){

        img=(ImageView)findViewById(R.id.imageView);

        img.setImageResource(R.drawable.gameover);
    }

    public void onBackPressed()
    {
        stopMusic2();
        Intent intent = new Intent(StartGameActivity.this, Game_activity.class);
        startActivity(intent);finish();
    }
    public void stopMusic2 (){
        stopService(new Intent(this, MyService2.class));
    }



}