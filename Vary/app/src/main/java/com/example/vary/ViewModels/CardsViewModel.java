package com.example.vary.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vary.Models.CurrentGameModel;
import com.example.vary.Repositories.CategoriesRepo;
import com.example.vary.Repositories.TeamsRepo;
import com.example.vary.Repositories.CurrentGameRepo;
import com.example.vary.Database.DbManager;
import com.example.vary.UI.FineType;
import com.example.vary.UI.GameMode;
import com.example.vary.UI.LoadDataCallback;
import com.example.vary.Network.LoadStatus;
import com.example.vary.Models.CardModel;
import com.example.vary.Models.CategoryModel;
import com.example.vary.Models.TeamModel;

import java.util.ArrayList;
import java.util.List;

public class CardsViewModel extends AndroidViewModel implements LoadDataCallback {
    private static TeamsRepo mTeamsRepo = TeamsRepo.getInstance();
    private static LiveData<List<TeamModel>> mTeams = mTeamsRepo.getTeams();
    private static MutableLiveData<LoadStatus> mLoadStatus = new MutableLiveData<>();
    private static CategoriesRepo mCategoriesRepo = CategoriesRepo.getInstance();
    private static LiveData<List<CategoryModel>> mCategories = mCategoriesRepo.getCategories();
    private static CurrentGameRepo gameRepo = CurrentGameRepo.getInstance();
    private static LiveData<CurrentGameModel> mGameModel = gameRepo.getGameModel();

    public void getNewCategories() {
        mCategoriesRepo.getNewCategories(this);
    }

    public ArrayList<String> getCategoriesNames() {
        return mCategoriesRepo.getCategoriesNames();
    }

    public CardsViewModel(@NonNull Application application) {
        super(application);
        mTeamsRepo.setDbManager(application);
        mCategoriesRepo.setDbManager(application);
        mCategoriesRepo.setNetworkService(application);
        gameRepo.setDbManager(application);
    }

    public void smth() {
        DbManager.getInstance(getApplication());
    }

    public void declineCard() {
        mCategoriesRepo.declineCard();
    }

    public void answerCard() {
        mCategoriesRepo.answerCard();
    }

    public LiveData<List<CardModel>> getAnsweredCards() {
        return mCategoriesRepo.getAnsweredCards();
    }

    public LiveData<List<CardModel>> getDeclinedCards() {
        return mCategoriesRepo.getDeclinedCards();
    }

    public void makeDeclined(int dec_pos) {
        mCategoriesRepo.makeDeclined(dec_pos);
    }

    public void makeAnswered(int ans_pos) {
        mCategoriesRepo.makeAnswered(ans_pos);
    }

    public void newRoundMix() {
        mCategoriesRepo.newRoundMix();
    }
    public LiveData<List<TeamModel>> getTeams() {
        return mTeams;
    }

    public void removeTeams() {
        mTeamsRepo.removeTeams();
    }

    public void addTeams(String team_name) {
        mTeamsRepo.addTeam(team_name);
    }

    public void increasePoints(int pos, int newPoints) {
        mTeamsRepo.increasePoints(pos, newPoints);
    }

    public void renameTeam(String name, int pos) {
        mTeamsRepo.renameTeam(name, pos);
    }

    public TeamModel getTeam(int position) {
        return mTeamsRepo.getTeam(position);
    }

    public void fillCards(int index, int amount) {
        mCategoriesRepo.fillCards(index, amount);
    }

    public LiveData<List<CardModel>> getCards() {
        return mCategoriesRepo.getCards();
    }

    public void mixCards() {
        mCategoriesRepo.mixCards();
    }

    public void saveState() {

    }

    public void setCurrentGame(int categoryIndex, int amountOfCards, int roundDuration, FineType fine, boolean steal, GameMode gameMode, int startTeam) {
        mCategoriesRepo.fillCards(categoryIndex, amountOfCards);
        mCategoriesRepo.mixCards();
        mTeamsRepo.changeOrder(startTeam);
        gameRepo.setGameModel(steal, fine, roundDuration, gameMode);
    }

    public LiveData<List<CategoryModel>> getCategories() {
        mCategories = mCategoriesRepo.getCategories();
        return mCategories;
    }

    public LiveData<CurrentGameModel> getGameModel() {
        mGameModel = gameRepo.getGameModel();
        return mGameModel;
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
        return mTeamsRepo.getCurTeamName(pos);
    }

    public void removeTeam(int pos) {
        mTeamsRepo.removeTeam(pos);
    }

    public void onLoad(Throwable throwable) {
        mLoadStatus.postValue(new LoadStatus(throwable));
    }

    public String getCard() {
        return mCategoriesRepo.getCard();
    }

    public int getAmountOfTeams() {
        return mTeamsRepo.getSize();
    }

    public int getAmountOfCards() {
        return mCategoriesRepo.getAmountOfCards();
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

    public String getAnsweredCardByPosition(int position) {
        return mCategoriesRepo.getAnsweredCardByPosition(position);
    }

    public String getDeclinedCardByPosition(int position) {
        return mCategoriesRepo.getDeclinedCardByPosition(position);
    }

    /*
    Таймер
     */
    private static MutableLiveData<Integer> mTimerCount = new MutableLiveData<>();

    public void setTimerCount(Integer seconds) {
        mTimerCount.postValue(seconds);
    }

    public LiveData<Integer> getTimerCount() {
        return mTimerCount;
    }
}
