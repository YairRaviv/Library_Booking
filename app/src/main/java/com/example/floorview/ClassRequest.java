package com.example.floorview;

import java.sql.Date;
import java.sql.Time;

public class ClassRequest implements Comparable<ClassRequest>
{
    int reservationId;
    String Student_Id;
    String Faculty;
    String Department;
    String Reason;
    int NumOfStudents;
    String PhoneNumber;

    public ClassRequest(int reservationId , String Student_Id , String Faculty , String Department , String Reason , int NumOfStudents , String PhoneNumber)
    {
        this.reservationId = reservationId;
        this.Student_Id = Student_Id;
        this.Faculty = Faculty;
        this.Department = Department;
        this.Reason = Reason;
        this.NumOfStudents = NumOfStudents;
        this.PhoneNumber = PhoneNumber;
    }

    public int getId(){
        return reservationId;
    }

    @Override
    public int compareTo(ClassRequest o)
    {
        return Integer.compare(reservationId, o.reservationId);
    }
    @Override
    public boolean equals(Object resObj)
    {
        ClassRequest other = (ClassRequest) resObj;
        return reservationId == other.reservationId;
    }
    public String toString()
    {
//        String table_exact_number = tableId.substring(12,tableId.length());
//        return "Reservation "+this.reservationId+"\n"+
//                "Floor "+this.floor+"\n"+
//                "Table "+table_exact_number+"\n"+
//                "Date "+this.reservationDate+"\n"+
//                "Start "+this.startTime+"\n"+
//                "End "+this.endTime;
        return "";
    }
}