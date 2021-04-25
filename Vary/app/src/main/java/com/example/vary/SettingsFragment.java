package com.example.vary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);

        Switch switchSound = view.findViewById(R.id.switch_sound);
        Switch switchPush = view.findViewById(R.id.switch_push);
        Switch switchCheckUpdates = view.findViewById(R.id.switch_updates);
        ImageButton buttonAuthVK = view.findViewById(R.id.vk_button);

        switchSound.setOnClickListener(v -> {
            boolean checked = ((Switch) v).isChecked();

            if (checked) {
                // TODO sounds ON
            } else {
                // TODO sounds OFF
            }
        });

        switchPush.setOnClickListener(v -> {
            boolean checked = ((Switch) v).isChecked();

            if (checked) {
                // TODO set push
            } else {
                // TODO reset push
            }
        });

        switchCheckUpdates.setOnClickListener(v -> {
            boolean checked = ((Switch) v).isChecked();

            if (checked) {
                // TODO set check updates
            } else {
                // TODO reset check updates
            }
        });

        buttonAuthVK.setOnClickListener(v -> {
            // TODO VK authentication
        });

        return view;
    }
}
