package com.example.floorview;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Student Main Screen - with two buttons:
 * - Book a chair button
 * - Book a classroom button
 */
public class StudentMainActivity extends AppCompatActivity{
    ProgressDialog progress;
    String userId;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        userId = bundle.getString("userId");
        setContentView(R.layout.student_main_activity);
        tv = (TextView)findViewById(R.id.book_class_btn);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onStop(){
        super.onStop();
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
        intent.putExtra("userId",userId);
        startActivity(intent);
    }

    public void BookClassButton(View view) {
        //Toast.makeText(MainActivity.this, "You clicked on BookChairButton button", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(StudentMainActivity.this, StudentBookClassActivity.class);
        intent.putExtra("userId",userId);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.student_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);
        switch (item.getItemId()) {
            case R.id.student_menu_book_chair:
                //Toast.makeText(this, "Book Chair clicked", Toast.LENGTH_LONG).show();
                intent = new Intent(this, StudentBookChairActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            case R.id.student_menu_book_class:
                intent = new Intent(this, StudentBookClassActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            case R.id.student_menu_change_details:
                intent = new Intent(this, ChangeCredentialsActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            case R.id.student_menu_logout:
                intent = new Intent(this, Login_Registration_Screen.class);
                startActivity(intent);
                return true;
        }
        return true;
    }


}