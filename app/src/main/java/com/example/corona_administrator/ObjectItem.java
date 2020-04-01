package com.example.corona_administrator;

import java.util.Random;

public class ObjectItem {
    private static String[] timeOver = new String[]{"5분 이하", "10분 이상", "30분 이상", "1시간 이상"};
    private static final int timeOverSize = 4;
    private static Random random = new Random();

    public String personName;
    public String birthDate;
    public String phoneNumber;
    public String address;
    public String state;
    public String state_time = "";

    // constructor
    public ObjectItem(String personName, String birthDate, String phoneNumber, String address, String state) {
        this.personName = personName;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.state = state;

        checkState();
    }

    public void checkState() {
        if (!state.equals("정상")){
            state_time = timeOver[random.nextInt(timeOverSize)];
        }
    }
}