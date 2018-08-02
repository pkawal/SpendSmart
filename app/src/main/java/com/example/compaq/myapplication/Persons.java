package com.example.compaq.myapplication;

public class Persons {
    String id;
    int amount;

    public Persons(String id, int amount) {
        this.id = id;
        this.amount = amount;
    }
    public Persons()
    {

    }
    public String getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
