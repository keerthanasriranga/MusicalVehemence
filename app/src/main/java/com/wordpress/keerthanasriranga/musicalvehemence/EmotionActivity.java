package com.wordpress.keerthanasriranga.musicalvehemence;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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

public class EmotionActivity extends AppCompatActivity {

    public EmotionServiceClient emotionServiceClient = new EmotionServiceRestClient("a421fd6dc6a24147abe0942f52e270ca");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotion);

        final Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.happy);
        final ImageView imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setImageBitmap(mBitmap);

        Button btnProcess = (Button) findViewById(R.id.btnemotion);

        //Convert image to stream

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask<InputStream,String,List<RecognizeResult>> emotionTask = new AsyncTask<InputStream,String,List<RecognizeResult>>()
                {

                    ProgressDialog mDialog = new ProgressDialog(EmotionActivity.this);

                    @Override
                    protected List<RecognizeResult> doInBackground(InputStream... params) {
                        try
                        {
                            publishProgress("Recognising.....");
                            List<RecognizeResult> result = emotionServiceClient.recognizeImage(params[0]);
                            return result;
                        }
                        catch (Exception ex){
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
                        for (RecognizeResult res : recognizeResults)
                        {
                            String status;
                            status = getEmo(res);
                            imageView.setImageBitmap(ImageHelper.drawRectOnBitmap(mBitmap, res.faceRectangle, status));
                        }
                    }

                    @Override
                    protected void onProgressUpdate(String... values) {
                        mDialog.setMessage(values[0]);
                    }
                };
                emotionTask.execute(inputStream);
            }
        });
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
        if(maxNum == scores.anger)
            return  "Anger";
        else if(maxNum == scores.happiness)
            return  "happiness";
        else if(maxNum == scores.contempt)
            return  "contempt";
        else if(maxNum == scores.disgust)
            return  "disgust";
        else if(maxNum == scores.fear)
            return  "fear";
        else if(maxNum == scores.fear)
            return  "neutral";
        else if(maxNum == scores.sadness)
            return  "sadness";
        else if(maxNum == scores.surprise)
            return  "surprise";
        else return "Neutral";
    }
}
