package com.github.anicolaspp.hibernate;

import com.mapr.ojai.store.impl.InMemoryDriver;
import lombok.val;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.ojai.store.DriverManager;

import java.sql.SQLException;

public class App {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        val factory = buildSessionFactory();

        printSeparator();
        addEmployees(factory);
    }

    private static SessionFactory buildSessionFactory() {
        try {
            val configuration = new Configuration();
            configuration.setProperty("hibernate.connection.url", "dojai:mapr:");

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
//            configuration.addAnnotatedClass(SuperEmployee.class);

            return configuration.buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static void printSeparator() {
        System.out.println();
        System.out.println("----------------------------------------");
        System.out.println();
    }

    private static void addEmployees(SessionFactory factory) {
        val repository = new EmployeeRepository(factory);

//        val empID1 = repository.addEmployee("Zarai", "Ali", 1000);
//        val empID2 = repository.addEmployee("Daisy", "Das", 5000);
//        repository.addEmployee("Johnn", "Pau", 10000);


        repository.listEmployees();
//        repository.updateEmployee(empID1, 5000);
//        repository.deleteEmployee(empID2);
//        repository.listEmployees();
    }
}

