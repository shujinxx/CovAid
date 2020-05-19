package com.ganshujin.androidrealtimelocation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class GameOver extends AppCompatActivity {

    private  Button StartGameAgain;
    private Button GoHome;
    private TextView DisplayScore;
    private String score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        score = getIntent().getExtras().get("score").toString();

        StartGameAgain = (Button) findViewById(R.id.play_again_btn);
        GoHome = (Button) findViewById(R.id.btn_go_home);

        DisplayScore = (TextView) findViewById(R.id.displayScore);

        StartGameAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainintent = new Intent(GameOver.this, Game.class);
                startActivity(mainintent);
            }
        });

        DisplayScore.setText("SCORE : " + score);

        GoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainintent = new Intent(GameOver.this, Dashboard.class);
                startActivity(mainintent);
            }
        });


    }
}
