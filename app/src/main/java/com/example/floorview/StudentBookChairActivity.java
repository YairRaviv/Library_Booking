package com.example.floorview;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StudentBookChairActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private TextView dateText;
    private TextView timeText;
    private TextView emptyText;
    int hour, minute;
    int saturday;
    DBConnector dbConnector;
    String connectionResult="";
    boolean isSuccess = false;
    DatePickerDialog datePickerDialog;
    int selected_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_bookchair_activity);

        dbConnector = DBConnector.getInstance();
//        try {
//            userReservationsView();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }

        // Spinner
        Spinner mySpinner = (Spinner)findViewById(R.id.spinner1);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(StudentBookChairActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.floors));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setPrompt("Select Floor");
        mySpinner.setAdapter(myAdapter);

        try {
            initiateReservations();
        } catch (SQLException | ParseException throwables) {
            throwables.printStackTrace();
        }
        Button show_start_time_dialog = (Button) StudentBookChairActivity.this.findViewById(R.id.show_start_time_dialog);
        show_start_time_dialog.setEnabled(false);
        Button btnReservations = (Button) StudentBookChairActivity.this.findViewById(R.id.btnReservations);
        btnReservations.setEnabled(false);


    }

    private void initiateReservations() throws SQLException, ParseException {
        System.out.println("in MyReservations()");
        ListView listview = (ListView) findViewById(R.id.listview);
        List<Map<String,String>> mydataList = null;
        mydataList =  getData();
        // user has no reservations yet
        if (mydataList.isEmpty()){
            ListView lv = (ListView)findViewById(R.id.listview);
            emptyText = (TextView)findViewById(R.id.empty);
            emptyText.setText("You Have No Reservations");
            lv.setEmptyView(emptyText);
        }
        else {
            System.out.println("after userReservationsView()");
            ArrayList<String> reservationsList = new ArrayList<String>();
            Map<String, String> reservationDetails = new HashMap<String, String>();
            for (int i = 0; i < mydataList.size(); i++) {
                System.out.println("gets reservation: " + i);
                reservationDetails = mydataList.get(i);
                Reservation currReservation = new Reservation(Integer.parseInt(reservationDetails.get("reservationId")),
                        reservationDetails.get("floor").charAt(0),
                        reservationDetails.get("tableId"),
                        Date.valueOf(reservationDetails.get("reservationDate")),
                        Time.valueOf(reservationDetails.get("startTime")),
                        Time.valueOf(reservationDetails.get("endTime")));
                reservationsList.add(currReservation.toString());
            }

            //generate list
            ArrayList<String> list = new ArrayList<String>();
            list.add("item1");
            list.add("item2");

            //instantiate custom adapter
            MyCustomAdapter myAdapter = new MyCustomAdapter(reservationsList, this);

            //handle listview and assign adapter
            ListView lView = (ListView) findViewById(R.id.listview);
            lView.setAdapter(myAdapter);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(StudentBookChairActivity.this, "Start", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause(){
        super.onPause();
        Toast.makeText(StudentBookChairActivity.this, "Pause", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop(){
        super.onStop();
        Toast.makeText(StudentBookChairActivity.this, "Stopp", Toast.LENGTH_LONG).show();
    }

    private void showDatePickerDialog() throws SQLException, ParseException {

        //DatePickerDialog datePickerDialog = new DatePickerDialog();
        datePickerDialog = DatePickerDialog.newInstance(this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        selected_date = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        datePickerDialog.setMinDate(Calendar.getInstance());
        Calendar curr_1 = Calendar.getInstance();
        curr_1.add(Calendar.DAY_OF_WEEK,2);
        datePickerDialog.setMaxDate(curr_1);
        List<Calendar> days = new ArrayList<>();
        int num_of_days = 3;
        Calendar curr;

        List<Map<String,String>> userReservations = null;
        userReservations = getData();
        List<String> chosen_dates = new ArrayList();
        if (userReservations != null){
            for (int i=0; i<userReservations.size(); i++){
                String dateString = userReservations.get(i).get("reservationDate");
//                SimpleDateFormat formatter = new SimpleDateFormat(dateString);
//                Date date = formatter.parse(dateString);
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTime(date);
                chosen_dates.add(dateString);
            }
        }
        for (int i=0; i<num_of_days; i++) {
            curr = Calendar.getInstance();
            curr.add(Calendar.DAY_OF_WEEK, i);
            // Disable saturdays
            if (curr.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                days.add(curr);
            }
            // Disable saturdays
            else if (userReservations != null){
                Date date = (Date) curr.getTime();
                String year = String.valueOf(curr.get(Calendar.YEAR));
                String day = String.valueOf(curr.get(Calendar.DAY_OF_MONTH));
                String month = String.valueOf(curr.get(Calendar.MONTH)+1);
                String dateString = year+"-"+month+"-"+day;
                if (chosen_dates.contains(dateString)){
                    days.add(curr);
                }
            }
        }
        Calendar[] disabledDays = days.toArray(new Calendar[days.size()]);
        datePickerDialog.setDisabledDays(disabledDays);
        datePickerDialog.setThemeDark(true);
        datePickerDialog.show(StudentBookChairActivity.this.getFragmentManager(), "DatePickerDialog");
        Button show_start_time_dialog = (Button) StudentBookChairActivity.this.findViewById(R.id.show_start_time_dialog);
        show_start_time_dialog.setEnabled(true);

    }

    public void SelectDateButton(View view) throws SQLException, ParseException {
        dateText = findViewById(R.id.date_text);
        showDatePickerDialog();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear = monthOfYear+1;
        String date = monthOfYear + "/" + dayOfMonth + "/" + year;
        Date d = new Date(year,monthOfYear,dayOfMonth);
        Calendar c = Calendar.getInstance();
        c.setTime(d);
//        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
//        if (dayOfWeek == 1){
//            dateText.setText(date);MyCustomAdapter
//        }
        dateText.setText(date);
//        else{
//
//        }
    }

    private void showTimePickerDialog(){
        int style = AlertDialog.THEME_HOLO_DARK;
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                style,
                this,
                hour,
                minute,
                true);
        timePickerDialog.setTitle("Select Time");




        timePickerDialog.show();
    }



    public void SelectStartTimeButton(View view) {
        timeText = findViewById(R.id.start_time_text);
        showTimePickerDialog();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        Calendar datetime = Calendar.getInstance();
        Calendar c = Calendar.getInstance();
        Button show_date_dialog = (Button) StudentBookChairActivity.this.findViewById(R.id.show_date_dialog);
        int day = datePickerDialog.getSelectedDay().getDay();
        int month = datePickerDialog.getSelectedDay().getMonth() + 1;
        int year = datePickerDialog.getSelectedDay().getYear();
        int hour = hourOfDay;
        int min = minute;
        String time = String.format(Locale.getDefault(), "%02d:%02d", hour, min);
        timeText.setText(time);
    }

    public void FloorNumber(View view) {
        Toast.makeText(StudentBookChairActivity.this, "You chose a floor", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(StudentBookChairActivity.this, MainActivity3.class);
        String str = view.getContext().toString();
        intent.putExtra("number_of_floor_key", str);
        startActivity(intent);
        finish();
    }

    public List<Map<String,String>> getData() throws SQLException {
        String id = "829189";
        //829189,316291996
        String queryString = "SELECT * FROM Reservations WHERE userId = '"+id+"'";
        ResultSet result = dbConnector.executeQuery(queryString);
        List<String> userReservations = new ArrayList<>();
        String reservationId = "";
        List<Map<String, String>> data = new ArrayList();
        if (result != null) {
            try {
                while (result.next()) {
                    reservationId = result.getString("reservationId");
                    userReservations.add(reservationId);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            int number_of_reservations = userReservations.size();
            if (number_of_reservations == 0) {
                System.out.println("No User Reservations Yet!!!");
            } else {
                for (int i = 0; i < userReservations.size(); i++) {
                    reservationId = userReservations.get(i);
                    System.out.println("User Reservation Id is " + reservationId);

                    queryString = "SELECT * FROM Reservations WHERE reservationId = '" + reservationId + "'";
                    result = dbConnector.executeQuery(queryString);
                    while (result.next()) {
                        System.out.println("in loop");
                        Map<String, String> dtname = new HashMap<String, String>();
                        dtname.put("reservationId", String.valueOf(result.getInt("reservationId")));
                        dtname.put("floor", result.getString("floor"));
                        dtname.put("tableId", result.getString("tableId"));
                        dtname.put("reservationDate", result.getDate("reservationDate").toString());
                        dtname.put("startTime", result.getTime("startTime").toString());
                        dtname.put("endTime", result.getTime("endTime").toString());
                        data.add(dtname);
                    }
                    System.out.println("After loop");
                    connectionResult = "Success";
                    isSuccess = true;
                    //dbConnector.close();
                }
            }
        }
        else{
            connectionResult = "Failed";
            System.out.println("Result is null - User has no Reservations Yet!!!");
        }
        System.out.println("Before return data");
        return data;
        //need to go over userReservations and get all user reservations details
    }

    SimpleAdapter sa;
    public void MyReservations(View view) throws SQLException {
        System.out.println("in MyReservations()");
        ListView listview = (ListView) findViewById(R.id.listview);
        List<Map<String,String>> mydataList = null;
        mydataList =  getData();
        System.out.println("after userReservationsView()");
        ArrayAdapter<String> adapter;
        ArrayList<String> reservationsList=new ArrayList<String>();
        Map<String, String> reservationDetails = new HashMap<String, String>();
        for(int i=0; i<mydataList.size(); i++) {
            System.out.println("gets reservation: "+i);
            reservationDetails = mydataList.get(i);
            Reservation currReservation = new Reservation(Integer.parseInt(reservationDetails.get("reservationId")),
                    reservationDetails.get("floor").charAt(0),
                    reservationDetails.get("tableId"),
                    Date.valueOf(reservationDetails.get("reservationDate")),
                    Time.valueOf(reservationDetails.get("startTime")),
                    Time.valueOf(reservationDetails.get("endTime")));
            reservationsList.add(currReservation.toString());
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, reservationsList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                tv.setTextColor(Color.parseColor("#145DA0"));
                return view;
            }
        };;
        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, reservationsList);
        listview.setAdapter(adapter);
    }

}