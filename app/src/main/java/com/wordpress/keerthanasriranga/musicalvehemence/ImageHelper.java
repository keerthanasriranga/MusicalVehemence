package com.wordpress.keerthanasriranga.musicalvehemence;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.microsoft.projectoxford.emotion.contract.FaceRectangle;

/**
 * Created by Keerthana Sriranga on 14-04-2017.
 */

public class ImageHelper {

    public  static Bitmap drawRectOnBitmap(Bitmap mBitmap, FaceRectangle faceRectangle, String status)
    {
        Bitmap bitmap = mBitmap.copy(Bitmap.Config.ARGB_8888,true);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(12);


        canvas.drawRect(faceRectangle.left, faceRectangle.top,
                faceRectangle.left+faceRectangle.width,
                faceRectangle.top+faceRectangle.height,paint);

        int cX = faceRectangle.left+faceRectangle.width;
        int cY = faceRectangle.top+faceRectangle.height;

        drawTextonBitmap(canvas,100,cX/2+cX/5,cY+100,Color.BLACK,status);

        return bitmap;
    }

    private static void drawTextonBitmap(Canvas canvas, int textSize, int cX, int cY, int color, String status) {

        Paint tempTextPaint = new Paint();
        tempTextPaint.setAntiAlias(true);
        tempTextPaint.setStyle(Paint.Style.FILL);
        tempTextPaint.setColor(color);
        tempTextPaint.setTextSize(textSize);

        canvas.drawText(status, cX,cY,tempTextPaint);

    }
}
