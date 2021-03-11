package com.example.asus.catchtheball;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.feedingducks.asus.catchtheball.R;


import java.util.Timer;
import java.util.TimerTask;

public class GameplayScreen extends AppCompatActivity {

    AnimationDrawable boxanimation;

    private TextView scoreLabel;
    private TextView startLabel;
    private ImageView box;
    private ImageView orange;
    private ImageView pink;
    private ImageView black;

    private ImageView imageview;
    private FrameLayout frame;

    // Size
    private int frameHeight;
    private int boxSize;
    private int screenWidth;
    private int screenHeight;

    // Position
    private int boxY;
    private int orangeX;
    private int orangeY;
    private int pinkX;
    private int pinkY;
    private int blackX;
    private int blackY;

    // Speed
    private int boxSpeed;
    private int orangeSpeed;
    private int pinkSpeed;
    private int blackSpeed;

    // Score
    private int score = 0;

    private Handler handler = new Handler();
    private Timer timer = new Timer();
    private MediaPlayer player;
    private SoundPlayer sound;

    // Status check
    private boolean action_flg = false;
    private boolean start_flg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Music
        player = MediaPlayer.create(GameplayScreen.this, R.raw.music);
        player.start();
        player.setLooping(true);

        sound = new SoundPlayer(this);

        // Animation
        box = (ImageView) findViewById(R.id.image);
        box.setBackgroundResource(R.drawable.animation);
        boxanimation = (AnimationDrawable) box.getBackground();

        scoreLabel = (TextView) findViewById(R.id.scoreLabel);
        startLabel = (TextView) findViewById(R.id.startLabel);
        orange = (ImageView) findViewById(R.id.orange);
        pink = (ImageView) findViewById(R.id.pink);
        black = (ImageView) findViewById(R.id.black);
        frame = findViewById(R.id.frame);

        // Get screen size
        WindowManager wm = getWindowManager();
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);

        screenWidth = size.x;
        screenHeight = size.y;

        boxSpeed = Math.round(screenHeight / 60F);
        orangeSpeed = Math.round(screenWidth / 60F);
        pinkSpeed = Math.round(screenWidth / 36F);
        blackSpeed = Math.round(screenWidth / 45F);

        // Out of screen
        orange.setX(-80);
        orange.setY(-80);
        pink.setX(-80);
        pink.setY(-80);
        black.setX(-80);
        black.setY(-80);

        scoreLabel.setText("Score: 0");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        boxanimation.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        player.stop();
        player.release();
    }

    protected void onResume() {
        super.onResume();
        player.start();
    }

    public void changePos() {

        hitCheck();

        // Orange
        orangeX -= orangeSpeed;
        if (orangeX < 0) {
            orangeX = screenWidth + 20;
            orangeY = (int) Math.floor(Math.random() * (frameHeight - orange.getHeight()));
        }
        orange.setX(orangeX);
        orange.setY(orangeY);

        // Black
        blackX -= blackSpeed;
        if (blackX < 0) {
            blackX = screenWidth + 10;
            blackY = (int) Math.floor(Math.random() * (frameHeight - black.getHeight()));
        }
        black.setX(blackX);
        black.setY(blackY);

        // Pink
        pinkX -= pinkSpeed;
        if (pinkX < 0) {
            pinkX = screenWidth + 5000;
            pinkY = (int) Math.floor(Math.random() * (frameHeight - pink.getHeight()));
        }
        pink.setX(pinkX);
        pink.setY(pinkY);

        // Move player
        if (action_flg == true) { // Hold
            boxY -= boxSpeed;
        } else { // Release
            boxY += boxSpeed;
        }

        if (boxY < 0) {
            boxY = 0;
        }

        if (boxY > frameHeight - boxSize) {
            boxY = frameHeight - boxSize;
        }
        box.setY(boxY);
        scoreLabel.setText("Score: " + score);
    }

    public void hitCheck() {

        //Orange
        int orangeCenterX = orangeX + orange.getWidth() / 2;
        int orangeCenterY = orangeY + orange.getHeight() / 2;

        if (0 <= orangeCenterX && orangeCenterX <= boxSize && boxY <= orangeCenterY && orangeCenterY <= boxY + boxSize) {
            score += 10;
            orangeX = -10;
            sound.playHitSound();
        }

        // Pink
        int pinkCenterX = pinkX + pink.getWidth() / 2;
        int pinkCenterY = pinkY + pink.getHeight() / 2;

        if (0 <= pinkCenterX && pinkCenterX <= boxSize && boxY <= pinkCenterY && pinkCenterY <= boxY + boxSize) {
            score += 30;
            pinkX = -10;
            sound.playHitSound();
        }

        // Black
        int blackCenterX = blackX + black.getWidth() / 2;
        int blackCenterY = blackY + black.getHeight() / 2;

        if (0 <= blackCenterX && blackCenterX <= boxSize && boxY <= blackCenterY && blackCenterY <= boxY + boxSize) { // STOP
            timer.cancel();
            timer = null;

            sound.playOverSound();

            Intent intent = new Intent(getApplicationContext(), GameOverScreen.class);
            intent.putExtra("SCORE", score);
            startActivity(intent);
            frame.setBackground(getResources().getDrawable(R.drawable.bg1));
        }

        if (score >= 500) {
            boxSpeed = Math.round(screenHeight / 60F);
            blackSpeed = Math.round(screenWidth / 40F);
            frame.setBackground(getResources().getDrawable(R.drawable.bg2));
        }

        if (score >= 1000) {
            boxSpeed = Math.round(screenHeight / 55F);
            blackSpeed = Math.round(screenWidth / 35F);
            frame.setBackground(getResources().getDrawable(R.drawable.bg3));
        }

        if (score >= 1500) {
            boxSpeed = Math.round(screenHeight / 50F);
            blackSpeed = Math.round(screenWidth / 30F);
            frame.setBackground(getResources().getDrawable(R.drawable.bg4));
        }

        if (score >= 2000) {
            boxSpeed = Math.round(screenHeight / 45F);
            blackSpeed = Math.round(screenWidth / 25F);
            frame.setBackground(getResources().getDrawable(R.drawable.bg5));
        }
    }

    public boolean onTouchEvent(MotionEvent me) {

        if (!start_flg) {
            start_flg = true;

            FrameLayout frame = (FrameLayout) findViewById(R.id.frame);
            frameHeight = frame.getHeight();

            boxY = (int) box.getY();
            boxSize = box.getHeight();

            startLabel.setVisibility(View.GONE);

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            }, 0, 20);
        } else {
            if (me.getAction() == MotionEvent.ACTION_DOWN) {
                action_flg = true;
            } else if (me.getAction() == MotionEvent.ACTION_UP) {
                action_flg = false;
            }
        }
        return true;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
