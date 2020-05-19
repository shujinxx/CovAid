package com.ganshujin.androidrealtimelocation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class Dashboard extends AppCompatActivity {

    ImageButton homeActivitybutton;
    ImageButton updatesbutton;
    ImageButton gameButton;
    ImageButton newsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        homeActivitybutton = (ImageButton) findViewById(R.id.btn_homeactivity);

        homeActivitybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomeActivity();
            }
        });


        updatesbutton = (ImageButton) findViewById(R.id.btn_quickupdate);

        updatesbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUpdateActivity();
            }
        });


        gameButton = (ImageButton) findViewById(R.id.btn_gameactivity);

        gameButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) { openGame();}
        });

        newsButton = (ImageButton) findViewById(R.id.btn_news);

        newsButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) { openWebViewActivity();}
        });


    }
    public void openHomeActivity() {
        Intent intent = new Intent(Dashboard.this, HomeActivity.class);
        startActivity(intent);
    }

    public void openUpdateActivity() {
        Intent intent = new Intent(this, UpdateActivity.class);
        startActivity(intent);
    }

    public void openGame() {
        Intent intent = new Intent(this, Game.class);
        startActivity(intent);
    }

    public void openWebViewActivity() {
        Intent intent = new Intent(this, WebViewActivity.class);
        startActivity(intent);
    }

}
