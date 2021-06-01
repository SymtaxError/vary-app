package com.example.vary.Repositories;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.vary.UI.GameActions;
import com.example.vary.UI.PenaltyType;
import com.example.vary.UI.GameMode;
import com.example.vary.Models.CardModel;
import com.example.vary.Models.TeamModel;
import com.example.vary.Models.CurrentGameModel;
import com.google.gson.Gson;

import java.util.List;
import java.util.Objects;

public class CurrentGameRepo {
    private final MutableLiveData<CurrentGameModel> gameModel = new MutableLiveData<>();
    private static CurrentGameRepo sInstance = null;

    public static synchronized CurrentGameRepo getInstance() {
        if (sInstance == null) {
            sInstance = new CurrentGameRepo();
        }
        return sInstance;
    }

    public LiveData<CurrentGameModel> getGameModel() {
//        if (gameModel.getValue() == null) {
//            restoreState();
//        }
        return gameModel;
    }

    public void saveState(List<CardModel> cards, List<TeamModel> commands, SharedPreferences.Editor editor,
                          int currentCard, int startCard, int roundTimeLeft) {
        CurrentGameModel model = gameModel.getValue();
        if (model == null) {
            return;
        }
        editor.clear();
        model.setTeams(commands);
        model.setCardModelList(cards);
        model.setCurrentAndStartCard(currentCard, startCard);
        model.setRoundTimeLeft(roundTimeLeft);
//        gameModel.postValue(model);

        Gson gson = new Gson();
        String json = gson.toJson(model);
        editor.putString("current_game_model", json);
        editor.commit();
        //save to db
    }


    public CurrentGameModel restoreState(SharedPreferences sp) {
        Gson gson = new Gson();
        String json = sp.getString("current_game_model", "");
        CurrentGameModel modelRestore = gson.fromJson(json, CurrentGameModel.class);

        if (modelRestore == null) {
            return null;
        }

        gameModel.postValue(modelRestore);
        return modelRestore;
        // load from db
    }

    public boolean gameIsVoid() {
        return Objects.requireNonNull(gameModel.getValue()).isVoid();
    }


    public void setGameModel(boolean steal, PenaltyType penalty, int roundDuration) {
        CurrentGameModel gameModelValue = gameModel.getValue();
        if (gameModelValue != null) {
            gameModelValue.setRoundDuration(roundDuration);
            gameModelValue.setSteal(steal);
            gameModelValue.setPenalty(penalty);
        } else
            gameModelValue = new CurrentGameModel(steal, penalty, roundDuration);
        gameModel.postValue(gameModelValue);
    }

    public void setDuration(int duration) {
        CurrentGameModel model = gameModel.getValue();
        assert model != null;
        model.setRoundDuration(duration);
        gameModel.postValue(model);
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
                Log.d("GameMode", "changed");
                game.setRoundTimeLeft(game.getRoundDuration());
                gameModel.postValue(game);
            }
        }
        return result;
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
        return PenaltyType.lose_points;
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
        return true;
    }

    public List<CardModel> getCards() {
        if (gameModel.getValue() != null) {
            return gameModel.getValue().getCardModelList();
        }
        return null;
    }

    public List<TeamModel> getTeams() {
        if (gameModel.getValue() != null) {
            return gameModel.getValue().getTeams();
        }
        return null;
    }

    public void setCurrentAndStartCard(int curCard, int startCard) {
        CurrentGameModel model = gameModel.getValue();
        if (model != null) {
            model.setCurrentAndStartCard(curCard, startCard);
            gameModel.postValue(model);
        }
    }

    public void setNewGame(SharedPreferences.Editor editor) {
        editor.clear();
        editor.commit();
//        CurrentGameModel model = new CurrentGameModel(false, PenaltyType.no_penalty, )
        gameModel.postValue(new CurrentGameModel(true, PenaltyType.lose_points, 0));
    }

    public int getStartRoundCard() {
        if (gameModel.getValue() != null) {
            return gameModel.getValue().getStartRoundCard();
        }
        return 0;
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

    public void setCurrentGameAction(GameActions gameAction) {
        CurrentGameModel model = gameModel.getValue();
        if (model != null) {
            model.setCurrentGameAction(gameAction);
            gameModel.postValue(model);
        }
    }

    public GameActions getCurrentGameAction() {
        CurrentGameModel value = gameModel.getValue();
        if (value != null) {
            return value.getCurrentGameAction();
        }
        return null;
    }
}
