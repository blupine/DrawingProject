package com.example.drawingproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.drawingproject.CanvasView.CanvasView;
import com.example.drawingproject.CanvasView.CanvasView2;
import com.example.drawingproject.CanvasView.Utils.PenMode;
import com.example.drawingproject.drawview.views.DrawCameraView;
import com.example.drawingproject.drawview.views.DrawView;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;

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
                mCanvas.redo();
                return true;
            case R.id.undo:
                mCanvas.undo();
                return true;
            case R.id.pen:
                mCanvas.setPenMode(PenMode.PEN);
                return true;
            case R.id.eraser:
                mCanvas.setPenMode(PenMode.ERASER);
                return true;
            case R.id.colorpicker:
                ColorPickerDialog.newBuilder().show(this);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
