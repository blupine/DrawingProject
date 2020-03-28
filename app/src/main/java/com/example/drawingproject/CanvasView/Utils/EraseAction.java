package com.example.drawingproject.CanvasView.Utils;

import android.graphics.Canvas;
import android.graphics.Paint;

public class EraseAction extends DrawAction {

    public EraseAction(float x, float y, float p, Paint paint, Canvas canvas, int penMode){
        super(x, y, p, paint, canvas, penMode);
    }

    @Override
    public void addPoint(float x, float y, float p) {
        this.mPath.quadTo((x + lastX)/2, (y + lastY)/2, x, y);
//        this.mPath.lineTo(x, y);

//        this.mPath.setWidth(strokeWidth * p);
//        this.mPaint.setStrokeWidth(strokeWidth * p);
//        this.mPaint.setStrokeWidth(this.mPath.getWidth());
        this.mCanvas.drawPath(this.mPath, this.mPaint);

        this.mPaint.setStrokeWidth(strokeWidth);

        this.mPaths.add(this.mPath);

        this.mPath = new StrokePath();

        this.lastX = x;
        this.lastY = y;

        this.mPath.moveTo(lastX, lastY);
    }
}
