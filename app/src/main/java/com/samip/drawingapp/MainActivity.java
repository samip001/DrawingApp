package com.samip.drawingapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

    private final String TAG = "MainActivity";

    private DrawingView drawingView;
    private AlertDialog.Builder currentDialog;
    private AlertDialog dialogLineWidth;
    private AlertDialog dialogColor;

    // used when changing the width of line from dialog_line_width layout
    private ImageView dialogWidthImageView;
    // used when chnaging the colof of line from dialog_color layout
    private SeekBar alphaSeekBar;
    private SeekBar redColorSeekBar;
    private SeekBar greenColorSeekBar;
    private SeekBar blueColorSeekBar;
    private View colorDialogColorView;

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
                showColorDialog();
                break;
            case R.id.line_width:
                showLineWidthDialog();
                break;
            case R.id.save:
                String path = drawingView.saveToInternalStorage();
                //drawingView.saveImageToExternalStorage();
                Intent intent = new Intent(getApplicationContext(),ImageLoadActivity.class);
                intent.putExtra("imagepath",path);
                startActivity(intent);
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
        lineWidthSeekBar.setProgress(drawingView.getLineWidth());
        lineWidthSeekBar.setOnSeekBarChangeListener(widthSeekBarChange);

        Button lineWidthBtn = dialogView.findViewById(R.id.dialog_line_width_btn);
        lineWidthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Line Width Seekbar: "+lineWidthSeekBar.getProgress());
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

    public void showColorDialog(){
        currentDialog = new AlertDialog.Builder(this);
        View colorDialogView = getLayoutInflater().inflate(R.layout.dialog_color,null);
        // get all the components from dialog_color layout
        alphaSeekBar = colorDialogView.findViewById(R.id.color_alpha);
        redColorSeekBar = colorDialogView.findViewById(R.id.color_red);
        greenColorSeekBar = colorDialogView.findViewById(R.id.color_green);
        blueColorSeekBar = colorDialogView.findViewById(R.id.color_blue);
        colorDialogColorView = colorDialogView.findViewById(R.id.color_colorview);
        Button colorBtn = colorDialogView.findViewById(R.id.color_line_color_btn);


        // register color seekbar event listeners
        alphaSeekBar.setOnSeekBarChangeListener(colorSeekbarChanged);
        redColorSeekBar.setOnSeekBarChangeListener(colorSeekbarChanged);
        greenColorSeekBar.setOnSeekBarChangeListener(colorSeekbarChanged);
        blueColorSeekBar.setOnSeekBarChangeListener(colorSeekbarChanged);

        // set btn action event listener
        colorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawingView.setLineColor(Color.argb(
                        alphaSeekBar.getProgress(),
                        redColorSeekBar.getProgress(),
                        greenColorSeekBar.getProgress(),
                        blueColorSeekBar.getProgress()
                ));

                dialogColor.dismiss();
                currentDialog = null;
            }
        });

        // setting seekbar with progress with selected color
        int color = drawingView.getLineColor();
        alphaSeekBar.setProgress(Color.alpha(color));
        redColorSeekBar.setProgress(Color.red(color));
        greenColorSeekBar.setProgress(Color.green(color));
        blueColorSeekBar.setProgress(Color.blue(color));

        // setting view with selected color
        colorDialogColorView.setBackgroundColor(Color.argb(
                Color.alpha(color),
                Color.red(color),
                Color.green(color),
                Color.blue(color))
        );

        currentDialog.setView(colorDialogView);
        dialogColor = currentDialog.create();
        dialogColor.setTitle("Set Line Color");
        dialogColor.show();


    }


    private SeekBar.OnSeekBarChangeListener colorSeekbarChanged = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int newColor = Color.argb(
                    alphaSeekBar.getProgress(),
                    redColorSeekBar.getProgress(),
                    greenColorSeekBar.getProgress(),
                    blueColorSeekBar.getProgress()
            );
            // setting new color
            //drawingView.setLineColor(newColor);

            // set the dialog view with color
            colorDialogColorView.setBackgroundColor(newColor);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

}
