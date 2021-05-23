package com.example.vary.UI;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.vary.R;


public class AddButtonViewHolder extends ViewHolder {
    private final Button addButton;
    private final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.5F);

    public AddButtonViewHolder(@NonNull View itemView) {
        super(itemView);
        addButton = itemView.findViewById(R.id.add_team);
    }

    public void bind(View.OnClickListener listener) {
        addButton.setOnClickListener(listener);
    }

}
