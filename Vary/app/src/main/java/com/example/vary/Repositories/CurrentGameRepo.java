package com.example.vary.Repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vary.Database.DbManager;
import com.example.vary.UI.PenaltyType;
import com.example.vary.UI.GameMode;
import com.example.vary.Models.CardModel;
import com.example.vary.Models.TeamModel;
import com.example.vary.Models.CurrentGameModel;

import java.util.List;

public class CurrentGameRepo {
    private MutableLiveData<CurrentGameModel> gameModel = new MutableLiveData<>();
    private static CurrentGameRepo sInstance = null;
    private DbManager dbManager;

    public static synchronized CurrentGameRepo getInstance() {
        if (sInstance == null) {
            sInstance = new CurrentGameRepo();
        }
        return sInstance;
    }

    public void setDbManager(Context context) {
        dbManager = DbManager.getInstance(context);
    }

    public LiveData<CurrentGameModel> getGameModel() {
        if (gameModel.getValue() == null) {
            restoreState();
        }
        return gameModel;
    }

    public void saveState(List<CardModel> cards, List<TeamModel> commands) {
        gameModel.getValue().setCardModelList(cards);
        gameModel.getValue().setCommands(commands);
        //save to db
    }

    public void setGameModel(boolean steal, PenaltyType penalty, int roundDuration, GameMode gameMode) {
        gameModel.postValue(new CurrentGameModel(steal, penalty, roundDuration, gameMode));
    }

    protected void restoreState() {
        //load from db
    }

    public int getRoundDuration() {
        return gameModel.getValue().getRoundDuration();
    }
}
