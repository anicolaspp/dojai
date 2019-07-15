# dojai (pronounced Du-jai)
A JDBC Driver for OJAI

This is an **experimental** library that allows connecting to MapR Database through JDBC. MapR Database is a NoSQL Database, so not everything that works on regular SQL will work here. 

We should be able to run queries that do not involve more than one table. In other words, not joins and not aggregations. Those are normally run using analytical tools such as Apache Drill. 

### Examples

The following get all records from a MapR Database table given some condition. 

```java
import lombok.val;

import java.sql.DriverManager;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
    
        Class.forName("com.github.anicolaspp.sql.DojaiDriver");
        
        val connection = DriverManager.getConnection("dojai:mapr:");
    
        System.out.println(connection.getClass().toString());
        
        val statement = connection.createStatement();
        
        String sql = "select _id, name from user.mapr.some_data where name = pepe or name = lolo limit 10";
        
        val result = statement.executeQuery(sql);
        
        while (result.next()) {
            System.out.println(result.getString(0));
            System.out.println(result.getString(1));
        }
    
        System.out.println(statement);
    }
}
```

We can also do `INSERT INTO` that works as you might expect. 

```java
import lombok.val;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

private static void testInsert(Connection connection) throws SQLException {

    String sql = "INSERT INTO `/user/mapr/tables/t2` (_id, name, age) select name as n from `/user/mapr/tables/t1`";

    val statement = connection.createStatement();

    statement.executeUpdate(sql);
}
```

Notice that we are selecting data from `/user/mapr/tables/t1` and writing to `/user/mapr/tables/t2`.

We can also insert static values in the following way.

```java
import lombok.val;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

private static void testInsertValues(Connection connection) throws SQLException {

    String sql = "INSERT INTO `/user/mapr/tables/users` (_id, name, age) Values ("001", "nick", 30)";

    val statement = connection.createStatement();

    statement.executeUpdate(sql);
}
```

Of course we could use this to insert data in a dynamic way. 

```java
import lombok.val;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

private static void testInsertFromStream(Connection connection, Stream<User> users) throws SQLException {

    String sql = "INSERT INTO `/user/mapr/tables/users` (_id, name, age) Values (%s, %s, %d)";

    users
        .map(user -> String.format(sql, user.getId(), user.getName(), user.getAge())
        .map(sqlToRun -> connection.createStatement().executeUpdate(sqlToRun))
        .forEach(System.out::println)
}
```

### Limitations

At this early stage 

- Not Start (`*`) schema allowed. Queries should use `select column1 [,<column2>, ...] ....`.
- No query should go across tables (`join`, etc...).
- Only `select` and `insert into` queries are in place at this point, we are adding more soon. 
