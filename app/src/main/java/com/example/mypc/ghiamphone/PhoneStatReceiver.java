package com.example.mypc.ghiamphone;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class PhoneStatReceiver extends BroadcastReceiver {

    private static final String TAG = "NGON";
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;

    public static final String FORDER = "GhiAmPhone";
    static MediaRecorder recorder;
    File audiofile = null;
    Context intent;


    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.w("intent " , intent.getAction().toString());
        this.intent = context;

        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
//            DISPLAY_NAME
//            Toast.makeText(context, intent.getExtras().getString("android.intent.extra.DISPLAY_NAME"), Toast.LENGTH_SHORT).show();

        }
        else{
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int state = 0;
            if(stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                state = TelephonyManager.CALL_STATE_IDLE;
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                state = TelephonyManager.CALL_STATE_RINGING;
            }

            onCallStateChanged(context, state, number);
        }
    }


    public void onCallStateChanged(Context context, int state, String number) {
        if(lastState == state){
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;

//                Toast.makeText(context, "Incoming Call Ringing" , Toast.LENGTH_SHORT).show();
                try {
                    startRecording(savedNumber,"Incoming");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if(lastState != TelephonyManager.CALL_STATE_RINGING){
                    isIncoming = false;
                    callStartTime = new Date();
                    try {
                        startRecording(savedNumber, "Outgoing");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(context, "Outgoing Call Started" , Toast.LENGTH_SHORT).show();
                    
                }

                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if(lastState == TelephonyManager.CALL_STATE_RINGING){
                    //Ring but no pickup-  a miss
                    stopRecording();
                    Toast.makeText(context, "Ringing but no pickup" + savedNumber + " Call time " + callStartTime +" Date " + new Date() , Toast.LENGTH_SHORT).show();
                }
                else if(isIncoming){

                    Toast.makeText(context, "Incoming " + savedNumber + " Call time " + callStartTime  , Toast.LENGTH_SHORT).show();
                    stopRecording();

                }
                else{

                    Toast.makeText(context, "outgoing " + savedNumber + " Call time " + callStartTime +" Date " + new Date() , Toast.LENGTH_SHORT).show();
                    stopRecording();

                }

                break;
        }
        lastState = state;
    }
    public void startRecording(String phone, String status) throws IOException {
//        File dir = Environment.getExternalStorageDirectory();
        File dir = new File(Environment.getExternalStorageDirectory()+"/"+PhoneStatReceiver.FORDER);
        if(!dir.exists())
            dir.mkdir();
        try {
            audiofile = File.createTempFile(status+"-"+phone+"-", ".3gp", dir);
        } catch (IOException e) {
//            Log.e(TAG, "external storage access error");
            Toast.makeText(intent, "Bộ nhớ không đủ!", Toast.LENGTH_SHORT).show();
            return;
        }
//Creating MediaRecorder and specifying audio source, output format, encoder & output format
        recorder = new MediaRecorder();
        Toast.makeText(intent, "Chuẩn bị ghi âm", Toast.LENGTH_SHORT).show();

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(audiofile.getAbsolutePath());
        recorder.prepare();
        recorder.start();
    }
    public void stopRecording() {
//stopping recorder
        recorder.stop();
        recorder.release();
//        Toast.makeText(intent, "Đã lưu cuộc gọi!", Toast.LENGTH_SHORT).show();
//after stopping the recorder, create the sound file and add it to media library.
        notifyThis("Thông báo", "Đã lưu thành công cuộc gọi!");
    }
    public void notifyThis(String title, String message) {
        NotificationCompat.Builder b = new NotificationCompat.Builder(intent);
        b.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.url)
                .setTicker("Lưu thành công...")
                .setContentTitle(title)
                .setContentText(message)
                .setContentInfo("App ghi âm");

        Intent startMyActivity = new Intent(intent, MainActivity.class);
        PendingIntent myIntent = PendingIntent.getActivity(intent, 1, startMyActivity, 0);
        b.setContentIntent(myIntent);

        NotificationManager nm = (NotificationManager) intent.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, b.build());
    }
}