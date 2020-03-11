package com.example.drawingproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.drawingproject.CanvasView.CanvasView;
import com.example.drawingproject.CanvasView.CanvasView2;
import com.example.drawingproject.drawview.views.DrawCameraView;
import com.example.drawingproject.drawview.views.DrawView;

public class CanvasActivity extends AppCompatActivity {

    private CanvasView mCanvas;
    private CanvasView2 mCanvas2;
    private DrawView mDrawview;
    private DrawCameraView mCameraView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);
        mCanvas = findViewById(R.id.canvas);
//        mCanvas.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        mDrawview = findViewById(R.id.canvas);
//        mCameraView = findViewById(R.id.camera_view);
//        mCameraView.attachCameraView();
    }

    public CanvasView getCanvas() {
        return this.mCanvas;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.canvas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.redo:
                mDrawview.redo();
                return true;
            case R.id.undo:
                mDrawview.undo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
