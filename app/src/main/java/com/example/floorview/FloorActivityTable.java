package com.example.floorview;


import android.app.TimePickerDialog;
import android.content.Intent;
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
import androidx.core.content.ContextCompat;

import java.sql.Time;
import java.util.HashMap;

public class FloorActivityTable extends AppCompatActivity {
    FloorState floorState;
    char levelChar;
    String startTime;
    String date;
    int contentViewId;
    String clickedTableId;
    Time selectedEndTime;
    TextView timeText;
    String userId;
    ReservedObjectType reservedObjectType;
    final LoadingBar loadingBar = new LoadingBar(FloorActivityTable.this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        levelChar = bundle.getChar("level");
        date = bundle.getString("date");
        userId = bundle.getString("userId");
        startTime = bundle.getString("startTime");
        reservedObjectType = ReservedObjectType.table;
        floorState = new FloorState(levelChar, date, startTime, reservedObjectType);
        setContentViewIdByLevel();
        setContentView(contentViewId);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initiateViewDataOnStart();
    }


    private void setContentViewIdByLevel() {
        switch (levelChar){
            case 'A':
                contentViewId=R.layout.floor_a_layout_table;
                break;
            case 'B':
                contentViewId=R.layout.floor_b_layout_table;
                break;
            case 'C':
                contentViewId=R.layout.floor_c_layout_table;
                break;
            default:
                contentViewId=R.layout.floor_d_layout_table;
                break;
        }
    }

    private void initiateViewDataOnStart(){
//        HashMap<String, ReservableObject> floorTablesState = floorState.getFloorState();
        AsyncTasksWrapper.ExtractDataFromDbTask extractDataFromDbTask = new AsyncTasksWrapper.ExtractDataFromDbTask(FloorActivityTable.this, loadingBar, floorState);
        extractDataFromDbTask.setListener(new AsyncTasksWrapper.ExtractDataFromDbTask.AsyncTaskListener(){
            @Override
            public void onAsyncTaskFinished(HashMap<String, ReservableObject> result) {
                updateViewComponents(result);
            }
        });
        extractDataFromDbTask.execute();
    }
    private void updateViewComponents(HashMap<String, ReservableObject>  floorTablesState) {
        for(int i = 1; i<= floorState.getNumRelevantObjectInFloor(); i++){
            String tableId = "floor"+ floorState.floor +"_table"+i;
            int tableResourceId = getResources().getIdentifier(tableId, "id", getPackageName());
            Button tableUiObject = (Button) findViewById(tableResourceId);
            if(tableUiObject != null) {
                Table table = (Table) floorTablesState.get(tableId);
                if(table != null) {
                    if (table.status == ReservableObjectStatus.available) {
                        tableUiObject.setBackground(ContextCompat.getDrawable(this.getBaseContext(), R.drawable.table_available));
                    } else {
                        tableUiObject.setBackground(ContextCompat.getDrawable(this.getBaseContext(), R.drawable.table_taken));
                    }
                    table.setName("T" + i);
                    tableUiObject.setText(table.name);
                }
                else{
                    tableUiObject.setBackground(ContextCompat.getDrawable(this.getBaseContext(), R.drawable.table_available));
                    Table newTable = (Table) floorState.addUnreservedObject(tableId, i);
                    tableUiObject.setText(newTable.name);
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
                        Table clickedTable = (Table) floorState.getUpdatedObjectState(clickedTableId);
                        Time selectedTime = Time.valueOf((hour>=10? hour: "0"+hour)+":"+(minute>=10? minute: "0"+minute)+":00");
                        if(selectedTime.after(clickedTable.maxTimeToBook)){
                            Toast.makeText(FloorActivityTable.this, "Can't select time after "+clickedTable.maxTimeToBook, Toast.LENGTH_SHORT).show();
                        }
                        else if(selectedTime.before(Time.valueOf(startTime))){
                            Toast.makeText(FloorActivityTable.this, "Can't select time before "+startTime, Toast.LENGTH_SHORT).show();
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



    public void tableClick(View view){
        String clickedTableFullId = view.getResources().getResourceName(view.getId());
        //TODO: Here we should check if the table is still available in the DB
        int startIndexOfId = clickedTableFullId.lastIndexOf('/')+1;
        clickedTableId = clickedTableFullId.substring(startIndexOfId);
        Table clickedTable = (Table) floorState.floorState.get(clickedTableId);
        System.out.println("CLICK");
        if(clickedTable.status == ReservableObjectStatus.available){
            showTableSelectionPopup(clickedTable);
        }
        else{
            Toast.makeText(this, "Selected table is not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void showTableSelectionPopup(Table clickedTable) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View selectionPopupView = getLayoutInflater().inflate(R.layout.table_selection_popup,null);
        Button bookSpotBtn = selectionPopupView.findViewById(R.id.bookBtn);
        Button closeSelectionPopupBtn = selectionPopupView.findViewById(R.id.closeSelectionPopupBtn);
        TextView tableName = selectionPopupView.findViewById(R.id.tableName);
        TextView tableDescription = selectionPopupView.findViewById(R.id.tableDescription);
        tableName.setText("Table: "+clickedTable.name);
        tableDescription.setText(clickedTable.description);
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
                    Toast.makeText(FloorActivityTable.this, "Missing valid end time selection", Toast.LENGTH_SHORT).show();
                }
                else{
                    AsyncTasksWrapper.ExecuteTableUpdateTask executeTableUpdateTask = new AsyncTasksWrapper.ExecuteTableUpdateTask(FloorActivityTable.this,
                            loadingBar, floorState, clickedTableId, selectedEndTime, userId);
                    executeTableUpdateTask.setListener(new AsyncTasksWrapper.ExecuteTableUpdateTask.AsyncTaskListener(){
                        @Override
                        public void onAsyncTaskFinished(Boolean result) {
                            if(!result){
                                initiateViewDataOnStart();
                                Toast.makeText(FloorActivityTable.this, "Selected table state has changed, please select again", Toast.LENGTH_LONG).show();
                                selectionPopupDialog.dismiss();
                            }
                            else {
                                Intent intent = new Intent(FloorActivityTable.this, StudentMainActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("userId", userId);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                selectionPopupDialog.dismiss();

                            }
                        }
                    });
                    executeTableUpdateTask.execute();
                }
            }
        });


    }

}
