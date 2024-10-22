package com.vary.UI;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import androidx.lifecycle.Observer;

import com.vary.Models.TeamModel;
import com.vary.R;
import com.vary.ViewModels.CardsViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SetTeamsFragment extends Fragment implements OnDeleteTeamClickListener, OnAddTeamClickListener, OnChangeTeamClickListener {
    private final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.5F);
    RecyclerView recyclerView;
    TeamsAdapter adapter = null;
    CallbackFragment fCallback;
    View view;
    LinearLayoutManager layoutManager;
    int namePostfix = 1;
    private CardsViewModel viewModel;
    int amount = 0;
    private int viewHeight = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.set_teams_fragment_placeholder, container, false);

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

        MaterialButton button = view.findViewById(R.id.open_game_settings);
        button.setOnClickListener(v -> {
            v.startAnimation(buttonClick);
            fCallback.callback(GameActions.open_game_settings);
        });

        Observer<List<TeamModel>> observer = commandModels -> {
            if (commandModels != null) {
                adapter.setViewModel(viewModel);

                adapter.notifyDataSetChanged();
            }
        };

        viewModel = new ViewModelProvider(requireActivity()).get(CardsViewModel.class);
        viewModel
                .getTeams()
                .observe(getViewLifecycleOwner(), observer);

        regulateMinAmount();
        view.post(() -> {
            RelativeLayout root = view.findViewById(R.id.team_layout);
            viewHeight = root.getHeight();
            amount = recyclerView.getHeight() / viewHeight - 2;
        });

        return view;
    }

    void setCallback(CallbackFragment callback) {
        fCallback = callback;
    }

    protected void regulateMinAmount() { // Добавление двух команд по умолчанию
        int min_amount = getResources().getInteger(R.integer.min_teams_amount);
        if (viewModel.getAmountOfTeams() < min_amount) {
            addItem();
            addItem();
        }
    }

    public void renameItem(int pos) { // Переименовать команду
        Context context = getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View promView = inflater.inflate(R.layout.add_command_window, null);

        if (context != null) {
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(context);

            EditText commandInput = promView.findViewById(R.id.edit_command_name);
            commandInput.setText(viewModel.getTeamName(pos));
            commandInput.setSelection(commandInput.getText().length());
            String add = getResources().getString(R.string.save);
            String cancel = getResources().getString(R.string.cancel);
            alertDialogBuilder
                    .setView(promView)
                    .setCancelable(false)
                    .setPositiveButton(add, (dialog, which) -> viewModel.renameTeam(commandInput.getText().toString(), pos))
                    .setNegativeButton(cancel, (dialog, which) -> dialog.cancel());

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    public void addItem() { // Добавить элемент
        String command = getResources().getString(R.string.team);
        viewModel.addTeams(command + ' ' + namePostfix);
        int min_teams_amount = getResources().getInteger(R.integer.min_teams_amount);
        if (viewModel.getAmountOfTeams() > min_teams_amount)
            scrollToEnd();
        namePostfix++;
    }

    public void deleteItem(int pos) { // Удалить элемент
        int amount = getResources().getInteger(R.integer.min_teams_amount);
        if (adapter != null && adapter.getItemCount() > amount + 1) {
            viewModel.removeTeam(pos);
        }
    }

    private void scrollToEnd() {
        if (layoutManager != null) {
            int index;
            if (viewModel.getAmountOfTeams() < amount) {
                index = 0;
            } else if (viewModel.getAmountOfTeams() > amount) {
                index = viewModel.getAmountOfTeams() - amount;
            } else {
                index = 0;
            }
            layoutManager.scrollToPositionWithOffset(index, 0);
        }
    }

    private void improveVisibility(int dy) {
        if (layoutManager != null) {
            int index = layoutManager.findFirstVisibleItemPosition();
if (dy > 10) {
                index += 1;
            } else if (dy < -10) {
                index -= 1;
//                Log.d("HELP", "decrease");
            }
            if (viewModel.getAmountOfTeams() < amount) {
                index = 0;
            } else if (viewModel.getAmountOfTeams() - index < amount) {
//                Log.d("HELP", "Normalize, amount = " + viewModel.getAmountOfTeams() + "index = " + index);
                index = viewModel.getAmountOfTeams() - amount;
            }
            Log.d("HELP", "erm " + index);
            layoutManager.scrollToPositionWithOffset(index, 0);
        }
    }

}
