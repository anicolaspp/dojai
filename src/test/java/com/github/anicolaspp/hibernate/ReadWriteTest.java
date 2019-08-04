package com.github.anicolaspp.hibernate;

import com.github.anicolaspp.hibernate.entities.Employee;
import com.github.anicolaspp.hibernate.repositories.EmployeeRepository;
import com.github.anicolaspp.ojai.JavaOjaiTesting;
import com.github.anicolaspp.sql.DojaiDriver;
import com.mapr.ojai.store.impl.InMemoryDriver;
import lombok.val;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Test;
import org.ojai.store.DriverManager;

public class ReadWriteTest implements JavaOjaiTesting {

    @Test
    public void readYourOwnWrites() {

        val factory = buildSessionFactory();

        val repository = new EmployeeRepository(factory);

        val empID1 = repository.addEmployee("Zarai", "Ali", 1000);
        val empID2 = repository.addEmployee("Daisy", "Das", 5000);

        assert !empID1.isEmpty();
        assert !empID2.isEmpty();

        val employees = repository.listEmployees();

        assert employees.size() == 2;

        assert employees.stream().anyMatch(e -> e.getId().equals(empID1));
        assert employees.stream().anyMatch(e -> e.getId().equals(empID2));
    }

    private static SessionFactory buildSessionFactory() {
        try {
            val configuration = new Configuration();
            configuration.setProperty("hibernate.connection.url", DojaiDriver.JDBC_DOJAI_MAPR_MEM);

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
}


