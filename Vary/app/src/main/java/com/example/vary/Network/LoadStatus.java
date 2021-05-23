package com.example.vary.Network;

public class LoadStatus {
    private Throwable error;
    private boolean success;
    private boolean notify;

    public LoadStatus(Throwable e, boolean notification) {
        notify = notification;
        if (e == null) {
            success = true;
        }
        else {
            error = e;
            success = false;
        }
    }

    public Throwable getError()
    {
        return error;
    }

    public boolean getNotification() {
        return notify;
    }
}
