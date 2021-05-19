package com.example.vary.Models;

import com.example.vary.UI.PenaltyType;
import com.example.vary.UI.GameMode;

import java.util.List;

public class CurrentGameModel {
    List<CardModel> mCardModelList;
    List<TeamModel> mCommands;
    boolean mSteal;
    PenaltyType mFine;
    int mRoundDuration;
    GameMode mCurMode;

    public CurrentGameModel(boolean steal, PenaltyType fine, int roundDuration, GameMode curMode) {
        mSteal = steal;
        mFine = fine;
        mRoundDuration = roundDuration;
        mCurMode = curMode;
    }

    public void setCardModelList(List<CardModel> cardModelList) {
        mCardModelList = cardModelList;
    }

    public void setCommands(List<TeamModel> teams) {
        mCommands = teams;
    }

    public int getRoundDuration() {
        return mRoundDuration;
    }
}
