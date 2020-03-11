package com.example.drawingproject.CanvasView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.example.drawingproject.drawview.views.ZoomRegionView;

import java.util.ArrayList;
import java.util.List;


/**
 * This class defines fields and methods for drawing.
 * Because we should use color picker, pen style picker etc.. for this, extends FrameLayout instead of View
 */
//public class CanvasView extends FrameLayout implements View.OnTouchListener {
public class CanvasView extends FrameLayout implements View.OnTouchListener {

    private static final String TAG = "CanvasView";
    private static final float TOUCH_TOLERANCE = 4;

    private int penMode = PenMode.PEN;

    private Canvas mCanvas;
    private Paint mPaint;
    private Bitmap mBitmap;
    private Rect mInvalidateRect;

    private List<DrawInfo> history = new ArrayList<DrawInfo>();
    private int historyPointer = -1;

    public CanvasView(Context context) {
        super(context);
        initCanvasView();
    }

    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initCanvasView();

    }

    public CanvasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCanvasView();


    }

    public CanvasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initCanvasView();
    }


    private void initCanvasView(){
        this.setWillNotDraw(false);

        setOnTouchListener(this);

        this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeWidth(3F);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mPaint.setStrokeJoin(Paint.Join.ROUND);

        this.mPaint.setColor(Color.BLACK);
        this.mPaint.setShadowLayer(0f, 0F, 0F, Color.BLACK);
        this.mPaint.setAlpha(255);
        this.mPaint.setPathEffect(null);

        getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {

                        getViewTreeObserver()
                                    .removeOnGlobalLayoutListener(this);

                        Bitmap init = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
                        mBitmap = init.copy(Bitmap.Config.ARGB_8888, true);
                        mCanvas = new Canvas(mBitmap);
                        init.recycle();
                    }
                });


    }

    @Override
    protected void onDraw(Canvas canvas) {
//        if(this.mBitmap != null){
//            canvas.drawBitmap(this.mBitmap, 0, 0, this.mPaint);
//        }
//
        for(int i = 0 ; i < this.historyPointer + 1; i++){
            DrawInfo info = this.history.get(i);
            canvas.drawPath(info.getPath(), info.getPaint());
        }

        //canvas.drawBitmap(this.mBitmap,0, 0, null);
        super.onDraw(canvas);

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //this.mCanvas.drawPoint(event.getX(), event.getY(), this.mPaint);
                switch(event.getAction()){
            case MotionEvent.ACTION_MOVE:
                onActionMove(event);
                break;
            case MotionEvent.ACTION_DOWN:

                onActionDown(event);
                break;
            case MotionEvent.ACTION_UP:
                onActionUp(event);
                break;
            default:
                break;
        }

        float touchX = event.getX();
        float touchY = event.getY();

        if(history.size() > 0){
            mInvalidateRect = new Rect(
                (int) (touchX - (history.get(historyPointer).getPaint().getStrokeWidth() * 2)),
                (int) (touchY - (history.get(historyPointer).getPaint().getStrokeWidth() * 2)),
                (int) (touchX + (history.get(historyPointer).getPaint().getStrokeWidth() * 2)),
                (int) (touchY + (history.get(historyPointer).getPaint().getStrokeWidth() * 2)));
        }

        /* call invalidate(Rect) to renew screen */
        this.invalidate(mInvalidateRect.left, mInvalidateRect.top, mInvalidateRect.right, mInvalidateRect.bottom);
        return true;  /* return true for serial touch event */
    }

    private void onActionDown(MotionEvent event){
        Log.d(TAG, "onActionDown called");
        switch (this.penMode){
            case PenMode.PEN:
                addNewDrawInfo(event);
                break;
            default:
                break;
        }
    }

    private void onActionMove(MotionEvent event){
        Log.d(TAG, "onActionMove called");
        float x, y;
        switch(this.penMode){
            case PenMode.PEN:
                for(int i = 0 ; i < event.getHistorySize(); i++){
                    x = event.getHistoricalX(i);
                    y = event.getHistoricalY(i);
                    drawPath(x, y);
                }
                drawPath(event.getX(), event.getY());
                break;
            default:
                break;
        }
    }
    private void addNewDrawInfo(MotionEvent event){
        DrawInfo dInfo = new DrawInfo(event, this.mPaint, this.penMode);
        this.history.add(dInfo);
        historyPointer += 1;
    }

    private void drawPath(float x, float y){
        this.history.get(historyPointer).moveTo(x, y);
    }


    private void onActionUp(MotionEvent event){

    }

}