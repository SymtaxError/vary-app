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

import com.example.vary.Models.TeamModel;
import com.example.vary.R;
import com.example.vary.ViewModels.CardsViewModel;

public class SetTeamsFragment extends Fragment implements OnDeleteTeamClickListener, OnAddTeamClickListener, OnChangeTeamClickListener {
    RecyclerView recyclerView;
    TeamsAdapter adapter = null;
    CallbackFragment fCallback;
    View view;

    private CardsViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.set_commands_fragment_placeholder, container, false);

        adapter = new TeamsAdapter();
        adapter.setOnDeleteTeamClickListener(this);
        adapter.setOnAddTeamClickListener(this);
        adapter.setOnChangeTeamClickListener(this);
        recyclerView = view.findViewById(R.id.commands_list);
        recyclerView.setItemViewCacheSize(10);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        bindButton(R.id.open_game_settings, GameActions.open_game_settings);
        Observer<List<TeamModel>> observer = new Observer<List<TeamModel>>() {
            @Override
            public void onChanged(List<TeamModel> commandModels) {
                if (commandModels != null) {
                    adapter.setViewModel(viewModel);
                    adapter.notifyDataSetChanged();
                }
            }
        };
        viewModel = new ViewModelProvider(requireActivity()).get(CardsViewModel.class);
        viewModel
                .getTeams()
                .observe(getViewLifecycleOwner(), observer);

        regulateMinAmount();
        return view;
    }

    void setCallback(CallbackFragment callback) {
        fCallback = callback;
    }

    void bindButton(int id, GameActions action) {
        Button button = view.findViewById(id);
        button.setOnClickListener(v -> fCallback.callback(action));
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
                    viewModel.renameTeam(commandInput.getText().toString(), pos);
                })
                .setNegativeButton(cancel, (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void addItem() { // Добавить элемент
        String command = getResources().getString(R.string.command);
        int number = viewModel.getSize() + 1;
        viewModel.addTeams(command + ' ' + number);
    }

    public void deleteItem(int pos) { // Удалить элемент
        int amount = getResources().getInteger(R.integer.min_commands_amount);
        if (adapter != null && adapter.getItemCount() > amount + 1) {
            viewModel.removeTeam(pos);
        }
    }
}
