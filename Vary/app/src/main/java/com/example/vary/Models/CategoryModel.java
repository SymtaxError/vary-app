package com.example.vary.Models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.vary.Models.CardModel;

import java.util.List;

/**
 * Пожалуйста, не трогайте, если не знаете, как это работает!
 */

@Entity
public class CategoryModel {
    @PrimaryKey @NonNull
    public String mName;
    public int mVersion;
    public int mAccessLevel;

    @Ignore
    public List<CardModel> mCards;

    @Ignore
    public String getName() {
        return mName;
    }

    @Ignore
    public List<CardModel> getCards() {
        return mCards;
    }

    public CategoryModel(String mName, int mVersion, int mAccessLevel) {
        this.mVersion = mVersion;
        this.mName = mName;
        this.mAccessLevel = mAccessLevel;
//        this.mCards = mCards;
    }

    public int getVersion() {
        return mVersion;
    }


//    public CategoryModel(String mName, int mVersion, int mAccessLevel, List<CardModel> mCards) {
//        this.mVersion = mVersion;
//        this.mName = mName;
//        this.mAccessLevel = mAccessLevel;
//        this.mCards = mCards;
//    }

//    @Ignore
//    public CategoryModel(String name, int version, int accessLevel) {
//        mVersion = version;
//        mName = name;
//        mAccessLevel = accessLevel;
//    }

}
