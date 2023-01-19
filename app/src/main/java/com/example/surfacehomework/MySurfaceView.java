package com.example.surfacehomework;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceholder;
    private final DrawThread drawThread;

    private Canvas canvas;
    private Pair<Float, Float> circlePosition;
    private Pair<Float, Float> nextPosition;

    private final long UPDATE_TIME = 20;
    private final float STEP_SIZE = 20.0f;


    public MySurfaceView(Context context) {
        super(context);
        getHolder().addCallback(this);
        drawThread = new DrawThread();
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        surfaceholder = holder;
        drawThread.start();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        nextPosition = new Pair<>(event.getX(), event.getY());
        return super.onTouchEvent(event);
    }

    class DrawThread extends Thread {
        private volatile boolean running = true;


        public void calculateNewPosition() {
            float dx = nextPosition.first - circlePosition.first;
            float dy = nextPosition.second - circlePosition.second;
            float length = (float) Math.sqrt((double) (dx*dx + dy*dy));

            if (length <= STEP_SIZE){
                circlePosition = nextPosition;
            }
            else {
                float delta = length/STEP_SIZE;
                dx /= delta;
                dy /= delta;
                circlePosition = new Pair<>(circlePosition.first + dx, circlePosition.second + dy);
            }
        }

        @Override
        public void run() {
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            nextPosition = circlePosition = new Pair<>(getWidth() / 2.0f, getHeight() / 2.0f);

            try {
                while (running) {
                    Thread.sleep(UPDATE_TIME);
                    calculateNewPosition();
                    canvas = surfaceholder.lockCanvas();
                    canvas.drawColor(Color.CYAN);
                    canvas.drawCircle(circlePosition.first, circlePosition.second, 50, paint);

                }
            } catch (Exception ignored) {}
            finally {
                surfaceholder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }


}
