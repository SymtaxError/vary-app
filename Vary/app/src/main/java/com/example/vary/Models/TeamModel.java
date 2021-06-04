package com.example.vary.Models;

public class TeamModel {
    String mName;
    int mPoints;
    final int mId;

    public TeamModel(String name, int id) {
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

}
