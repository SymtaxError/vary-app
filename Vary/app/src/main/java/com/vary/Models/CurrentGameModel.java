package com.vary.Models;

import com.vary.UI.GameActions;
import com.vary.UI.PenaltyType;
import com.vary.UI.GameMode;

import java.util.List;

public class CurrentGameModel {
    List<CardModel> mCardModelList;
    List<TeamModel> mTeams;
    boolean mSteal;
    PenaltyType mPenalty;
    int mRoundDuration;
    GameMode mCurMode = GameMode.explain_mode;
    int currentCard = 0;
    int startRoundCard = 0;
    int currentRoundPoints = 0;
    int roundTimeLeft = 0;
    GameActions currentGameAction;

    public CurrentGameModel(boolean steal, PenaltyType penalty, int roundDuration) {
        mSteal = steal;
        mPenalty = penalty;
        mRoundDuration = roundDuration;
        mCardModelList = null;
        mTeams = null;
    }

    public void setCurrentGameAction(GameActions gameAction) {
        currentGameAction = gameAction;
    }

    public GameActions getCurrentGameAction() {
        return currentGameAction;
    }

    public void setRoundDuration(int roundDuration) {
        mRoundDuration = roundDuration;
    }

    public void setPenalty(PenaltyType penalty) {
        mPenalty = penalty;
    }

    public void setSteal(boolean steal) {
        mSteal = steal;
    }


    public void setCardModelList(List<CardModel> cardModelList) {
        mCardModelList = cardModelList;
    }

    public void setTeams(List<TeamModel> teams) {
        mTeams = teams;
    }

    public int getRoundDuration() {
        return mRoundDuration;
    }

    public List<TeamModel> getTeams() {
        return mTeams;
    }

    public List<CardModel> getCardModelList() {
        return mCardModelList;
    }

    public boolean nextGameMode() {
        if (mCurMode == GameMode.explain_mode) {
            mCurMode = GameMode.gesture_mode;
        }
        else if (mCurMode == GameMode.gesture_mode) {
            mCurMode = GameMode.one_word_mode;
        }
        else {
            mCurMode = GameMode.end;
            return false;
        }
        return true;
    }

    public PenaltyType getPenalty() {
        return mPenalty;
    }

    public boolean getSteal() {
        return mSteal;
    }

    public GameMode getGameMode() {
        return mCurMode;
    }

    public void setCurrentAndStartCard(int curCard, int startCard) {
        currentCard = curCard;
        startRoundCard = startCard;
    }

    public int getCurrentCard() {
        return currentCard;
    }

    public int getStartRoundCard() {
        return startRoundCard;
    }

    public void setCurrentRoundPoints(int currentPoints) {
        currentRoundPoints = currentPoints;
    }

    public int getCurrentRoundPoints() {
        return currentRoundPoints;
    }

    public void setRoundTimeLeft(int time) {
        roundTimeLeft = time;
    }

    public int getRoundTimeLeft() {
        return roundTimeLeft;
    }

    public boolean isVoid() {
        return mCardModelList == null;
    }


}
