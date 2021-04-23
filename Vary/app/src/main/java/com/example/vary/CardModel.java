package com.example.vary;

import java.util.UUID;

public class CardModel {
    int mVersion;
    UUID mId;
    String mText;



    public CardModel(int version, UUID id, String text) {
        mVersion = version;
        mId = id;
        mText = text;
    }
}
