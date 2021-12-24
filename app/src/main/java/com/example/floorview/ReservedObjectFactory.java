package com.example.floorview;

import java.sql.Time;

public class ReservedObjectFactory {



    public static ReservableObject createReservedObject(String id, Time maxTimeToBook, Time startTime, int nameIndex, ReservedObjectType reservedObjectType){
        if(reservedObjectType == ReservedObjectType.table){
            return new Table(id, maxTimeToBook, startTime, nameIndex);
        }
        else {
            return new Classroom(id, maxTimeToBook, startTime, nameIndex);
        }
    }
    public static ReservableObject createReservedObject(String id, Time maxTimeToBook, Time startTime, ReservedObjectType reservedObjectType){
        if(reservedObjectType == ReservedObjectType.table){
            return new Table(id, maxTimeToBook, startTime);
        }
        else {
            return new Classroom(id, maxTimeToBook, startTime);
        }
    }
}
