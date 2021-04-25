package com.example.vary.UI;

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

import androidx.lifecycle.ViewModelProvider;

import java.util.List;
import androidx.lifecycle.Observer;

import com.example.vary.Models.CommandModel;
import com.example.vary.R;
import com.example.vary.ViewModels.CardsViewModel;

public class SetCommandsFragment extends Fragment implements OnDeleteCommandClickListener, OnAddCommandClickListener, OnChangeCommandClickListener {
    RecyclerView recyclerView;
    CommandsAdapter adapter = null;
    private CardsViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.set_commands_fragment_placeholder, container, false);

        adapter = new CommandsAdapter();
        adapter.setOnDeleteCommandClickListener(this);
        adapter.setOnAddCommandClickListener(this);
        adapter.setOnChangeCommandClickListener(this);
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
        Observer<List<CommandModel>> observer = new Observer<List<CommandModel>>() {
            @Override
            public void onChanged(List<CommandModel> commandModels) {
                if (commandModels != null) {
                    adapter.setViewModel(viewModel);
                    adapter.notifyDataSetChanged();
                }
            }
        };

        viewModel = new ViewModelProvider(requireActivity()).get(CardsViewModel.class);
        viewModel
                .getCommands()
                .observe(getViewLifecycleOwner(), observer);

        regulateMinAmount();
        return view;
    }

    protected void regulateMinAmount() { // Добавление двух команд по умолчанию
        int min_amount = getResources().getInteger(R.integer.min_commands_amount);
        if (viewModel.getSize() < min_amount)
        {
            addItem();
            addItem();
        }
    }

    public void renameItem(int pos) { // Переименовать команду
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
                    viewModel.renameCommand(commandInput.getText().toString(), pos);
                })
                .setNegativeButton(cancel, (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void addItem() { // Добавить элемент
        String command = getResources().getString(R.string.command);
        int number = viewModel.getSize() + 1;
        viewModel.addCommand(command + ' ' + number);
    }

    public void deleteItem(int pos) { // Удалить элемент
        int amount = getResources().getInteger(R.integer.min_commands_amount);
        if (adapter != null && adapter.getItemCount() > amount + 1) {
            viewModel.removeCommand(pos);
        }
    }
}
