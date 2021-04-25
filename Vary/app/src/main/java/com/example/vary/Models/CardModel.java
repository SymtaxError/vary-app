package com.example.vary.Models;

import java.util.UUID;

public class CardModel {
    int mVersion;
    String mId;
    String mText;
    String mCategoryId;

    public String getText() {
        return mText;
    }

    public CardModel(int version, String id, String text, String categoryId) {
        mVersion = version;
        mId = id;
        mText = text;
        mCategoryId = categoryId;
    }
}
