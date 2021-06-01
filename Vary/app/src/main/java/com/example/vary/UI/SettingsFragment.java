package com.example.vary.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.vary.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SettingsFragment extends Fragment {
    private boolean sound;
    private boolean push;
    private boolean check_updates;
    private Switch switchSound;
    private Switch switchPush;
    private Switch switchCheckUpdates;
    CallbackSettings sCallback;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);

        switchSound = view.findViewById(R.id.switch_sound);
        switchPush = view.findViewById(R.id.switch_push);
        switchCheckUpdates = view.findViewById(R.id.switch_updates);
        ImageButton buttonAuthVK = view.findViewById(R.id.vk_button);

        switchSound.setChecked(sound);
        switchPush.setChecked(push);
        switchCheckUpdates.setChecked(check_updates);

        switchSound.setOnClickListener(v -> sCallback.callback(SettingActions.sound_setting, ((Switch) v).isChecked()));
        switchPush.setOnClickListener(v -> sCallback.callback(SettingActions.push_setting, ((Switch) v).isChecked()));
        switchCheckUpdates.setOnClickListener(v -> sCallback.callback(SettingActions.check_updates_setting, ((Switch) v).isChecked()));

        buttonAuthVK.setOnClickListener(v -> {
//             TODO VK authentication
        });

        return view;
    }

    void setCallback(CallbackSettings callback) {
        sCallback = callback;
    }

    void setSwitches(boolean sound, boolean push, boolean check_updates) {
        this.sound = sound;
        this.push = push;
        this.check_updates = check_updates;
    }
}