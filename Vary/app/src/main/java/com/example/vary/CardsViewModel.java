package com.example.vary;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class CardsViewModel extends ViewModel implements LoadDataCallback{
    private CardsRepo mCardsRepo = new CardsRepo();
    private CommandsRepo mCommandsRepo = new CommandsRepo();
    private LiveData<List<CommandModel>> mCommands = mCommandsRepo.getCommands();
    private int version;
    private LiveData<List<CardModel>> mCards;
    private MutableLiveData<LoadStatus> mLoadStatus = new MutableLiveData<>();

    private CategoriesRepo mCategoriesRepo = new CategoriesRepo();
    private LiveData<List<CategoryModel>> mCategories = mCategoriesRepo.getCategories();

    public void getNewCategories() {
        mCategoriesRepo.getNewCategories(version, this);
    }


//    public LiveData<List<CardModel>> getCards() {
//        return mCards;
//    }

//    public LiveData<List<CategoryModel>> getDecks() {
//        return mDecks;
//    }


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

    public void getCards(int index) {
        mCategoriesRepo.getCards(index);
    }

    public void mixCards() {
        mCategoriesRepo.mixCards();
    }

    public void saveState() {
        mCategoriesRepo.saveState();
        mCommandsRepo.saveState();
    }

    public int getSize() {
        return mCommandsRepo.getSize();
    }

    public void removeCommand(int pos) {
        mCommandsRepo.removeCommand(pos);
    }

    public void onError(Throwable throwable) {
        mLoadStatus.postValue(new LoadStatus(throwable));
    }

    public CardModel getCard(int pos) {
        return mCardsRepo.getCard();
    }

}
