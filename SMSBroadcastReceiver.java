package com.example.SMSApp;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.example.SMSApp.AESEncrypter;
import com.example.SMSApp.MainActivity;

import java.util.logging.Level;
import java.util.logging.Logger;


public class SMSBroadcastReceiver extends BroadcastReceiver
{
    MediaPlayer player;
    Context context;
    private static final String TAG = SMSBroadcastReceiver.class.getSimpleName();
    public static final String pdu_type = "pdus";

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        MainActivity.intent = intent;

        try
        {
            String msgData;

            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs;
            String strMessage = "";
            String format = bundle.getString("format");
            //Retrieve the SMS message received.
            Object[] pdus = (Object[]) bundle.get(pdu_type);
            if (pdus != null) {
                // Check the Android version.
                boolean isVersionM =
                        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
                // Fill the msgs array.
                msgs = new SmsMessage[pdus.length];

                int j = 0;

                for (int i = 0; i < msgs.length; i++) {
                    // Check Android version and use appropriate createFromPdu.
                    if (isVersionM) {
                        // If Android version M or newer:
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                    } else {
                        // If Android version L or older:
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    }

                    strMessage += msgs[i].getMessageBody();
                    Log.d(TAG, "onReceive: " + strMessage);

                    j = i;
                }

                if (msgs[j].getDisplayOriginatingAddress().equals(/*"6505551212"*/"+27832329316")) {
                    AESEncrypter en = new AESEncrypter("lv94eptlvihaqqsr");
                    String str = en.decrypt(strMessage);

                    String severityLevel;
                    String[] sms = extractDetails(str);


                    if (sms.length == 7) {
                        severityLevel = sms[2];

                        if (severityLevel.equals("Fatal"))
                            context.sendBroadcast(new Intent("START_FATAL"));
                        else if (severityLevel.equals("Critical"))
                            context.sendBroadcast(new Intent("START_CRITICAL"));
                        else if (severityLevel.equals("Warning"))
                            context.sendBroadcast(new Intent("START_WARNING"));
                        else if (severityLevel.equals("Info"))
                            context.sendBroadcast(new Intent("START_INFO"));
                    }


                }
            }
        }
        catch (Exception e)
        {
            Logger.getLogger(AESEncrypter.class.getName()).log(Level.SEVERE, null, e);
        }


    }

    private void getMessage(String strMessage) throws Exception
    {
        AESEncrypter en = new AESEncrypter("lv94eptlvihaqqsr");
        Log.d(TAG, "in getMessage() method");
        String str = en.decrypt(strMessage);

        Toast.makeText(context, "Decrypted message: " + str, Toast.LENGTH_LONG).show();



        /*if (!str.equals(""))
        {
            String msg[] = extractDetails(str);

            if (msg[2] != null)
                if (msg[2].equals("Fatal"))
                    context.sendBroadcast(new Intent("START_FATAL"));
                else if (msg[2].equals("Critical"))
                    context.sendBroadcast(new Intent("START_CRITICAL"));
                else if (msg[2].equals("Warning"))
                    context.sendBroadcast(new Intent("START_WARNING"));
                else if (msg[2].equals("Info"))
                    context.sendBroadcast(new Intent("START_INFO"));
        }*/
    }

    private String[] extractDetails(String sms)
    {
        try
        {
            String str = sms;


            String ar[] = new String[7];
            int idx = 0;

            while(str.contains("#"))
            {
                ar[idx] = str.substring(0, str.indexOf("#"));
                str = str.substring(str.indexOf("#")+1);
                ++idx;
            }

            ar[6] = str.substring(1);

            return ar;
        }
        catch (Exception e)
        {
            return new String[1];
        }
    }


}
