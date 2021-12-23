package com.example.floorview;

import java.util.HashMap;

public class Floor {
    private char floor;
    private int numTablesInFloor;
    private int numClassroomsInFloor;




    private void setNumTableInFloor() {
        switch (floor){
            case 'A':
            case 'B':
                numTablesInFloor=23;
                break;
            case 'C':
                numTablesInFloor=24;
                break;
            default:
                numTablesInFloor=28;
                break;
        }
    }

    private void setNumClassroomsInFloor() {
        switch (floor){
            case 'A':
                numClassroomsInFloor=2;
                break;
            case 'B':
                numClassroomsInFloor=3;
                break;
            case 'C':
                numTablesInFloor=4;
                break;
            default:
                numTablesInFloor=5;
                break;
        }
    }
}
