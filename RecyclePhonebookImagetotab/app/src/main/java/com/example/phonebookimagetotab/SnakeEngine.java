package com.example.phonebookimagetotab;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

class SnakeEngine extends SurfaceView {
    // To hold a reference to the Activity
    private Context context;

    private Random random;

    // For tracking movement Heading
    public enum Heading {UP, RIGHT, DOWN, LEFT}
    // Start by heading to the right
    private Heading heading = Heading.RIGHT;

    // To hold the screen size in pixels
    private int screenX;
    private int screenY;

    // How long is the snake
    private int snakeLength;
    private final int MAX_SNAKE_LENGTH = 200;

    // Where is Bob hiding?
    private int bobX;
    private int bobY;

    // The size in pixels of a snake segment
    private final int BLOCK_SIZE = 30;

    // The size in segments of the playable area
    private int numBlocksWide;
    private int numBlocksHigh;

    private int wideEnd;
    private int highEnd;

    private final long DELAY = 100;
    private Timer timer;
    private TimerTask updateTask;

    // The location in the grid of all the segments
    private int[] snakeXs;
    private int[] snakeYs;

    // Everything we need for drawing

    // A canvas for our paint
    private Canvas canvas;

    // Required to use canvas
    private SurfaceHolder surfaceHolder;

    // Some paint for our canvas
    private Paint paint;

    private float prevTouchX = 0f;
    private float prevTouchY = 0f;

    public SnakeEngine(Context context, Point size) {
        super(context);

        this.context = context;

        random = new Random();

        screenX = size.x;
        screenY = size.y;

        numBlocksWide = screenX / BLOCK_SIZE;
        wideEnd = numBlocksWide * BLOCK_SIZE;
        // How many blocks of the same size will fit into the height
        numBlocksHigh = screenY / BLOCK_SIZE;
        highEnd = numBlocksHigh * BLOCK_SIZE;

        timer = new Timer();
        updateTask = new TimerTask(){
            @Override
            public void run(){
                // Did the head of the snake eat Bob?
                if (snakeXs[0] == bobX && snakeYs[0] == bobY) {
                    eatBob();

                    if(snakeLength == MAX_SNAKE_LENGTH) {
                        // player win
                        newGame();
                        draw();
                        return;
                    }
                }

                moveSnake();

                if (detectDeath()) {
                    //start again
                    newGame();
                }

                draw();
            }
        };

        // Initialize the drawing objects
        surfaceHolder = getHolder();
        paint = new Paint();

        // If you score 200 you are rewarded with a crash achievement!
        snakeXs = new int[MAX_SNAKE_LENGTH];
        snakeYs = new int[MAX_SNAKE_LENGTH];

        // Start the game
        newGame();
        draw();

        timer.schedule(updateTask, DELAY, DELAY);
    }

    public void pause() {
        timer.cancel();
        timer = null;
    }

    public void resume() {
        if(timer == null) {
            timer = new Timer();
            timer.schedule(updateTask, DELAY, DELAY);
        }
    }

    public void newGame() {
        // Start with a single snake segment
        snakeLength = 1;
        snakeXs[0] = numBlocksWide / 2;
        snakeYs[0] = numBlocksHigh / 2;

        // Get Bob ready for dinner
        spawnBob();
    }

    public void spawnBob() {
        bobX = random.nextInt(numBlocksWide);
        bobY = random.nextInt(numBlocksHigh);
    }

    private void eatBob(){
        //  Got him!
        // Increase the size of the snake
        snakeLength++;
        //replace Bob
        // This reminds me of Edge of Tomorrow. Oneday Bob will be ready!
        spawnBob();
    }

    private void moveSnake(){
        // Move the body
        for (int i = snakeLength; i > 0; i--) {
            // Start at the back and move it
            // to the position of the segment in front of it
            snakeXs[i] = snakeXs[i - 1];
            snakeYs[i] = snakeYs[i - 1];

            // Exclude the head because
            // the head has nothing in front of it
        }

        // Move the head in the appropriate heading
        switch (heading) {
            case UP:
                snakeYs[0]--;
                break;

            case RIGHT:
                snakeXs[0]++;
                break;

            case DOWN:
                snakeYs[0]++;
                break;

            case LEFT:
                snakeXs[0]--;
                break;
        }
    }

    private boolean detectDeath(){
        // Has the snake died?
        boolean dead = false;

        // Hit the screen edge
        if (snakeXs[0] == -1) dead = true;
        if (snakeXs[0] == numBlocksWide) dead = true;
        if (snakeYs[0] == -1) dead = true;
        if (snakeYs[0] == numBlocksHigh) dead = true;

        // Eaten itself?
        for (int i = snakeLength - 1; i > 4; i--) {
            if ((snakeXs[0] == snakeXs[i]) && (snakeYs[0] == snakeYs[i])) {
                dead = true;
            }
        }

        return dead;
    }

    public void draw() {
        // Get a lock on the canvas
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();

            canvas.drawColor(Color.argb(255, 0, 0, 0));

            canvas.save();
            canvas.clipRect(0, 0, wideEnd, highEnd);
            // Fill the screen with Game Code School blue
            // 배경
            canvas.drawColor(Color.argb(255, 137, 119, 173));
            canvas.restore();

            // Set the color of the paint to draw the snake white
            paint.setColor(Color.argb(255, 0, 0, 0));

            // Scale the HUD text
            paint.setTextSize(90);
            //Length text - snake 색깔
            canvas.drawText("Length:" + snakeLength, 10, 70, paint);

            // Draw the snake one block at a time
            for (int i = 0; i < snakeLength; i++) {
                canvas.drawRect(snakeXs[i] * BLOCK_SIZE,
                        (snakeYs[i] * BLOCK_SIZE),
                        (snakeXs[i] * BLOCK_SIZE) + BLOCK_SIZE,
                        (snakeYs[i] * BLOCK_SIZE) + BLOCK_SIZE,
                        paint);
            }

            // Set the color of the paint to draw Bob red
            // 먹이
            paint.setColor(Color.argb(255, 255, 0, 0));

            // Draw Bob
            canvas.drawRect(bobX * BLOCK_SIZE,
                    (bobY * BLOCK_SIZE),
                    (bobX * BLOCK_SIZE) + BLOCK_SIZE,
                    (bobY * BLOCK_SIZE) + BLOCK_SIZE,
                    paint);

            // Unlock the canvas and reveal the graphics for this frame
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                prevTouchX = motionEvent.getX();
                prevTouchY = motionEvent.getY();
                break;

            case MotionEvent.ACTION_UP:
                float dx = motionEvent.getX() - prevTouchX;
                float dy = motionEvent.getY() - prevTouchY;

                if(Math.abs(dx) > Math.abs(dy)) {
                    if(dx > 0) {
                        if(heading != Heading.LEFT)
                            heading = Heading.RIGHT;
                    } else {
                        if(heading != Heading.RIGHT)
                            heading = Heading.LEFT;
                    }
                } else {
                    if(dy > 0) {
                        if(heading != Heading.UP)
                            heading = Heading.DOWN;
                    } else {
                        if(heading != Heading.DOWN)
                            heading = Heading.UP;
                    }
                }
        }
        return true;
    }
}