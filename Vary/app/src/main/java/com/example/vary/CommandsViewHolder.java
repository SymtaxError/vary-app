package com.example.vary;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

public class CommandsViewHolder extends ViewHolder {
    private final TextView name;
    private final Button delete;

    public CommandsViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.command_name);
        delete = itemView.findViewById(R.id.command_delete);
    }

    public void bind(CommandModel command, View.OnClickListener delListener, View.OnClickListener renameListener) {
        name.setText(command.mName);
        delete.setOnClickListener(delListener);
        name.setOnClickListener(renameListener);
    }
}
