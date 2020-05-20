package com.example.corona_administrator;

import java.util.Random;
import java.util.concurrent.TimeUnit;


public class Person {
    public static final String STATE_NORMAL = "정상";
    public static final String STATE_LOST_COMMUN = "통신안됨";
    public static final String STATE_LEFT = "이탈";

    private static final long STAY_SENT_DIFF_MARGIN = 15; //15 secs
    private static final long STAY_CURRENT_DIFF_MARGIN = 10 * 60; //10 mins

    private String name;
    private String address;
    private String zipCode;

    private long timeLastSent;
    private long timeLastStay;

    private String state = "";
    private String stateTime = "";
    private String birthDate;
    private String phoneNumber;


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
        this.timeLastSent = TimeUnit.MILLISECONDS.toSeconds(timeLastSent);
    }

    public void setTimeLastStay(long timeLastStay){
        this.timeLastStay = TimeUnit.MILLISECONDS.toSeconds(timeLastStay);
    }

    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    public void setBirthDate(String birthDate){
        this.birthDate = birthDate;
    }

    public void setState(){
        long currentTimeSec = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

        if (Math.abs(timeLastStay - timeLastSent) < STAY_SENT_DIFF_MARGIN){

            if (currentTimeSec < timeLastStay + STAY_CURRENT_DIFF_MARGIN)
                this.state = STATE_NORMAL;
            else
                this.state = STATE_LOST_COMMUN;

        } else {
            this.state = STATE_LEFT;
        }

        setStateTime(currentTimeSec);
    }


    private void setStateTime(long currentTimeSec) {
        if (state.equals(STATE_NORMAL))
            return;

        long timeDifference = currentTimeSec - timeLastStay;

        if (timeDifference <= 10 * 60){
            stateTime = ": 10분 이하";
        }
        else if (timeDifference > 10 * 60 && timeDifference <= 30 * 60){
            stateTime = ": 10분 이상";
        }
        else if (timeDifference > 30 * 60 && timeDifference <= 60 * 60){
            stateTime = ": 30분 이상";
        }
        else if (timeDifference > 60 * 60){
            stateTime = ": 1시간 이상";
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
