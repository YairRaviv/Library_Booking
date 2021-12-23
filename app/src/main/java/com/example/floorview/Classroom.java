package com.example.floorview;

import java.sql.Time;

public class Classroom extends ReservableObject {


    public Classroom(String id, Time maxTimeToBook, Time startTime, String name) {
        super(id, maxTimeToBook, startTime, name);
    }

    public Classroom(String id, Time maxTimeToBook, Time startTime) {
        super(id, maxTimeToBook, startTime);
    }


    @Override
    void addReservation(Reservation reservation) {

    }
}
