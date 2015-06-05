package com.puigmusic.hramosdgil.android.Activities;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.puigmusic.hramosdgil.android.tabs.MainActivity;
import com.puigmusic.hramosdgil.puigmusic.R;

public class PlayMusic extends ActionBarActivity {

    private MediaPlayer mediaPlayer;
    public TextView songName, duration;
    private double timeElapsed = 0, finalTime = 0;
    private int forwardTime = 2000, backwardTime = 2000;
    private Handler durationHandler = new Handler();
    private SeekBar seekbar;
    private static final String TAG_PATH = "path";
    private static final String TAG_TITLE = "title";
    private static final String TAG_URI = "uri";
    private static final String PATH = "http://puigmusic-prueba121.rhcloud.com";
    Intent in;
    String url;
    String title ;
    private ListView navList;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ArrayList<HashMap<String, String>> listSongs;
    int position;
    //ArrayList<HashMap<String, String>> songList;

    public PlayMusic() {
        // Required empty public constructor
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Play");
        //set the layout of the Activity
        setContentView(R.layout.play_music);
        infoIntent();
        //initialize views
        final String[] names = getResources().getStringArray(
                R.array.nav_options);
        this.navList = (ListView) findViewById(R.id.left_drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, names));
        navList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView arg0, View arg1, int arg2, long arg3) {
                ChangeActivity(arg2);
                drawerLayout.closeDrawers();
            }
        });
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        initializeViews();
    }

    public void infoIntent(){
        in = getIntent();
        listSongs = (ArrayList<HashMap<String, String>>) getIntent().getSerializableExtra("list");
        position = in.getIntExtra("position", 0);

    }
    public void initializeViews(){
        url = listSongs.get(position).get(TAG_URI);
        title = listSongs.get(position).get(TAG_TITLE);
        if(url==null){
            url= listSongs.get(position).get(TAG_PATH);
            url = PATH+listSongs.get(position).get(TAG_PATH);
            title = listSongs.get(position).get(TAG_TITLE);
        }


        songName = (TextView) findViewById(R.id.songName);
        songName.setText(title);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(url);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finalTime = mediaPlayer.getDuration();
        duration = (TextView) findViewById(R.id.songDuration);
        seekbar = (SeekBar) findViewById(R.id.seekBar);
        seekbar.setMax((int) finalTime);
        seekbar.setClickable(false);
    }

    // call initSong()
    public void play(View view) {

        initSong();
    }
    // play the song
    public void initSong(){
        mediaPlayer.start();
        timeElapsed = mediaPlayer.getCurrentPosition();
        seekbar.setProgress((int) timeElapsed);
        durationHandler.postDelayed(updateSeekBarTime, 100);
    }

    //handler to change seekBarTime
    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            //get current position
            timeElapsed = mediaPlayer.getCurrentPosition();
            //set seekbar progress
            seekbar.setProgress((int) timeElapsed);
            //set time remaing
            double timeRemaining = finalTime - timeElapsed;
            duration.setText(String.format("%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining), TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining))));
            if(timeRemaining==0){
                nextSong();
            }
            //repeat yourself that again in 100 miliseconds
            durationHandler.postDelayed(this, 100);

        }
    };

    // change the next song and play this
    public void nextSong(){
        mediaPlayer.stop();
        position++;
        initializeViews();
        initSong();
    }
    // pause the song
    public void pause(View view) {
        mediaPlayer.pause();
    }

    // look if exit the next song and change.
    public void forward(View view) {

        if(position+1<listSongs.size()) {
            nextSong();
        }
        else{
            mediaPlayer.stop();
            initializeViews();
        }
    }

    // change the last song
    public void lastSong(){
        mediaPlayer.stop();
        position--;
        initializeViews();
        initSong();
    }
    // look if exit the last song and change
    public void backward(View view) {
        if(position-1>=0) {
            lastSong();
        }
        else{
            mediaPlayer.stop();
            initializeViews();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }
    // Change Activity in Navigation Drawer
    public void ChangeActivity(int args) {
        Intent i;
        switch (args) {
            case 0:
                i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                break;
            case 1:
                i = new Intent(getApplicationContext(), DownloadActivity.class);
                startActivity(i);
                break;

            case 2:
                i = new Intent(getApplicationContext(), LocalSongs.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }
}
