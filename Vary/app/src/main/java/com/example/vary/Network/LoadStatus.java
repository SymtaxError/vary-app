package com.example.vary.Network;

public class LoadStatus {
    private final Throwable error;
    private final boolean notify;

    public LoadStatus(Throwable e, boolean notification) {
        notify = notification;
        error = e;
    }

    public Throwable getError()
    {
        return error;
    }

    public boolean getNotification() {
        return notify;
    }
}
