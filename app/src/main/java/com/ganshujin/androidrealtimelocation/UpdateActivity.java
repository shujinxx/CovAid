package com.ganshujin.androidrealtimelocation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class UpdateActivity extends AppCompatActivity {

    public static TextView data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        data = (TextView) findViewById(R.id.fetcheddata);

        fetchData process = new fetchData();
        process.execute();



    }
}
