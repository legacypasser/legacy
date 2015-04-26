package com.androider.legacy.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.androider.legacy.activity.PublishActivity;

import java.io.IOException;
import java.security.Policy;
import java.util.List;

/**
 * Created by Think on 2015/4/22.
 */
public class CapturePreview extends SurfaceView implements SurfaceHolder.Callback{

    SurfaceHolder holder;
    public Camera camera;

    public CapturePreview(Context context) {
        super(context);
        camera = Camera.open(0);
        holder = getHolder();
        holder.addCallback(this);
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.setDisplayOrientation(90);
        Camera.Parameters p = camera.getParameters();
        p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        p.setJpegQuality(100);
        camera.setParameters(p);
        camera.cancelAutoFocus();
    }

    public void takePicture(){
        camera.takePicture(null, null, PublishActivity.instance);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera.startPreview();
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(holder.getSurface() == null)
            return;
        camera.stopPreview();
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

}
