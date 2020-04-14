package com.example.corona_administrator;

import java.util.Random;
import java.util.concurrent.TimeUnit;


public class Person {
    private static final String STATE_NORMAL = "정상";
    private static final String STATE_LOST_COMMUN = "통신안됨";
    private static final String STATE_LEFT = "이탈";

    private static final long TIME_MARGIN = 5;

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

    /*public Person(String name, String address, String state, String birthDate, String phoneNumber) {
        this.name = name;
        this.address = address;
        this.state = state;

        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;


        checkState();
    }*/

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
        this.timeLastSent = TimeUnit.MILLISECONDS.toMinutes(timeLastSent);
    }

    public void setTimeLastStay(long timeLastStay){
        this.timeLastStay = TimeUnit.MILLISECONDS.toMinutes(timeLastStay);
    }

    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    public void setBirthDate(String birthDate){
        this.birthDate = birthDate;
    }

    public void setState(){
        long currentTimeMin = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis());

        if (timeLastStay == timeLastSent){
            if (currentTimeMin < timeLastStay + TIME_MARGIN)
                this.state = STATE_NORMAL;
            else
                this.state = STATE_LOST_COMMUN;
        } else {
            this.state = STATE_LEFT;
        }

        checkState(currentTimeMin);
    }


    private void checkState(long currentTimeMin) {
        if (state.equals("정상"))
            return;

        long timeDifference = currentTimeMin - timeLastStay;

        if (timeDifference <= 10){
            stateTime = "10분 이하";
        }
        else if (timeDifference > 10 && timeDifference <= 30){
            stateTime = "10분 이상";
        }
        else if (timeDifference > 30 && timeDifference <= 60){
            stateTime = "30분 이상";
        }
        else if (timeDifference > 60){
            stateTime = "1시간 이상";
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
