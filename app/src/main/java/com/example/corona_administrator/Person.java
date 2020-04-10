package com.example.corona_administrator;

import java.util.Random;

public class Person {
    private static String[] timeOver = new String[]{"5분 이하", "10분 이상", "30분 이상", "1시간 이상"};
    private static final int timeOverSize = 4;
    private static Random random = new Random();

    private String name;
    private String address;
    private String state;
    private String stateTime = "";
    private String birthDate;
    private String phoneNumber;


    // constructor
    public Person(String name, String address, String state, String birthDate, String phoneNumber) {
        this.name = name;
        this.address = address;
        this.state = state;

        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;


        checkState();
    }

    private void checkState() {
        if (!state.equals("정상")){
            stateTime = timeOver[random.nextInt(timeOverSize)];
        }
    }

    public String getName(){return this.name;}
    public String getAddress(){return this.address;}
    public String getState(){return this.state;}
    public String getStateTime(){return this.stateTime;}
    public String getBirthDate(){return this.birthDate;}
    public String getPhoneNumber(){return this.phoneNumber;}
}
