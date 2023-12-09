package com.example.myapplication;

public class Donation {
    private String name;
    private String mobile;
    private int amount;

    public Donation(String name, String mobile, int amount) {
        this.name = name;
        this.mobile = mobile;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public int getAmount() {
        return amount;
    }
    @Override
    public String toString() {
        return "Name: " + name + "\nMobile: " + mobile + "\nAmount: " + amount + " RS";
    }
}