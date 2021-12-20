package com.example.floorview;
import android.os.StrictMode;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DBConnector {
    String dbUrl;
    int dbPort;
    String dbName;
    String userName;
    String userPassword;
    String jdbcDriver;
    private static DBConnector instance = null;


    private DBConnector(){
        jdbcDriver  = "com.mysql.jdbc.Driver";
        dbUrl = "sql6.freesqldatabase.com";
        dbPort = 3306;
        dbName = "sql6456621";
        userName = "sql6456621";
        userPassword = "5MZZM83LGd";
    }

    public static DBConnector getInstance(){
        if(instance==null){
            synchronized (DBConnector.class) {
                if(instance==null) {
                    instance = new DBConnector();
                    instance.connect();
                }
            }
        }
        return instance;
    }

    public ResultSet executeQuery(String query){
        Connection connection = connect();
        ResultSet result = null;
        if(connection!=null){
            try {
                Statement sqlStatement = connection.createStatement();
                result = sqlStatement.executeQuery(query);

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return result;
    }

    public void executeUpdate(String query){
        Connection connection = connect();
        ResultSet result = null;
        if(connection!=null){
            try {
                Statement sqlStatement = connection.createStatement();
                System.out.println(sqlStatement.executeUpdate(query));

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    private Connection connect() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String connectionUrl = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connectionUrl = "jdbc:mysql://"+dbUrl+":"+dbPort+"/"+dbName+"?enabledTLSProtocols=TLSv1.2";
            connection = DriverManager.getConnection(connectionUrl, userName, userPassword);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
