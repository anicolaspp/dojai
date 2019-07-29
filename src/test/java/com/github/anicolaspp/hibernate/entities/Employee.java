package com.github.anicolaspp.hibernate.entities;

import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.util.UUID;

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
        id = "`" + UUID.randomUUID().toString() + "`";
    }

    public Employee(String firstName, String lastName, int salary) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
    }

    public String getId() {
        return id;
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
