package com.example.vary;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;


public class AddButtonViewHolder extends ViewHolder {
    private final Button addButton;

    public AddButtonViewHolder(@NonNull View itemView) {
        super(itemView);
        addButton = itemView.findViewById(R.id.add_command);
    }

    public void bind(View.OnClickListener listener) {
        addButton.setOnClickListener(listener);
    }

}
