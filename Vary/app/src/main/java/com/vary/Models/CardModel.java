package com.vary.Models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class CardModel {
    public final int mVersion;
    @PrimaryKey @NonNull
    public final String mId;
    public final String mText;
    public final String mCategory;
    @Ignore
    private int answeredTeam;
    @Ignore
    private boolean mAnswered;

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

// --Commented out by Inspection START (04.06.2021, 12:48):
//    public int getVersion() {
//        return mVersion;
//    }
// --Commented out by Inspection STOP (04.06.2021, 12:48)

    @Ignore
    public void setAnswerState(boolean ansState) {
        mAnswered = ansState;
    }

    @Ignore
    public void changeAnswerState() {
        mAnswered = !mAnswered;
    }

    @Ignore
    public boolean getAnswerState() {
        return mAnswered;
    }

    @Ignore
    public int getAnsweredTeam() {
        return answeredTeam;
    }

    @Ignore
    public void setAnsweredTeam(int team) {
        answeredTeam = team;
    }

}
