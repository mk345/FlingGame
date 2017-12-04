package com.mk.test.swipegame;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int INITIAL_TIME_LIMIT = 3000;
    private int timeLimit = INITIAL_TIME_LIMIT;

    private GestureDetectorCompat mGestureDetectorCompat;
    TextView scoreTextView, directionTextView, highScore;
    Button resetHighScoreButton;
    Handler handler = new Handler();
    static long lastFling;
    private int score = 0;
    private int direction;

    Random random;
    int randomNum;

    private int savedHighScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGestureDetectorCompat = new GestureDetectorCompat(this, new GestureListener());

        directionTextView = (TextView) findViewById(R.id.direction);
        scoreTextView = (TextView) findViewById(R.id.score);
        highScore = (TextView) findViewById(R.id.highscore);
        resetHighScoreButton = (Button) findViewById(R.id.resetHighScoreButton);
        resetHighScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetSavedHighScore();
            }
        });

        random = new Random();
        randomNum = random.nextInt((4));

        setDirection();

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        savedHighScore = sharedPref.getInt(getString(R.string.saved_high_score), 0);
        highScore.setText(String.valueOf(savedHighScore));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mGestureDetectorCompat.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

            directionTextView.setTextColor(Color.BLACK);

            if (System.currentTimeMillis()  < (lastFling + timeLimit)) {
                if (getFlingDirection(velocityX, velocityY) == direction) {
                    score++;
                    scoreTextView.setText(String.valueOf(score));
                    if (timeLimit > 200) {
                        timeLimit = timeLimit - 100;
                    }
                } else {
                    reset();
                }
            } else {
                reset();
            }
            lastFling = System.currentTimeMillis();
            setDirection();

            handler.removeCallbacksAndMessages(null);

            handler.postDelayed(new Runnable() {
                public void run() {
                    reset();
                }
            }, timeLimit);

            return true;
        }
    }

    private void setDirection() {
        randomNum = random.nextInt((4));

        direction = randomNum;

        switch (randomNum) {
            case 0: directionTextView.setText("UP");
            break;
            case 1: directionTextView.setText("RIGHT");
                break;
            case 2: directionTextView.setText("DOWN");
                break;
            default: directionTextView.setText("LEFT");
                break;
        }

        directionTextView.setTextColor(Color.RED);
    }

    private int getFlingDirection(float velocityX, float velocityY) {
        if (Math.abs(velocityX) > Math.abs(velocityY)) {
            if (velocityX > 0) {
                return 1;
            } else {
                return 3;
            }
        } else {
            if (velocityY > 0) {
                return 2;
            } else {
                return 0;
            }
        }
    }

    private void reset() {
        saveHighScore(score);
        score = 0;
        scoreTextView.setText(String.valueOf(score));
        setDirection();
        timeLimit = INITIAL_TIME_LIMIT;
    }

    private void saveHighScore(int score) {
        if (score > savedHighScore) {
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(getString(R.string.saved_high_score), score);
            editor.commit();
            savedHighScore = score;
            highScore.setText(String.valueOf(score));
        }
    }

    private void resetSavedHighScore() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.saved_high_score), 0);
        editor.commit();
        savedHighScore = 0;
        highScore.setText(String.valueOf(0));
    }
}
