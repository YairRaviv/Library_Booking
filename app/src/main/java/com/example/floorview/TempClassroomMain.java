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
public class TempClassroomMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Toast.makeText(StudentMainActivity.this, "Your Text", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(TempClassroomMain.this, LibrarianMainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userId", "1");
        intent.putExtras(bundle);
        startActivity(intent);
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

}