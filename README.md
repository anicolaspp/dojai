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

### Limitations

At this early stage 

- Not aliases are allowed (`name as n`). 
- Not Start (`*`) schema allowed. Queries should use `select [<column1>, ...] ....`.
- MapR Database table path are represented using `.` instead of `/` or `\`. Notice on the example `user.mapr.some_data`. That refers to a table that lives on `/user/mapr/some_data`.
