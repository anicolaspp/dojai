package com.github.anicolaspp.hibernate.repositories;

import com.github.anicolaspp.hibernate.entities.Employee;
import lombok.val;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class EmployeeRepository {
    private final SessionFactory factory;

    public EmployeeRepository(SessionFactory factory) {
        this.factory = factory;
    }

    public String addEmployee(String fname, String lname, int salary) {
        val session = factory.openSession();
        Transaction tx = null;
        String employeeID = "null";

        try {
            tx = session.beginTransaction();
            val employee = new Employee(fname, lname, salary);
            employeeID = (String) session.save(employee);

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return employeeID;
    }

    public List<Employee> listEmployees() {
        val session = factory.openSession();
        Transaction tx = null;

        List<Employee> employees = new ArrayList<>();

        try {
            tx = session.beginTransaction();

            employees.addAll(session.createQuery("FROM Employee").getResultList());

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return employees;
    }

    public void updateEmployee(Integer EmployeeID, int salary) {
        val session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            val employee = session.get(Employee.class, EmployeeID);
            employee.setSalary(salary);
            session.update(employee);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void deleteEmployee(Integer EmployeeID) {
        val session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            val employee = session.get(Employee.class, EmployeeID);
            session.delete(employee);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}