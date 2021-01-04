package com.example.imagefromstorage;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.Math;

import com.google.android.material.snackbar.Snackbar;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity {
    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator currentAnimator;
    private Bitmap Image;

    private float X;
    private float Y;

    private float Width;
    private float Height;

    //처음 이미지를 선택했을 때, 이미지의 X,Y 값과 클릭 지점 간의 거리
    private float offsetX;
    private float offsetY;

    // 드래그시 좌표 저장

    int posX1=0, posX2=0, posY1=0, posY2=0;

    // 핀치시 두좌표간의 거리 저장

    float oldDist = 1f;
    float newDist = 1f;

    // 드래그 모드인지 핀치줌 모드인지 구분
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;

    int mode = NONE;

    private int spancount;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int shortAnimationDuration;

    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieve and cache the system's default "short" animation time.
        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        // should be in onViewCreated
        if(customCheckPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            showImage();
        } else {
            requestReadExternalStoragePermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_READ_EXTERNAL_STORAGE){
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                showImage();
            }
            else{
                Toast.makeText(this, "debug", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean customCheckPermission(String permission) {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestReadExternalStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Snackbar.make(this.findViewById(android.R.id.content), "이미지 갤러리를 사용하려면 권한이 필요합니다.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("확인", new View.OnClickListener() {
                        @Override
                        public void onClick(View V) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    }).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    private Image[] loadImage() {
        ProgressDialog pd;
        ArrayList<Image> images = new ArrayList<Image>();

        pd = ProgressDialog.show(MainActivity.this, "Loading Images", "Please Wait");

        Cursor c = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] {
                        MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.DISPLAY_NAME
                },
                null, null, null);

        int idColumn = c.getColumnIndex(MediaStore.Images.Media._ID);
        int nameColumn = c.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);

        while(c.moveToNext()) {
            long id = c.getLong(idColumn);
            String name = c.getString(nameColumn);

            Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            images.add(new Image(contentUri, name));
        }
        c.close();
        pd.cancel();

        return images.toArray(new Image[0]);
    }

    private void showImage() {
        Image[] images = loadImage();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter mAdapter = new ImageAdapter(this, images);
        recyclerView.setAdapter(mAdapter);
    }

    void zoomImageFromThumb(final View thumbView, Uri uri) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.expanded_image);

        try {
            InputStream is = getContentResolver().openInputStream(uri);
            expandedImageView.setImageDrawable(Drawable.createFromStream(is, uri.toString()));
        } catch (FileNotFoundException e) {
            expandedImageView.setImageResource(R.drawable.ic_launcher_background);
        }

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        set.start();
        currentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentAnimator != null) {
                    currentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(shortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }
                });
                set.start();
                currentAnimator = set;
            }
        });
    }

    public class MovingUnit{
        //이미지
        private Bitmap Image;

        private float X;
        private float Y;

        private float Width;
        private float Height;

        //처음 이미지를 선택했을 때, 이미지의 X,Y 값과 클릭 지점 간의 거리
        private float offsetX;
        private float offsetY;

        // 드래그시 좌표 저장

        int posX1=0, posX2=0, posY1=0, posY2=0;

        // 핀치시 두좌표간의 거리 저장

        float oldDist = 1f;
        float newDist = 1f;

        // 드래그 모드인지 핀치줌 모드인지 구분
        static final int NONE = 0;
        static final int DRAG = 1;
        static final int ZOOM = 2;

        int mode = NONE;



        //Image를 인자로 받는다.
        public MovingUnit(Bitmap Image) {
            // TODO Auto-generated constructor stub
            this.Image=Image;

            setSize(Image.getHeight(),Image.getWidth());
            setXY(0,0);

        }

        public void TouchProcess(MotionEvent event) {
            int act = event.getAction();
            switch(act & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:    //첫번째 손가락 터치
                    if(InObject(event.getX(), event.getY())){//손가락 터치 위치가 이미지 안에 있으면 DragMode가 시작된다.
                        posX1 = (int) event.getX();
                        posY1 = (int) event.getY();
                        offsetX=posX1-X;
                        offsetY=posY1-Y;

                        Log.d("zoom", "mode=DRAG" );

                        mode = DRAG;
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    if(mode == DRAG) {   // 드래그 중이면, 이미지의 X,Y값을 변환시키면서 위치 이동.
                        X=posX2-offsetX;
                        Y=posY2-offsetY;
                        posX2 = (int) event.getX();
                        posY2 = (int) event.getY();
                        if(Math.abs(posX2-posX1)>20 || Math.abs(posY2-posY1)>20) {
                            posX1 = posX2;
                            posY1 = posY2;
                            Log.d("drag","mode=DRAG");
                        }

                    } else if (mode == ZOOM) {    // 핀치줌 중이면, 이미지의 거리를 계산해서 확대를 한다.
                        newDist = spacing(event);

                        if (newDist - oldDist > 20) {  // zoom in
                            float scale= (float) Math.sqrt(((float)(newDist-oldDist)*(newDist-oldDist))/(Height*Height + Width * Width));
                            Y=Y-(Height*scale/2);
                            X=X-(Width*scale/2);

                            Height=Height*(1+scale);
                            Width=Width*(1+scale);

                            oldDist = newDist;

                        } else if(oldDist - newDist > 20) {  // zoom out
                            float scale=(float) Math.sqrt(((newDist-oldDist)*(newDist-oldDist))/(Height*Height + Width * Width));
                            scale=0-scale;
                            Y=Y-(Height*scale/2);
                            X=X-(Width*scale/2);

                            Height=Height*(1+scale);
                            Width=Width*(1+scale);

                            oldDist = newDist;
                        }
                    }
                    break;

                case MotionEvent.ACTION_UP:    // 첫번째 손가락을 떼었을 경우
                case MotionEvent.ACTION_POINTER_UP:  // 두번째 손가락을 떼었을 경우
                    mode = NONE;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    //두번째 손가락 터치(손가락 2개를 인식하였기 때문에 핀치 줌으로 판별)
                    mode = ZOOM;
                    newDist = spacing(event);
                    oldDist = spacing(event);
                    Log.d("zoom", "newDist=" + newDist);
                    Log.d("zoom", "oldDist=" + oldDist);
                    Log.d("zoom", "mode=ZOOM");

                    break;
                case MotionEvent.ACTION_CANCEL:
                default :
                    break;
            }

        }
        //Rect 형태로 넘겨준다.
        public Rect getRect(){
            Rect rect=new Rect();
            rect.set((int)X,(int)Y, (int)(X+Width), (int)(Y+Height));
            return rect;
        }

        private float spacing(MotionEvent event) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(x * x + y * y);

        }
        public boolean InObject(float eventX,float eventY){
            if(eventX<(X+Width+30) &&  eventX>X-30 && eventY<Y+Height+30 &&eventY>Y-30){
                return true;
            }
            return false;
        }
        public void setSize(float Height,float Width){
            this.Height=Height;
            this.Width=Width;

        }
        public void setXY(float X, float Y){
            this.X=X;
            this.Y=Y;
        }
        public Bitmap getImage(){
            return Image;
        }
    }
    public class MoveObject extends View{
        int X,Y,Height,Width;
        private MovingUnit MU;

        public MoveObject(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }
        public void setSelectedImage(String ImagePath){
            Bitmap Image= BitmapFactory.decodeFile(ImagePath);
            MU=new MovingUnit(Image);
            invalidate();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            // TODO Auto-generated method stub
            MU.TouchProcess(event);
            invalidate();
            return (true);
        }

        @Override

        public void draw(Canvas canvas) {
            // TODO Auto-generated method stub
            canvas.drawBitmap(MU.getImage(), null,  MU.getRect(), null);


            super.draw(canvas);
        }
    }

}