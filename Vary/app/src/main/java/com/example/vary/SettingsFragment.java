package com.example.vary;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
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
    public static final String soundKey = "sound";
    public static final String checkUpdatesKey = "check_updates";
    public static final String pushKey = "push";
    SharedPreferences.Editor sharedPreferencesEditor;
    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);

        Switch switchSound = view.findViewById(R.id.switch_sound);
        Switch switchPush = view.findViewById(R.id.switch_push);
        Switch switchCheckUpdates = view.findViewById(R.id.switch_updates);
        ImageButton buttonAuthVK = view.findViewById(R.id.vk_button);

        switchSound.setChecked(sharedPreferences.getBoolean(soundKey, true));
        switchPush.setChecked(sharedPreferences.getBoolean(pushKey, true));
        switchCheckUpdates.setChecked(sharedPreferences.getBoolean(checkUpdatesKey, true));

        switchSound.setOnClickListener(v -> {
            boolean checked = ((Switch) v).isChecked();
            sharedPreferencesEditor.putBoolean(soundKey, checked);
            sharedPreferencesEditor.apply();

//            TODO сделать callback для настроек звука, пуш и проверки обновлений ?
//            AudioManager manager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
//            manager.setStreamMute(AudioManager.STREAM_NOTIFICATION, !checked);
        });

        switchPush.setOnClickListener(v -> {
            boolean checked = ((Switch) v).isChecked();
            sharedPreferencesEditor.putBoolean(pushKey, checked);
            sharedPreferencesEditor.apply();
            // TODO настроить пуш уведомления
        });

        switchCheckUpdates.setOnClickListener(v -> {
            boolean checked = ((Switch) v).isChecked();
            sharedPreferencesEditor.putBoolean(checkUpdatesKey, checked);
            sharedPreferencesEditor.apply();
            // TODO настроить получение уведомлений
        });

        buttonAuthVK.setOnClickListener(v -> {
            // TODO VK authentication
        });

        return view;
    }


    void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        this.sharedPreferencesEditor = sharedPreferences.edit();
    }
}