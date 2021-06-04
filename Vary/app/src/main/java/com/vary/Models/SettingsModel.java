package com.vary.Models;

public class SettingsModel {
    boolean soundOn;
    boolean notificationsOn;
    boolean lowerVolume = false;

    public SettingsModel(boolean sound, boolean notifications) {
        soundOn = sound;
        notificationsOn = notifications;
    }

    public void setSoundOn(boolean sound) {
        soundOn = sound;
    }

    public boolean isSoundOn() {
        return soundOn;
    }

    public void setNotificationsOn(boolean notificationsOn) {
        this.notificationsOn = notificationsOn;
    }

    public boolean isNotificationsOn() {
        return notificationsOn;
    }

    public void setLowerVolume(boolean lower) {
        lowerVolume = lower;
    }

    public boolean isLowerVolume() {
        return lowerVolume;
    }
}
