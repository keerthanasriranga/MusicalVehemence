package com.wordpress.keerthanasriranga.musicalvehemence;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    ListView listView;

    List<String>list;

    ListAdapter adapter;
    int position1;
    int progress;
    static MediaPlayer mediaPlayer;
    Handler handler;
    SeekBar seekBar;
    Runnable runnable;
    Button pausebutton;

    FloatingActionButton emotionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = new ArrayList<>();
        handler = new Handler();

        listView = (ListView) findViewById(R.id.listView);
        setListViewHeightBasedOnChildren(listView);

        pausebutton = (Button) findViewById(R.id.pausebutton);

        seekBar = (SeekBar) findViewById(R.id.seekBar);

        emotionButton = (FloatingActionButton) findViewById(R.id.emotionButton);
        emotionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isNetworkAvailable())
                    Toast.makeText(MainActivity.this, "Connect to internet to access Emotion Recognition feature", Toast.LENGTH_LONG).show();

                else{Intent i = new Intent(MainActivity.this, EmotionActivity.class);
                    startActivity(i);}
            }
        });



        Field[] fields = R.raw.class.getFields();
        for (int i = 0; i < fields.length; i++) {
            list.add(fields[i].getName());
        }

        list.remove(0);
        list.remove(fields.length - 2);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);





        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> adapterView, View view,int i,long l){
                if (mediaPlayer!=null){
                    mediaPlayer.release();
                }
                int resId = getResources().getIdentifier(list.get(i),"raw",getPackageName());
                mediaPlayer = mediaPlayer.create(MainActivity.this,resId);
                mediaPlayer.start();
                playCycle();

                seekBar.setMax(mediaPlayer.getDuration());
            }
        });







        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean input) {
               if(mediaPlayer!=null) { if(input)
                {

                    mediaPlayer.seekTo(progress);
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                }}
                else Toast.makeText(MainActivity.this , "Choose a song first", Toast.LENGTH_LONG);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        pausebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer!=null){
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        pausebutton.setText("Play");
                    } else {
                        mediaPlayer.start();
                        pausebutton.setText("Pause");
                    }}
                else{Toast.makeText(MainActivity.this,"Choose a song",Toast.LENGTH_LONG).show();}
            }
        });



    }
    public void playCycle()
    {
        Log.i("Seekbar",String.valueOf(mediaPlayer.getCurrentPosition()));
        seekBar.setProgress(mediaPlayer.getCurrentPosition());

        if(!mediaPlayer.isPlaying()) Log.i("Seekbar","Not playing");
        if(mediaPlayer.isPlaying())
        {
            Log.i("Seekbar",String.valueOf(mediaPlayer.getCurrentPosition()));
            runnable=new Runnable() {
                @Override
                public void run() {
                    Log.i("Seekbar",String.valueOf(mediaPlayer.getCurrentPosition()));
                    playCycle();

                }
            };
            handler.postDelayed(runnable,100);
        }
    }






    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ActionBar.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void onSaveInstanceState(Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);
        if(mediaPlayer!=null) {
            savedInstanceState.putInt("Position", mediaPlayer.getCurrentPosition());
            savedInstanceState.putBoolean("isplaying", mediaPlayer.isPlaying());
            savedInstanceState.putInt("seekbar",seekBar.getProgress());
            if (mediaPlayer.isPlaying())
                mediaPlayer.pause();
        }

    }


    @Override

    public void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);
        progress = savedInstanceState.getInt("seekbar");
        position1 = savedInstanceState.getInt("Position");
        seekBar.setProgress(progress);
        if(mediaPlayer!=null) {
            mediaPlayer.seekTo(position1);
            if (savedInstanceState.getBoolean("isplaying"))
                mediaPlayer.start();
            seekBar.setProgress(progress);




        }

    }


}