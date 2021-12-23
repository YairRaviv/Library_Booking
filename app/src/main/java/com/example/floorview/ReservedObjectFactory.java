package com.example.floorview;

import java.sql.Time;

public class ReservedObjectFactory {



    public static ReservableObject createReservedObject(String id, Time maxTimeToBook, Time startTime, String name, ReservedObjectType reservedObjectType){
        if(reservedObjectType == ReservedObjectType.table){
            return new Table(id, maxTimeToBook, startTime, name);
        }
        else {
            return new Classroom(id, maxTimeToBook, startTime, name);
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
