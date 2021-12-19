package com.example.floorview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity_OldYuval extends AppCompatActivity {

    Floor floor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    protected void onStart() {
        super.onStart();
        Intent floorLayoutActivity = new Intent(MainActivity_OldYuval.this, FloorActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userId", "829189");
        bundle.putChar("level", 'D');
        bundle.putString("date", "2021-12-07");
        bundle.putString("startTime", "14:30:00");
        floorLayoutActivity.putExtras(bundle);
        startActivity(floorLayoutActivity);
//        for (int i = 1; i <= 23; i++) {
//                String tableId = "floorA_table" + i;
//                int resID = getResources().getIdentifier(tableId, "id", getPackageName());
//                Button tableBtn = (Button) findViewById(resID);
//                System.out.println(findViewById(resID));
//                if (tableBtn != null) {
//                    tableBtn.setText("T"+i);
//                    int numTakenSeats = (int) (Math.random() * 8 + 1);
//                    if (numTakenSeats == 8) {
//                        tableBtn.setBackground(ContextCompat.getDrawable(this.getBaseContext(), R.drawable.table_taken));
//                    } else {
//                        tableBtn.setBackground(ContextCompat.getDrawable(this.getBaseContext(), R.drawable.table_available));
//                    }
//                }
//        }
    }
}