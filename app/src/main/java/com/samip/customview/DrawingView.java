package com.samip.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;

public class DrawingView extends View {

    public static final float TOUCH_TOLERANCE = 10f;

    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private Paint paintScreen;
    private Paint paintLine;
    private HashMap<Integer, Path> pathMap;
    private HashMap<Integer, Point> previousPointMap;

    public DrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        paintScreen = new Paint();

        paintLine = new Paint();
        paintLine.setStrokeWidth(5f);
        paintLine.setAntiAlias(true);
        paintLine.setColor(Color.BLACK);
        paintLine.setStrokeCap(Paint.Cap.ROUND);
        paintLine.setStyle(Paint.Style.STROKE);

        pathMap = new HashMap<>();
        previousPointMap = new HashMap<>();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // initialize bitmap as a bucket where all pixel goes
        bitmap = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.WHITE);
        bitmapCanvas = new Canvas(bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // testing to draw circle
        //canvas.drawCircle(getWidth()/2, getHeight()/2, 100f, paintLine);

        // draw bitmap in a canvas
        canvas.drawBitmap(bitmap,0, 0, paintScreen);
        //
        for(Integer key: pathMap.keySet()){
            // called after each point draw
            Log.d("On Draw", "onDraw: called");
            canvas.drawPath(pathMap.get(key), paintLine);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked(); // event type like: touched or not
        int actionIndex = event.getActionIndex(); // pointer on screen by finger, mouse, stylus

        if(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_UP){
            // to get coordinates while touching
            touchStarted(event.getX(actionIndex),
                    event.getY(actionIndex),
                    event.getPointerId(actionIndex));

            // logging
            Log.d("Touch Started TEST: ", "onTouchEvent: "+String.valueOf(event.getPointerId(actionIndex)));
        }else if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP){
            //
            touchEnded(event.getPointerId(actionIndex));
        }else{
            touchMoved(event);
        }

        invalidate();// redraw the screen

        return true;
    }

    private void touchMoved(MotionEvent event) {
        for (int i =0; i < event.getPointerCount(); i++){

            int pointerId = event.getPointerId(i);
            int pointerIndex = event.findPointerIndex(pointerId);


            if(pathMap.containsKey(pointerId)){
                float newX = event.getX(pointerIndex);
                float newY = event.getY(pointerIndex);


                Path path = pathMap.get(pointerId);
                Point point = previousPointMap.get(pointerId);


                // Calculate how far the user moved from the last update
                float deltaX = Math.abs(newX - point.x);
                float deltaY = Math.abs(newY - point.y);


                // if the distance is significant enough to be considered a movement then...
                if (deltaX >= TOUCH_TOLERANCE ||
                        deltaY >= TOUCH_TOLERANCE) {
                    // move the path to the new location
                    path.quadTo(point.x, point.y,
                            (newX + point.x) / 2,
                            (newY + point.y) / 2);


                    // store the new coordinates
                    point.x = (int) newX;
                    point.y = (int) newY;
                }
            }
        }
        
    }

    // removes all the drawing from the bitmap
    public void clearAll(){
        pathMap.clear(); // remove all the path
        previousPointMap.clear();
        bitmap.eraseColor(Color.WHITE);
        invalidate(); // refresh the screen
    }

    // set color of line
    public void setLineColor(int color){
        paintLine.setColor(color);
    }

    public int getLineColor(){
        return paintLine.getColor();
    }

    // set paint line width
    public void setLineWidth(int lineWidth){
        paintLine.setStrokeWidth(lineWidth);
    }

    public int getLineWidth(){
        return (int) paintLine.getStrokeWidth();
    }
    private void touchEnded(int pointerId) {
        Path path = pathMap.get(pointerId); // get the corresponding path
        bitmapCanvas.drawPath(path,paintLine); // draw the bitmapCanvas
        path.reset();
    }

    private void touchStarted(float x, float y, int pointerId) {
        // draw a line by path
        Path path; // store the path for given line
        Point point; // store the last point in path

        if(pathMap.containsKey(pointerId)){
            path = pathMap.get(pointerId);
            point = previousPointMap.get(pointerId);
        }else{
            path = new Path();
            pathMap.put(pointerId,path);
            point = new Point();
            previousPointMap.put(pointerId,point);
        }

        // move to the coordinates of the touch
        path.moveTo(x,y);
        point.x = (int) x;
        point.y = (int) y;

    }
}
