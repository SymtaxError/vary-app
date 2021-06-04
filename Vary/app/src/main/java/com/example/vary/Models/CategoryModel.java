package com.example.vary.Models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.List;

@Entity
public class CategoryModel {
    @PrimaryKey @NonNull
    public final String mName;
    public final int mVersion;
    public final int mAccessLevel;

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

    public CategoryModel(@NotNull String mName, int mVersion, int mAccessLevel) {
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
