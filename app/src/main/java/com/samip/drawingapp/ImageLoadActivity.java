package com.samip.drawingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.samip.customview.DrawingView;

public class ImageLoadActivity extends AppCompatActivity {


    private static final String TAG = ImageLoadActivity.class.getName();
    DrawingView drawingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_load);

        Intent intent = getIntent();
        String path = intent.getStringExtra("imagepath");
        Log.d(TAG, "onCreate: Path Value: "+path);
        drawingView = new DrawingView(this,null);
        ImageView imageView = findViewById(R.id.load_imageview);
        imageView.setImageBitmap(drawingView.loadImageFromStorage(path));
    }
}
