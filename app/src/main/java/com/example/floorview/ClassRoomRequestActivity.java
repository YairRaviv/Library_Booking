package com.example.floorview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClassRoomRequestActivity extends AppCompatActivity
{
    DBConnector dbConnector;
    String userId;
    String startTime;
    String endTime;
    String reservationDate;
    String classroomId;
    char floor;

    int reservationId;
    int NumOfStudents;
    String Faculty;
    String Department;
    String Reason;
    String PhoneNumber;

    EditText NumOfStudentsBox;
    EditText FacultyBox;
    EditText DepartmentBox;
    EditText ReasonBox;
    EditText PhoneNumberBox;

    Button Send;
    Button Cancel;
    private DatabaseReference RealTimeDB;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_room_request);

        RealTimeDB = FirebaseDatabase.getInstance().getReference();
        Spinner facultiesSpinner = (Spinner)findViewById(R.id.faculties_spinner);
        ArrayAdapter<String> facultiesAdapter = new ArrayAdapter<String>(ClassRoomRequestActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.faculties));
        facultiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //mySpinner.setPrompt("Select Faculty");
        facultiesSpinner.setAdapter(facultiesAdapter);

        Spinner ReasonsSpinner = (Spinner)findViewById(R.id.reasons_spinner);
        ArrayAdapter<String> ReasonsAdapter = new ArrayAdapter<String>(ClassRoomRequestActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.reasons));
        ReasonsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //mySpinner.setPrompt("Select Faculty");
        ReasonsSpinner.setAdapter(ReasonsAdapter);

        dbConnector = DBConnector.getInstance();
        //screen objects
        Send = (Button)findViewById(R.id.Send);
        Cancel = (Button)findViewById(R.id.Cancel);
        NumOfStudentsBox = (EditText)findViewById(R.id.NumOfStudentsBox);
        //FacultyBox = (EditText)findViewById(R.id.FacultyBox);
        DepartmentBox = (EditText)findViewById(R.id.DepartmentBox);
        //ReasonBox = (EditText)findViewById(R.id.ReasonBox);
        PhoneNumberBox = (EditText)findViewById(R.id.PhoneNumberBox);

        //Bundle data
        Bundle bundle = getIntent().getExtras();
        userId = bundle.getString("userId");
        startTime = bundle.getString("startTime");
        endTime = bundle.getString("endTime");
        reservationDate = bundle.getString("date");
        classroomId = bundle.getString("classroomId");
        floor = bundle.getChar("floor");



        Cancel.setOnClickListener(view ->
        {
            Intent intent = new Intent(ClassRoomRequestActivity.this , StudentMainActivity.class);
            intent.putExtra("userId",userId);
            startActivity(intent);
        });
        Send.setOnClickListener(view ->
        {
            // create the reservation and upload it to DB (to "Class Reservations" Table)
            // Floor.UploadClassReservationToDB(floor,classroomId,reservationDate,startTime,endTime,userId,"Didnt Arrived Yet" , "Pending");

            String UploadQuery = "INSERT INTO ClassReservations (`Floor`, `ClassRoomID`, `ReservationDate`, `StartTime`, `EndTime` , `UserID` , `Arrived` , `Status`) " +
                    "VALUES ('"+floor+"', '"+classroomId+"', '"+reservationDate+"', '"+startTime+"', '"+endTime+"' , '"+userId+"' , '"+"No"+"' , '"+"Pending"+"' );";
            dbConnector.executeUpdate(UploadQuery);


            //create the request and upload it to DB
            String RID_Query = "SELECT ReservationID from ClassReservations WHERE ClassRoomID = '" +classroomId+ "' AND ReservationDate = '" +reservationDate+"' AND UserID = '"+ userId+"'";
            ResultSet result = dbConnector.executeQuery(RID_Query);
            if(result!=null)
            {
                try
                {
                    while(result.next())
                    {
                        System.out.println("query result is : " + result);
                        reservationId = result.getInt("ReservationID");
                    }
                }
                catch (SQLException throwables)
                {
                    throwables.printStackTrace();
                }
            }
            //extract student id (the real one) from FireBase
//            final String[] StudentID = new String[1];
//            RealTimeDB.child("Users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
//            {
//                @Override
//                public void onComplete(@NonNull Task<DataSnapshot> task)
//                {
//                    if (!task.isSuccessful())
//                    {
//                        Log.e("firebase", "Error getting data", task.getException());
//                    }
//                    else
//                    {
//                        User current_user =  task.getResult().getValue(User.class);
//                        assert current_user != null;
//                        StudentID[0] = current_user._id;
//                    }
//                }
//            });


            Faculty = facultiesSpinner.getSelectedItem().toString();
            Department = DepartmentBox.getText().toString();
            Reason = ReasonsSpinner.getSelectedItem().toString();
            PhoneNumber = PhoneNumberBox.getText().toString();
            NumOfStudents = Integer.parseInt(NumOfStudentsBox.getText().toString());
//            String InsertRequest_Query = "INSERT INTO ClassRequests (`ReservationID`, `StudentID`, `NumOfStudents`, `Faculty`, `Department`, `Reason` , `PhoneNumber`) " +
//                    "VALUES ('" +reservationId+"', '"+StudentID[0]+"', '"+NumOfStudents+"', '"+Faculty+"', '"+Department+"', '"+Reason+"' , '"+PhoneNumber+"' );";
            String InsertRequest_Query = "INSERT INTO ClassRequests (`ReservationID`, `StudentID`, `NumOfStudents`, `Faculty`, `Department`, `Reason` , `PhoneNumber`) " +
                    "VALUES ('" +reservationId+"', '"+userId+"', '"+NumOfStudents+"', '"+Faculty+"', '"+Department+"', '"+Reason+"' , '"+PhoneNumber+"' );";
            dbConnector.executeUpdate(InsertRequest_Query);
            Intent intent = new Intent(ClassRoomRequestActivity.this , StudentMainActivity.class);
            intent.putExtra("userId",userId);
            startActivity(intent);
        });
    }
}