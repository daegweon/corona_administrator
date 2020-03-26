package com.example.corona_administrator;

public class ObjectItem {

    public String personName;
    public String birthDate;
    public String phoneNumber;
    public String Address;
    public String State;

    // constructor
    public ObjectItem(String personName, String birthDate, String phoneNumber, String Address, String State) {
        this.personName = personName;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.Address = Address;
        this.State = State;
    }
}