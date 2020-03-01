package com.example.drawingproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.drawingproject.CanvasView.CanvasView;

public class CanvasActivity extends AppCompatActivity {

    private CanvasView mCanvas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);
        mCanvas = findViewById(R.id.canvas);

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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
