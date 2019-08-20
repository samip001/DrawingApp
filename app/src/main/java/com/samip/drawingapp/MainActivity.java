package com.samip.drawingapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.samip.customview.DrawingView;

public class MainActivity extends AppCompatActivity {

    private DrawingView drawingView;
    private AlertDialog.Builder currentDialog;
    private AlertDialog dialogLineWidth;
    // used when changing the width of line
    private ImageView dialogWidthImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawingView = findViewById(R.id.drawingview);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater  menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId){
            case R.id.color_palette:
                break;
            case R.id.line_width:
                showLineWidthDialog();
                break;
            case R.id.erase:
                break;
            case R.id.save:
                break;
            case R.id.clear:
                drawingView.clearAll();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showLineWidthDialog(){
        currentDialog = new AlertDialog.Builder(this);

        // get dialog view
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_line_width,null);
        // get all the components inside of a dialog
        dialogWidthImageView = dialogView.findViewById(R.id.dialog_line_width_image_view);
        final SeekBar lineWidthSeekBar = dialogView.findViewById(R.id.dialog_line_width_seek_bar);
        lineWidthSeekBar.setOnSeekBarChangeListener(widthSeekBarChange);

        Button lineWidthBtn = dialogView.findViewById(R.id.dialog_line_width_btn);
        lineWidthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.setLineWidth(lineWidthSeekBar.getProgress());
                // line width dialog is canceled
                dialogLineWidth.dismiss();
                // no dialog is set i.e no pop up dialog is setted
                currentDialog = null;
            }
        });

        // dialog_line_width layout will be set in AlertDialog.builder
        currentDialog.setView(dialogView);

        // dialog line width set and show
        dialogLineWidth = currentDialog.create();
        dialogLineWidth.setTitle("Set Width of Line");
        dialogLineWidth.show();

    }

    // seekbar changed action listener
    private SeekBar.OnSeekBarChangeListener widthSeekBarChange = new SeekBar.OnSeekBarChangeListener() {
        // make a bitmap and show in imageview when line width is changed via seek bar
        Bitmap bitmap = Bitmap.createBitmap(400,100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            Paint p = new Paint();
            p.setColor(drawingView.getLineColor());
            p.setStrokeCap(Paint.Cap.ROUND);
            p.setStrokeWidth(progress);

            bitmap.eraseColor(Color.WHITE);
            canvas.drawLine(30,50,370,50,p);

            // set bitmap in imagedialog view
            dialogWidthImageView.setImageBitmap(bitmap);

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

}
