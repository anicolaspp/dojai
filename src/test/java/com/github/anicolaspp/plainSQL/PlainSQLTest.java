package com.github.anicolaspp.plainSQL;

import com.github.anicolaspp.ojai.JavaOjaiTesting;
import com.mapr.ojai.store.impl.InMemoryDriver;
import lombok.val;
import org.junit.Test;
import org.ojai.store.DriverManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlainSQLTest implements JavaOjaiTesting {

    @Test
    public void testSelect() throws SQLException, ClassNotFoundException {
        Class.forName("com.github.anicolaspp.sql.DojaiDriver");
        DriverManager.registerDriver(InMemoryDriver.apply());

        String sql = "select _id, name as fname from `%s` limit 10";

        String table = "anicolaspp/user/mapr/tables/employees";

        String selectSQL = String.format(sql, table);

        val connection = java.sql.DriverManager.getConnection("dojai:mapr:mem:");

        val statement = connection.createStatement();

        String insertSQL = "INSERT INTO `anicolaspp/user/mapr/tables/employees` (_id, name, age) values ('s001', nick, 30)";

        assert statement.executeUpdate(insertSQL) == 1;

        val result = statement.executeQuery(selectSQL);

        assert result.next();

        assert result.getString("_id").equals("s001");
        assert result.getString("fname").equals("nick");
    }

    @Test
    public void testSubSelect() throws ClassNotFoundException, SQLException {
        Class.forName("com.github.anicolaspp.sql.DojaiDriver");
        DriverManager.registerDriver(InMemoryDriver.apply());

        String sql = "select _id, name as fname from `%s` limit 10";

        String table = "anicolaspp/user/mapr/tables/employees";

        String selectSQL = String.format(sql, table);

        val connection = java.sql.DriverManager.getConnection("dojai:mapr:mem:");

        val statement = connection.createStatement();


        String insertSQL = "INSERT INTO `anicolaspp/user/mapr/tables/t1` (_id, name, age) values ('%s', %s, %d)";
        assert addSome(insertSQL, 1, connection) == 1;

//        assert statement.executeUpdate(insertSQL) == 1;

        String insertSubSelect = "INSERT INTO `anicolaspp/user/mapr/tables/employees` (_id, name, age) select _id, name as n from `anicolaspp/user/mapr/tables/t1`";

        assert statement.executeUpdate(insertSubSelect) == 1;

        val result = statement.executeQuery(selectSQL);

        assert result.next();

        assert result.getString("_id").equals("s001");
        assert result.getString("fname").equals("nick");
    }

    @Test
    public void testDeleteSome() throws ClassNotFoundException, SQLException {
        Class.forName("com.github.anicolaspp.sql.DojaiDriver");
        DriverManager.registerDriver(InMemoryDriver.apply());

        String insertSQL = "INSERT INTO `anicolaspp/user/mapr/tables/employees` (_id, name, age) values ('%s', %s, %d)";
        val connection = java.sql.DriverManager.getConnection("dojai:mapr:mem:");

        assert addSome(insertSQL, 10, connection) == 10;

        String deleteSQL = "DELETE FROM `anicolaspp/user/mapr/tables/employees` WHERE age = 9";

        assert connection.createStatement().executeUpdate(deleteSQL) == 1;

        val result = getAll(connection);

        int count = 0;

        while (result.next()) {
            count++;

            assert result.getInt("age") != 9;
        }

        assert count == 9;
    }

    private ResultSet getAll(Connection connection) throws SQLException {
        String sql = "select _id, name as fname, age from `%s` limit 10";

        String table = "anicolaspp/user/mapr/tables/employees";

        return connection.createStatement().executeQuery(String.format(sql, table));
    }

    private int addSome(String insertSQL, int howMany, Connection connection) throws SQLException {
        int total = 0;

        for (int i = 0; i < howMany; i++) {
            val sqlWithValues = String.format(insertSQL, String.valueOf(i), String.valueOf(i), i);

            assert connection.createStatement().executeUpdate(sqlWithValues) == 1;

            total++;
        }

        return total;
    }
}
