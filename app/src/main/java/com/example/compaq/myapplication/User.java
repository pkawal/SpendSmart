package com.example.compaq.myapplication;

import com.example.compaq.myapplication.Transaction;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Israni on 05-12-2016.
 */
public class User {
    public String name;
    public String email;
    //public ArrayList<Transaction> transactions=new ArrayList<Transaction>();
    public HashMap<String,ArrayList<Transaction>> transactions;
    // public ArrayList<Reminders> reminders;

    public int spent;
    public int budget;

   /* public User(String name, String email, HashMap<String,ArrayList<Transaction>> transactions,ArrayList<Reminders> reminders, int spent, int budget) {
        this.name = name;
        this.email = email;
        this.transactions = transactions;
        this.spent = spent;
        this.budget = budget;
        this.reminders=reminders;
    }*/

    public User(String name, String email,HashMap<String,ArrayList<Transaction>> transactions, int spent, int budget) {
        this.name = name;
        this.email = email;
        this.transactions = transactions;
        this.spent = spent;
        this.budget = budget;
    }
    public User()
    {

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTransactions(HashMap<String,ArrayList<Transaction>> transactions) {
        this.transactions = transactions;
    }

    public void setSpent(int spent) {
        this.spent = spent;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

   /* public void setReminders(ArrayList<Reminders> reminders) {
        this.reminders = reminders;
    }*/

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public HashMap<String,ArrayList<Transaction>>  getTransactions() {
        return transactions;
    }

    public int getSpent() {
        return spent;
    }

    public int getBudget() {
        return budget;
    }

   /* public ArrayList<Reminders> getReminders() {
        return reminders;
    }*/
}
