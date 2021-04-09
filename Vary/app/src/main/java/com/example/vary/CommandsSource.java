package com.example.vary;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("CollectionAddedToSelf")
public class CommandsSource {
    public final List<CommandModel> commands;
    private static CommandsSource sInstance;

    public CommandsSource() {
        commands = new ArrayList<>();
    }

    public List<CommandModel> getRemoteData() {
        return commands;
    }

    public void removeCommands() {
        commands.removeAll(commands);
    }

    public void addCommand(String command_name) {
        commands.add(new CommandModel(command_name));
    }

    public synchronized static CommandsSource getInstance() {
        if (sInstance == null) {
            sInstance = new CommandsSource();
        }

        return sInstance;
    }

    public static ArrayList getCommandNames() {
        ArrayList<String> names = new ArrayList<>();

        for (int i = 0; i < sInstance.commands.size(); i++) {
            names.add(sInstance.commands.get(i).mName);
        }

        return names;
    }

    public void renameCommand(String name, int pos) {
        commands.get(pos).mName = name;
    }
}
