package com.example.vary.Models;

public class CommandModel {
    String mName;
    int mPoints;
    int mId;

    public CommandModel(String name, int id) {
        mName = name;
        mPoints = 0;
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public int getPoints() {
        return mPoints;
    }

    public void increasePoints(int points) {
        mPoints += points;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getId() {
        return mId;
    }
}
