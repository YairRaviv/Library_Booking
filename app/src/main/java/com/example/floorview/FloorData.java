package com.example.floorview;

import java.util.HashMap;

public class FloorData {
    private char floor;
    private int numTablesInFloor;
    private int numClassroomsInFloor;

    public FloorData(char floor){
        this.floor = floor;
        setNumTableInFloor();
        setNumClassroomsInFloor();

    }


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
            case 'B':
                numClassroomsInFloor=2;
                break;
            case 'C':
                numClassroomsInFloor=3;
                break;
            default:
                numClassroomsInFloor=4;
                break;
        }
    }

    public int getNumRelevantObjectsInFloor(ReservedObjectType reservedObjectType) {
        if(reservedObjectType == ReservedObjectType.table){
            return numTablesInFloor;
        }
        else{
            return numClassroomsInFloor;
        }

    }
}
