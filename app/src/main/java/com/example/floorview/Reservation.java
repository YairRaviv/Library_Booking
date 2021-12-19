package com.example.floorview;

import java.sql.Date;
import java.sql.Time;

public class Reservation implements Comparable<Reservation> {
    int reservationId;
    char floor;
    String tableId;
    Date reservationDate;
    Time startTime;
    Time endTime;

    public Reservation(int reservationId, char floor, String tableId, Date reservationDate, Time startTime, Time endTime){
        this.reservationId = reservationId;
        this.floor = floor;
        this.tableId = tableId;
        this.reservationDate = reservationDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId(){
        return reservationId;
    }

    @Override
    public int compareTo(Reservation o) {
        return Integer.compare(reservationId, o.reservationId);
    }
    @Override
    public boolean equals(Object resObj) {
        Reservation other = (Reservation) resObj;
        return reservationId == other.reservationId;
    }
}
