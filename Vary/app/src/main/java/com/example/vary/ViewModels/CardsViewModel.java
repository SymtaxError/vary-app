package com.example.vary.ViewModels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vary.Models.CurrentGameModel;
import com.example.vary.Models.SettingsModel;
import com.example.vary.R;
import com.example.vary.Repositories.CategoriesRepo;
import com.example.vary.Repositories.SettingsRepo;
import com.example.vary.Repositories.TeamsRepo;
import com.example.vary.Repositories.CurrentGameRepo;
import com.example.vary.UI.CardCallback;
import com.example.vary.UI.GameActions;
import com.example.vary.UI.PenaltyType;
import com.example.vary.UI.GameMode;
import com.example.vary.UI.LoadDataCallback;
import com.example.vary.Network.LoadStatus;
import com.example.vary.Models.CardModel;
import com.example.vary.Models.CategoryModel;
import com.example.vary.Models.TeamModel;

import java.util.ArrayList;
import java.util.List;

public class CardsViewModel extends AndroidViewModel implements LoadDataCallback {
    private static final TeamsRepo mTeamsRepo = TeamsRepo.getInstance();
    private static final MutableLiveData<LoadStatus> mLoadStatus = new MutableLiveData<>();
    private static final CategoriesRepo mCategoriesRepo = CategoriesRepo.getInstance();
    private static final CurrentGameRepo gameRepo = CurrentGameRepo.getInstance();
    private static final SettingsRepo settingsRepo = SettingsRepo.getInstance();


    public void getNewCategories() {
        mCategoriesRepo.getNewCategories();
    }

    public ArrayList<String> getCategoriesNames() {
        return mCategoriesRepo.getCategoriesNames();
    }

    public CardsViewModel(@NonNull Application application) {
        super(application);
        mCategoriesRepo.setNetworkService(application);
        mCategoriesRepo.setDbManager(application);
        mCategoriesRepo.setLoadCallback(this);
    }

    public void declineCard() {
        mCategoriesRepo.declineCard();
    }

    public void answerCard(int team) {
        mCategoriesRepo.answerCard(team);
    }

    public void changeAnswerState(int pos) {
        mCategoriesRepo.changeAnswerState(pos);
    }

    public boolean getAnswerState(int pos) {
        return mCategoriesRepo.getAnswerState(pos);
    }

    public void setAnsweredTeam(int team) {
        mCategoriesRepo.setAnsweredTeam(team);
    }

    public String getUsedCardByPosition(int pos) {
        String result = mCategoriesRepo.getUsedCardByPosition(pos);
        int team = mCategoriesRepo.getAnsweredTeam(pos);
        Integer time = getTimerCount().getValue();
        if (time == null)
            time = 0;
        if (gameRepo.getSteal() && pos == getAmountOfUsedCards() - 1 && team != -1 && time != -2) {
            result = result + " (" + getCurTeamName(team) + ")";
        }
        return result;
    }

    public void setCardsCallback(CardCallback callback) {
        mCategoriesRepo.setCardsCallback(callback);
    }

    public int getAmountOfUsedCards() {
        return mCategoriesRepo.getAmountOfUsedCards();
    }

    public void newRoundMix() {
        mCategoriesRepo.newRoundMix();
    }

    public LiveData<List<CardModel>> getCards() {
        return mCategoriesRepo.getCards();
    }

    public String getCard() {
        return mCategoriesRepo.getCard();
    }

    public LiveData<List<TeamModel>> getTeams() {
        return mTeamsRepo.getTeams();
    }

    public void removeTeams() {
        mTeamsRepo.removeTeams();
    }

    public void addTeams(String team_name) {
        mTeamsRepo.addTeam(team_name);
    }

    public void changeTeamPoints() {
        int lastTeam = mCategoriesRepo.getAnsweredTeam(getAmountOfUsedCards() - 1);
        if (lastTeam > 0 && gameRepo.getSteal())
            mTeamsRepo.increasePoints(lastTeam, 1);
        int points = gameRepo.getCurrentRoundPoints();
        mTeamsRepo.increasePoints(0, points);
    }

    public CharSequence[] getTeamsNamesChar(Context context) {
        int count = getAmountOfTeams() + 1;
        CharSequence[] teamsNames = new CharSequence[count];
        count = 0;
        for (String model: getTeamsNames())
        {
            teamsNames[count] = model;
            count += 1;
        }
        teamsNames[count] = context.getResources().getString(R.string.nobody);
        return teamsNames;
    }

    public int countPoints() {
        return mCategoriesRepo.countPoints(gameRepo.getPenalty());
    }

    public void renameTeam(String name, int pos) {
        mTeamsRepo.renameTeam(name, pos);
    }

    public void saveState(SharedPreferences.Editor editor) {
        List<CardModel> mCardModels = mCategoriesRepo.getCards().getValue();
        int timer = 0;
        if (mCardModels != null){
            if (getTimerCount().getValue() != null) {
                timer = getTimerCount().getValue();

            }
            gameRepo.saveState(mCardModels,
                    mTeamsRepo.getTeams().getValue(),
                    editor,
                    mCategoriesRepo.getCurrentPosition(),
                    mCategoriesRepo.getStartRoundPosition(),
                    timer
            );
        }
//        if (mCardModels == null) {
//            gameRepo.setNewGame(editor);
////            return; //честно не знаю на что может повлиять, но по крайней мере работает)
//        }
//        if (gameRepo.getGameModel().getValue().isVoid())
//        {
//            gameRepo.setNewGame(editor);
//            return;
//        }


    }

    public void sortTeamsByPoints() {
        mTeamsRepo.sortTeamsByPoints();
    }


    public void restoreState(SharedPreferences sp) {
        gameRepo.restoreState(sp);
    }

    public void continueOldGame() {
        if (!gameRepo.gameIsVoid()) {
            mCategoriesRepo.setCards(gameRepo.getCards());
            mTeamsRepo.setTeams(gameRepo.getTeams());
            mCategoriesRepo.setCurrentPosition(gameRepo.getCurrentCard());
            mCategoriesRepo.setStartRoundPosition(gameRepo.getStartRoundCard());
            mTimerCount.postValue(gameRepo.getRoundTimeLeft());
        }
    }

    public int getRoundPoints() {
        return gameRepo.getCurrentRoundPoints();
    }

    public void setNewGame(SharedPreferences.Editor editor) {
        mCategoriesRepo.setCards(null);
        gameRepo.setNewGame(editor);
    }


    public void setCurrentGame(int categoryIndex, int amountOfCards, int roundDuration, PenaltyType penalty, boolean steal, int startTeam) {
        mCategoriesRepo.fillCards(categoryIndex, amountOfCards);
        mCategoriesRepo.mixCards();
        mTeamsRepo.changeOrder(startTeam);
        gameRepo.setGameModel(steal, penalty, roundDuration);
    }

    public boolean endGame() {
        return mCategoriesRepo.newRoundRequired() && gameRepo.getGameMode() == GameMode.one_word_mode;
    }

    public int getCardsLeft() {
        return mCategoriesRepo.getCardsLeft();
    }

    public boolean nextRound() {
        mTeamsRepo.changeOrder(1);
        if (mCategoriesRepo.newRoundRequired()) {
            Log.d("Cards", "In mix required");
            mCategoriesRepo.mixCards();
            return gameRepo.nextGameMode();
        } else {
            newRoundMix();
        }
        setRoundTimeLeft(getRoundDuration());
        return true;
    }

    public GameMode getNextGameMode() {
        boolean nextGameMode = mCategoriesRepo.newRoundRequired();
        GameMode gameMode = getGameMode();
        Log.d("GameMode", "game mode == " + gameMode);
        if (nextGameMode) {
            if (gameMode == GameMode.explain_mode)
                gameMode = GameMode.gesture_mode;
            else if (gameMode == GameMode.gesture_mode)
                gameMode = GameMode.one_word_mode;
            else {
                gameMode = GameMode.end;
            }
        }
        return gameMode;
    }

    public PenaltyType getPenalty() {
        return gameRepo.getPenalty();
    }

    public GameMode getGameMode() {
        return gameRepo.getGameMode();
    }

    public boolean getSteal() {
        return gameRepo.getSteal();
    }

    public void setCurrentRoundPoints(int points) {
        gameRepo.setCurrentRoundPoints(points);
    }

    public void setRoundTimeLeft(int time) {
        gameRepo.setRoundTimeLeft(time);
    }

    public void setRoundDuration(int time) {
        gameRepo.setDuration(time);
    }

    public LiveData<List<CategoryModel>> getCategories() {
        return mCategoriesRepo.getCategories();
    }

// --Commented out by Inspection START (04.06.2021, 12:50):
//    public void getCount(DbManager.CountListener listener) {
//        mCategoriesRepo.getCount(listener);
//    }
// --Commented out by Inspection STOP (04.06.2021, 12:50)

    public LiveData<CurrentGameModel> getGameModel() {
        return gameRepo.getGameModel();
    }

    public LiveData<LoadStatus> getLoadStatus() {
        return mLoadStatus;
    }

    public ArrayList<String> getTeamsNames() {
        return mTeamsRepo.getTeamsNames();
    }

    public int getSize() {
        return mTeamsRepo.getSize();
    }

    public String getCurTeamName(int pos) {
        return mTeamsRepo.getTeamName(pos);
    }

    public void removeTeam(int pos) {
        mTeamsRepo.removeTeam(pos);
    }

    public void onLoad(Throwable throwable, boolean notify) {
        mLoadStatus.postValue(new LoadStatus(throwable, notify));
    }

    public int getAmountOfTeams() {
        return mTeamsRepo.getSize();
    }

    public String getTeamName(int position) {
        return mTeamsRepo.getTeamName(position);
    }

    public int getTeamPoints(int position) {
        return mTeamsRepo.getTeamPoints(position);
    }

    public int getRoundDuration() {
        return gameRepo.getRoundDuration();
    }

    /*
    GameActions
     */
    public void setGameAction(GameActions gameAction) {
        gameRepo.setCurrentGameAction(gameAction);
    }

    public GameActions getGameAction() {
        return gameRepo.getCurrentGameAction();
    }

    /*
    Таймер
     */
    private static final MutableLiveData<Integer> mTimerCount = new MutableLiveData<>();

    public void setTimerCount(Integer seconds) {
        mTimerCount.postValue(seconds);
    }

    public LiveData<Integer> getTimerCount() {
        return mTimerCount;
    }

    public Integer getTime() {
        return (mTimerCount.getValue() == null) ? 0 : mTimerCount.getValue();
    }

    /*
    Settings
     */

    public LiveData<SettingsModel> getSettings() {
        return settingsRepo.getSettings();
    }

    public void setSettings(boolean soundOn, boolean notificationsOn) {
        settingsRepo.setSettings(soundOn, notificationsOn);
    }

    public boolean getSoundState() {
        return settingsRepo.getSoundState();
    }

    public boolean getNotificationState() {
        return settingsRepo.getNotificationState();
    }

    public void setSoundState(boolean sound) {
        settingsRepo.setSoundState(sound);
    }

    public void setNotificationState(boolean notifications) {
        settingsRepo.setNotificationState(notifications);
    }

    public void restoreSettings(SharedPreferences sp, String soundKey, String checkUpdatesKey) {
        boolean sound = sp.getBoolean(soundKey, true);
        boolean updates = sp.getBoolean(checkUpdatesKey, true);
        setSettings(sound, updates);
    }

    public void saveSettings(SharedPreferences.Editor editor, String soundKey, String checkUpdatesKey) {
        editor.putBoolean(soundKey, getSoundState());
        editor.putBoolean(checkUpdatesKey, getNotificationState());
    }
    public void setLowerVolume(boolean lower) {
       settingsRepo.setLowerVolume(lower);
    }

    public boolean getLowerVolume() {
        return settingsRepo.getLowerVolume();
    }


}
