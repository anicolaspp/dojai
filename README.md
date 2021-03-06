# dojai (pronounced Du-jai)

A JDBC Driver for OJAI

This is an **experimental** library that allows connecting to MapR Database through JDBC. MapR Database is a NoSQL Database, so not everything that works on regular SQL will work here. 

We should be able to run queries that do not involve more than one table. In other words, not joins and not aggregations. Those are normally run using analytical tools such as Apache Drill. 

- [Plain SQL](https://github.com/anicolaspp/dojai/blob/master/README.md#plain-sql)
- [Working with Hibernate](https://github.com/anicolaspp/dojai/blob/master/README.md#working-with-hibernate)
    - [Hibernate Configuration](https://github.com/anicolaspp/dojai/blob/master/README.md#hibernate-configuration)
    - [Entity Definition](https://github.com/anicolaspp/dojai/blob/master/README.md#our-hibernate-employee-entity)
    - [Running Hibernate Queries](https://github.com/anicolaspp/dojai/blob/master/README.md#running-hibernate-queries)
        - [Adding an Employee](https://github.com/anicolaspp/dojai/blob/master/README.md#adding-an-employee)
        - [Loading all Employees](https://github.com/anicolaspp/dojai/blob/master/README.md#loading-all-employees)
- [Limitation](https://github.com/anicolaspp/dojai/blob/master/README.md#limitations)

```xml
<dependency>
  <groupId>com.github.anicolaspp</groupId>
  <artifactId>dojai</artifactId>
  <version>1.0.0</version>
</dependency>
```

### Plain SQL

The following get all records from a MapR Database table given some condition. 

```java
import lombok.val;

import java.sql.DriverManager;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
    
        Class.forName("anicolaspp.sql.DojaiDriver");
        
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

Notice that in the previous `INSERT` example, `_id` is autogenerated when inserting into `t2` since it was not selected from `t1`, `name` is filled up with the select part of the query, and `age` is inserted as `null`.

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

Deletes can be executed in the following way.

```java
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
```

# Working with Hibernate

Since `DOJAI` is implemented in terms of JDBC, we can integrate it with Hibernate so we dont have to create the SQL queries manually, instead, we can relie of Hibernate to do this work while we focus on the application logic. 

## Hibernate Configuration

First, let's look at how we can configure Hibernate so it uses `DOJAI` as a datasource.

The following snippet shows how we can optain a Hibernate `SessionFactory`. 

```java
 private static SessionFactory buildSessionFactory() {
        try {
            val configuration = new Configuration();
            configuration.setProperty("hibernate.connection.url", "dojai:mapr:mem:");

            Class.forName("com.github.anicolaspp.sql.DojaiDriver");

            DriverManager.registerDriver(InMemoryDriver.apply());

            configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
            configuration.setProperty("hibernate.connection.driver_class", "com.github.anicolaspp.sql.DojaiDriver");
            configuration.setProperty("hibernate.show_sql", "true");

            configuration.setProperty("hibernate.c3p0.min_size", "5");
            configuration.setProperty("hibernate.c3p0.max_size", "20");
            configuration.setProperty("hibernate.c3p0.timeout", "300");
            configuration.setProperty("hibernate.c3p0.max_statements", "50");
            configuration.setProperty("hibernate.c3p0.idle_test_period", "3000");

            configuration.addPackage("com.github.anicolaspp.hibernate");
            configuration.addAnnotatedClass(Employee.class);

            return configuration.buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
```

Let's review some interesting features of the code above.

- `configuration.setProperty("hibernate.connection.url", "dojai:mapr:mem:");` allows us the define what kind of OJAI connection we want. We can choose between `dojai:mapr:mem:` for an in-memery implementation of `MapR Database` using the [OJAI Testing Project](https://github.com/anicolaspp/ojai-testing) or `dojai:mapr:` for a real implementation of `MapR Database`. 

- `configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");` Notice we use `MySQL5Dialect`. 

- `configuration.setProperty("hibernate.connection.driver_class", "com.github.anicolaspp.sql.DojaiDriver");` indicates that Hibernate will use the `DojaiDriver`. Internally, the `DojaiDriver` will select to use [OJAI Testing Project](https://github.com/anicolaspp/ojai-testing) or real `MapR Database` based on the `"hibernate.connection.url"` described above. 

## Our Hibernate Employee Entity

```java
@Entity
@Table(name = "`anicolaspp/user/mapr/tables/employee`")
@ToString
public class Employee {

    @Id
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "_id")
    private String id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "salary")
    private int salary;

    public Employee() {
    }

    @PrePersist
    private void generateCodeIdentifier(){
        id = "\"" + UUID.randomUUID().toString() + "\"";
    }

    public Employee(String firstName, String lastName, int salary) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
    }

    public String getId() {
        return "\"" + id + "\"";
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String first_name) {
        this.firstName = first_name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String last_name) {
        this.lastName = last_name;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }
}
```

A few things to notice:

```java
@Table(name = "`anicolaspp/user/mapr/tables/employee`") 
``` 
Shows the table to be used. In case of using [OJAI Testing Project](https://github.com/anicolaspp/ojai-testing), the table path must start with `anicolaspp`. In case of using a real `MapR Database` cluster, this path should be a real path in the cluster. 

```java
    @Id
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(name = "_id")
    private String id;
```
Notice that `id` column in this case is being mapped to `_id`, internally the identity column for `MapR Database`. Also it is important to mention that `MapR Database` does not auto generate ids, they must be managed on the client side. In our case we use the `generateCodeIdentifier` function for that. Hibernate calls this function before saving new entities. 

## Running Hibernate Queries

### Adding an Employee

```java
 public String addEmployee(String fname, String lname, int salary){
        val session = factory.openSession();
        Transaction tx = null;
        String employeeID = "null";

        try {
            tx = session.beginTransaction();
            val employee = new Employee(fname, lname, salary);
            System.out.println(session.save(employee));

            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return employeeID;
    }

```

### Loading all employees

```java
 public void listEmployees( ){
        val session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            List<Employee> employees = session.createQuery("FROM Employee").getResultList();

            for (Employee obj : employees) {
                System.out.print("  First Name: " + employee.getFirstName());
                System.out.print("  Last Name: " + employee.getLastName());
                System.out.println("  Salary: " + employee.getSalary());
            }

            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

```

### Limitations

At this early stage 

- Not Start (`*`) schema allowed. Queries should use `select column1 [,<column2>, ...] ....`.
- No query should go across tables (`join`, etc...).
- Only `select`, `insert into` and `delete` queries are in place at this point, we are adding more soon. 
