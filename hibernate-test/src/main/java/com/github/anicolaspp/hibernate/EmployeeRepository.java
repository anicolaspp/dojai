package com.github.anicolaspp.hibernate;

import lombok.val;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class EmployeeRepository {
    private final SessionFactory factory;

    public EmployeeRepository(SessionFactory factory){
        this.factory = factory;
    }

    public Integer addEmployee(String fname, String lname, int salary){
        val session = factory.openSession();
        Transaction tx = null;
        Integer employeeID = null;

        try {
//            tx = session.beginTransaction();
            val employee = new Employee(fname, lname, salary);
            employeeID = (Integer) session.save(employee);
//            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return employeeID;
    }

    public void listEmployees( ){
        val session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            val employees = session.createQuery("FROM Employee").list();
            for (val obj : employees) {
                val employee = (Employee) obj;
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

    public void updateEmployee(Integer EmployeeID, int salary ){
        val session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            val employee = session.get(Employee.class, EmployeeID);
            employee.setSalary( salary );
            session.update(employee);
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void deleteEmployee(Integer EmployeeID){
        val session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            val employee = session.get(Employee.class, EmployeeID);
            session.delete(employee);
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}