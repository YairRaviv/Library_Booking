package com.example.floorview;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Student Main Screen - with two buttons:
 * - Book a chair button
 * - Book a classroom button
 */
public class StudentMainActivity extends AppCompatActivity {
    ProgressDialog progress;
    String userId;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        userId = bundle.getString("userId");
        setContentView(R.layout.student_main_activity);
        // Toast.makeText(StudentMainActivity.this, "Your Text", Toast.LENGTH_LONG).show();
        tv = (TextView)findViewById(R.id.button6);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Toast.makeText(MainActivity.this, "Start", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause(){
        super.onPause();
        //Toast.makeText(MainActivity.this, "Pause", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop(){
        super.onStop();
        //Toast.makeText(MainActivity.this, "Stop", Toast.LENGTH_LONG).show();
    }

    /**
     * When the 'BOOK A CHAIR' button is clicked the user is transfered to another screen where he can:
     *  - Enter a new reservation details
     *  - Watch the existing reservations
     * @param view
     */
    public void BookChairButton(View view) {
        //Toast.makeText(MainActivity.this, "You clicked on BookChairButton button", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(StudentMainActivity.this, StudentBookChairActivity.class);
        intent.putExtra("id",userId);
        startActivity(intent);
    }

    public void BookClassButton(View view) {
        //Toast.makeText(MainActivity.this, "You clicked on BookChairButton button", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(StudentMainActivity.this, StudentBookClassActivity.class);
        intent.putExtra("id",userId);
        startActivity(intent);
    }
}