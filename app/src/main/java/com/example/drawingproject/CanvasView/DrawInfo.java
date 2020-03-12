package com.example.drawingproject.CanvasView;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;

import com.example.drawingproject.CanvasView.Utils.Bezier;
import com.example.drawingproject.CanvasView.Utils.Point;

import java.util.ArrayList;
import java.util.List;


public class DrawInfo {
    private static final String TAG = "DrawInfo";

    private Canvas mCanvas;
    private int mPenMode;
    // private BackgroundIMG bgImg;

    private List<Point> mPoints = new ArrayList<Point>();
    private List<Bezier> mBezierCurves = new ArrayList<Bezier>();
    private Path mPath;
    private Paint mPaint;
    private int lastBezierStartIdx = 0;
    private int lastPointIndex = -1;

    public DrawInfo(MotionEvent event, Paint paint, Canvas canvas, int penMode){
//        this.mPath = new Path();
//        this.mPath.moveTo(lastX, lastY);
        this.mCanvas = canvas;
        this.mPoints.add(new Point(event.getX(), event.getY(), event.getPressure()));
        this.mPaint = new Paint(paint);
        this.mPenMode = penMode;
        lastPointIndex++;

    }

    public void addPoint(MotionEvent event){
        this.mPoints.add(new Point(event.getX(), event.getY(), event.getPressure()));
        createBezier();
    }

    public void addPoint(float x, float y, float p){
        this.mPoints.add(new Point(x, y, p));
//        this.mPath.lineTo(x, y);
        createBezier();
    }

    private void createBezier(){
        if(lastBezierStartIdx + 3 < mPoints.size()){
            Point p1 = this.mPoints.get(lastBezierStartIdx + 0);
            Point p2 = this.mPoints.get(lastBezierStartIdx + 1);
            Point p3 = this.mPoints.get(lastBezierStartIdx + 2);
            Point p4 = this.mPoints.get(lastBezierStartIdx + 3);
            Bezier localBezier = new Bezier(p1, p2, p3, p4);
            this.mBezierCurves.add(localBezier);
            localBezier.draw(this.mCanvas, this.mPaint);
            lastBezierStartIdx += 3;

        }
    }

    public void drawOnCanvas(Canvas canvas){
        for(Bezier b : this.mBezierCurves){
            b.draw(canvas, this.mPaint);
        }
    }

    public Path getPath(){return this.mPath;}
    public Paint getPaint(){return this.mPaint;}

}

