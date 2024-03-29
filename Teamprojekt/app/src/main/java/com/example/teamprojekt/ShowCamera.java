package com.example.teamprojekt;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;
import java.util.List;

public class ShowCamera extends SurfaceView implements SurfaceHolder.Callback {

    Camera camera;
    SurfaceHolder holder;
    SharedPreferences sharedPreferences;
    private final String prefName = "MyPref";
    private final String pref_aufloesung_breite = "breite";
    private final String pref_aufloesung_hoehe = "hoehe";


    public ShowCamera(Context context, Camera camera) {
        super(context);
        this.camera = camera;
        holder = getHolder();
        holder.addCallback(this);
        sharedPreferences = context.getSharedPreferences(prefName, 0);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Camera.Parameters params;
        params = camera.getParameters();

        List<Camera.Size> sizes = params.getSupportedPictureSizes();
        Camera.Size mSize = null;
        mSize = sizes.get(0);
        /*
        mSize.width = 640;
        mSize.height = 480;
        */


        //Sucht die kleinste Auflösung heraus und weist diese zu

        /*
        for(Camera.Size size : sizes){
            //System.out.println("Size width: "+ size.width +", Size height: " + size.height);
            if(size.width == saveClass.getAufloesung_breite() && mSize.height == saveClass.getAufloesung_hoehe()){
                mSize = size;
            }
        }
        */


        System.out.println("final Size : "+ sharedPreferences.getInt(pref_aufloesung_breite, 320) +" x " + sharedPreferences.getInt(pref_aufloesung_hoehe, 240));

        //Change orientation

        if(this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
            params.set("orientation", "portrait");
            camera.setDisplayOrientation(90);
            params.setRotation(90);
        }else{
            params.set("orientation", "landscape");
            camera.setDisplayOrientation(0);
            params.setRotation(0);
        }

        //Setzt die neue Auflösung
        params.setPictureSize(sharedPreferences.getInt(pref_aufloesung_breite, 320), sharedPreferences.getInt(pref_aufloesung_hoehe, 240));
        camera.setParameters(params);

        try{
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        }catch(IOException e){
            e.printStackTrace();
        }

    }
}
