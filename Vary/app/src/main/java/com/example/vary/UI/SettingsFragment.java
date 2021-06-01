package com.example.vary.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.vary.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import static com.example.vary.UI.SettingActions.*;

public class SettingsFragment extends Fragment {
    private boolean sound;
    private boolean check_updates;
    CallbackSettings sCallback;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);

        ImageView soundView = view.findViewById(R.id.sound_view);
        ImageView updatesView = view.findViewById(R.id.check_updates_view);

        if (sound) {
            soundView.setImageResource(R.drawable.ic_baseline_music_note_24);
        } else {
            soundView.setImageResource(R.drawable.ic_baseline_music_off_24);
        }

        if (check_updates) {
            updatesView.setImageResource(R.drawable.ic_baseline_system_update_24);
        } else {
            updatesView.setImageResource(R.drawable.ic_baseline_mobile_off_24);
        }

        soundView.setOnClickListener(v -> {
            sound = !sound;
            if (sound)  {
                soundView.setImageResource(R.drawable.ic_baseline_music_note_24);
                sCallback.callback(sound_setting, sound);
            } else {
                sCallback.callback(sound_setting, sound);
                soundView.setImageResource(R.drawable.ic_baseline_music_off_24);
            }
        });

        updatesView.setOnClickListener(v -> {
            check_updates = !check_updates;
            if (check_updates)  {
                updatesView.setImageResource(R.drawable.ic_baseline_system_update_24);
                sCallback.callback(check_updates_setting, check_updates);
            } else {
                sCallback.callback(check_updates_setting, check_updates);
                updatesView.setImageResource(R.drawable.ic_baseline_mobile_off_24);
            }
        });

        return view;
    }

    void setCallback(CallbackSettings callback) {
        sCallback = callback;
    }

    void setSwitches(boolean sound, boolean check_updates) {
        this.sound = sound;
        this.check_updates = check_updates;
    }
}