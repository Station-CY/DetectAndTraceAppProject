package com.example.detectandtraceapp;

public class GetData {

    String fName, lName, email, date, campus, phone;

    public GetData() {

    }

    public GetData(String fName, String lName, String email, String date, String campus, String phone) {
        this.fName = fName;
        this.lName = lName;
        this.date = date;
        this.email = email;
        this.campus = campus;
        this.phone = phone;
    }

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }

    public String getDate() {
        return date;
    }

    public String getEmail() {
        return email;
    }

    public String getCampus() {
        return campus;
    }

    public String getPhone() {
        return phone;
    }
}
