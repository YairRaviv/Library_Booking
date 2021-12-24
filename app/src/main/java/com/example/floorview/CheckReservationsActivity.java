package com.example.floorview;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
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


/*
Intent callIntent = new Intent(Intent.ACTION_CALL);
    callIntent.setData(Uri.parse("tel:"+PhoneNumber));
    startActivity(callIntent);
 */


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
        //userId = bundle.getString("userId");
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
        // user has no reservations yet
        if (RequestsList.isEmpty())
        {
            emptyText = (TextView)findViewById(R.id.empty);
            emptyText.setText("There is No Requests");
            listview.setEmptyView(emptyText);
            Toast.makeText(CheckReservationsActivity.this, "bad request list", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(CheckReservationsActivity.this, "thats works", Toast.LENGTH_SHORT).show();
            //instantiate custom adapter
            RequestsAdapter Requestsadapter = new RequestsAdapter(RequestsList, this);
            //handle listview and assign adapter
            listview.setAdapter(Requestsadapter);
        }
    }


    public List<ClassRequest> GetPendingRequests() throws SQLException
    {
        String queryString = "SELECT * FROM ClassRequests";
        ResultSet result = dbConnector.executeQuery(queryString);
        List<ClassRequest> ClassRequests = new ArrayList<>();
        //String reservationId = "";
        //List<Map<String, String>> data = new ArrayList();
        if (result != null)
        {
            try
            {
                while (result.next())
                {
                    //Map<String, String> dtname = new HashMap<String, String>();
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
            System.out.println("Before return data");
            //need to go over userReservations and get all user reservations details
        }
        return ClassRequests;
    }
}