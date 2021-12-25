package com.example.floorview;


import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class FloorActivityClassroom extends AppCompatActivity {
    FloorState floorState;
    char levelChar;
    String startTime;
    String date;
    int contentViewId;
    String clickedClassroomId;
    Time selectedEndTime;
    TextView timeText;
    String userId;
    ReservedObjectType reservedObjectType;
    UserType userType;
    ArrayList<Reservation> selectedClassroomsReservations;
    final LoadingBar loadingBar = new LoadingBar(FloorActivityClassroom.this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        levelChar = bundle.getChar("level");
        date = bundle.getString("date");
        userId = bundle.getString("userId");
        startTime = bundle.getString("startTime");
        userType = UserType.valueOf(bundle.getString("userType"));
        reservedObjectType = ReservedObjectType.classroom;
        selectedClassroomsReservations = new ArrayList<Reservation>();
        floorState = new FloorState(levelChar, date, startTime, reservedObjectType);
        setContentViewIdByLevel();
        setContentView(contentViewId);
        setSelectMultipleClassroomsButtonVisibility();
    }

    private void setSelectMultipleClassroomsButtonVisibility() {
        Button bookAllSelectedBtn = (Button) findViewById(R.id.bookAllBtn);
        if(userType == UserType.student){
            bookAllSelectedBtn.setVisibility(View.GONE);
        }
        else{
            bookAllSelectedBtn.setVisibility(View.VISIBLE);
            bookAllSelectedBtn.setEnabled(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initiateViewDataOnStart();
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                loadingBar.dismiss();
//            }
//        }, 3000);
    }


    private void setContentViewIdByLevel() {
        switch (levelChar){
            case 'A':
                contentViewId=R.layout.floor_a_layout_classroom;
                break;
            case 'B':
                contentViewId=R.layout.floor_b_layout_classroom;
                break;
            case 'C':
                contentViewId=R.layout.floor_c_layout_classroom;
                break;
            default:
                contentViewId=R.layout.floor_d_layout_classroom;
                break;
        }
    }

    private void initiateViewDataOnStart(){
        System.out.println("**************************************************************");
        selectedClassroomsReservations.clear();
        AsyncTasksWrapper.ExtractDataFromDbTask extractDataFromDbTask = new AsyncTasksWrapper.ExtractDataFromDbTask(FloorActivityClassroom.this, loadingBar, floorState);
        extractDataFromDbTask.setListener(new AsyncTasksWrapper.ExtractDataFromDbTask.AsyncTaskListener(){
            @Override
            public void onAsyncTaskFinished(HashMap<String, ReservableObject> result) {
                updateViewComponents(result);
            }
        });
        extractDataFromDbTask.execute();
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
    }

    private void updateViewComponents(HashMap<String, ReservableObject>  floorClassroomsState) {
        for(int i = 1; i<= floorState.getNumRelevantObjectInFloor(); i++){
            String classroomId = "floor"+ floorState.floor +"_classroom"+i;
            int classroomResourceId = getResources().getIdentifier(classroomId, "id", getPackageName());
            Button classroomUiObject = (Button) findViewById(classroomResourceId);
            if(classroomUiObject != null) {
                Classroom classroom = (Classroom) floorClassroomsState.get(classroomId);
                if(classroom != null) {
                    if (classroom.status == ReservableObjectStatus.available) {
                        classroomUiObject.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_blue)));;
                    } else {
                        classroomUiObject.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_gray)));
                    }
                    classroom.setName("C" + i);
                    classroomUiObject.setText(classroom.name);
                }
                else{
                    classroomUiObject.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_blue)));;
                    Classroom newClassroom = (Classroom) floorState.addUnreservedObject(classroomId, i);
                    classroomUiObject.setText(newClassroom.name);
                }
            }
        }
    }

    private void showtimePickerDialog(){
        int hour = Integer.parseInt(startTime.split(":")[0]);
        int minute = Integer.parseInt(startTime.split(":")[1]);
        int style = android.app.AlertDialog.THEME_HOLO_DARK;
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                style,
                new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hour, int minute) {
                        Classroom clickedClassroom = (Classroom) floorState.getUpdatedObjectState(clickedClassroomId);
                        Time selectedTime = Time.valueOf((hour>=10? hour: "0"+hour)+":"+(minute>=10? minute: "0"+minute)+":00");
                        if(selectedTime.after(clickedClassroom.maxTimeToBook)){
                            Toast.makeText(FloorActivityClassroom.this, "Can't select time after "+clickedClassroom.maxTimeToBook, Toast.LENGTH_SHORT).show();
                        }
                        else if(selectedTime.before(Time.valueOf(startTime))){
                            Toast.makeText(FloorActivityClassroom.this, "Can't select time before "+startTime, Toast.LENGTH_SHORT).show();
                        }
                        else{
                            selectedEndTime = selectedTime;
                            timeText.setText((hour>=10? hour: "0"+hour)+":"+(minute>=10? minute: "0"+minute));
                        }

                    }},
                hour,
                minute,
                true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    public void SelectEndTimeButton(View view) {
        showtimePickerDialog();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void bookAllClick(View view){
        if(userType == UserType.librarian){
            if(selectedClassroomsReservations.isEmpty()){
                Toast.makeText(this, "No classrooms selected", Toast.LENGTH_SHORT).show();
            }
            else{
                AsyncTasksWrapper.ExecuteClassroomsUpdateTask executeClassroomsUpdateTask = new AsyncTasksWrapper.ExecuteClassroomsUpdateTask(FloorActivityClassroom.this, loadingBar, floorState, selectedClassroomsReservations, userId);
                executeClassroomsUpdateTask.setListener(new AsyncTasksWrapper.ExecuteClassroomsUpdateTask.AsyncTaskListener(){
                        @Override
                        public void onAsyncTaskFinished(Boolean result) {
                            if(!result){
                                initiateViewDataOnStart();
                                Toast.makeText(FloorActivityClassroom.this, "Selected "+(selectedClassroomsReservations.size()>1 ? "classrooms" : "classroom")+
                                        "state has changed, please select again", Toast.LENGTH_LONG).show();
                                return;
                            }
                            else {
                                Intent intent = new Intent(FloorActivityClassroom.this, userType == UserType.student ? StudentMainActivity.class : LibrarianMainActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("userId", userId);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        }
                    });
                executeClassroomsUpdateTask.execute();

            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void classroomClick(View view){
        String clickedClassroomFullId = view.getResources().getResourceName(view.getId());
        //TODO: Here we should check if the table is still available in the DB
        int startIndexOfId = clickedClassroomFullId.lastIndexOf('/')+1;
        clickedClassroomId = clickedClassroomFullId.substring(startIndexOfId);
        Optional<Reservation> clickedClassRoomOptionalReservation = selectedClassroomsReservations.stream().filter(reservation -> reservation.reservedObjectId.equals(clickedClassroomId)).findFirst();
        if(clickedClassRoomOptionalReservation.isPresent()){
            Reservation clickedClassRoomReservation = clickedClassRoomOptionalReservation.get();
            selectedClassroomsReservations.remove(clickedClassRoomReservation);
            int classroomResourceId = getResources().getIdentifier(clickedClassroomId, "id", getPackageName());
            Button classroomUiObject = (Button) findViewById(classroomResourceId);
            classroomUiObject.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_blue)));;
            if(selectedClassroomsReservations.isEmpty()){
                Button bookAllSelectedBtn = (Button) findViewById(R.id.bookAllBtn);
                bookAllSelectedBtn.setEnabled(false);
            }
        }
        else {
            Classroom clickedClassroom = (Classroom) floorState.floorState.get(clickedClassroomId);
            System.out.println("CLICK");
            if (clickedClassroom.status == ReservableObjectStatus.available) {
                showClassroomSelectionPopup(clickedClassroom);
            } else {
                Toast.makeText(this, "Selected classroom is not available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showClassroomSelectionPopup(Classroom clickedClassroom) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View selectionPopupView = getLayoutInflater().inflate(R.layout.classroom_selection_popup,null);
        Button bookSpotBtn = selectionPopupView.findViewById(R.id.bookBtn);
        Button closeSelectionPopupBtn = selectionPopupView.findViewById(R.id.closeSelectionPopupBtn);
        TextView classroomName = selectionPopupView.findViewById(R.id.classroomName);
        TextView classroomDescription = selectionPopupView.findViewById(R.id.classroomDescription);
        classroomName.setText("Classroom: "+clickedClassroom.name);
        classroomDescription.setText(clickedClassroom.description);
        dialogBuilder.setView(selectionPopupView);
        AlertDialog selectionPopupDialog = dialogBuilder.create();
        selectionPopupDialog.show();
        timeText = selectionPopupView.findViewById(R.id.pickedTime);
        closeSelectionPopupBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                selectedEndTime=null;
                selectionPopupDialog.dismiss();
            }
        });
        bookSpotBtn.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if(selectedEndTime == null){
                    Toast.makeText(FloorActivityClassroom.this, "Missing valid end time selection", Toast.LENGTH_SHORT).show();
                }
                else{
                    try {
                        selectedClassroomsReservations.add(new Reservation(floorState.floor, clickedClassroomId, Date.valueOf(date),
                                Time.valueOf(startTime), selectedEndTime, reservedObjectType));
                        if(userType == UserType.student) {
                            Intent intent = new Intent(FloorActivityClassroom.this, ClassRoomRequestActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString( "userId", userId);
                            bundle.putString( "classroomId", clickedClassroomId);
                            bundle.putString( "startTime", startTime);
                            bundle.putString( "endTime", selectedEndTime.toString());
                            bundle.putString( "date", date);
                            bundle.putChar( "floor", floorState.floor);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        else{
                            int classroomResourceId = getResources().getIdentifier(clickedClassroomId, "id", getPackageName());
                            Button classroomUiObject = (Button) findViewById(classroomResourceId);
                            classroomUiObject.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_green)));;
                            Button bookAllSelectedBtn = (Button) findViewById(R.id.bookAllBtn);
                            bookAllSelectedBtn.setEnabled(true);
                        }
                    } catch (Exception e) {
                        initiateViewDataOnStart();
                        Toast.makeText(FloorActivityClassroom.this, "Selected classroom state has changed, please select again", Toast.LENGTH_LONG).show();
                    }
                    finally {
                        selectionPopupDialog.dismiss();
                    }
                }
            }
        });
    }

}
