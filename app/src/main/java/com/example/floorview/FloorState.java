package com.example.floorview;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class FloorState {
    final Time ABSOLUTE_MAX_RESERVATION_TIME = Time.valueOf("20:00:00");
    final int ABSOLUTE_MAX_RESERVATION_DURATION = 6;

    private ArrayList<Reservation> reservationsList;
    char floor;
    String reservationDate;
    String startTime;
    DBConnector dbConnector;
    HashMap<String, ReservableObject> floorState;
    public Time maxEndTime;
    ReservedObjectType reservedObjectType;
    FloorData floorData;


    public FloorState(char floorChar, String reservationDate, String startTime, ReservedObjectType reservedObjectType) {
        this.floorData = new FloorData(floorChar);
        this.reservationDate = reservationDate;
        this.startTime = startTime;
        this.reservedObjectType = reservedObjectType;
        this.floor = floorChar;
        floorState = new HashMap<>();
        dbConnector = DBConnector.getInstance();
        setMaxEndTime();
    }

    public int getNumRelevantObjectInFloor(){
        return floorData.getNumRelevantObjectsInFloor(reservedObjectType);
    }


    private void setMaxEndTime() {
        String startHourStr = startTime.split(":")[0];
        String startMinutesStr = startTime.split(":")[1];
        int startHour = Integer.parseInt(startHourStr);
        int optionalEndHour = startHour+ABSOLUTE_MAX_RESERVATION_DURATION;
        if(optionalEndHour < 20 ){
            String endTime = optionalEndHour+":"+startMinutesStr+":00";
            maxEndTime = Time.valueOf(endTime);
        }
        else{
            maxEndTime = ABSOLUTE_MAX_RESERVATION_TIME;
        }


    }


    public ArrayList<Reservation> getUpdatedReservationsFromDB() {
        ArrayList<Reservation> reservations = new ArrayList<>();
        System.out.println("updateFloorState");
        String queryStringStartBeforeEndDuring = "(`startTime` < '"+startTime+"' AND `endTime` < '"+maxEndTime+"')";
        String queryStringStartAfter = "(`startTime` > '"+startTime+"' AND `startTime` < '"+maxEndTime+"')";
        String queryStringStartAt = "(`startTime` = '"+startTime+"')";
        String queryString = "SELECT * FROM `Reservations` WHERE `floor` = '"+ floor +"' AND `reservationDate` = '"+reservationDate
                +"' AND ("+queryStringStartBeforeEndDuring+" OR " +queryStringStartAfter+" OR "+queryStringStartAt+")";
        ResultSet result = dbConnector.executeQuery(queryString);
        if(result!=null){
                try {
                    while(result.next()) {
                        int reservationId = result.getInt(1);
                        char floor = result.getString(2).charAt(0);
                        String id = result.getString(3);
                        Date reservationDate = result.getDate(4);
                        Time startTime = result.getTime(5);
                        Time endTime = result.getTime(6);
                        System.out.println("reservationId: "+result.getInt(1)+" | floor: "+ result.getString(2)+
                                " | id: "+result.getString(3)+" | reservationDate: "+result.getDate(4) +
                                " | startTime: "+result.getTime(5)+" | endTime: "+result.getTime(6));
                        Reservation reservation = new Reservation(reservationId, floor, id, reservationDate, startTime, endTime , reservedObjectType);
                        reservations.add(reservation);
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
        }
        return reservations;
    }


    public HashMap<String, ReservableObject> getFloorState() {
        reservationsList = getUpdatedReservationsFromDB();
        HashMap<String, ReservableObject> floorState = new HashMap<>();
        for(Reservation reservation : reservationsList){
            String id = reservation.reservedObjectId;
            if(floorState.get(id)!=null){
                ReservableObject reservableObject = floorState.get(id);
                try {
                    reservableObject.addReservation(reservation);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{
                ReservableObject reservableObject = ReservedObjectFactory.createReservedObject(id, maxEndTime, Time.valueOf(startTime), reservedObjectType);
                try {
                    reservableObject.addReservation(reservation);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                floorState.put(id, reservableObject);
            }
        }
        this.floorState = floorState;
        return this.floorState;
    }


    public ReservableObject addUnreservedObject(String id, int indexForStringName){
        ReservableObject reservableObject = ReservedObjectFactory.createReservedObject(id, maxEndTime, Time.valueOf(startTime), "T"+indexForStringName, reservedObjectType);
        floorState.put(id, reservableObject);
        return reservableObject;
    }

    public ReservableObject getUpdatedObjectState(String id){
        //TODO: This function should access the DB via the DB connector and check the current status of the table
        return floorState.get(id);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addReservationToDB(String id, Time selectedEndTime, String userId) throws Exception {
        ArrayList<Reservation> newReservationsState = getUpdatedReservationsFromDB();
        Collections.sort(newReservationsState, Comparator.comparing(Reservation::getReservationid));
        ArrayList<Reservation> currentReservationsStateCopy = new ArrayList<>();
        currentReservationsStateCopy.addAll(this.reservationsList);
        Collections.sort(currentReservationsStateCopy, Comparator.comparing(Reservation::getReservationid));
        if(newReservationsState.equals(currentReservationsStateCopy)){
            String queryStringReservations = "INSERT INTO Reservations (`floor`, `tableId`, `reservationDate`, `startTime`, `endTime`, `userId`) " +
                    "VALUES ('" + floor +"', '"+id+"', '"+reservationDate+"', '"+startTime+"', '"+selectedEndTime.toString()+"', '"+userId+"');";
            dbConnector.executeUpdate(queryStringReservations);
        }
        else{
            reservationsList = newReservationsState;
            throw new Exception("State has change");
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addReservationsToDB(ArrayList<Reservation> reservations, String userId) throws Exception {
        ArrayList<Reservation> newReservationsState = getUpdatedReservationsFromDB();
        Collections.sort(newReservationsState, Comparator.comparing(Reservation::getReservationid));
        ArrayList<Reservation> currentReservationsStateCopy = new ArrayList<>();
        currentReservationsStateCopy.addAll(this.reservationsList);
        Collections.sort(currentReservationsStateCopy, Comparator.comparing(Reservation::getReservationid));
        if(newReservationsState.equals(currentReservationsStateCopy)){
            String queryStringReservations= "INSERT INTO Reservations (`floor`, `tableId`, `reservationDate`, `startTime`, `endTime`, `userId`) VALUES" ;
            for(Reservation reservation : reservations) {
                queryStringReservations += " ('" + floor + "', '" + reservation.reservedObjectId + "', '" + reservationDate + "', '" +
                        startTime + "', '" + reservation.endTime.toString() + "', '" + userId + "'),";
            }
            queryStringReservations = queryStringReservations.substring(0, queryStringReservations.length()-1) + ";";
            dbConnector.executeUpdate(queryStringReservations);
        }
        else{
            reservationsList = newReservationsState;
            throw new Exception("State has change");
        }

    }
}
