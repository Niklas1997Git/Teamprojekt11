package com.example.teamprojekt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.view.View;

import java.io.IOException;
import java.io.OutputStream;

public class TakePicturesAutonom {

    Camera camera;
    AutonomerModusActivity autonomerModus;


    public TakePicturesAutonom(Camera camera, AutonomerModusActivity autonomerModusActivity){
        this.camera = camera;
        this.autonomerModus = autonomerModusActivity;
    }

    //wird durch captureImage() aufgerufen
    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback(){

        //Methode, die vom Button ausgelöst wird
        @Override
        public void onPictureTaken(byte[] data, Camera camera){
            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(cutOut(bmp), 60, 150, false);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            /*
            int mPhotoWidth = bmp.getWidth();
            int mPhotoHeight = bmp.getHeight();
            int[] pix = new int[mPhotoWidth * mPhotoHeight];
             */
            /*
            //  Here I want to slice a piece "out of bounds" starting at -50, -25
            //  Given an endposition of 150, 75 you will get a result of 200x100px
            Rect rect = new Rect(-50, -25, 150, 75);
            //  Be sure that there is at least 1px to slice.
            assert(rect.left < rect.right && rect.top < rect.bottom);
            //  Create our resulting image (150--50),(75--25) = 200x100px
            Bitmap resultBmp = Bitmap.createBitmap(rect.right-rect.left, rect.bottom-rect.top, Bitmap.Config.ARGB_8888);
            //  draw source bitmap into resulting image at given position:
            new Canvas(resultBmp).drawBitmap(bmp, -rect.left, -rect.top, null);
             */
            autonomerModus.capturePreview.setImageBitmap(rotatedBitmap);
            camera.startPreview();
        }
    };


    private Bitmap cutOut(Bitmap origialBitmap) {
        //Bitmap origialBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.original);

        int zeileOben = 100;
        int zeileUnten = 200;
        assert (zeileOben>=0 && zeileOben<zeileUnten && zeileUnten<=origialBitmap.getWidth());
        int spalteLinks = 200;
        int spalteRechts = 100;
        assert (spalteRechts>=0 && spalteRechts<spalteLinks && spalteLinks<=origialBitmap.getHeight());
        Bitmap cutBitmap = Bitmap.createBitmap(origialBitmap.getWidth() / 2,
                origialBitmap.getHeight() / 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(cutBitmap);
        Rect desRect = new Rect(0, 0, origialBitmap.getWidth() / 2, origialBitmap.getHeight() / 2);

        Rect srcRect = new Rect(zeileOben, spalteRechts,
                zeileUnten,
                spalteLinks);
        canvas.drawBitmap(origialBitmap, srcRect, desRect, null);
        return cutBitmap;
    }


    //Methode für den Button zum auslösen
    public void captureImageAutonom(View v){
        if(camera !=null){
            camera.takePicture(null,null, mPictureCallback);
        }
    }
}
