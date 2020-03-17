package com.example.drawingproject.CanvasView;

import android.content.Context;
import android.gesture.Gesture;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
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

    private List<HistoricalAction> history = new ArrayList<HistoricalAction>();
    private int curHistoryPtr = -1;
    private int lastHistoryPtr = -1;

    private Rect mCanvasClipBounds;
    private boolean mZoomEnabled = true;
    private boolean isZooming = false;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.0f;
    private float mScaleCenterX, mScaleCenterY;

    private int mActivePointerID; // while handling multi touch, we should check PointerID to prevent strokes being smashed.
    private boolean isMultiTouching = false;


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

        this.mContext = getContext();

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

        this.mCanvasClipBounds = new Rect();

        this.mScaleDetector = new ScaleGestureDetector(this.mContext, new ScaleGestureDetector.SimpleOnScaleGestureListener(){

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                isZooming = true;
                Log.d(TAG, "ScaleFactor : " + detector.getScaleFactor());
                mScaleFactor *= detector.getScaleFactor();
                mScaleCenterX = detector.getFocusX();
                mScaleCenterY = detector.getFocusY();
                invalidate();
                return true;
            }

        });

        this.mGestureDetector = new GestureDetector(this.mContext, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if(isZooming) return false;
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });

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
        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor, mScaleCenterX, mScaleCenterY);

        canvas.getClipBounds(mCanvasClipBounds);

        canvas.drawBitmap(this.mBitmap,0, 0, null);
        canvas.restore();


        super.onDraw(canvas);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mScaleDetector.onTouchEvent(event);
//        mGestureDetector.onTouchEvent(event);
        float x = event.getX() / mScaleFactor + mCanvasClipBounds.left;
        float y = event.getY() / mScaleFactor + mCanvasClipBounds.top;

        if(event.getPointerCount() == 1) {

            float p = event.getPressure();

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_MOVE:

                    if(this.mActivePointerID == event.getPointerId(event.getActionIndex()) && !isMultiTouching) {

                        for(int i = 0 ; i < event.getHistorySize() ; i++){

                            float tx = event.getHistoricalX(i) / mScaleFactor + mCanvasClipBounds.left;
                            float ty = event.getHistoricalY(i) / mScaleFactor + mCanvasClipBounds.top;
                            float tp = event.getHistoricalPressure(i);

                            onActionMove(tx, ty, tp);
                        }
                        onActionMove(x, y, p);
                    }
                    break;

                case MotionEvent.ACTION_DOWN:

                    this.mActivePointerID = event.getPointerId(0); // save first touch pointer
                    this.isMultiTouching = false;
                    onActionDown(x, y, p);
                    break;

                default:
                    break;
            }


            if (history.size() > 0) {
                mInvalidateRect = new Rect(
                        (int) (x - (this.mPaint.getStrokeWidth() * 2)),
                        (int) (y - (this.mPaint.getStrokeWidth() * 2)),
                        (int) (x + (this.mPaint.getStrokeWidth() * 2)),
                        (int) (y + (this.mPaint.getStrokeWidth() * 2)));
            }

            /* call invalidate(Rect) to renew screen */
            this.invalidate(mInvalidateRect.left, mInvalidateRect.top, mInvalidateRect.right, mInvalidateRect.bottom);

        }
        else{
            //
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