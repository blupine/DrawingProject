package com.example.drawingproject.CanvasView.Utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Bezier {
    private Point controlPointOne;
    private Point controlPointTwo;
    private int drawSteps;
    private Point endPoint;
    private int mColor;
    private Point startPoint;
    private float width;

    public Bezier() {
    }

    public Bezier(Point paramPoint1, Point paramPoint2, Point paramPoint3,
                  Point paramPoint4) {
        this.startPoint = paramPoint1;
        this.controlPointOne = paramPoint2;
        this.controlPointTwo = paramPoint3;
        this.endPoint = paramPoint4;
        this.drawSteps = ((int) (paramPoint1.distanceTo(paramPoint2)
                + paramPoint2.distanceTo(paramPoint3) + paramPoint3
                .distanceTo(paramPoint4)));
        this.width = paramPoint1.pressure;
    }

    public void draw(Canvas canvas, Paint paint) {
        float originalWidth = paint.getStrokeWidth();
        for (int i = 0; i < drawSteps; i++) {
            float t = ((float) i) / drawSteps;
            float tt = t * t;
            float ttt = tt * t;
            float u = 1 - t;
            float uu = u * u;
            float uuu = uu * u;

            float x = uuu * startPoint.x;
            x += 3 * uu * t * this.controlPointOne.x;
            x += 3 * u * tt * this.controlPointTwo.x;
            x += ttt * endPoint.x;

            float y = uuu * startPoint.y;
            y += 3 * uu * t * this.controlPointOne.y;
            y += 3 * u * tt * this.controlPointTwo.y;
            y += ttt * endPoint.y;

            //paint.setStrokeWidth(paint.getStrokeWidth() + this.width);
            canvas.drawPoint(x, y, paint);
        }

        paint.setStrokeWidth(originalWidth);
    }


    public void draw(Canvas canvas, Paint paint, float startWidth, float endWidth) {
        float originalWidth = paint.getStrokeWidth();
        float widthDelta = endWidth - startWidth;

        for (int i = 0; i < drawSteps; i++) {
            float t = ((float) i) / drawSteps;
            float tt = t * t;
            float ttt = tt * t;
            float u = 1 - t;
            float uu = u * u;
            float uuu = uu * u;

            float x = uuu * startPoint.x;
            x += 3 * uu * t * getControlPointOne().x;
            x += 3 * u * tt * getControlPointTwo().x;
            x += ttt * endPoint.x;

            float y = uuu * startPoint.y;
            y += 3 * uu * t * getControlPointOne().y;
            y += 3 * u * tt * getControlPointTwo().y;
            y += ttt * endPoint.y;

            paint.setStrokeWidth(startWidth + ttt * widthDelta);
            canvas.drawPoint(x, y, paint);
        }

        paint.setStrokeWidth(originalWidth);
    }

    public int getColor() {
        return this.mColor;
    }

    public Point getControlPointOne() {
        return this.controlPointOne;
    }

    public Point getControlPointTwo() {
        return this.controlPointTwo;
    }

    public int getDrawSteps() {
        return this.drawSteps;
    }

    public Point getEndPoint() {
        return this.endPoint;
    }

    public Point getStartPoint() {
        return this.startPoint;
    }

    public void setColor(int paramInt) {
        this.mColor = Color.BLACK;
    }

    public void setControlPointOne(Point paramPoint) {
        this.controlPointOne = paramPoint;
    }

    public void setControlPointTwo(Point paramPoint) {
        this.controlPointTwo = paramPoint;
    }

    public void setDrawSteps(int paramInt) {
        this.drawSteps = paramInt;
    }

    public void setEndPoint(Point paramPoint) {
        this.endPoint = paramPoint;
    }

    public void setStartPoint(Point paramPoint) {
        this.startPoint = paramPoint;
    }
}