package com.example.floorview;

import android.content.Context;
import android.widget.Button;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;

public class Table {

    public ArrayList<Reservation> tableReservations;
    public String tableId;
    public String tableName;
    public int numSeats;
    public int numFreeSeats;
    public Time maxTimeToBook;
    public Time startTime;
    public int numReservationsAfterStart;
    String description;
    TableStatus status;

    public Table(String tableId, int numSeats, Time maxTimeToBook, Time startTime) {
        tableReservations = new ArrayList<>();
        this.tableId = tableId;
        this.numSeats = numSeats;
        this.numFreeSeats = numSeats;
        this.numReservationsAfterStart = numSeats;
        this.status = TableStatus.available;
        this.maxTimeToBook = maxTimeToBook;
        this.startTime = startTime;
        updateDescription();
    }

    public Table(String tableId, int numSeats, Time maxTimeToBook, Time startTime, String tableName) {
        tableReservations = new ArrayList<>();
        this.tableId = tableId;
        this.numSeats = numSeats;
        this.numFreeSeats = numSeats;
        this.status = TableStatus.available;
        this.maxTimeToBook = maxTimeToBook;
        this.tableName = tableName;
        this.numReservationsAfterStart = numSeats;
        this.startTime = startTime;
        updateDescription();
    }


    private void updateDescription() {
        if(numFreeSeats == 0){
            status = TableStatus.notAvailable;
            description = "Table ID: " + tableId + "\nFully Booked";
        }
        else{
            status = TableStatus.available;
            description = numFreeSeats + " Available Seats Left\nCan Be Booked Till: " + maxTimeToBook.toString();
        }
    }

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
            tableReservations.add(reservation);
        }
        else{
            tableReservations.add(reservation);
            maxTimeToBook = tableReservations.get(0).startTime;
            for(int i=1; i<tableReservations.size(); i++){
                Time tableReservationsStartTime = tableReservations.get(i).startTime;
                if(tableReservationsStartTime.compareTo(maxTimeToBook)>0){
                    maxTimeToBook = tableReservationsStartTime;
                }
            }
        }

    }





}
