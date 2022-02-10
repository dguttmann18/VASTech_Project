package com.example.SMSApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class ReceiveSMS extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Toast.makeText(context, "SMS Received", Toast.LENGTH_LONG).show();

        Bundle bundle = intent.getExtras();

        if (bundle != null)
            Toast.makeText(context, "SMS is NOT empty", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, "SMS is empty", Toast.LENGTH_LONG).show();

    }
}
