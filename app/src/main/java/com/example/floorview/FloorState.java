package com.example.floorview;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

public class FloorStateTable<T> implements FloorState {
    static final Time absoluteMaxTime = Time.valueOf("20:00:00");
    ArrayList<Reservation> reservationsList;
    char floorLevel;
    String reservationDate;
    String startTime;
    DBConnector dbConnector;
    HashMap<String, T> floorState;
    public Time maxEndTime;
    int numTablesInFloor;



    public FloorStateTable(char floorLevel, String reservationDate, String startTime) {
        this.floorLevel = floorLevel;
        this.reservationDate = reservationDate;
        this.startTime = startTime;
        floorState = new HashMap<>();
        dbConnector = DBConnector.getInstance();
        setMaxEndTime();
        setNumTableInFloor();
    }

    private void setNumTableInFloor() {
        switch (floorLevel){
            case 'A':
            case 'B':
                numTablesInFloor=23;
                break;
            case 'C':
                numTablesInFloor=24;
                break;
            default:
                numTablesInFloor=28;
                break;
        }
    }

    private void setMaxEndTime() {
        String startHourStr = startTime.split(":")[0];
        String startMinutesStr = startTime.split(":")[1];
        int startHour = Integer.parseInt(startHourStr);
        int optionalEndHour = startHour+6;
        if(optionalEndHour < 20 ){
            String endTime = optionalEndHour+":"+startMinutesStr+":00";
            maxEndTime = Time.valueOf(endTime);
        }
        else{
            maxEndTime = absoluteMaxTime;
        }


    }

    //SELECT * FROM `Reservations` WHERE `reservationDate` = '2021-12-07' AND `startTime` > '12:00:00'
    //SELECT * FROM `Reservations` WHERE `reservationDate` = '2021-12-07';
    //SELECT * FROM `Reservations` WHERE `startTime` > '14:00:00'
    public ArrayList<Reservation> getUpdatedReservationsFromDB() {
        ArrayList<Reservation> reservations = new ArrayList<>();
        System.out.println("updateFloorState");
        String queryStringStartBeforeEndDuring = "(`startTime` < '"+startTime+"' AND `endTime` < '"+maxEndTime+"')";
        String queryStringStartAfter = "(`startTime` > '"+startTime+"' AND `startTime` < '"+maxEndTime+"')";
        String queryStringStartAt = "(`startTime` = '"+startTime+"')";
        String queryString = "SELECT * FROM `Reservations` WHERE `floor` = '"+floorLevel+"' AND `reservationDate` = '"+reservationDate
                +"' AND ("+queryStringStartBeforeEndDuring+" OR " +queryStringStartAfter+" OR "+queryStringStartAt+")";
        ResultSet result = dbConnector.executeQuery(queryString);
        if(result!=null){
                try {
                    while(result.next()) {
                        int reservationId = result.getInt(1);
                        char floor = result.getString(2).charAt(0);
                        String tableId = result.getString(3);
                        Date reservationDate = result.getDate(4);
                        Time startTime = result.getTime(5);
                        Time endTime = result.getTime(6);
                        System.out.println("reservationId: "+result.getInt(1)+" | floor: "+ result.getString(2)+
                                " | tableId: "+result.getString(3)+" | reservationDate: "+result.getDate(4) +
                                " | startTime: "+result.getTime(5)+" | endTime: "+result.getTime(6));
                        Reservation reservation = new Reservation(reservationId, floor, tableId, reservationDate, startTime, endTime);
                        reservations.add(reservation);
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
        }
        return reservations;
    }


    @Override
    public HashMap<String, Table> getFloorState() {
        reservationsList = getUpdatedReservationsFromDB();
        HashMap<String, Table> floorTableState = new HashMap<>();
        for(Reservation reservation : reservationsList){
            String tableId = reservation.tableId;
            if(floorTableState.get(tableId)!=null){
                Table reservedTable = floorTableState.get(tableId);
                try {
                    reservedTable.addReservation(reservation);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{
                Table reservedTable = new Table(tableId, maxEndTime, Time.valueOf(startTime));
                try {
                    reservedTable.addReservation(reservation);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                floorTableState.put(tableId, reservedTable);
            }
        }
        this.floorState = floorTableState;
        return this.floorState;
    }


    public Table addUnreservedTable(String tableId, int indexForStringName){
        Table table = new Table(tableId, maxEndTime, Time.valueOf(startTime), "T"+indexForStringName);
        floorState.put(tableId, table);
        return table;
    }
    public Table getUpdatedTableState(String tableStringId){
        //TODO: This function should access the DB via the DB connector and check the current status of the table
        return floorState.get(tableStringId);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addReservationToDB(String clickedTableId, Time selectedEndTime, String userId) throws Exception {
        ArrayList<Reservation> newReservationsState = getUpdatedReservationsFromDB();
        Collections.sort(newReservationsState, Comparator.comparing(Reservation::getId));
        ArrayList<Reservation> currentReservationsStateCopy = new ArrayList<>();
        currentReservationsStateCopy.addAll(this.reservationsList);
        Collections.sort(currentReservationsStateCopy, Comparator.comparing(Reservation::getId));
        if(newReservationsState.equals(currentReservationsStateCopy)){
            String queryStringReservations = "INSERT INTO Reservations (`floor`, `tableId`, `reservationDate`, `startTime`, `endTime`, `userId`) " +
                    "VALUES ('" +floorLevel+"', '"+clickedTableId+"', '"+reservationDate+"', '"+startTime+"', '"+selectedEndTime.toString()+"', '"+userId+"');";
            dbConnector.executeUpdate(queryStringReservations);
        }
        else{
            reservationsList = newReservationsState;
            throw new Exception("State has change");
        }

    }
}
