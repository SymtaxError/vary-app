package com.example.vary.UI;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.vary.R;

public class TeamsViewHolder extends ViewHolder {
    private final TextView name;
    private final Button delete;

    public TeamsViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.team_name_item);
        delete = itemView.findViewById(R.id.team_delete);
    }

    public void bind(String team, View.OnClickListener delListener, View.OnClickListener renameListener) {
        name.setText(team);
        delete.setOnClickListener(delListener);
        name.setOnClickListener(renameListener);
    }
}
