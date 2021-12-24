package com.example.floorview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class LibrarianMainActivity extends AppCompatActivity {

    String userId;
    private Button OnClickClassBooking;
    private Button OnClickCheckReservations;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        userId = bundle.getString("userId");
        setContentView(R.layout.activity_librarian_main);
        OnClickClassBooking = (Button)findViewById(R.id.BookClassroom);
        OnClickCheckReservations = (Button)findViewById(R.id.CheckRequests);

        OnClickClassBooking.setOnClickListener(view -> {

            Intent intent = new Intent(LibrarianMainActivity.this, LibrarianBookClassActivity.class);
            intent.putExtra("userId",userId);
            startActivity(intent);

        });
        OnClickCheckReservations.setOnClickListener(view ->
        {
            Intent intent = new Intent(LibrarianMainActivity.this, CheckReservationsActivity.class);
            intent.putExtra("userId",userId);
            startActivity(intent);
        });
    }
}