package com.example.vary.Models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Пожалуйста, не трогайте, если не знаете, как это работает!
 */

@Entity
public class CardModel {
    public int mVersion;
    @PrimaryKey @NonNull
    public String mId;
    public String mText;
    public String mCategory;

    @Ignore
    public String getText() {
        return mText;
    }

    public CardModel(int mVersion, @org.jetbrains.annotations.NotNull String mId, String mText, String mCategory) {
        this.mVersion = mVersion;
        this.mId = mId;
        this.mText = mText;
        this.mCategory = mCategory;
    }

    public int getVersion() {
        return mVersion;
    }
}
