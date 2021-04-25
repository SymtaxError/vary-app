package com.example.vary.Models;

import com.example.vary.Models.CardModel;

import java.util.List;

public class CategoryModel {
    String mName;
    int mVersion;
    int mAccessLevel;

    List<CardModel> mCards;

    public String getName() {
        return mName;
    }

    public List<CardModel> getCards() {
        return mCards;
    }

    public CategoryModel(String name, int version, int accessLevel, List<CardModel> cards) {
        mVersion = version;
        mName = name;
        mAccessLevel = accessLevel;
        mCards = cards;
    }

}
