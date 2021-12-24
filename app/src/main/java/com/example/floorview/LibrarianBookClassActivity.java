package com.example.floorview;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class LibrarianBookClassActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    String userId;
    private TextView dateText;
    private TextView timeText;
    private TextView emptyText;
    String floor;
    int hour, minute;
    int saturday;
    DBConnector dbConnector;
    String connectionResult="";
    boolean isSuccess = false;
    DatePickerDialog datePickerDialog;
    int selected_date;
    List<Reservation> reservationsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.librarian_bookclass_activity);
        Bundle bundle = getIntent().getExtras();
        userId = bundle.getString("userId");
        dbConnector = DBConnector.getInstance();
//        try {
//            userReservationsView();
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }

        // Spinner
        Spinner mySpinner = (Spinner)findViewById(R.id.spinner1);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(LibrarianBookClassActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.floors));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setPrompt("Select Floor");
        mySpinner.setAdapter(myAdapter);
        try {
            initiateReservations();
        } catch (SQLException | ParseException throwables) {
            throwables.printStackTrace();
        }
        Button show_start_time_dialog = (Button) LibrarianBookClassActivity.this.findViewById(R.id.show_start_time_dialog);
        show_start_time_dialog.setEnabled(false);
        Button btnReservations = (Button) LibrarianBookClassActivity.this.findViewById(R.id.btnReservations);
        btnReservations.setEnabled(false);


    }

    private void initiateReservations() throws SQLException, ParseException {
        System.out.println("in MyReservations()");
        ListView listview = (ListView) findViewById(R.id.listview);
        reservationsList =  getData();
        // user has no reservations yet
        if (reservationsList.isEmpty()){
            ListView lv = (ListView)findViewById(R.id.listview);
            emptyText = (TextView)findViewById(R.id.empty);
            emptyText.setText("You Have No Reservations");
            lv.setEmptyView(emptyText);
        }
        else {
            System.out.println("after userReservationsView()");
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
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onStop(){
        super.onStop();
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

        List<String> chosen_dates = new ArrayList();
        if (reservationsList != null){
            for (int i=0; i<reservationsList.size(); i++){
                String dateString = (reservationsList.get(i).reservationDate).toString();
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
            else if (reservationsList != null){
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
        datePickerDialog.show(LibrarianBookClassActivity.this.getFragmentManager(), "DatePickerDialog");
        Button show_start_time_dialog = (Button) LibrarianBookClassActivity.this.findViewById(R.id.show_start_time_dialog);
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
        Button show_date_dialog = (Button) LibrarianBookClassActivity.this.findViewById(R.id.show_date_dialog);
        int day = datePickerDialog.getSelectedDay().getDay();
        int month = datePickerDialog.getSelectedDay().getMonth() + 1;
        int year = datePickerDialog.getSelectedDay().getYear();
        int hour = hourOfDay;
        int min = minute;
        String time = String.format(Locale.getDefault(), "%02d:%02d", hour, min);
        timeText.setText(time);
    }

    public List<Reservation> getData() throws SQLException {
        String queryString = "SELECT * FROM Reservations WHERE userId = '" + userId + "'";
        ResultSet result = dbConnector.executeQuery(queryString);
        List<Reservation> userReservations = new ArrayList<>();
        String reservationId = "";
        List<Map<String, String>> data = new ArrayList();
        if (result != null) {
            try {
                while (result.next()) {
                    Map<String, String> dtname = new HashMap<String, String>();
                    Reservation currReservation = new Reservation(result.getInt("reservationId"),
                            result.getString("floor").charAt(0),
                            result.getString("tableId"),
                            result.getDate("reservationDate"),
                            result.getTime("startTime"),
                            result.getTime("endTime"), ReservedObjectType.classroom);
                    userReservations.add(currReservation);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            System.out.println("Before return data");
            //need to go over userReservations and get all user reservations details
        }
        return userReservations;
    }

    SimpleAdapter sa;
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void MyReservations(View view) throws SQLException {
        System.out.println("in MyReservations()");
        ListView listview = (ListView) findViewById(R.id.listview);
        System.out.println("after userReservationsView()");
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, reservationsList.stream().map(reservation -> reservation.toString()).collect(Collectors.toList())){
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

    public void SelectClassButton(View view) throws SQLException, ParseException {
        Spinner mySpinner = (Spinner)findViewById(R.id.spinner1);
        floor = String.valueOf(mySpinner.getSelectedItem());
        Intent intent = new Intent(LibrarianBookClassActivity.this, FloorActivityClassroom.class);
        char level = floor.charAt(0);
        String [] dateStringArr = dateText.getText().toString().split("/");
        String dateString = dateStringArr[2]+"-"+dateStringArr[0]+"-"+dateStringArr[1];
        String timeString = timeText.getText().toString()+":00";
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);
        bundle.putChar("level", level);
        bundle.putString("date",dateString);
        bundle.putString("startTime", timeString);
        bundle.putString("userType","librarian");
        intent.putExtras(bundle);
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