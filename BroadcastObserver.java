package com.example.smsapp;

import java.util.Observable;

public class BroadcastObserver extends Observable
{
    private void triggerObservers()
    {
        setChanged();
        notifyObservers();
    }

    public void change()
    {
        triggerObservers();
    }
}
