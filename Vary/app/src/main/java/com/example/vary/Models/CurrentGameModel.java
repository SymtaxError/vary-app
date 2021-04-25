package com.example.vary.Models;

import com.example.vary.UI.FineType;
import com.example.vary.UI.GameMode;

import java.util.List;

public class CurrentGameModel {
    List<CardModel> mCardModelList;
    List<CommandModel> mCommands;
    boolean mSteal;
    FineType mFine;
    int mRoundDuration;
    GameMode mCurMode;

    public CurrentGameModel(boolean steal, FineType fine, int roundDuration, GameMode curMode) {
        mSteal = steal;
        mFine = fine;
        mRoundDuration = roundDuration;
        mCurMode = curMode;
    }

    public void setCardModelList(List<CardModel> cardModelList) {
        mCardModelList = cardModelList;
    }

    public void setCommands(List<CommandModel> commands) {
        mCommands = commands;
    }
}
