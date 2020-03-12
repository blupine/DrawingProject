package com.example.drawingproject.CanvasView.Utils;

public class Point {

    private long time;
    float x;
    float y;
    float pressure;

    public Point(float paramFloat1, float paramFloat2) {
        this.x = paramFloat1;
        this.y = paramFloat2;
    }

    public Point(float paramFloat1, float paramFloat2, long paramLong) {
        this.x = paramFloat1;
        this.y = paramFloat2;
        this.time = paramLong;
    }

    public Point(float x, float y, float p){
        this.x = x;
        this.y = y;
        this.pressure = p + (float)0.7;
    }

    protected float distanceTo(Point paramPoint) {
        float f1 = this.x - paramPoint.getX();
        float f2 = this.y - paramPoint.getY();
        return (float)Math.sqrt(f1 * f1 + f2 * f2);
    }

    public long getTime() {
        return this.time;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public void setX(float paramFloat) {
        this.x = paramFloat;
    }

    public void setY(float paramFloat) {
        this.y = paramFloat;
    }

    public float velocityFrom(Point start) {
        return distanceTo(start) / (this.time - start.time);
    }
}