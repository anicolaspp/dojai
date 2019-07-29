package com.github.anicolaspp.plainSQL;

import com.github.anicolaspp.ojai.JavaOjaiTesting;
import com.mapr.ojai.store.impl.InMemoryDriver;
import lombok.val;
import org.junit.Test;
import org.ojai.store.DriverManager;

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

        String insertSQL = "INSERT INTO `anicolaspp/user/mapr/tables/t1` (_id, name, age) values ('s001', nick, 30)";

        assert statement.executeUpdate(insertSQL) == 1;

        String insertSubSelect = "INSERT INTO `anicolaspp/user/mapr/tables/employees` (_id, name, age) select _id, name as n from `anicolaspp/user/mapr/tables/t1`";

        assert statement.executeUpdate(insertSubSelect) == 1;

        val result = statement.executeQuery(selectSQL);

        assert result.next();

        assert result.getString("_id").equals("s001");
        assert result.getString("fname").equals("nick");
    }
}
