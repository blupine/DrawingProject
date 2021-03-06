package com.example.drawingproject.CanvasView.Utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;


import java.util.ArrayList;
import java.util.List;


public class DrawAction extends HistoricalAction{
    private static final String TAG = "DrawAction";

    Canvas mCanvas;
    private int mPenMode;
    // private BackgroundIMG bgImg;

    List<StrokePath> mPaths = new ArrayList<>();
    StrokePath mPath;

    Paint mPaint;
    float strokeWidth;

    float lastX, lastY;

    public DrawAction(float x, float y, float p, Paint paint, Canvas canvas, int penMode){
        this.isActivated = true;

        this.mCanvas = canvas;
        this.mPaint = new Paint(paint);
        this.strokeWidth = this.mPaint.getStrokeWidth();
        this.mPenMode = penMode;

        this.mPath = new StrokePath();
        this.mPath.moveTo(x, y);

        this.lastX = x;
        this.lastY =y;
    }

    public void addPoint(float x, float y, float p){

        this.mPath.quadTo((x + lastX)/2, (y + lastY)/2, x, y);
//        this.mPath.lineTo(x, y);

        this.mPath.setWidth(strokeWidth * p);
        this.mPaint.setStrokeWidth(strokeWidth * p);
//        this.mPaint.setStrokeWidth(this.mPath.getWidth());
        this.mCanvas.drawPath(this.mPath, this.mPaint);

        this.mPaint.setStrokeWidth(strokeWidth);

        this.mPaths.add(this.mPath);

        this.mPath = new StrokePath();

        this.lastX = x;
        this.lastY = y;

        this.mPath.moveTo(lastX, lastY);
    }

    public void redraw() {
        for(StrokePath path : this.mPaths){
            this.mPaint.setStrokeWidth(path.getWidth());
            this.mCanvas.drawPath(path, this.mPaint);
        }
    }

    public List<StrokePath> getPathList(){return this.mPaths;}
    public Paint getPaint(){return this.mPaint;}
    public boolean isActivated(){return this.isActivated;}

}

