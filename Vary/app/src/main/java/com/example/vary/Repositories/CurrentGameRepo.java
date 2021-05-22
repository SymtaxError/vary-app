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
    private final MutableLiveData<CurrentGameModel> gameModel = new MutableLiveData<>();
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
        CurrentGameModel model = gameModel.getValue();
        if (model != null) {
            model.setCommands(commands);
            model.setCardModelList(cards);
            gameModel.postValue(model);
        }

        //save to db
    }

    public void setGameModel(boolean steal, PenaltyType penalty, int roundDuration) {
        gameModel.postValue(new CurrentGameModel(steal, penalty, roundDuration));
    }

    /*
      Возвращает true, если игра продолжается, иначе false
     */
    public boolean nextGameMode() {
        CurrentGameModel game = gameModel.getValue();
        boolean result = false;
        if (game != null) {
            result = game.nextGameMode();
            if (result) {
                gameModel.postValue(game);
            }
        }
        return result;
    }

    protected void restoreState() {
        //load from db
    }

    public int getRoundDuration() {
        if (gameModel.getValue() != null) {
            return gameModel.getValue().getRoundDuration();
        }
        return 0;
    }

    public PenaltyType getPenalty() {
        if (gameModel.getValue() != null) {
            return gameModel.getValue().getPenalty();
        }
        return PenaltyType.no_penalty;
    }

    public GameMode getGameMode() {
        if (gameModel.getValue() != null) {
            return gameModel.getValue().getGameMode();
        }
        return GameMode.explain_mode;
    }

    public boolean getSteal() {
        if (gameModel.getValue() != null) {
            return gameModel.getValue().getSteal();
        }
        return false;
    }

    public void setCurrentAndStartCard(int curCard, int startCard) {
        CurrentGameModel model = gameModel.getValue();
        if (model != null) {
            model.setCurrentAndStartCard(curCard, startCard);
            gameModel.postValue(model);
        }
    }

    public int getCurrentCard() {
        if (gameModel.getValue() != null) {
            return gameModel.getValue().getCurrentCard();
        }
        return 0;
    }

    public void setCurrentRoundPoints(int currentPoints) {
        CurrentGameModel model = gameModel.getValue();
        if (model != null) {
            model.setCurrentRoundPoints(currentPoints);
            gameModel.postValue(model);
        }
    }

    public int getCurrentRoundPoints() {
        if (gameModel.getValue() != null) {
            return gameModel.getValue().getCurrentRoundPoints();
        }
        return 0;
    }

    public void setRoundTimeLeft(int time) {
        CurrentGameModel model = gameModel.getValue();
        if (model != null) {
            model.setRoundTimeLeft(time);
            gameModel.postValue(model);
        }
    }

    public int getRoundTimeLeft() {
        if (gameModel.getValue() != null) {
            return gameModel.getValue().getRoundTimeLeft();
        }
        return 0;
    }
}
