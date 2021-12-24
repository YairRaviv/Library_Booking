package com.example.floorview;

import java.sql.Time;

public class Classroom extends ReservableObject {


    public Classroom(String id, Time maxTimeToBook, Time startTime, int nameIndex) {
        super(id, maxTimeToBook, startTime, "C"+nameIndex);
        updateDescription();
    }

    public Classroom(String id, Time maxTimeToBook, Time startTime) {
        super(id, maxTimeToBook, startTime);
        updateDescription();
    }


    @Override
    void addReservation(Reservation reservation) {
        Time resStartTime = reservation.startTime;
        if (resStartTime.compareTo(startTime) > 0) {
            if(maxTimeToBook == null){
                maxTimeToBook = resStartTime;
            }
            else if (maxTimeToBook.compareTo(resStartTime) > 0){
                maxTimeToBook = resStartTime;
            }

        }
        else{
            status = ReservableObjectStatus.notAvailable;
        }
        updateDescription();
    }

    protected void updateDescription() {
        if(status == ReservableObjectStatus.available){
            description = "Classroom "+name+" is available till "+maxTimeToBook.toString();
        }
        else{
            description = "Classroom "+name+" is not available";
        }
    }
}
