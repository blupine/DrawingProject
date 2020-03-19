package com.example.drawingproject.CanvasView;

import android.content.Context;
import android.gesture.Gesture;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.drawingproject.CanvasView.Utils.DrawAction;
import com.example.drawingproject.CanvasView.Utils.HistoricalAction;
import com.example.drawingproject.CanvasView.Utils.PenMode;

import java.util.ArrayList;
import java.util.List;


/**
 * This class defines fields and methods for drawing.
 * Because we should use color picker, pen style picker etc.. for this, extends FrameLayout instead of View
 */
//public class CanvasView extends FrameLayout implements View.OnTouchListener {
public class CanvasView extends FrameLayout implements View.OnTouchListener {

    private static final String TAG = "CanvasView";

    private int penMode = PenMode.PEN;

    private Context mContext;

    private Canvas mCanvas;
    private Paint mPaint;
    private Bitmap mBitmap;
    private Rect mInvalidateRect;

    private List<HistoricalAction> history = new ArrayList<>();
    private int curHistoryPtr = -1;
    private int lastHistoryPtr = -1;

    private ScaleGestureDetector mScaleDetector;

    private int mActivePointerID; // while handling multi touch, we should check PointerID to prevent strokes being smashed.
    private boolean isMultiTouching = false;


    private Matrix drawMatrix;
    private float lastFocusX, lastFocusY;

    private GestureDetector mGestureDetector;

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

        this.mContext = getContext();


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

        this.drawMatrix = new Matrix();
        this.mScaleDetector = new ScaleGestureDetector(this.mContext, new ScaleGestureListener());

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

    /* ScaleGestureListener for handling scaling and translating canvas */
    private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            lastFocusX = detector.getFocusX();
            lastFocusY = detector.getFocusY();
            return super.onScaleBegin(detector);
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Log.d(TAG, String.format("onScale : (%f)", detector.getScaleFactor()));
            Matrix transformationMatrix = new Matrix();

            float focusX = detector.getFocusX();
            float focusY = detector.getFocusY();

            //Zoom focus is where the fingers are centered,
            transformationMatrix.postTranslate(-focusX, -focusY);

            float focusShiftX = focusX - lastFocusX;
            float focusShiftY = focusY - lastFocusY;
            transformationMatrix.postScale(detector.getScaleFactor(), detector.getScaleFactor());
            transformationMatrix.postTranslate(focusX + focusShiftX, focusY + focusShiftY);

            drawMatrix.postConcat(transformationMatrix);
            lastFocusX = focusX;
            lastFocusY = focusY;
            invalidate();
            return true;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(this.mBitmap, drawMatrix, null);

        super.onDraw(canvas);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mScaleDetector.onTouchEvent(event);

        if(event.getPointerCount() == 1) {
            /* Mapping touch coordinate onto translated & scaled canvas matrix */
            Matrix invertMatrix = new Matrix();
            drawMatrix.invert(invertMatrix);

            float[] translated_xy = {event.getX(), event.getY()};
            invertMatrix.mapPoints(translated_xy);

            float p = event.getPressure();

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_MOVE:

                    if(this.mActivePointerID == event.getPointerId(event.getActionIndex()) && !isMultiTouching) {

                        for(int i = 0 ; i < event.getHistorySize() ; i++){
                            float[] hTranslated_xy = {event.getHistoricalX(i), event.getHistoricalY(i)};
                            float pressure = event.getHistoricalPressure(i);

                            invertMatrix.mapPoints(hTranslated_xy);
                            onActionMove(hTranslated_xy[0], hTranslated_xy[1], pressure);
                        }
                        onActionMove(translated_xy[0], translated_xy[1], p);
                    }
                    break;

                case MotionEvent.ACTION_DOWN:

                    this.mActivePointerID = event.getPointerId(0); // save first touch pointer
                    this.isMultiTouching = false;
                    onActionDown(translated_xy[0], translated_xy[1], p);
                    break;

                default:
                    break;
            }


            if (history.size() > 0) {
                mInvalidateRect = new Rect(
                        (int) (translated_xy[0] - (this.mPaint.getStrokeWidth() * 2)),
                        (int) (translated_xy[1] - (this.mPaint.getStrokeWidth() * 2)),
                        (int) (translated_xy[0] + (this.mPaint.getStrokeWidth() * 2)),
                        (int) (translated_xy[1] + (this.mPaint.getStrokeWidth() * 2)));
            }

            /* call invalidate(Rect) to renew screen */
            this.invalidate(mInvalidateRect.left, mInvalidateRect.top, mInvalidateRect.right, mInvalidateRect.bottom);

        }
        else{
            this.isMultiTouching = true;
        }
        return true;  /* return true for serial touch event */
    }

    private void onActionDown(float x, float y, float p){
        Log.d(TAG, "onActionDown called");

        switch (this.penMode){
            case PenMode.PEN:
                addNewDrawInfo(x, y, p);
                break;
            case PenMode.ERASER:

                break;
            default:
                break;
        }

    }

    private void onActionMove(float x, float y, float p){
        Log.d(TAG, "onActionMove called");
        switch(this.penMode){
            case PenMode.PEN:
                ((DrawAction)this.history.get(curHistoryPtr)).addPoint(x, y, p);
                break;
            case PenMode.ERASER:

                break;
            default:
                break;
        }
    }


    private void addNewDrawInfo(float x, float y, float p){
        DrawAction dInfo = new DrawAction(x, y, p, this.mPaint, this.mCanvas,this.penMode);
        this.history.add(dInfo);
        this.curHistoryPtr += 1;
        this.lastHistoryPtr = this.curHistoryPtr;
    }

    private void onActionUp(MotionEvent event){

    }

    public void undo(){
        // clean up canvas, and re draw & invalidate
        if(this.curHistoryPtr < 0){
            Toast.makeText(getContext(), "Cannot undoable", Toast.LENGTH_SHORT).show();
        }else{
        }
    }

    public void redo(){
        if(this.lastHistoryPtr > this.curHistoryPtr){
            
        }else{
            Toast.makeText(getContext(), "Cannot redoable", Toast.LENGTH_SHORT).show();
        }
    }

    public void penMode(int penMode){
        this.penMode = penMode;
    }

}