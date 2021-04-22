package com.example.vary;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

public class CommandsAdapter extends RecyclerView.Adapter<ViewHolder> {

    OnDeleteCommandClickListener deleteListener;
    OnAddCommandClickListener addListener;
    OnChangeCommandClickListener renameListener;

    public final int type_command = 0;
    public final int type_add_button = 1;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == type_command) {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.command_item, parent, false);
            return new CommandsViewHolder(view);
        } else {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.add_command_button, parent, false);
            return new AddButtonViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final int itemType = getItemViewType(position);
        if (itemType == type_command) {
            CommandModel model = CommandsSource
                    .getInstance()
                    .getRemoteData()
                    .get(position);
            View.OnClickListener delListener = v -> deleteListener.deleteItem(position);
            View.OnClickListener renListener = v -> renameListener.renameItem(position);
            ((CommandsViewHolder) holder).bind(model, delListener, renListener);
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
        return type_command;
    }

    public void setOnDeleteCommandClickListener(OnDeleteCommandClickListener listener) {
        deleteListener = listener;
    }

    public void setOnAddCommandClickListener(OnAddCommandClickListener listener) {
        addListener = listener;
    }

    public void setOnChangeCommandClickListener(OnChangeCommandClickListener listener) {
        renameListener = listener;
    }

    @Override
    public int getItemCount() {
        return CommandsSource
                .getInstance()
                .getRemoteData()
                .size() + 1;
    }
}
