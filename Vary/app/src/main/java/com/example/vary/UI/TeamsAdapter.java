package com.example.vary.UI;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.vary.R;
import com.example.vary.ViewModels.CardsViewModel;

public class TeamsAdapter extends RecyclerView.Adapter<ViewHolder> {

    OnDeleteTeamClickListener deleteListener;
    OnAddTeamClickListener addListener;
    OnChangeTeamClickListener renameListener;
    private CardsViewModel mViewModel;

    public final int type_team = 0;
    public final int type_add_button = 1;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == type_team) {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_team, parent, false);
            return new TeamsViewHolder(view);
        } else {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.add_team_button, parent, false);
            return new AddButtonViewHolder(view);
        }
    }

    public void setViewModel(CardsViewModel viewModel) {
        mViewModel = viewModel;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final int itemType = getItemViewType(position);
        if (itemType == type_team) {
            String team = mViewModel.getTeamName(position);
            View.OnClickListener delListener = v -> deleteListener.deleteItem(position);
            View.OnClickListener renListener = v -> renameListener.renameItem(position);
            ((TeamsViewHolder) holder).bind(team, delListener, renListener);
        } else if (itemType == type_add_button) {
            View.OnClickListener listener = v -> addListener.addItem();
            ((AddButtonViewHolder) holder).bind(listener);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return type_add_button;
        }
        return type_team;
    }

    public void setOnDeleteTeamClickListener(OnDeleteTeamClickListener listener) {
        deleteListener = listener;
    }

    public void setOnAddTeamClickListener(OnAddTeamClickListener listener) {
        addListener = listener;
    }

    public void setOnChangeTeamClickListener(OnChangeTeamClickListener listener) {
        renameListener = listener;
    }

    @Override
    public int getItemCount() {
        return mViewModel.getAmountOfTeams() + 1;
    }
}
