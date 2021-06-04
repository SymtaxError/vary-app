package com.example.vary.Repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vary.Models.SettingsModel;

public class SettingsRepo {
    private final MutableLiveData<SettingsModel> settings = new MutableLiveData<>();
    private static SettingsRepo sInstance = null;

    public static synchronized SettingsRepo getInstance() {
        if (sInstance == null) {
            sInstance = new SettingsRepo();
        }
        return sInstance;
    }

    public LiveData<SettingsModel> getSettings() {
        return settings;
    }

    public void setSettings(boolean soundOn, boolean notificationsOn) {
        settings.postValue(new SettingsModel(soundOn, notificationsOn));
    }

    public boolean getSoundState() {
        if (settings.getValue() != null) {
            return settings.getValue().isSoundOn();
        }
        return false;
    }

    public boolean getNotificationState() {
        if (settings.getValue() != null) {
            return settings.getValue().isNotificationsOn();
        }
        return false;
    }

    public void setSoundState(boolean sound) {
        SettingsModel model = settings.getValue();
        if (model != null) {
            model.setSoundOn(sound);
            settings.postValue(model);
        }
    }

    public void setNotificationState(boolean notifications) {
        SettingsModel model = settings.getValue();
        if (model != null) {
            model.setNotificationsOn(notifications);
            settings.postValue(model);
        }
    }

    public void setLowerVolume(boolean lower) {
        SettingsModel model = settings.getValue();
        if (model != null) {
            model.setLowerVolume(lower);
            settings.postValue(model);
        }
    }

    public boolean getLowerVolume() {
        if (settings.getValue() != null) {
            return settings.getValue().isLowerVolume();
        }
        return false;
    }
}
