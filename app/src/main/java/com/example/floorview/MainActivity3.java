package com.example.floorview;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This is the activity that gets the floor number that was sele
 */
public class MainActivity3 extends AppCompatActivity {
    TextView floor_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        floor_number = (TextView)findViewById(R.id.recieved_value_id);
        Intent intent = getIntent();
        String str = intent.getStringExtra("number_of_floor_key");
        floor_number.setText(str);
    }
}