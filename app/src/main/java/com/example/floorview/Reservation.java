package com.example.floorview;

import java.sql.Date;
import java.sql.Time;

public class Reservation implements Comparable<Reservation> {
    int reservationId;
    char floor;
    String reservedObjectId;
    Date reservationDate;
    Time startTime;
    Time endTime;
    String status;
    ReservedObjectType reservedObjectType;

    public Reservation(int reservationId, char floor, String reservedObjectId, Date reservationDate, Time startTime, Time endTime, ReservedObjectType reservedObjectType){
        this.reservationId = reservationId;
        this.floor = floor;
        this.reservedObjectId = reservedObjectId;
        this.reservationDate = reservationDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reservedObjectType = reservedObjectType;
    }

    public Reservation(int reservationId, char floor, String reservedObjectId, Date reservationDate, Time startTime, Time endTime, String status, ReservedObjectType reservedObjectType){
        this.reservationId = reservationId;
        this.floor = floor;
        this.reservedObjectId = reservedObjectId;
        this.reservationDate = reservationDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.reservedObjectType = reservedObjectType;
    }

    public Reservation(char floor, String reservedObjectId, Date reservationDate, Time startTime, Time endTime, ReservedObjectType reservedObjectType){
        this.reservationId = -1;
        this.floor = floor;
        this.reservedObjectId = reservedObjectId;
        this.reservationDate = reservationDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reservedObjectType = reservedObjectType;
    }

    public int getReservationid(){
        return reservationId;
    }

    @Override
    public int compareTo(Reservation o) {
        return Integer.compare(reservationId, o.reservationId);
    }
    @Override
    public boolean equals(Object resObj) {
        Reservation other = (Reservation) resObj;
        return reservationId == other.reservationId && reservedObjectType == other.reservedObjectType && reservedObjectId.equals(other.reservedObjectId);
    }

    public String toString(){
        String reservedObjectNum = reservedObjectId.substring(12);
        if (status == null) {
            return "Reservation " + this.reservationId + "\n" +
                    "Floor " + this.floor + "\n" +
                    (reservedObjectType == ReservedObjectType.table ? "Table" : "Classroom") + reservedObjectNum + "\n" +
                    "Date " + this.reservationDate + "\n" +
                    "Start " + this.startTime + "\n" +
                    "End " + this.endTime;
        }
        return "Reservation " + this.reservationId + "\n" +
                "Floor " + this.floor + "\n" +
                (reservedObjectType == ReservedObjectType.table ? "Table" : "Classroom") + reservedObjectNum + "\n" +
                "Date " + this.reservationDate + "\n" +
                "Start " + this.startTime + "\n" +
                "End " + this.endTime + "\n" +
                "Status " + this.status;
    }
}
