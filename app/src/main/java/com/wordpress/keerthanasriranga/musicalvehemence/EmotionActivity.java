package com.wordpress.keerthanasriranga.musicalvehemence;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.projectoxford.emotion.EmotionServiceClient;
import com.microsoft.projectoxford.emotion.EmotionServiceRestClient;
import com.microsoft.projectoxford.emotion.contract.RecognizeResult;
import com.microsoft.projectoxford.emotion.contract.Scores;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.wordpress.keerthanasriranga.musicalvehemence.MainActivity.mediaPlayer;

public class EmotionActivity extends AppCompatActivity {

    final EmotionServiceClient emotionServiceClient = new EmotionServiceRestClient("a421fd6dc6a24147abe0942f52e270ca");
    TextView emotion;
    static MediaPlayer mplayer;
    Button emoplay;
    Button clickpic;
    ImageView imageView;
    Button btnProcess;

    ByteArrayInputStream inputStream =null;
    Bitmap photo=null;
    public static final int REQUEST_CAPTURE=1;
        int i1;
        int position;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CAPTURE)
            if(resultCode == RESULT_OK){
                Bundle extras = data.getExtras();
                photo = (Bitmap) extras.get("data");
                imageView.setImageBitmap(photo);
                btnProcess.setEnabled(true);



            }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotion);
        emotion = (TextView) findViewById(R.id.emotion);
        emoplay = (Button) findViewById(R.id.emoplay);
        clickpic = (Button) findViewById(R.id.clickpic);
        //int[] p = {R.drawable.angry, R.drawable.disgust,R.drawable.happy, R.drawable.sad, R.drawable.surprise};

       /* Random r = new Random();
        if (savedInstanceState != null) {
            i1 = savedInstanceState.getInt("number", 0);
        }
        else i1 = 0 +  r.nextInt(4);

         mBitmap = BitmapFactory.decodeResource(getResources(), p[i1]);
        imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setImageBitmap(mBitmap);*/

        btnProcess = (Button) findViewById(R.id.btnemotion);

        //Convert image to stream

       //final  ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //mBitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
      //   inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        imageView = (ImageView)findViewById(R.id.imageView);
        if(photo == null)
        {
            btnProcess.setEnabled(false);
        }
        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnProcess.setEnabled(false);
                if(!isNetworkAvailable())
                    Toast.makeText(EmotionActivity.this, "Connect to internet to access Emotion Recognition feature", Toast.LENGTH_LONG).show();
                else {

                    final  ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                    inputStream = new ByteArrayInputStream(outputStream.toByteArray());


                    AsyncTask<InputStream, String, List<RecognizeResult>> emotionTask = new AsyncTask<InputStream, String, List<RecognizeResult>>() {

                        ProgressDialog mDialog = new ProgressDialog(EmotionActivity.this);

                        @Override
                        protected List<RecognizeResult> doInBackground(InputStream... params) {
                            try {

                                publishProgress("Recognising.....");
                                List<RecognizeResult> result = emotionServiceClient.recognizeImage(params[0]);
                                return result;
                            } catch (Exception ex) {
                                return null;

                            }


                        }

                        @Override
                        protected void onPreExecute() {
                            mDialog.show();
                        }

                        @Override
                        protected void onPostExecute(List<RecognizeResult> recognizeResults) {
                            mDialog.dismiss();

                            for (RecognizeResult res : recognizeResults) {
                                String status;
                                status = getEmo(res);
                                imageView.setImageBitmap(ImageHelper.drawRectOnBitmap(photo, res.faceRectangle, status));

                            }
                        }


                        @Override
                        protected void onProgressUpdate(String... values) {
                            mDialog.setMessage(values[0]);
                        }
                    };
                    emotionTask.execute(inputStream);
                }
            }
        });

        emoplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mplayer!=null) {
                    if (mplayer.isPlaying()) {
                        mplayer.pause();
                        emoplay.setText("Play");
                    } else {
                        mplayer.start();
                        emoplay.setText("Pause");
                    }
                }
                else
                {
                    Toast.makeText(getApplication(),
                            "Recognise the emotion first", Toast.LENGTH_LONG).show();
                }
            }
        });

        if(!hasCamera()){
            clickpic.setEnabled(false);
        }
        clickpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(in, REQUEST_CAPTURE);

            }
        });


    }

    private boolean hasCamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private String getEmo(RecognizeResult res) {

        List<Double> list = new ArrayList<>();
        Scores scores = res.scores;

        list.add(scores.anger);
        list.add(scores.happiness);
        list.add(scores.contempt);
        list.add(scores.disgust);
        list.add(scores.fear);
        list.add(scores.neutral);
        list.add(scores.sadness);
        list.add(scores.surprise);

        Collections.sort(list);
        double maxNum = list.get(list.size()-1);
        if(maxNum == scores.anger) {
            emotion.setText("Anger");
            mplayer = mplayer.create(EmotionActivity.this, R.raw.pakkamvathu);
            mplayer.start();
            return "Anger";
        }
        else if(maxNum == scores.happiness) {
            emotion.setText("Happy");
            mplayer = mplayer.create(EmotionActivity.this, R.raw.ifeelgood);
            mplayer.start();
            return "happiness";
        }
        else if(maxNum == scores.contempt) {
            emotion.setText("contempt");
            mplayer = mplayer.create(EmotionActivity.this, R.raw.perfecttwo);
            mplayer.start();
            return "contempt";
        }
        else if(maxNum == scores.disgust) {
            emotion.setText("disgust");
            mplayer = mplayer.create(EmotionActivity.this, R.raw.haledil);
            mplayer.start();
            return "disgust";
        }
        else if(maxNum == scores.fear) {
            emotion.setText("fear");
            mplayer = mplayer.create(EmotionActivity.this, R.raw.zehnaseeb);
            mplayer.start();
            return "fear";
        }
        else if(maxNum == scores.neutral) {
            emotion.setText("neutral");
            mplayer = mplayer.create(EmotionActivity.this, R.raw.first);
            mplayer.start();
            return "neutral";
        }
        else if(maxNum == scores.sadness) {
            emotion.setText("sad");
            mplayer = mplayer.create(EmotionActivity.this, R.raw.neeyaaro);
            mplayer.start();
            return "sadness";
        }
        else if(maxNum == scores.surprise) {
            emotion.setText("surprise");
            mplayer = mplayer.create(EmotionActivity.this, R.raw.iskiuski);
            mplayer.start();
            return "surprise";
        }
        else{
            emotion.setText("neutral");
            mplayer = mplayer.create(EmotionActivity.this, R.raw.ifeelgood);
            mplayer.start();
            return "Neutral";
        }




    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Make sure to call the super method so that the states of our views are saved
        super.onSaveInstanceState(outState);
        if(mplayer!=null) {
            outState.putInt("Position", mplayer.getCurrentPosition());
            outState.putBoolean("isplaying", mplayer.isPlaying());

            if (mplayer.isPlaying())
                mplayer.pause();
        }
        // Save our own state now
        outState.putInt("number", i1);
    }




    @Override

    public void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);

        position = savedInstanceState.getInt("Position");
        if(mplayer!=null) {
            mplayer.seekTo(position);
            if (savedInstanceState.getBoolean("isplaying"))
                mplayer.start();

        }

    }


}