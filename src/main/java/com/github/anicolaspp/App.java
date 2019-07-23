package com.github.anicolaspp;


import lombok.val;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App {

    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {

        Class.forName("com.github.anicolaspp.sql.DojaiDriver");

        val connection = DriverManager.getConnection("dojai:mapr:");

//        testSelect(connection);
        testSubSelects(connection);

//        testInsert(connection);
        testSelect(connection);
//        testDeleteSome(connection);
//        testDeleteAll(connection);
    }

    private static void testSelect(Connection connection) throws SQLException {
        val statement = connection.createStatement();

        val result = statement.executeQuery("select _id from `/user/mapr/tables/users` limit 10");

//        statement.executeQuery("update user.mapr.some_data set name = 'hehe'");

        while (result.next()) {
            System.out.println(result.getString(0));
//            System.out.println(result.getString(1));
        }
    }

    private static void testSubSelects(Connection connection) throws SQLException, IOException {
        val statement = connection.createStatement();

        val result = statement.executeQuery("select intKey from (select _id, intKey from `/user/mapr/tables/users` limit 10)");

        while (result.next()) {
            System.out.println(result.getInt(0));
        }
    }

    private static void testInsert(Connection connection) throws SQLException {

        String sql = "INSERT INTO `/user/mapr/tables/dojai` (_id, name, age) select name as n from `/user/mapr/tables/t1`";

        val statement = connection.createStatement();

        statement.executeUpdate(sql);
    }

    private static void testDeleteAll(Connection connection) throws SQLException {
        String sql = "DELETE FROM `/user/mapr/tables/dojai`";

        val statement = connection.createStatement();

        statement.executeUpdate(sql);
    }

    private static void testDeleteSome(Connection connection) throws SQLException {
        String sql = "DELETE FROM `/user/mapr/tables/t1` WHERE age = 40";

        val statement = connection.createStatement();

        statement.executeUpdate(sql);
    }
}



