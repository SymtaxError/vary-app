package com.example.vary.UI;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.vary.R;
import com.google.android.material.button.MaterialButton;


public class AddButtonViewHolder extends ViewHolder {
    private final MaterialButton addButton;
//    private final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.5F);

    public AddButtonViewHolder(@NonNull View itemView) {
        super(itemView);
        addButton = itemView.findViewById(R.id.add_team);
    }

    public void bind(View.OnClickListener listener) {
        addButton.setOnClickListener(listener);
    }

}
