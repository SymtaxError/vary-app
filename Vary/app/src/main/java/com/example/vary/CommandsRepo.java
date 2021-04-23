package com.example.vary;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("CollectionAddedToSelf")
public class CommandsRepo {
//    public final List<CommandModel> commands;

    private MutableLiveData<List<CommandModel>> mCommands = new MutableLiveData<>();
    private static CommandsRepo sInstance;

    public CommandsRepo() {
        mCommands.setValue(new ArrayList<>());
    }

    public void saveState() {

    }

    public LiveData<List<CommandModel>> getCommands() {
        return mCommands;
    }

    public void removeCommands() {
        List<CommandModel> listCommands = mCommands.getValue();
        listCommands.removeAll(listCommands);
        mCommands.postValue(listCommands);
    }

    public void addCommand(String command_name) {
        List<CommandModel> listCommands = mCommands.getValue();
        listCommands.add(new CommandModel(command_name, mCommands.getValue().size()));
        mCommands.postValue(listCommands);
    }

    public synchronized static CommandsRepo getInstance() {
        if (sInstance == null) {
            sInstance = new CommandsRepo();
        }

        return sInstance;
    }

    public CommandModel getCommand(int position) {
        return mCommands.getValue()
                .get(position);
    }

    public void removeCommand(int pos) {
        List<CommandModel> listCommands = mCommands.getValue();
        listCommands.remove(pos);
        mCommands.postValue(listCommands);
    }

    public void increasePoints(int pos, int newPoints) {

        List<CommandModel> listCommands = mCommands.getValue();
        listCommands
                .get(pos)
                .mPoints += newPoints;
        mCommands.postValue(listCommands);
    }

    public void renameCommand(String name, int pos) {

        List<CommandModel> listCommands = mCommands.getValue();
        listCommands
                .get(pos)
                .mName = name;
        mCommands.postValue(listCommands);
    }

    public int getSize() {
        return mCommands.getValue()
                .size();
    }

}
