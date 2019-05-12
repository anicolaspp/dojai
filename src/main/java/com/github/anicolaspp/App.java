package com.github.anicolaspp;


import lombok.val;

import java.sql.DriverManager;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
    
        Class.forName("com.github.anicolaspp.DojaiDriver");
        
        val connection = DriverManager.getConnection("dojai:mapr:");
    
        System.out.println(connection.getClass().toString());
    }
}


