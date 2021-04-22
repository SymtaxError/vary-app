package com.example.vary;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

public class SetCommandsFragment extends Fragment {
    RecyclerView recyclerView;
    CommandsAdapter adapter = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.set_commands_fragment_placeholder, container, false);


        adapter = new CommandsAdapter();
        adapter.setOnDeleteCommandClickListener(this::deleteItem);
        adapter.setOnAddCommandClickListener(this::addItem);
        adapter.setOnChangeCommandClickListener(this::renameItem);
        regulateMinAmount();

        recyclerView = view.findViewById(R.id.commands_list);
        recyclerView.setItemViewCacheSize(10);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);


        Button gameSettingsBut = view.findViewById(R.id.open_game_settings);
        gameSettingsBut.setOnClickListener(v -> {
            GameSettingsFragment fragment = new GameSettingsFragment();
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }


    protected void regulateMinAmount() { // Добавление двух команд по умолчанию
        int min_amount = getResources().getInteger(R.integer.min_commands_amount);
        if (CommandsSource.getInstance().getRemoteData().size() < min_amount)
        {
            addItem();
            addItem();
        }
    }

    protected void renameItem(int pos) { // Переименовать команду
        Context context = getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View promView = inflater.inflate(R.layout.add_command_window, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promView);
        EditText commandInput = promView.findViewById(R.id.edit_command_name);

        String add = getResources().getString(R.string.save);
        String cancel = getResources().getString(R.string.cancel);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(add, (dialog, which) -> {
                    CommandsSource
                            .getInstance()
                            .renameCommand(commandInput.getText().toString(), pos);
                    notifyChangedRecyclerView(pos);
                })
                .setNegativeButton(cancel, (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    protected void addItem() { // Добавить элемент
        String command = getResources().getString(R.string.command);
        int number = CommandsSource
                .getInstance()
                .getRemoteData()
                .size() + 1;
        CommandsSource
                .getInstance()
                .addCommand(command + ' ' + number);
        notifyAddedRecyclerView();
    }

    protected void deleteItem(int pos) { // Удалить элемент
        int amount = getResources().getInteger(R.integer.min_commands_amount);
        if (adapter != null && adapter.getItemCount() > amount + 1) {
            CommandsSource
                    .getInstance()
                    .getRemoteData()
                    .remove(pos);
            adapter.notifyItemRemoved(pos);
            adapter.notifyItemRangeChanged(pos, adapter.getItemCount());
        }
    }

    public void notifyAddedRecyclerView() {
        if (adapter != null) {

            adapter.notifyItemInserted(CommandsSource
                    .getInstance()
                    .getRemoteData()
                    .size());
        }
    }

    public void notifyChangedRecyclerView(int pos) {
        if (adapter != null) {
            adapter.notifyItemChanged(pos);
        }
    }

}