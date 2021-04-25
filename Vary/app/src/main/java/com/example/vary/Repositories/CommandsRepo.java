package com.example.vary.Repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vary.Database.DbManager;
import com.example.vary.Models.CommandModel;

import java.util.ArrayList;
import java.util.List;


public class CommandsRepo {
    private MutableLiveData<List<CommandModel>> mCommands = new MutableLiveData<>();
    private static CommandsRepo sInstance;
    private static DbManager dbManager = null;

    public CommandsRepo() {
        mCommands.setValue(new ArrayList<>());
    }

    public void setDbManager(Context context) {
        dbManager = DbManager.getInstance(context);
    }

    public void changeOrder(int newStartIndex) {
        List<CommandModel> commands = mCommands.getValue();
        if (commands != null) {
            for (int i = 0; i < newStartIndex; i++) {
                CommandModel command = commands.remove(i);
                commands.add(command);
            }
            mCommands.postValue(commands);
        }
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
                .increasePoints(newPoints);
        mCommands.postValue(listCommands);
    }

    public void renameCommand(String name, int pos) {

        List<CommandModel> listCommands = mCommands.getValue();
        listCommands
                .get(pos)
                .setName(name);
        mCommands.postValue(listCommands);
    }

    public int getSize() {
        return mCommands.getValue()
                .size();
    }

    public String getCurCommandName(int pos) {
        return mCommands
                .getValue()
                .get(pos)
                .getName();
    }

    public ArrayList<String> getCommandNames() {
        ArrayList<String> names = new ArrayList<>();

        for (int i = 0; i < getSize(); i++) {
            names.add(mCommands
                    .getValue()
                    .get(i)
                    .getName());
        }

        return names;
    }

}
