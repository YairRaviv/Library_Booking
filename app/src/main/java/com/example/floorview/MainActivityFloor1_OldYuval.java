package com.example.floorview;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivityFloor1_OldYuval extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.floor_a_layout);
    }

    protected void onStart() {
        super.onStart();
        updateViewBasedOnDB();
    }

    public void updateViewBasedOnDB(){
        for (int i = 1; i <= 23; i++) {
            String tableId = "floorA_table" + i;
            int resID = getResources().getIdentifier(tableId, "id", getPackageName());
            Button tableBtn = (Button) findViewById(resID);
            System.out.println(findViewById(resID));
            if (tableBtn != null) {
                tableBtn.setText("T"+i);
                int numTakenSeats = (int) (Math.random() * 8 + 1);
                if (numTakenSeats == 8) {
                    tableBtn.setBackground(ContextCompat.getDrawable(this.getBaseContext(), R.drawable.table_taken));
                } else {
                    tableBtn.setBackground(ContextCompat.getDrawable(this.getBaseContext(), R.drawable.table_available));
                }
            }
        }
    }
}