package com.example.drawingproject.CanvasView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.drawingproject.CanvasView.Utils.Bezier;
import com.example.drawingproject.CanvasView.Utils.Point;

import java.util.ArrayList;

public class SignatureViewDemo extends View {
    private int color = Color.BLACK;
    private Bitmap m_Bitmap;
    private final Paint m_BorderPaint;
    private Canvas m_Canvas;
    private Point m_CropBotRight;
    private Point m_CropTopLeft;
    private float m_CurrentX;
    private float m_CurrentY;
    private final float m_DesiredDash;
    private float m_LastWidth = 6.5F;
    private Paint m_PenPaint;
    private int m_PointIndex = 0;
    private ArrayList<Point> m_Points = new ArrayList<Point>();
    private final float m_StrokeWidth;
    boolean m_Empty;


    public SignatureViewDemo(Context paramContext) {

        this(paramContext, null);
    }

    public SignatureViewDemo(Context paramContext,
                             AttributeSet paramAttributeSet) {
        this(paramContext, paramAttributeSet, 0);
    }


    public SignatureViewDemo(Context paramContext,
                             AttributeSet paramAttributeSet, int paramInt) {

        super(paramContext, paramAttributeSet, paramInt);
        setFocusable(true);
        this.m_PenPaint = new Paint();
        this.m_PenPaint.setAntiAlias(true);
        this.m_PenPaint.setColor(Color.BLACK);
        this.m_PenPaint.setStrokeWidth(5.0F);
        this.m_PenPaint.setStrokeJoin(Paint.Join.ROUND);
        this.m_PenPaint.setStrokeCap(Paint.Cap.ROUND);
        this.m_CurrentY = (0.0F / 0.0F);
        this.m_CurrentX = (0.0F / 0.0F);
        this.m_StrokeWidth = 5.0F;
        this.m_DesiredDash = 10.0F;
        this.m_BorderPaint = new Paint();
        this.m_BorderPaint.setColor(Color.BLACK);
        this.m_BorderPaint.setStyle(Paint.Style.STROKE);
        this.m_BorderPaint.setStrokeWidth(this.m_StrokeWidth);
    }

    public void addBezier(Bezier paramBezier, float paramFloat1,
                          float paramFloat2) {
        if (this.m_Bitmap == null) {
            this.m_Bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                    Bitmap.Config.ARGB_8888);
            this.m_Canvas = new Canvas(this.m_Bitmap);
        }
        paramBezier.draw(this.m_Canvas, this.m_PenPaint, paramFloat1,
                paramFloat2);
    }

    public void addPoint(Point paramPoint) {
        if ((paramPoint.getX() < this.m_CropTopLeft.getX())
                && (paramPoint.getX() >= 0.0F))
            this.m_CropTopLeft.setX(paramPoint.getX());
        if ((paramPoint.getY() < this.m_CropTopLeft.getY())
                && (paramPoint.getY() >= 0.0F))
            this.m_CropTopLeft.setY(paramPoint.getY());
        if ((paramPoint.getX() > this.m_CropBotRight.getX())
                && (paramPoint.getX() <= this.m_Canvas.getWidth()))
            this.m_CropBotRight.setX(paramPoint.getX());
        if ((paramPoint.getY() > this.m_CropBotRight.getY())
                && (paramPoint.getY() <= this.m_Canvas.getHeight()))
            this.m_CropBotRight.setY(paramPoint.getY());
        this.m_Points.add(paramPoint);
        drawPoints();
    }

// public void clear() {
// if (this.m_Canvas == null)
// this.m_Canvas.drawColor(0, PorterDuff.Mode.CLEAR);
// invalidate();
// // return;
// }

    public void clear() {
        if (this.m_Canvas == null)
            return;
        while (m_Empty) {
            m_Canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            m_Empty = true;
            invalidate();
            return;
        }
    }

    public void drawBitmap(Bitmap paramBitmap) {
        clear();
        if ((paramBitmap != null) && (this.m_Canvas != null)
                && (this.m_Canvas.getWidth() != 0)
                && (this.m_Canvas.getHeight() != 0)) {
            Matrix localMatrix = new Matrix();
            localMatrix.setRectToRect(
                    new RectF(0.0F, 0.0F, paramBitmap.getWidth(), paramBitmap
                            .getHeight()),
                    new RectF(0.0F, 0.0F, this.m_Canvas.getWidth(),
                            this.m_Canvas.getHeight()),
                    Matrix.ScaleToFit.CENTER);
            this.m_Canvas.drawBitmap(paramBitmap, localMatrix, null);
            m_Empty = false;
        }
        invalidate();
    }

    public void drawPoints() {
        if ((m_Points.size() >= 4)
                && (4 + this.m_PointIndex <= this.m_Points.size())) {
            Point localPoint1 = (Point) this.m_Points.get(this.m_PointIndex);
            Point localPoint2 = (Point) this.m_Points
                    .get(1 + this.m_PointIndex);
            Point localPoint3 = (Point) this.m_Points
                    .get(2 + this.m_PointIndex);
            Point localPoint4 = (Point) this.m_Points
                    .get(3 + this.m_PointIndex);
            Bezier localBezier = new Bezier(localPoint1, localPoint2,
                    localPoint3, localPoint4);
            localBezier.setColor(Color.GREEN);
            float f = strokeWidth(8.0F / localPoint4.velocityFrom(localPoint1));
            addBezier(localBezier, this.m_LastWidth, f);
            invalidate();
            this.m_LastWidth = f;
            this.m_PointIndex = (3 + this.m_PointIndex);
            m_Empty = false;
        }
    }

    public boolean isEmpty() {
        return m_Empty;
    }

    public Bitmap getBitmap() {
        return this.m_Bitmap;
    }

    public int getColor() {
        return this.color;
    }

    protected void onDraw(Canvas paramCanvas) {
        if (this.m_Bitmap != null)
            paramCanvas.drawBitmap(this.m_Bitmap, 0.0F, 0.0F, null);
    }

    protected void onMeasure(int paramInt1, int paramInt2) {
        int i = View.MeasureSpec.getSize(paramInt1);
        int j = View.MeasureSpec.getSize(paramInt2);
        this.m_CropTopLeft = new Point(i, j);
        this.m_CropBotRight = new Point(0.0F, 0.0F);
        setMeasuredDimension(i, j);
    }

    protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3,
                                 int paramInt4) {
        Bitmap localBitmap = Bitmap.createBitmap(paramInt1, paramInt2,
                Bitmap.Config.ARGB_8888);
        this.m_Canvas = new Canvas(localBitmap);
        float f1 = 2.0F * (this.m_Canvas.getWidth() + this.m_Canvas.getHeight() - 2.0F * this.m_StrokeWidth);
        float f2 = f1
                * this.m_DesiredDash
                / (Math.round(f1 / (4.0F * this.m_DesiredDash)) * (4.0F * this.m_DesiredDash));
        Paint localPaint = this.m_BorderPaint;
        float[] arrayOfFloat = new float[2];
        arrayOfFloat[0] = f2;
        arrayOfFloat[1] = f2;
        localPaint.setPathEffect(new DashPathEffect(arrayOfFloat, f2 / 2.0F));
        clear();
        if (this.m_Bitmap != null) {
            Rect localRect = new Rect(0, 0, this.m_Canvas.getWidth(),
                    this.m_Canvas.getHeight());
            this.m_Canvas.drawBitmap(this.m_Bitmap, null, localRect, null);
            m_Empty = false;
        }
        this.m_Bitmap = localBitmap;
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent) {
        int i = 0xFF & paramMotionEvent.getAction();
        if (i == MotionEvent.ACTION_DOWN) {
            this.m_CurrentX = paramMotionEvent.getX();
            this.m_CurrentY = paramMotionEvent.getY();
            addPoint(new Point(this.m_CurrentX, this.m_CurrentY,
                    paramMotionEvent.getEventTime()));
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        // while (m_Empty) {
        if ((i == MotionEvent.ACTION_UP) || (i == MotionEvent.ACTION_CANCEL)) {
            this.m_CurrentY = (0.0F / 0.0F);
            this.m_CurrentX = (0.0F / 0.0F);
            this.m_Points.clear();
            this.m_PointIndex = 0;
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        // if ((this.m_Points.size() < 4) || (4 + this.m_PointIndex >
        // this.m_Points.size()))
        // while (1 + this.m_PointIndex <= this.m_Points.size())
        drawPoints();
        if ((i == MotionEvent.ACTION_MOVE) || (i == MotionEvent.ACTION_UP)) {
            for (int j = 0; j < paramMotionEvent.getHistorySize(); j++)
                addPoint(new Point(paramMotionEvent.getHistoricalX(j),
                        paramMotionEvent.getHistoricalY(j),
                        paramMotionEvent.getHistoricalEventTime(j)));
            addPoint(new Point(paramMotionEvent.getX(),
                    paramMotionEvent.getY(), paramMotionEvent.getEventTime()));

        }
        // }
        return true;
    }

    public void setColor(int paramInt) {
        this.color = Color.BLACK;
    }

    public Point getCropBotRight() {
        return this.m_CropBotRight;
    }

    public Point getCropTopLeft() {
        return this.m_CropTopLeft;
    }

// public float strokeWidth(float paramFloat) {
// if (paramFloat > 11.0F)
// paramFloat = 10.0F;
// if (paramFloat < 5.0F)
// paramFloat = 6.0F;
// return paramFloat;
// }

    public float strokeWidth(float paramFloat) {
        if (paramFloat > 11.0F)
            paramFloat = 10.0F;
        while (m_Empty) {
            if (paramFloat < 5.0F)
                paramFloat = 6.0F;
            return paramFloat;
        }
        return paramFloat;
    }
}