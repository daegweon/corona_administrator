package com.example.corona_administrator;

public class Manager {
    private static Manager mManager = null;
    private String mName, mID, mPhone;

    private Manager(){}

    public static Manager getInstance(){
        if (mManager == null)
            mManager = new Manager();

        return mManager;
    }

    public void setInfo(String name, String ID, String phone){
        mName = name;
        mID = ID;
        mPhone = phone;
    }

    public String getName(){return mName;}
    public String getID() {return mID;}
    public String getPhone() {return mPhone;}
}
