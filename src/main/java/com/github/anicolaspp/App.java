package com.github.anicolaspp;


import lombok.val;

import java.sql.DriverManager;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
    
        Class.forName("com.github.anicolaspp.sql.DojaiDriver");
        
        val connection = DriverManager.getConnection("dojai:mapr:");
    
        System.out.println(connection.getClass().toString());
        
        val statement = connection.createStatement();
        
        val result = statement.executeQuery("select name from user.mapr.some_data");
        
        statement.executeQuery("update user.mapr.some_data set name = 'hehe'");
        
        while (result.next()) {
            System.out.println(result.getString(0));
//            System.out.println(result.getString(1));
        }
    
        System.out.println(statement);
    }
}


