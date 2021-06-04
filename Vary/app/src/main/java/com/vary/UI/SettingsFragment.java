package com.vary.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.vary.R;

import static com.vary.UI.SettingActions.*;

public class SettingsFragment extends Fragment {
    private boolean sound;
    private boolean check_updates;
    CallbackSettings sCallback;
    CallbackFragment fCallback;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);

        ImageView soundView = view.findViewById(R.id.sound_view);
        ImageView updatesView = view.findViewById(R.id.check_updates_view);
        TextView soundTextView = view.findViewById(R.id.sound_text_view);
        TextView updatesTextView = view.findViewById(R.id.check_updates_text_view);
        Button backToMain = view.findViewById(R.id.back_to_main_menu_button);

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
                sCallback.callback(sound_setting, false);
                soundView.setImageResource(R.drawable.ic_baseline_music_off_24);
            }
        });


        soundTextView.setOnClickListener(v -> {
            sound = !sound;
            if (sound)  {
                soundView.setImageResource(R.drawable.ic_baseline_music_note_24);
                sCallback.callback(sound_setting, sound);
            } else {
                sCallback.callback(sound_setting, false);
                soundView.setImageResource(R.drawable.ic_baseline_music_off_24);
            }
        });


        updatesTextView.setOnClickListener(v -> {
            check_updates = !check_updates;
            if (check_updates)  {
                updatesView.setImageResource(R.drawable.ic_baseline_system_update_24);
                sCallback.callback(check_updates_setting, check_updates);
            } else {
                sCallback.callback(check_updates_setting, false);
                updatesView.setImageResource(R.drawable.ic_baseline_mobile_off_24);
            }
        });

        updatesView.setOnClickListener(v -> {
            check_updates = !check_updates;
            if (check_updates)  {
                updatesView.setImageResource(R.drawable.ic_baseline_system_update_24);
                sCallback.callback(check_updates_setting, check_updates);
            } else {
                sCallback.callback(check_updates_setting, false);
                updatesView.setImageResource(R.drawable.ic_baseline_mobile_off_24);
            }
        });


        backToMain.setOnClickListener(v -> fCallback.callback(GameActions.open_menu));

        return view;
    }

    void setFCallback(CallbackFragment callback) {
        fCallback = callback;
    }

    void setCallback(CallbackSettings callback) {
        sCallback = callback;
    }

    void setSwitches(boolean sound, boolean check_updates) {
        this.sound = sound;
        this.check_updates = check_updates;
    }
}