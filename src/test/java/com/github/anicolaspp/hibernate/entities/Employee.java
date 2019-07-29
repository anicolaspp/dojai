package com.github.anicolaspp.hibernate.entities;

import lombok.Getter;
import lombok.Setter;
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
@Getter
@Setter
public class Employee {

    @Id
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
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
    private void generateCodeIdentifier() {
        id = "`" + UUID.randomUUID().toString() + "`";
    }

    public Employee(String firstName, String lastName, int salary) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
    }
}
