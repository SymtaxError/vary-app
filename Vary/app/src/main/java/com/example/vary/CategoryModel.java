package com.example.vary;

import java.util.List;
import java.util.UUID;

public class CategoryModel {
    UUID mId;
    String mName;
    boolean mAdult;
    int mVersion;
    int mAccessLevel;

    List<CardModel> mCards;

    public CategoryModel(UUID id, String name, int version, boolean adult, int accessLevel, List<CardModel> cards) {
        mId = id;
        mVersion = version;
        mAdult = adult;
        mName = name;
        mAccessLevel = accessLevel;
        mCards = cards;
    }

}
