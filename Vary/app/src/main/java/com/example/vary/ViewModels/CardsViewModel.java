package com.example.vary.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vary.Repositories.CategoriesRepo;
import com.example.vary.Repositories.CommandsRepo;
import com.example.vary.Repositories.CurrentGameRepo;
import com.example.vary.Database.DbManager;
import com.example.vary.UI.FineType;
import com.example.vary.UI.GameMode;
import com.example.vary.UI.LoadDataCallback;
import com.example.vary.Repositories.LoadStatus;
import com.example.vary.Models.CardModel;
import com.example.vary.Models.CategoryModel;
import com.example.vary.Models.CommandModel;

import java.util.ArrayList;
import java.util.List;

public class CardsViewModel extends AndroidViewModel implements LoadDataCallback {
    private static CommandsRepo mCommandsRepo = CommandsRepo.getInstance();
    private static LiveData<List<CommandModel>> mCommands = mCommandsRepo.getCommands();
    private static MutableLiveData<LoadStatus> mLoadStatus = new MutableLiveData<>();
    private static CategoriesRepo mCategoriesRepo = CategoriesRepo.getInstance();
    private static LiveData<List<CategoryModel>> mCategories = mCategoriesRepo.getCategories();
    private static CurrentGameRepo gameRepo = CurrentGameRepo.getInstance();

    public void getNewCategories(int version) {
        mCategoriesRepo.getNewCategories(version, this);
        
    }

    public ArrayList<String> getCategoriesNames() {
        return mCategoriesRepo.getCategoriesNames();
    }


    public CardsViewModel(@NonNull Application application) {
        super(application);
        mCommandsRepo.setDbManager(application);
        mCategoriesRepo.setDbManager(application);
        mCategoriesRepo.setNetworkService(application);
        gameRepo.setDbManager(application);
    }

    public void smth() {
        DbManager.getInstance(getApplication());
    }


    public LiveData<List<CommandModel>> getCommands() {
        return mCommands;
    }

    public void removeCommands() {
        mCommandsRepo.removeCommands();
    }

    public void addCommand(String command_name) {
        mCommandsRepo.addCommand(command_name);
    }

    public void increasePoints(int pos, int newPoints) {
        mCommandsRepo.increasePoints(pos, newPoints);
    }

    public void renameCommand(String name, int pos) {
        mCommandsRepo.renameCommand(name, pos);
    }

    public CommandModel getCommand(int position) {
        return mCommandsRepo.getCommand(position);
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

    public void setCurrentGame(int categoryIndex, int amountOfCards, int roundDuration, FineType fine, boolean steal, GameMode gameMode, int startCommand) {
        mCategoriesRepo.fillCards(categoryIndex, amountOfCards);
        mCategoriesRepo.mixCards();
        mCommandsRepo.changeOrder(startCommand);
        gameRepo.setGameModel(steal, fine, roundDuration, gameMode);
    }

    public LiveData<List<CategoryModel>> getCategories() {
        mCategories = mCategoriesRepo.getCategories();
        return mCategories;
    }

    public LiveData<LoadStatus> getLoadStatus() {
        return mLoadStatus;
    }

    public ArrayList<String> getCommandNames() {
        return mCommandsRepo.getCommandNames();
    }

    public int getSize() {
        return mCommandsRepo.getSize();
    }

    public String getCurCommandName(int pos) {
        return mCommandsRepo.getCurCommandName(pos);
    }

    public void removeCommand(int pos) {
        mCommandsRepo.removeCommand(pos);
    }

    public void onLoad(Throwable throwable) {
        mLoadStatus.postValue(new LoadStatus(throwable));
    }

    public String getCard() {
        return mCategoriesRepo.getCard();
    }

    public int getAmountOfCards() {
        return mCategoriesRepo.getAmountOfCards();
    }

}
