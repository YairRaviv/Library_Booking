package com.example.floorview;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class CheckReservationsActivity extends AppCompatActivity
{
    DBConnector dbConnector;
    String userId;
    List<ClassRequest> RequestsList;
    private TextView timeText;
    private TextView emptyText;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_reservations);
        Bundle bundle = getIntent().getExtras();
        userId = bundle.getString("userId");
        dbConnector = DBConnector.getInstance();
        try
        {
            initiateRequests();
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void initiateRequests() throws SQLException, ParseException
    {
        ListView listview = (ListView) findViewById(R.id.RequestsListView);
        RequestsList =  GetPendingRequests();
        if (RequestsList.isEmpty())
        {
            emptyText = (TextView)findViewById(R.id.empty);
            emptyText.setText("There is No Requests");
            listview.setEmptyView(emptyText);
        }
        else
        {
            RequestsAdapter Requestsadapter = new RequestsAdapter(RequestsList, this);
            listview.setAdapter(Requestsadapter);
        }
    }


    public List<ClassRequest> GetPendingRequests() throws SQLException
    {
        String queryString = "SELECT * FROM ClassRequests";
        ResultSet result = dbConnector.executeQuery(queryString);
        List<ClassRequest> ClassRequests = new ArrayList<>();
        if (result != null)
        {
            try
            {
                while (result.next())
                {

                    ClassRequest currRequest = new ClassRequest(result.getInt("ReservationID"),
                            result.getString("StudentID"),
                            result.getString("Faculty"),
                            result.getString("Department"),
                            result.getString("Reason"),
                            result.getInt("NumOfStudents"),
                            result.getString("PhoneNumber"));
                    ClassRequests.add(currRequest);
                }
            }
            catch (SQLException throwables)
            {
                throwables.printStackTrace();
            }

        }
        return ClassRequests;
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