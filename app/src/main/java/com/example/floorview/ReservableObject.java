package com.example.floorview;

import java.sql.Time;
import java.util.ArrayList;

public abstract class ReservableObject {

    public String id;
    public String name;
    public Time maxTimeToBook;
    public Time startTime;
    public String description;
    public ReservableObjectStatus status;
    protected ArrayList<Reservation> reservations;

    public ReservableObject(String id, Time maxTimeToBook, Time startTime){
        this.reservations = new ArrayList<>();
        this.id = id;
        this.maxTimeToBook = maxTimeToBook;
        this.startTime = startTime;
        this.status = ReservableObjectStatus.available;
    }

    public ReservableObject(String id, Time maxTimeToBook, Time startTime, String name){
        this.reservations = new ArrayList<>();
        this.id = id;
        this.maxTimeToBook = maxTimeToBook;
        this.startTime = startTime;
        this.name = name;
        this.status = ReservableObjectStatus.available;
    }

    abstract void addReservation(Reservation reservation);
}
