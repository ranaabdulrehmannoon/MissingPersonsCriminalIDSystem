package application;

import java.sql.Date;

public class MissingCase {
    private int id;
    private String fullName;
    private int age;
    private Date dateMissing;
    private String city;

    public MissingCase(int id, String fullName, int age, Date dateMissing, String city) {
        this.id = id;
        this.fullName = fullName;
        this.age = age;
        this.dateMissing = dateMissing;
        this.city = city;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public int getAge() {
        return age;
    }

    public Date getDateMissing() {
        return dateMissing;
    }

    public String getCity() {
        return city;
    }
}
