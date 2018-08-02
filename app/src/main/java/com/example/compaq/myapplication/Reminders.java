package com.example.compaq.myapplication;
import java.util.Date;

public class Reminders {
    public String company;
    public int amount;
    public Date date;

    public Reminders()
    {
        //Empty Constructor
    }
    public Reminders(String company, int amount, Date date) {
        this.company = company;
        this.amount = amount;
        this.date = date;
    }

    public int getAmount() {
        return amount;
    }

    public String getCompany() {
        return company;
    }

    public Date getDate() {
        return date;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
