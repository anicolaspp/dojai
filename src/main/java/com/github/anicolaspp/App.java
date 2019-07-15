package com.github.anicolaspp;


import lombok.val;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        Class.forName("com.github.anicolaspp.sql.DojaiDriver");

        val connection = DriverManager.getConnection("dojai:mapr:");

        testInsert(connection);
        testSelect(connection);
    }

    private static void testSelect(Connection connection) throws SQLException {
        System.out.println(connection.getClass().toString());

        val statement = connection.createStatement();

        val result = statement.executeQuery("select name as n, age from `/user/mapr/tables/dojai` where n = 'ivan' AND age = 30");

//        statement.executeQuery("update user.mapr.some_data set name = 'hehe'");

        while (result.next()) {
            System.out.println(result.getString(0));
            System.out.println(result.getString(1));
//            System.out.println(result.getString("n"));
//            System.out.println(result.getString("age"));
        }

        System.out.println(statement);
    }

    private static void testInsert(Connection connection) throws SQLException {

        String sql = "INSERT INTO `/user/mapr/tables/dojai` (_id, name, age) values (\"000001\", \"kandi\", 22)";

        val statement = connection.createStatement();

        statement.executeUpdate(sql);
    }
}



