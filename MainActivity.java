package com.example.SMSApp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.net.Uri;

//import android.support.v4.app.ActivityCompat;
//import android.support.v7.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.example.SMSApp.AESEncrypter;
import com.example.smsapp.Alarm;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.logging.Level;
import java.util.logging.Logger;
import android.os.Vibrator;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private TextView myTextView;
    private TextView memo;
    TextView tvAlarmNumber, tvAlarmName, tvSeverityLevel, tvAlarmDetails, tvLocation, tvDateDetected, tvTimeDetected, lblAlarmIdx;
    Button stopButton, readButton;
    FloatingActionButton btnPrev, btnNext;

    private static final int CAMERA_REQUEST = 123;
    boolean hasCameraFlash = false;

    public ArrayList<Alarm> alarms = new ArrayList<Alarm>();
    public int alarmIdx = -1;

    Switch flashControl;
    CameraManager cameraManager;

    MediaPlayer player;

    Vibrator v;

    private static final String TAG = com.example.SMSApp.MainActivity.class.getSimpleName();
    public static final String pdu_type = "pdus";
    public static Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);

        hasCameraFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        registerReceiver(fatalReceiver, new IntentFilter("START_FATAL"));
        registerReceiver(criticalReceiver, new IntentFilter("START_CRITICAL"));
        registerReceiver(warningReceiver, new IntentFilter("START_WARNING"));
        registerReceiver(infoReceiver, new IntentFilter("START_INFO"));
        registerReceiver(startReceiver, new IntentFilter("CHECK"));

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        Display display = getWindowManager().getDefaultDisplay();
        int width = (display.getWidth());
        int height = (display.getHeight());

        /*Toast.makeText(this, "Width: " + width, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Height: " + height, Toast.LENGTH_SHORT).show();*/


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS}, 1000);
        }

        findViewById(R.id.tvAlarmDetails).bringToFront();
        myTextView = findViewById(R.id.btnReadSMS);
        stopButton = findViewById(R.id.stopBtn);
        readButton = findViewById(R.id.btnReadSMS);

        tvAlarmNumber = findViewById(R.id.tvAlNumDetails);
        tvAlarmName = findViewById(R.id.tvAlNameDetails);
        tvSeverityLevel = findViewById(R.id.tvSlDetails);
        tvAlarmDetails = findViewById(R.id.tvAlDetDetails);
        tvLocation = findViewById(R.id.tvLocDetails);
        tvDateDetected = findViewById(R.id.tvDateDetails);
        tvTimeDetected = findViewById(R.id.tvTimeDetails);

        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);

        lblAlarmIdx = findViewById(R.id.lblAlarmIdx);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);
        /*cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);

        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
        {

        }
        else
        {
            Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT)
        }*/
    }

    private void flashLightOn()
    {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try
        {
            String cameraID = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraID, true);
        }
        catch (CameraAccessException e)
        {

        }
    }

    private void flashLightOff()
    {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try
        {
            String cameraID = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraID, false);
        }
        catch (CameraAccessException e)
        {

        }
    }

    public void blinkFLash(View v)
    {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String myString = "010101010101010101010101010101010101010101010101010101010101010101010101";
        long blinkDelay = 100;
        for (int i = 0; i < myString.length(); i++)
        {
            if(myString.charAt(i) == '0')
            {
                try
                {
                    String cameraID = cameraManager.getCameraIdList()[0];
                    cameraManager.setTorchMode(cameraID, true);
                }
                catch(CameraAccessException e)
                {

                }
            }
            else
            {
                try
                {
                    String cameraID = cameraManager.getCameraIdList()[0];
                    cameraManager.setTorchMode(cameraID, false);
                }
                catch(CameraAccessException e)
                {

                }
            }
            try
            {
                Thread.sleep(blinkDelay);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hasCameraFlash = getPackageManager().
                            hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
                } else {
                    //btnFlashLight.setEnabled(false);
                    //btnBlinkFlashLight.setEnabled(false);
                    Toast.makeText(MainActivity.this, "Permission Denied for the Camera", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void Vibrate(View view)
    {
        long[] pattern = {0, 100, 1000};
        v.vibrate(pattern, 0);
    }

    public void stopVibrate(View view)
    {
        v.cancel();
    }

    private void receiveWarning()
    {
        playWarning();
        readButton.setEnabled(true);
    }

    private void receiveCritical()
    {
        playCritical();
        readButton.setEnabled(true);
    }

    private void receiveFatal()
    {
        playFatal();
        readButton.setEnabled(true);
    }

    private void receiveInfo()
    {
        playInfo();
        readButton.setEnabled(true);
    }

    BroadcastReceiver warningReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            playWarning();
            readButton.setEnabled(true);
        }
    };

    BroadcastReceiver criticalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            playCritical();
            readButton.setEnabled(true);
        }
    };

    BroadcastReceiver fatalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            playFatal();
            readButton.setEnabled(true);
        }
    };

    BroadcastReceiver infoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            playInfo();
            readButton.setEnabled(true);
        }
    };

    BroadcastReceiver startReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String pdu_type = "pdus";

            try
            {
                //readButton.setEnabled(false);
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
                    }

                    AESEncrypter en = new AESEncrypter("lv94eptlvihaqqsr");
                    String str = en.decrypt(strMessage);

                    String[] sms = extractDetails(str);

                    if (sms[2].equals("Fatal"))
                        receiveFatal();
                    else if (sms[2].equals("Critical"))
                        receiveCritical();
                    else if (sms[2].equals("Warning"))
                        receiveWarning();
                    else if (sms[2].equals("Info"))
                        receiveInfo();

                }
            }
            catch(Exception e)
            {
                setDefaultCaptions();
                Logger.getLogger(AESEncrypter.class.getName()).log(Level.SEVERE, null, e);
            }
        }

    };


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(fatalReceiver);
        unregisterReceiver(criticalReceiver);
        unregisterReceiver(warningReceiver);
        unregisterReceiver(infoReceiver);
    }


    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void Read_SMS(View view) throws Exception {
        String pdu_type = "pdus";

        try
        {
            readButton.setEnabled(false);
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
                }

                AESEncrypter en = new AESEncrypter("lv94eptlvihaqqsr");
                String str = en.decrypt(strMessage);

                if (str.equals("no message"))
                {
                    setDefaultCaptions();
                    return;
                }

                String alarmNumber, alarmName, severityLevel, alarmDetails, location, dateDetected, timeDetected;
                String[] sms = extractDetails(str);

                if (sms.length == 1)
                {
                    setDefaultCaptions();
                    return;
                }

                alarmNumber = sms[0];
                alarmName = sms[1];
                severityLevel = sms[2];
                alarmDetails = sms[3];
                location = sms[4];
                dateDetected = sms[5];
                timeDetected = sms[6];

                alarms.add(new Alarm(alarmNumber, alarmName, severityLevel, alarmDetails, location, dateDetected, timeDetected));
                ++alarmIdx;

                lblAlarmIdx.setText(String.valueOf(alarmIdx+1) + " of " + String.valueOf(alarms.size()));

                if (alarms.size() > 1)
                {
                    btnPrev.setEnabled(true);
                }

                tvAlarmNumber.setText(alarmNumber);
                tvAlarmName.setText(alarmName);
                tvSeverityLevel.setText(severityLevel);
                tvAlarmDetails.setText(alarmDetails);
                tvLocation.setText(location);
                tvDateDetected.setText(dateDetected);
                tvTimeDetected.setText(timeDetected);
            }
        }
        catch(Exception e)
        {
            setDefaultCaptions();
            Logger.getLogger(AESEncrypter.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void nextAlarm(View v)
    {
        btnPrev.setEnabled(true);

        ++alarmIdx;
        Alarm a = alarms.get(alarmIdx);

        tvAlarmNumber.setText(a.number);
        tvAlarmName.setText(a.name);
        tvSeverityLevel.setText(a.severity);
        tvAlarmDetails.setText(a.description);
        tvLocation.setText(a.location);
        tvDateDetected.setText(a.date);
        tvTimeDetected.setText(a.time);

        if (alarmIdx + 1 > alarms.size() - 1)
        {
            btnNext.setEnabled(false);
        }

        lblAlarmIdx.setText(String.valueOf(alarmIdx+1) + " of " + String.valueOf(alarms.size()));
    }

    public void prevAlarm(View v)
    {
        btnNext.setEnabled(true);

        --alarmIdx;
        Alarm a = alarms.get(alarmIdx);

        tvAlarmNumber.setText(a.number);
        tvAlarmName.setText(a.name);
        tvSeverityLevel.setText(a.severity);
        tvAlarmDetails.setText(a.description);
        tvLocation.setText(a.location);
        tvDateDetected.setText(a.date);
        tvTimeDetected.setText(a.time);

        if (alarmIdx - 1 < 0)
        {
            btnPrev.setEnabled(false);
        }

        lblAlarmIdx.setText(String.valueOf(alarmIdx+1) + " of " + String.valueOf(alarms.size()));
    }

    private void setDefaultCaptions()
    {
        tvAlarmNumber.setText("no alarm number to display");
        tvAlarmName.setText("no alarm name to display");
        tvSeverityLevel.setText("no security level to display");
        tvAlarmDetails.setText("no alarm details to display");
        tvLocation.setText("no location to display");
        tvDateDetected.setText("no date to display");
        tvTimeDetected.setText("no time to display");
    }

    private String[] extractDetails(String sms)
    {
        try
        {
            String str = sms;

            Toast.makeText(this, "SMS: " + str, Toast.LENGTH_SHORT).show();

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
            setDefaultCaptions();
            return new String[1];
        }
    }

    public void playWarning()
    {
        if (player == null) {
            player = MediaPlayer.create(this, R.raw.warning);
            stopButton.setEnabled(true);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlayer();
                }
            });
        }

        player.start();
    }

    public void playCritical()
    {
        if (player == null) {
            player = MediaPlayer.create(this, R.raw.critical);
            stopButton.setEnabled(true);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlayer();
                }
            });
        }

        player.start();
    }

    public void playFatal()
    {
        if (player == null) {
            player = MediaPlayer.create(this, R.raw.fatal);
            stopButton.setEnabled(true);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlayer();
                }
            });
        }

        player.start();
    }

    public void playInfo()
    {
        if (player == null) {
            player = MediaPlayer.create(this, R.raw.info);
            stopButton.setEnabled(true);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlayer();
                }
            });
        }

        player.start();
    }

    public void stop(View v) {
        stopPlayer();
    }

    private void stopPlayer() {
        if (player != null) {
            player.release();
            player = null;
            stopButton.setEnabled(false);
            Toast.makeText(this, "MediaPlayer released", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayer();
    }

}