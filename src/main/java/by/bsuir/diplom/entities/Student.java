package by.bsuir.diplom.entities;

import javax.persistence.*;

@Entity
@Table(name = "Student")
public class Student {
    @Id
    @Column
    String name;

    @Column
    String surname;

    @Column
    Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Student: " + name;
    }
}
