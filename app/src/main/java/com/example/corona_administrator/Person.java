package com.example.corona_administrator;

import java.util.Random;

public class Person {
    private static final String[] timeOver = new String[]{"5분 이하", "10분 이상", "30분 이상", "1시간 이상"};
    private static final String[] stateList = new String[]{"정상", "통신안됨", "이탈"};
    private static final int stateListSize = 3;
    private static final int timeOverSize = 4;
    private static Random random = new Random();

    private String name;
    private String address;
    private String zipCode;

    private long timeLastSent;
    private long timeLastStay;

    private String state = "";
    private String stateTime = "";
    private String birthDate;
    private String phoneNumber;


    // constructor
    public Person(){}

    public Person(String name, String address, String state, String birthDate, String phoneNumber) {
        this.name = name;
        this.address = address;
        this.state = state;

        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;


        checkState();
    }

    public void setName(String name){
        this.name = name;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public void setZipCode(String zipCode){
        this.zipCode = zipCode;
    }

    public void setTimeLastSent(long timeLastSent){
        this.timeLastSent = timeLastSent;
    }

    public void setTimeLastStay(long timeLastStay){
        this.timeLastStay = timeLastStay;
    }

    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    public void setBirthDate(String birthDate){
        this.birthDate = birthDate;
    }

    public void setState(){
        /*long currentTime = System.currentTimeMillis();

        if (currentTime)
        this.state = state;*/
        this.state = stateList[random.nextInt(stateListSize)];
        checkState();
    }


    private void checkState() {
        if (!state.equals("정상")){
            stateTime = timeOver[random.nextInt(timeOverSize)];
        }
    }

    public String getName(){return this.name;}
    public String getAddress(){return this.address;}

    public String getZipCode(){return this.zipCode;}
    public long getTimeLastSent(){return  this.timeLastSent;}
    public long getTimeLastStay(){return this.timeLastStay;}

    public String getState(){return this.state;}
    public String getStateTime(){return this.stateTime;}
    public String getBirthDate(){return this.birthDate;}
    public String getPhoneNumber(){return this.phoneNumber;}
}
