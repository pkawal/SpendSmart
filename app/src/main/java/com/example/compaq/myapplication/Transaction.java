package com.example.compaq.myapplication;

import java.util.ArrayList;

public class Transaction {
    public int amount;
    public String place ;
    public String date;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String type;
    public ArrayList<Persons> persons;

    public Transaction( int amount, String place,String date) {

        this.amount = amount;
        this.place = place;
        this.date=date;
    }
    public Transaction( int amount, String place,String date,String type) {

        this.amount = amount;
        this.place = place;
        this.date=date;
        this.type=type;
    }

    public Transaction(int amount, String place, String date, ArrayList<Persons> persons) {
        this.amount = amount;
        this.place = place;
        this.date = date;
        this.persons = persons;
    }

    public ArrayList<Persons> getPersons() {
        return persons;
    }

    public void setPersons(ArrayList<Persons> persons) {
        this.persons = persons;
    }

    public Transaction()
    {

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setPlace(String place) {
        this.place = place;
    }


    public int getAmount() {
        return amount;
    }

    public String getPlace() {
        return place;
    }
}
