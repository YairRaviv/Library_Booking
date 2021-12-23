package com.example.floorview;

import java.sql.Time;
import java.util.ArrayList;

public class Table extends ReservableObject {

    private final int NUM_SEATS = 8;
    private int numFreeSeats;
    private int numReservationsAfterStart;

    public Table(String id, Time maxTimeToBook, Time startTime) {
        super(id, maxTimeToBook, startTime);
        this.numFreeSeats = NUM_SEATS;
        this.numReservationsAfterStart = NUM_SEATS;
        updateDescription();
    }

    public Table(String id, Time maxTimeToBook, Time startTime, String name) {
        super(id, maxTimeToBook, startTime, name);
        this.numFreeSeats = NUM_SEATS;
        this.numReservationsAfterStart = NUM_SEATS;
        updateDescription();
    }


    @Override
    public void addReservation(Reservation reservation) {
        Time resStartTime = reservation.startTime;
        if (resStartTime.compareTo(startTime) > 0) {
            numReservationsAfterStart++;
            addReservationStartAfter(reservation);
        }
        else{
            numFreeSeats--;
        }
        updateDescription();
    }


    private void addReservationStartAfter(Reservation reservation){
        if(numReservationsAfterStart < numFreeSeats){
            reservations.add(reservation);
        }
        else{
            reservations.add(reservation);
            maxTimeToBook = reservations.get(0).startTime;
            for(int i = 1; i< reservations.size(); i++){
                Time tableReservationsStartTime = reservations.get(i).startTime;
                if(tableReservationsStartTime.compareTo(maxTimeToBook)>0){
                    maxTimeToBook = tableReservationsStartTime;
                }
            }
        }

    }

    private void updateDescription() {
        if(numFreeSeats == 0){
            status = ReservableObjectStatus.notAvailable;
            description = "Table ID: " + id + "\nFully Booked";
        }
        else{
            status = ReservableObjectStatus.available;
            description = numFreeSeats + " Available Seats Left\nCan Be Booked Till: " + maxTimeToBook.toString();
        }
    }





}
