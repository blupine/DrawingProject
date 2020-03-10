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

    public DrawInfo(MotionEvent event, Paint paint, int penMode){
        this.mPath = new Path();
        this.mPath.moveTo(event.getX(), event.getY());
        this.mPaint = paint;
        this.mPenMode = penMode;
    }

    public void moveTo(MotionEvent event){
        Log.d(TAG, "moveTo : " + event.getX() + ", " + event.getY());
        this.mPath.lineTo(event.getX(), event.getY());
    }

    public Path getPath(){return this.mPath;}
    public Paint getPaint(){return this.mPaint;}

}
