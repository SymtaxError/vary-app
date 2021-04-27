package com.example.vary.UI;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.vary.R;


public class AddButtonViewHolder extends ViewHolder {
    private final Button addButton;

    public AddButtonViewHolder(@NonNull View itemView) {
        super(itemView);
        addButton = itemView.findViewById(R.id.add_team);
    }

    public void bind(View.OnClickListener listener) {
        addButton.setOnClickListener(listener);
    }

}
