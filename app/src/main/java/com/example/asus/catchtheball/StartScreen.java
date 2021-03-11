package com.example.asus.catchtheball;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

import com.feedingducks.asus.catchtheball.R;

public class StartScreen extends AppCompatActivity {

    private SoundPlayer sound;
    private ImageButton bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        sound = new SoundPlayer(this);
        bt = (ImageButton)findViewById(R.id.share);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String shareBody="I've found out about this fun and addictive game! You might check it out as well! Here is the download link: https://play.google.com/store/apps/details?id=com.feedingducks.asus.catchtheball";
                String shareSub="Feeding Ducks";
                myIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(myIntent, "Share using"));
            }
        });
    }

    public void startGame(View view){
        startActivity(new Intent(getApplicationContext(), GameplayScreen.class));
    }

    public boolean dispatchKeyEvent(KeyEvent event){
        if(event.getAction()==KeyEvent.ACTION_DOWN){
            switch (event.getKeyCode()){
                case KeyEvent.KEYCODE_BACK:
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
