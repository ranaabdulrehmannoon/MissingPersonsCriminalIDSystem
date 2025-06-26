package application;

import java.sql.Date;

public class CriminalRecord {
    private int id;
    private String fullName;
    private Date dob;
    private String status;
    private String crimes;
    public CriminalRecord(int id, String fullName, Date dob, String status, String crimes) {
        this.id = id;
        this.fullName = fullName;
        this.dob = dob;
        this.status = status;
        this.crimes = crimes;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCrimes() {
        return crimes;
    }

    public void setCrimes(String crimes) {
        this.crimes = crimes;
    }
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
