package com.example.drawingproject.CanvasView;

import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;


public class DrawInfo {
    private static final String TAG = "DrawInfo";
    private int mPenMode;
    // private BackgroundIMG bgImg;
    private Path mPath;
    private Paint mPaint;
    private float lastX, lastY;

    public DrawInfo(MotionEvent event, Paint paint, int penMode){
        lastX = event.getX();
        lastY = event.getY();

        this.mPath = new Path();
        this.mPath.moveTo(lastX, lastY);
        this.mPaint = new Paint(paint);
        this.mPenMode = penMode;

    }

    public void moveTo(float x, float y){

        this.mPath.lineTo(x, y);

        lastX = x;
        lastY = y;
    }

    public Path getPath(){return this.mPath;}
    public Paint getPaint(){return this.mPaint;}

}
