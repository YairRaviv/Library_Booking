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
        //String table_exact_number = tableId.substring(12,tableId.length());
        return "Request Number: "+this.reservationId+"\n"+
                "Faculty:"+this.Faculty+"\n"+
                "Department: "+Department+"\n"+
               // "Student ID: "+this.Student_Id+"\n"+
                "Reason: "+this.Reason+"\n"+
                "Number Of Students: "+this.NumOfStudents+"\n"+
                "Student Phone Number: "+this.PhoneNumber;
    }
}