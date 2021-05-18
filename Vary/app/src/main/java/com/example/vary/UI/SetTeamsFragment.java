package com.example.vary.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
    LinearLayoutManager layoutManager;
    int namePostfix = 1;
    private CardsViewModel viewModel;
    int amount = 0;

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

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
                improveVisibility(dy);
            }
        });
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
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
        view.post(new Runnable() {
            @Override
            public void run() {
                RelativeLayout root = view.findViewById(R.id.team_layout);
                int viewHeight = root.getHeight();
                amount = recyclerView.getHeight() / viewHeight - 1;
            }
        });
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
        if (viewModel.getAmountOfTeams() < min_amount)
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
        viewModel.addTeams(command + ' ' + namePostfix);
        namePostfix++;
    }

    public void deleteItem(int pos) { // Удалить элемент
        int amount = getResources().getInteger(R.integer.min_commands_amount);
        if (adapter != null && adapter.getItemCount() > amount + 1) {
            viewModel.removeTeam(pos);
        }
    }

    private void improveVisibility(int dy) {
        if (layoutManager != null) {
            int index = layoutManager.findFirstVisibleItemPosition();
            if (dy > 0) {
//                Log.d("HELP", "increase");
                index += 1;
            }
            else {
                index -= 1;
//                Log.d("HELP", "decrease");
            }
            if (viewModel.getAmountOfTeams() < amount) {
                index = 0;
            } else if (viewModel.getAmountOfTeams() - index < amount) {
//                Log.d("HELP", "Normalize, amount = " + viewModel.getAmountOfTeams() + "index = " + index);
                index = viewModel.getAmountOfTeams() - 10;
            }
//            Log.d("HELP", "erm" + index);
            layoutManager.scrollToPositionWithOffset(index, 0);
        }
    }

}
