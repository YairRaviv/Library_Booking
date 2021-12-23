package com.example.floorview;


import android.app.ProgressDialog;
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

public class FloorActivity extends AppCompatActivity {
    Floor floor;
    char levelChar;
    String startTime;
    String date;
    int contentViewId;
    String clickedTableId;
    Time selectedEndTime;
    TextView timeText;
    String userId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        levelChar = bundle.getChar("level");
        date = bundle.getString("date");
        userId = bundle.getString("userId");
        startTime = bundle.getString("startTime");
        floor = new Floor(levelChar, date, startTime);
        setContentViewIdByLevel();
        setContentView(contentViewId);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initiateViewDataOnStart();
//        showSelectionInstructions();
    }

//    private void showSelectionInstructions() {
//        new Thread()
//        {
//            public void run() {
//                TextView instructionsView = findViewById(R.id.instructions);
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                instructionsView.setVisibility(View.INVISIBLE);
//            }
//        }.start();
//
//    }


    private void setContentViewIdByLevel() {
        switch (levelChar){
            case 'A':
                contentViewId=R.layout.floor_a_layout;
                break;
            case 'B':
                contentViewId=R.layout.floor_b_layout;
                break;
            case 'C':
                contentViewId=R.layout.floor_c_layout;
                break;
            default:
                contentViewId=R.layout.floor_d_layout;
                break;
        }
    }

    private void initiateViewDataOnStart(){
        HashMap<String, Table> floorTablesState = floor.getFloorState();
        for(int i=1; i<=floor.numTablesInFloor; i++){
            String tableId = "floor"+floor.floorLevel+"_table"+i;
            int tableResourceId = getResources().getIdentifier(tableId, "id", getPackageName());
            Button tableUiObject = (Button) findViewById(tableResourceId);
            if(tableUiObject != null) {
                Table table = floorTablesState.get(tableId);
                if(table != null) {
                    if (table.status == TableStatus.available) {
                        tableUiObject.setBackground(ContextCompat.getDrawable(this.getBaseContext(), R.drawable.table_available));
                    } else {
                        tableUiObject.setBackground(ContextCompat.getDrawable(this.getBaseContext(), R.drawable.table_taken));
                    }
                    table.tableName = "T" + i;
                    tableUiObject.setText(table.tableName);
                }
                else{
                    tableUiObject.setBackground(ContextCompat.getDrawable(this.getBaseContext(), R.drawable.table_available));
                    Table newTable = floor.addUnreservedTable(tableId, i);
                    tableUiObject.setText(newTable.tableName);
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
                        Table clickedTable = floor.getUpdatedTableState(clickedTableId);
                        Time selectedTime = Time.valueOf((hour>=10? hour: "0"+hour)+":"+(minute>=10? minute: "0"+minute)+":00");
                        if(selectedTime.after(clickedTable.maxTimeToBook)){
                            Toast.makeText(FloorActivity.this, "Can't select time after "+clickedTable.maxTimeToBook, Toast.LENGTH_SHORT).show();
                        }
                        else if(selectedTime.before(Time.valueOf(startTime))){
                            Toast.makeText(FloorActivity.this, "Can't select time before "+startTime, Toast.LENGTH_SHORT).show();
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
        Table clickedTable = floor.floorState.get(clickedTableId);
        System.out.println("CLICK");
        if(clickedTable.status == TableStatus.available){
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
        tableName.setText("Table: "+clickedTable.tableName);
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
                    Toast.makeText(FloorActivity.this, "Missing valid end time selection", Toast.LENGTH_SHORT).show();
                }
                else{
                    try {
                        floor.addReservationToDB(clickedTableId, selectedEndTime, userId);
                        Intent intent = new Intent(FloorActivity.this, StudentBookChairActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("id", userId);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } catch (Exception e) {
                        initiateViewDataOnStart();
                        Toast.makeText(FloorActivity.this, "Selected table state has changed, please select again", Toast.LENGTH_LONG).show();
                    }
                    finally {
                        selectionPopupDialog.dismiss();
                    }
                }
            }
        });


    }

}
