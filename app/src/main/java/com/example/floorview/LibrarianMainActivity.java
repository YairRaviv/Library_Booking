package com.example.floorview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.librarian_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);
        switch (item.getItemId()) {
            case R.id.librarian_menu_book_class:
                //Toast.makeText(this, "Book Chair clicked", Toast.LENGTH_LONG).show();
                intent = new Intent(this, LibrarianBookClassActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            case R.id.librarian_menu_requests:
                intent = new Intent(this, CheckReservationsActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            case R.id.librarian_menu_change_details:
                intent = new Intent(this, ChangeCredentialsActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            case R.id.librarian_menu_logout:
                intent = new Intent(this, Login_Registration_Screen.class);
                startActivity(intent);
                return true;
        }
        return true;
    }

}