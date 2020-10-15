package com.example.drawingproject.CanvasView.Utils;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;

public class TestTextView extends AppCompatTextView {
    private static final String TAG = "TestTextView";

    FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(500, 110);
    public TestTextView(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "" + event.getRawX() + ", " + event.getRawY());

        layoutParams.setMargins((int)event.getRawX(), (int)event.getRawY(), 0, 0);
        this.setLayoutParams(layoutParams);
        return true;
    }
}
