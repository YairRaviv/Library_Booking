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
    public String toString(){
        String table_exact_number = tableId.substring(12,tableId.length());
        return "Reservation "+this.reservationId+"\n"+
                "Floor "+this.floor+"\n"+
                "Table "+table_exact_number+"\n"+
                "Date "+this.reservationDate+"\n"+
                "Start "+this.startTime+"\n"+
                "End "+this.endTime;
    }
}
