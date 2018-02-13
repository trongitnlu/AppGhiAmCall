package com.example.mypc.ghiamphone;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class PlayActivity extends AppCompatActivity {

    private TextView textMaxTime;
    private TextView textCurrentPosition;
    private Button buttonPause;
    private Button buttonStart;
    private SeekBar seekBar;
    private Handler threadHandler = new Handler();
    private int tua;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        String path = intent.getStringExtra("PATH");
        init();
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        this.mediaPlayer = MediaPlayer.create(this, uri);
    }

    public void init() {
        this.textCurrentPosition = (TextView) this.findViewById(R.id.textView_currentPosion);
        this.textMaxTime = (TextView) this.findViewById(R.id.textView_maxTime);
        this.buttonStart = (Button) this.findViewById(R.id.button_start);
        this.buttonPause = (Button) this.findViewById(R.id.button_pause);

        this.buttonPause.setEnabled(false);

        this.seekBar = (SeekBar) this.findViewById(R.id.seekBar);
        this.seekBar.setClickable(false);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tua = progress;
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress);

                    long minutes = TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getCurrentPosition());
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getCurrentPosition())
                            - TimeUnit.MINUTES.toSeconds(minutes);
                    textCurrentPosition.setText(String.format("%02d:%02d", minutes, seconds));

                    PlayActivity.UpdateSeekBarThread updateSeekBarThread = new PlayActivity.UpdateSeekBarThread();
                    threadHandler.postDelayed(updateSeekBarThread, 50);

                } else if (mediaPlayer == null && fromUser) {
//                    prepareMediaPlayerFromPoint(progress);
//                    updateSeekBar();

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int currentPosition = mediaPlayer.getDuration();
                Toast.makeText(PlayActivity.this, "" + currentPosition, Toast.LENGTH_SHORT).show();
//                int mili = tua*currentPosition/100;
//                if(seekBar.getProgress()>tua){
//                    mediaPlayer.seekTo(currentPosition - mili);
//                }else{
//                    mediaPlayer.seekTo(currentPosition + mili);
//                }

            }
        });

    }

//    public void getListFile() {
//        String path = Environment.getExternalStorageDirectory().toString() + "/" + PhoneStatReceiver.FORDER;
//        File directory = new File(path);
//        File[] files = directory.listFiles();
//        Toast.makeText(this, files.length + "", Toast.LENGTH_SHORT).show();
//        Uri uri = Uri.fromFile(files[0]);
//        this.mediaPlayer = MediaPlayer.create(this, uri);
//    }

    // Chuyển số lượng milli giây thành một String có ý nghĩa.
    private String millisecondsToString(int milliseconds) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes((long) milliseconds);
        long seconds = TimeUnit.MILLISECONDS.toSeconds((long) milliseconds);
        return minutes + ":" + seconds;
    }

    public void doStart(View view) {

        // Khoảng thời gian của bài hát (Tính theo mili giây).
        int duration = this.mediaPlayer.getDuration();

        int currentPosition = this.mediaPlayer.getCurrentPosition();
        if (currentPosition == 0) {
            this.seekBar.setMax(duration);
            String maxTimeString = this.millisecondsToString(duration);
            this.textMaxTime.setText(maxTimeString);
        } else if (currentPosition == duration) {

            // Trở lại trạng thái ban đầu trước khi chơi.
            this.mediaPlayer.reset();
        }
        this.mediaPlayer.start();

        // Tạo một thread để update trạng thái của SeekBar.
        PlayActivity.UpdateSeekBarThread updateSeekBarThread = new PlayActivity.UpdateSeekBarThread();
        threadHandler.postDelayed(updateSeekBarThread, 50);

        this.buttonPause.setEnabled(true);
        this.buttonStart.setEnabled(false);
    }


    // Thread sử dụng để Update trạng thái cho SeekBar.
    class UpdateSeekBarThread implements Runnable {

        public void run() {
            int currentPosition = mediaPlayer.getCurrentPosition();
            String currentPositionStr = millisecondsToString(currentPosition);
            textCurrentPosition.setText(currentPositionStr);

            seekBar.setProgress(currentPosition);

            // Ngừng thread 50 mili giây.
            threadHandler.postDelayed(this, 50);
        }
    }


    // Khi người dùng Click vào nút tạm dừng (Pause).
    public void doPause(View view) {
        this.mediaPlayer.pause();
        this.buttonPause.setEnabled(false);
        this.buttonStart.setEnabled(true);
    }


    // Khi người dùng Click vào nút tua lại (Rewind)
    public void doRewind(View view) {
        int currentPosition = this.mediaPlayer.getCurrentPosition();
        int duration = this.mediaPlayer.getDuration();

        // 5 giây.
        int SUBTRACT_TIME = 5000;

        if (currentPosition - SUBTRACT_TIME > 0) {
            this.mediaPlayer.seekTo(currentPosition - SUBTRACT_TIME);
        }
    }


    // Khi người dùng Click vào nút tua đi (Fast-Forward).
    public void doFastForward(View view) {
        int currentPosition = this.mediaPlayer.getCurrentPosition();
        int duration = this.mediaPlayer.getDuration();

        // 5 giây.
        int ADD_TIME = 5000;

        if (currentPosition + ADD_TIME < duration) {
            this.mediaPlayer.seekTo(currentPosition + ADD_TIME);
        }
    }
}
