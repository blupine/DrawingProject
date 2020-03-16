package com.example.drawingproject.CanvasView.Utils;

import android.graphics.Path;

public class StrokePath extends Path {
    private float width;

    public void setWidth(float w){
        this.width = w;
    }

    public float getWidth(){
        return this.width;
    }
}
